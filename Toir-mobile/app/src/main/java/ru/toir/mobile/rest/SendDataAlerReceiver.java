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

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.Journal;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;

/**
 * Created by koputo on 6/1/17.
 *
 * @author Logachev Dmitriy
 */

public class SendDataAlerReceiver extends BroadcastReceiver {
    private static final String TAG = SendDataAlerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive()");
        String action = "";
        Realm realm;
        long[] ids;
        Intent serviceIntent;
        Bundle bundle = null;
        // Мобильный клиент сейчас вообще ни как не разделяет пользователей по организациям.
        // В этой связи может возникнуть ситуация когда пользователь из одной организации попытается
        // отправить наряд пользователя из другой. По этому отправлять будем только наряды текущего
        // пользователя, которые гарантированно будут отправленны в нужную базу на сервере.
        AuthorizedUser user = AuthorizedUser.getInstance();
        boolean isValidUser = user.getLogin() != null && user.getToken() != null;

        if (intent != null) {
            action = intent.getAction();
            if (action == null) {
                action = "";
            }
        }

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:
                Log.d(TAG, Intent.ACTION_BOOT_COMPLETED);
                Intent alarmIntent;

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager == null) {
                    Log.d(TAG, "Не удалось получить AlarmManager.");
                    break;
                }

                // Взводим будильник при включении устройства для отправки логов и координат
                alarmIntent = new Intent(context, SendDataAlerReceiver.class);
                alarmIntent.setAction(SendGPSnLogService.ACTION);
                if (!isAlarmRun(context, alarmIntent)) {
                    // задержка перед запуском будильника
                    long startTime = SystemClock.elapsedRealtime() + 5 * 1000;
                    // раз в минуту будем принудительно запускать сервис отправки данных
                    long frequency = 60 * 1000;
                    riseAlarm(context, alarmManager, alarmIntent, startTime, frequency);
                }

                // Взводим будильник при включении устройства для отправки нарядов
                alarmIntent = new Intent(context, SendDataAlerReceiver.class);
                alarmIntent.setAction(SendOrdersService.ACTION);
                if (!isAlarmRun(context, alarmIntent)) {
                    // задержка перед запуском будильника
                    long startTime = SystemClock.elapsedRealtime() + 25 * 1000;
                    // раз в минуту будем принудительно запускать сервис отправки данных
                    long frequency = 60 * 1000;
                    riseAlarm(context, alarmManager, alarmIntent, startTime, frequency);
                }

                // Взводим будильник при включении устройства для получения нарядов
                alarmIntent = new Intent(context, SendDataAlerReceiver.class);
                alarmIntent.setAction(GetOrdersService.ACTION);
                if (!isAlarmRun(context, alarmIntent)) {
                    // задержка перед запуском будильника
                    long startTime = SystemClock.elapsedRealtime() + 45 * 1000;
                    // раз в минуту будем принудительно запускать сервис получения нарядов
                    long frequency = 60 * 1000;
                    riseAlarm(context, alarmManager, alarmIntent, startTime, frequency);
                }

                break;

            case SendGPSnLogService.ACTION:
                Log.d(TAG, SendGPSnLogService.ACTION);
                if (!isValidUser) {
                    Log.d(TAG, "Нет активного пользователя для отправки логов и координат на сервер.");
                    break;
                }

                // получаем данные для отправки
                realm = Realm.getDefaultInstance();
                RealmResults<GpsTrack> gpsItems = realm.where(GpsTrack.class)
                        .equalTo("sent", false).findAll();
                if (gpsItems.size() > 0) {
                    ids = new long[gpsItems.size()];
                    for (int i = 0; i < gpsItems.size(); i++) {
                        ids[i] = gpsItems.get(i).get_id();
                    }

                    bundle = new Bundle();
                    bundle.putLongArray(SendGPSnLogService.GPS_IDS, ids);
                }

                RealmResults<Journal> logItems = realm.where(Journal.class)
                        .equalTo("sent", false).findAll();
                if (logItems.size() > 0) {
                    ids = new long[logItems.size()];
                    for (int i = 0; i < logItems.size(); i++) {
                        ids[i] = logItems.get(i).get_id();
                    }

                    if (bundle == null) {
                        bundle = new Bundle();
                        bundle.putLongArray(SendGPSnLogService.LOG_IDS, ids);
                    }
                }

                // стартуем сервис отправки данных на сервер
                if (bundle != null) {
                    serviceIntent = new Intent(context, SendGPSnLogService.class);
                    serviceIntent.setAction(SendGPSnLogService.ACTION);
                    serviceIntent.putExtras(bundle);
                    context.startService(serviceIntent);
                }

                realm.close();
                break;

            case SendOrdersService.ACTION:
                Log.d(TAG, SendOrdersService.ACTION);
                if (!isValidUser) {
                    Log.d(TAG, "Нет активного пользователя для отправки нарядов на сервер.");
                    break;
                }

                // получаем данные для отправки
                realm = Realm.getDefaultInstance();
                RealmResults<Orders> orders = realm.where(Orders.class)
                        .beginGroup()
                        .equalTo("user.uuid", user.getUuid())
                        .equalTo("sent", false)
                        .endGroup()
                        .beginGroup()
                        .equalTo("orderStatus.uuid", OrderStatus.Status.COMPLETE).or()
                        .equalTo("orderStatus.uuid", OrderStatus.Status.UN_COMPLETE).or()
//                        .equalTo("orderStatus.uuid", OrderStatus.Status.IN_WORK).or()
                        .equalTo("orderStatus.uuid", OrderStatus.Status.CANCELED)
                        .endGroup()
                        .findAll();
                if (orders.size() == 0) {
                    Log.d(TAG, "Нет нарядов для отправки.");
                    ids = null;
                } else {
                    ids = new long[orders.size()];
                    for (int i = 0; i < orders.size(); i++) {
                        ids[i] = orders.get(i).get_id();
                    }
                }

                // стартуем сервис отправки данных на сервер
                serviceIntent = new Intent(context, SendOrdersService.class);
                serviceIntent.setAction(SendOrdersService.ACTION);
                bundle = new Bundle();
                bundle.putLongArray(SendOrdersService.ORDERS_IDS, ids);
                serviceIntent.putExtras(bundle);
                context.startService(serviceIntent);
                realm.close();
                break;

            case GetOrdersService.ACTION:
                Log.d(TAG, GetOrdersService.ACTION);
                if (!isValidUser) {
                    Log.d(TAG, "Нет активного пользователя для получения нарядов.");
                    break;
                }

                ArrayList<String> statusUuids = new ArrayList<>();
                statusUuids.add(OrderStatus.Status.NEW);

                // стартуем сервис отправки данных на сервер
                serviceIntent = new Intent(context, GetOrdersService.class);
                serviceIntent.setAction(GetOrdersService.ACTION);
                bundle = new Bundle();
                bundle.putStringArrayList(GetOrdersService.ORDER_STATUS_UUIDS, statusUuids);
                serviceIntent.putExtras(bundle);
                context.startService(serviceIntent);
                break;

            default:
                Log.d(TAG, "Unknown ACTION");
                break;
        }
    }

    /**
     * Проверяем не взведён ли уже необходимый будильник.
     *
     * @param context Context
     * @param intent  Intent
     * @return boolean
     */
    private boolean isAlarmRun(Context context, Intent intent) {
        return (PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_NO_CREATE) != null);
    }

    /**
     * Взводим будильник.
     *
     * @param context   Context
     * @param manager   AlarmManager
     * @param intent    Intent
     * @param start     Start time
     * @param frequency Frequency
     */
    private void riseAlarm(Context context, AlarmManager manager, Intent intent, long start,
                           long frequency) {

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, start, frequency, pendingIntent);
        } else {
            manager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, start, frequency, pendingIntent);
        }
    }
}
