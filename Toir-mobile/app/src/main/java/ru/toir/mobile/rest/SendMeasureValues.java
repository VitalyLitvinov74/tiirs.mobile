package ru.toir.mobile.rest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.util.LongSparseArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.*;
import ru.toir.mobile.db.realm.MeasuredValue;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/21/18.
 */

public class SendMeasureValues extends AsyncTask<MeasuredValue[], Void, LongSparseArray<String>> {

    private ProgressDialog dialog;

    public SendMeasureValues(ProgressDialog d) {
        dialog = d;
    }

    @Override
    protected LongSparseArray<String> doInBackground(MeasuredValue[]... lists) {
        List<MeasuredValue> args = Arrays.asList(lists[0]);
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Call<ResponseBody> call = ToirAPIFactory.getMeasuredValueService().send(args);
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
        if (dialog != null) {
            dialog.dismiss();
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

        // TODO: решить что делать!
        // это сообщение связанно с логикой отправки результатов на сервер
        // т.е. этот процесс запускается последним, и по его завершению сообщаем пользователю
        // что всё отправлено
        // TODO: нужно избавиться от такого поведения, сообщение выдавать в подходящем месте!!!
//        Toast.makeText(context, "Результаты отправлены на сервер.", Toast.LENGTH_SHORT).show();
    }
}
