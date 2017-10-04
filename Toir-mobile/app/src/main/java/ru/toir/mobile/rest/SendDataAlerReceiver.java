package ru.toir.mobile.rest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.Journal;

/**
 * Created by koputo on 6/1/17.
 *
 * @author Logachev Dmitriy
 */

public class SendDataAlerReceiver extends BroadcastReceiver {
    private static final String TAG = "SendDataAlerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = "";

        if (intent != null) {
            action = intent.getAction();
            if (action == null) {
                action = "";
            }
        }

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
                Log.d(TAG, Intent.ACTION_BOOT_COMPLETED);
                // Взводим будильник при включении устройства
                Intent alarmIntent = new Intent(context, SendDataAlerReceiver.class);
                alarmIntent.setAction(SendGPSnLogService.ACTION);
                boolean isAlarmRun = (PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
                if (!isAlarmRun) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    // TODO: параметры таймера привести к разумным значениям
                    long startTime = SystemClock.elapsedRealtime() + 5 * 1000;
                    long frequency = 15 * 1000;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime, frequency, pendingIntent);
                    } else {
                        alarmManager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime, frequency, pendingIntent);
                    }
                }

                break;

            case SendGPSnLogService.ACTION:
                Log.d(TAG, SendGPSnLogService.ACTION);
                // получаем данные для отправки
                Realm realm = Realm.getDefaultInstance();

                RealmResults<GpsTrack> gpsItems = realm.where(GpsTrack.class).equalTo("sent", false).findAll();
                long[] gpsIds = new long[gpsItems.size()];
                for (int i = 0; i < gpsItems.size(); i++) {
                    gpsIds[i] = gpsItems.get(i).get_id();
                }

                RealmResults<Journal> logItems = realm.where(Journal.class).equalTo("sent", false).findAll();
                long[] logIds = new long[logItems.size()];
                for (int i = 0; i < logItems.size(); i++) {
                    logIds[i] = logItems.get(i).get_id();
                }

                // стартуем сервис отправки данных на сервер
                Intent serviceIntent = new Intent(context, SendGPSnLogService.class);
                serviceIntent.setAction(SendGPSnLogService.ACTION);
                Bundle bundle = new Bundle();
                bundle.putLongArray(SendGPSnLogService.GPS_IDS, gpsIds);
                bundle.putLongArray(SendGPSnLogService.LOG_IDS, logIds);
                serviceIntent.putExtras(bundle);
                context.startService(serviceIntent);
                realm.close();
                break;

            default:
                Log.d(TAG, "Unknown ACTION");
                break;
        }
    }
}
