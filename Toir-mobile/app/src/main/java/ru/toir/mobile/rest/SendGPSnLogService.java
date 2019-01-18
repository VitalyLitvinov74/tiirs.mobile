package ru.toir.mobile.rest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.Response;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.Journal;

/**
 * Created by koputo on 6/1/17.
 *
 * @author Logachev Dmitriy
 */

public class SendGPSnLogService extends Service {
    public static final String GPS_IDS = "gpsIds";
    public static final String LOG_IDS = "logIds";
    public static final String ACTION = "ru.toir.mobile.rest.SEND_GPS_AND_LOG";
    private static final String TAG = "SendGPSnLog";
    private boolean isRuning;
    private long[] gpsIds;
    private long[] logIds;

    /**
     * Метод для выполнения отправки данных на сервер.
     */
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            Realm realm = Realm.getDefaultInstance();
            Call<ResponseBody> call;

            if (gpsIds != null && gpsIds.length > 0) {
                int count = gpsIds.length;
                Long[] data = new Long[count];
                for (int i = 0; i < count; i++) {
                    data[i] = gpsIds[i];
                }

                RealmResults<GpsTrack> items = realm.where(GpsTrack.class).in("_id", data)
                        .equalTo("sent", false).findAll();
                // отправляем данные с координатами
                call = ToirAPIFactory.getGpsTrackService()
                        .send(new CopyOnWriteArrayList<>(realm.copyFromRealm(items)));
                try {
                    Response response = call.execute();
                    ResponseBody result = (ResponseBody) response.body();
                    if (response.isSuccessful()) {
                        LongSparseArray<String> idUuid = new LongSparseArray<>();
                        JSONObject jObj = new JSONObject(result.string());
                        // при сохранении данных на сервере произошли ошибки
                        // данный флаг пока не используем
//                        boolean success = (boolean) jObj.get("success");
                        JSONArray jData = (JSONArray) jObj.get("data");
                        for (int idx = 0; idx < jData.length(); idx++) {
                            JSONObject item = (JSONObject) jData.get(idx);
                            Long _id = Long.parseLong(item.get("_id").toString());
                            String uuid = item.get("uuid").toString();
                            idUuid.append(_id, uuid);
                        }

                        // так как на клиенте не используем эту информацию, удаляем
                        // после успешной отправки и сохранения
                        realm.beginTransaction();
                        for (int idx = 0; idx < idUuid.size(); idx++) {
                            long _id = idUuid.keyAt(idx);
                            String uuid = idUuid.valueAt(idx);
                            realm.where(GpsTrack.class).equalTo("_id", _id)
                                    .equalTo("userUuid", uuid)
                                    .findFirst()
                                    .deleteFromRealm();
                        }

                        realm.commitTransaction();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Ошибка при отправке GPS лога.");
                    e.printStackTrace();
                }
            }

            if (logIds != null && logIds.length > 0) {
                int count = logIds.length;
                Long[] data = new Long[count];
                for (int i = 0; i < count; i++) {
                    data[i] = logIds[i];
                }

                RealmResults<Journal> items = realm.where(Journal.class).in("_id", data)
                        .findAll();
                // отправляем данные с логами
                call = ToirAPIFactory.getJournalService()
                        .send(new CopyOnWriteArrayList<>(realm.copyFromRealm(items)));
                try {
                    Response response = call.execute();
                    ResponseBody result = (ResponseBody) response.body();
                    if (response.isSuccessful()) {
                        LongSparseArray<String> idUuid = new LongSparseArray<>();
                        JSONObject jObj = new JSONObject(result.string());
                        // при сохранении данных на сервере произошли ошибки
                        // данный флаг пока не используем
//                        boolean success = (boolean) jObj.get("success");
                        JSONArray jData = (JSONArray) jObj.get("data");
                        for (int idx = 0; idx < jData.length(); idx++) {
                            JSONObject item = (JSONObject) jData.get(idx);
                            Long _id = Long.parseLong(item.get("_id").toString());
                            String uuid = item.get("uuid").toString();
                            idUuid.append(_id, uuid);
                        }

                        // так как на клиенте не используем эту информацию, удаляем
                        // после успешной отправки и сохранения
                        realm.beginTransaction();
                        for (int idx = 0; idx < idUuid.size(); idx++) {
                            long _id = idUuid.keyAt(idx);
                            String uuid = idUuid.valueAt(idx);
                            realm.where(Journal.class).equalTo("_id", _id)
                                    .equalTo("userUuid", uuid)
                                    .findFirst()
                                    .deleteFromRealm();
                        }

                        realm.commitTransaction();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Ошибка при отправке журнала.");
                }
            }

            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        isRuning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (!isRuning) {
            Log.d(TAG, "Запускаем поток отправки логов и координат на сервер...");
            isRuning = true;
            gpsIds = intent.getLongArrayExtra(GPS_IDS);
            logIds = intent.getLongArrayExtra(LOG_IDS);
            new Thread(task).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRuning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
