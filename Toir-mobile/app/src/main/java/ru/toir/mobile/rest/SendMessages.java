package ru.toir.mobile.rest;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.Message;

/**
 * @author Olejek
 * Created by olejek on 26/09/19.
 */

public class SendMessages extends AsyncTask<Message[], Void, LongSparseArray<String>> {

    private AtomicInteger count;
    private WeakReference<Context> context;

    public SendMessages(@NonNull Context context, AtomicInteger count) {
        this.context = new WeakReference<>(context);
        this.count = count;
    }

    @Override
    protected LongSparseArray<String> doInBackground(Message[]... lists) {
        List<Message> args = Arrays.asList(lists[0]);
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Call<ResponseBody> call = ToirAPIFactory.getMessageService().send(args);
        try {
            Response response = call.execute();
            ResponseBody result = (ResponseBody) response.body();
            if (response.isSuccessful()) {
                JSONObject jObj = new JSONObject(result.string());
                // при сохранении данных на сервере произошли ошибки
                // данный флаг пока не используем
//                            boolean success = (boolean) jObj.get("success");
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

        return idUuid;
    }

    @Override
    protected void onPostExecute(LongSparseArray<String> idUuid) {
        super.onPostExecute(idUuid);

        if (count.decrementAndGet() <= 0) {
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context.get());
            broadcastManager.sendBroadcast(new Intent("all_task_have_complete"));
        }

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int idx = 0; idx < idUuid.size(); idx++) {
            long _id = idUuid.keyAt(idx);
            String uuid = idUuid.valueAt(idx);
            MeasuredValue value = realm.where(MeasuredValue.class).equalTo("_id", _id)
                    .equalTo("uuid", uuid)
                    .findFirst();
            if (value != null) {
                value.setSent(true);
            }
        }

        realm.commitTransaction();
        realm.close();
    }
}
