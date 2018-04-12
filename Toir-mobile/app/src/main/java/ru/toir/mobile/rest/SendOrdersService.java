package ru.toir.mobile.rest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 4/11/18.
 */

public class SendOrdersService extends Service {
    public static final String ORDERS_IDS = "ordersIds";
    public static final String ACTION = "ru.toir.mobile.rest.SEND_ORDERS";
    private static final String TAG = SendOrdersService.class.getSimpleName();
    private boolean isRuning;
    private Thread thread;
    private long[] ids;

    /**
     * Метод для выполнения отправки данных на сервер.
     */
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            AuthorizedUser user = AuthorizedUser.getInstance();
            boolean isValidUser = user.getLogin() != null && user.getToken() != null;
            if (!isValidUser) {
                stopSelf();
                return;
            }

            if (ids == null || ids.length == 0) {
                stopSelf();
                return;
            }

            int count = ids.length;
            Long[] ordersIds = new Long[count];
            for (int i = 0; i < count; i++) {
                ordersIds[i] = ids[i];
            }

            Realm realm = Realm.getDefaultInstance();
            RealmResults<Orders> items = realm.where(Orders.class).in("_id", ordersIds)
                    .findAll();
            // массив в который сохраним ид успешно переданных нарядов
            LongSparseArray<String> idUuid = new LongSparseArray<>();
            Call<ResponseBody> call = ToirAPIFactory.getOrdersService().send(realm.copyFromRealm(items));
            try {
                retrofit2.Response response = call.execute();
                ResponseBody result = (ResponseBody) response.body();
                if (response.isSuccessful()) {
                    JSONObject jObj = new JSONObject(result.string());
                    // при сохранении данных на сервере произошли ошибки
                    // данный флаг пока не используем
//                    boolean success = (boolean) jObj.get("success");
                    JSONArray data = (JSONArray) jObj.get("data");
                    for (int idx = 0; idx < data.length(); idx++) {
                        JSONObject item = (JSONObject) data.get(idx);
                        Long _id = Long.parseLong(item.get("_id").toString());
                        String uuid = item.get("uuid").toString();
                        idUuid.append(_id, uuid);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // отмечаем успешно отправленные наряды
            realm.beginTransaction();
            for (int idx = 0; idx < idUuid.size(); idx++) {
                long _id = idUuid.keyAt(idx);
                String uuid = idUuid.valueAt(idx);
                Orders value = realm.where(Orders.class)
                        .equalTo("_id", _id)
                        .equalTo("uuid", uuid)
                        .findFirst();
                if (value != null) {
                    value.setSent(true);
                }
            }

            realm.commitTransaction();
            realm.close();
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
            Log.d(TAG, "Запускаем поток отправки нарядов на сервер...");
            isRuning = true;
            ids = intent.getLongArrayExtra(ORDERS_IDS);
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
}
