package ru.toir.mobile.multi.rest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.GpsTrack;
import ru.toir.mobile.multi.db.realm.Journal;
import ru.toir.mobile.multi.db.realm.OrderStatus;
import ru.toir.mobile.multi.db.realm.Orders;

/**
 * @author Logachev Dmitriy
 *         Created by koputo on 4/20/18.
 */

public class ForegroundService extends Service {

    private static final String TAG = ForegroundService.class.getSimpleName();
    private static final long START_INTERVAL = 60000;
    private Handler sendLog;
    private Handler sendResult;
    private Handler getOrder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        String channelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        } else {
            channelId = "sman";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.toir_notify)
                .setContentText("Сервис ТОИРУС")
                .setSubText("Получение/отправка данных");
        Notification notification;
        notification = builder.build();
        startForeground(777, notification);

        // запуск отправки логов и координат на сервер
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSendLog();
            }
        }, 0);

        // запуск отправки результатов работы на сервер
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startSendResult();
            }
        }, 20000);

        // запуск получения нарядов с сервера
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startGetOrder();
            }
        }, 40000);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = "sman";
        String channelName = "My Background Service";
        NotificationChannel channel = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (service != null) {
            service.createNotificationChannel(channel);
        }

        return channelId;
    }

    /**
     *
     */
    private void startSendLog() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long[] ids;
                Intent serviceIntent;
                Bundle bundle = null;

                Log.d(TAG, "startSendLog()");

                if (!isValidUser()) {
                    Log.d(TAG, "Нет активного пользователя для отправки логов и координат на сервер.");
                    // взводим следующий запуск
                    sendLog.postDelayed(this, START_INTERVAL);
                    return;
                }

                // получаем данные для отправки
                Realm realm = Realm.getDefaultInstance();
                RealmResults<GpsTrack> gpsItems = realm.where(GpsTrack.class)
                        .equalTo("sent", false).limit(10).findAll();
                if (gpsItems.size() > 0) {
                    ids = new long[gpsItems.size()];
                    for (int i = 0; i < gpsItems.size(); i++) {
                        ids[i] = gpsItems.get(i).get_id();
                    }

                    bundle = new Bundle();
                    bundle.putLongArray(SendGPSnLogService.GPS_IDS, ids);
                }

                RealmResults<Journal> logItems = realm.where(Journal.class)
                        .equalTo("sent", false).limit(10).findAll();
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
                Context context = getApplicationContext();
                if (bundle != null) {
                    serviceIntent = new Intent(context, SendGPSnLogService.class);
                    serviceIntent.setAction(SendGPSnLogService.ACTION);
                    serviceIntent.putExtras(bundle);
                    context.startService(serviceIntent);
                }

                realm.close();

                // взводим следующий запуск
                sendLog.postDelayed(this, START_INTERVAL);
            }
        };
        sendLog = new Handler();
        sendLog.postDelayed(runnable, START_INTERVAL);
    }

    /**
     *
     */
    private void startSendResult() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long[] ids;
                Intent serviceIntent;

                Log.d(TAG, "startSendResult()");

                if (!isValidUser()) {
                    Log.d(TAG, "Нет активного пользователя для отправки нарядов на сервер.");
                    // взводим следующий запуск
                    sendResult.postDelayed(this, START_INTERVAL);
                    return;
                }

                // получаем данные для отправки
                AuthorizedUser user = AuthorizedUser.getInstance();
                Realm realm = Realm.getDefaultInstance();
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
                        .limit(10)
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
                Context context = getApplicationContext();
                serviceIntent = new Intent(context, SendOrdersService.class);
                serviceIntent.setAction(SendOrdersService.ACTION);
                Bundle bundle = new Bundle();
                bundle.putLongArray(SendOrdersService.ORDERS_IDS, ids);
                serviceIntent.putExtras(bundle);
                context.startService(serviceIntent);
                realm.close();

                // взводим следующий запуск
                sendResult.postDelayed(this, START_INTERVAL);
            }
        };
        sendResult = new Handler();
        sendResult.postDelayed(runnable, START_INTERVAL);
    }

    /**
     *
     */
    private void startGetOrder() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startGetOrder()");

                if (!isValidUser()) {
                    Log.d(TAG, "Нет активного пользователя для получения нарядов.");
                    // взводим следующий запуск
                    getOrder.postDelayed(this, START_INTERVAL);
                    return;
                }

                ArrayList<String> statusUuids = new ArrayList<>();
                statusUuids.add(OrderStatus.Status.NEW);

                // стартуем сервис получения новых нарядов с сервера
                Context context = getApplicationContext();
                Intent serviceIntent = new Intent(context, GetOrdersService.class);
                serviceIntent.setAction(GetOrdersService.ACTION);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(GetOrdersService.ORDER_STATUS_UUIDS, statusUuids);
                serviceIntent.putExtras(bundle);
                context.startService(serviceIntent);

                // взводим следующий запуск
                getOrder.postDelayed(this, START_INTERVAL);
            }
        };
        getOrder = new Handler();
        getOrder.postDelayed(runnable, START_INTERVAL);
    }

    private boolean isValidUser() {
        AuthorizedUser user = AuthorizedUser.getInstance();
        boolean isValidUser = user.getLogin() != null && user.getToken() != null;
        return isValidUser;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
