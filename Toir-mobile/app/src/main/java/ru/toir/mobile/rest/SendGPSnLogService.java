package ru.toir.mobile.rest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
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
    private Thread thread;
    private long[] gpsIds;
    private long[] logIds;
    /**
     * Метод для выполнения отправки данных на сервер.
     */
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

                RealmResults<GpsTrack> items = realm.where(GpsTrack.class).in("_id", data).findAll();
                // отправляем данные
                call = ToirAPIFactory.getGpsTrackService().sendGpsTrack(items);
                try {
                    Response<ToirAPIResponse> response = call.execute();
                    if (response.isSuccessful()) {
                        // отмечаем отправленные данные
                        realm.beginTransaction();
                        for (GpsTrack item : items) {
                            item.setSent(true);
                        }

                        realm.commitTransaction();
                    } else {
                        // TODO: реализовать отметку только тех данных которые успешно переданны на сервер
                        response.body().getData();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }

            if (logIds.length > 0) {
                int count = logIds.length;
                Long[] data = new Long[count];
                for (int i = 0; i < count; i++) {
                    data[i] = logIds[i];
                }

                RealmResults<Journal> items = realm.where(Journal.class).in("_id", data).findAll();
                // отправляем данные
                call = ToirAPIFactory.getJournalService().sendJournal(items);
                try {
                    Response<ToirAPIResponse> response = call.execute();
                    if (response.isSuccessful()) {
                        // отмечаем отправленные данные
                        realm.beginTransaction();
                        for (Journal item : items) {
                            item.setSent(true);
                        }

                        realm.commitTransaction();
                    } else {
                        // TODO: реализовать отметку только тех данных которые успешно переданны на сервер
                        response.body().getData();
                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
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
}
