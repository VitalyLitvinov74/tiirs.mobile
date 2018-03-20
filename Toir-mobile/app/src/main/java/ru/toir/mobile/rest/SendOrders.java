package ru.toir.mobile.rest;

import android.os.AsyncTask;
import android.support.v4.util.LongSparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.*;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/21/18.
 */

public class SendOrders extends AsyncTask<Orders[], Void, LongSparseArray<String>> {
    @Override
    protected LongSparseArray<String> doInBackground(Orders[]... lists) {
        List<Orders> args = Arrays.asList(lists[0]);
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Call<ResponseBody> call = ToirAPIFactory.getOrdersService().send(args);
        try {
            retrofit2.Response response = call.execute();
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
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int idx = 0; idx < idUuid.size(); idx++) {
            long _id = idUuid.keyAt(idx);
            String uuid = idUuid.valueAt(idx);
            Orders value = realm.where(Orders.class).equalTo("_id", _id)
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
