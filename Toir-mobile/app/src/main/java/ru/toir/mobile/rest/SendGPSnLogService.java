package ru.toir.mobile.rest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.*;
import retrofit2.Response;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.ISend;
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
    private Thread thread;
    private long[] gpsIds;
    private long[] logIds;
    /**
     * Метод для выполнения отправки данных на сервер.
     */
    @SuppressWarnings("unchecked")
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            Realm realm = Realm.getDefaultInstance();
            Call<ToirAPIResponse> call;

            if (gpsIds.length > 0) {
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
                    Response<ToirAPIResponse> response = call.execute();
                    if (response.isSuccessful()) {
                        ToirAPIResponse apiResponse = response.body();
                        realm.beginTransaction();
                        if (!apiResponse.isSuccess()) {
                            List<String> listIds;
                            try {
                                listIds = ((List<String>) apiResponse.getData());
                                // удаляем из списка не сохраннённые элементы
                                removeNotSaved(items, listIds);

                                // отмечаем отправленные данные
                                for (GpsTrack item : items) {
                                    item.setSent(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        realm.commitTransaction();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Ошибка при отправке GPS лога.");
                    e.printStackTrace();
                }
            }

            if (logIds.length > 0) {
                int count = logIds.length;
                Long[] data = new Long[count];
                for (int i = 0; i < count; i++) {
                    data[i] = logIds[i];
                }

                RealmResults<Journal> items = realm.where(Journal.class).in("_id", data).equalTo("sent", false).findAll();
                // отправляем данные с логами
                call = ToirAPIFactory.getJournalService().send(new CopyOnWriteArrayList<>(realm.copyFromRealm(items)));
                try {
                    Response<ToirAPIResponse> response = call.execute();
                    if (response.isSuccessful()) {
                        ToirAPIResponse apiResponse = response.body();
                        realm.beginTransaction();
                        if (!apiResponse.isSuccess()) {
                            List<String> listIds;
                            try {
                                listIds = ((List<String>) apiResponse.getData());
                                // удаляем из списка не сохраннённые элементы
                                removeNotSaved(items, listIds);

                                // отмечаем отправленные данные
                                for (Journal item : items) {
                                    item.setSent(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        thread = new Thread(task);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (!isRuning) {
            Log.d(TAG, "Запускаем поток отправки данных на сервер...");
            isRuning = true;
            gpsIds = intent.getLongArrayExtra(GPS_IDS);
            logIds = intent.getLongArrayExtra(LOG_IDS);
            thread.start();
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

    /**
     * Вспомогательный метод для удаления из отправленого списка записей тех которые
     * не сохранены на сервере.
     *
     * @param list Список отправленных записей.
     * @param data Список id записей которые не сохранили.
     */
    private void removeNotSaved(List<? extends ISend> list, List<String> data) {

        if (list == null || data == null) {
            return;
        }

        List<Long> ids = new ArrayList<>();
        for (String item : data) {
            ids.add(Long.valueOf(item));
        }

        for (Object item : list) {
            if (ids.contains(((ISend) item).get_id())) {
                // удаляем из списка данных для отметки об успешной отправки, те что не сохранил сервер
                list.remove(item);
            }
        }
    }


}
