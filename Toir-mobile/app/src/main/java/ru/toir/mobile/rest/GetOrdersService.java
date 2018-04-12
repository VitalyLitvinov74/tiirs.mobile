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
import retrofit2.Call;
import retrofit2.Response;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.Journal;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 4/12/18.
 */

public class GetOrdersService extends Service {
    public static final String ORDERS_IDS = "ordersIds";
    public static final String ACTION = "ru.toir.mobile.rest.GET_ORDERS";
    private static final String TAG = GetOrdersService.class.getSimpleName();
    private boolean isRuning;
    private Thread thread;
    private long[] ids;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
