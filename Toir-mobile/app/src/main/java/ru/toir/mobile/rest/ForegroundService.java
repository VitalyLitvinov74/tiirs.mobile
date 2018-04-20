package ru.toir.mobile.rest;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ru.toir.mobile.R;

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

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "toir")
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

    /**
     *
     */
    private void startSendLog() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "startSendLog()");
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
                Log.d(TAG, "startSendResult()");
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
                getOrder.postDelayed(this, START_INTERVAL);
            }
        };
        getOrder = new Handler();
        getOrder.postDelayed(runnable, START_INTERVAL);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
