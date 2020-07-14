package ru.toir.mobile.multi.rest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.LongSparseArray;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.multi.db.realm.MediaFile;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/21/18.
 */

public class SendFiles extends AsyncTask<MediaFile[], Void, LongSparseArray<String>> {

    private File extDir;
    private WeakReference<Context> context;
    private AtomicInteger count;

    public SendFiles(File e, @NonNull Context context, AtomicInteger count) {
        extDir = e;
        this.context = new WeakReference<>(context);
        this.count = count;
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        File file = new File(fileUri.getPath());
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        MediaType mediaType = MediaType.parse(type);
        RequestBody requestFile = RequestBody.create(mediaType, file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    @Override
    protected LongSparseArray<String> doInBackground(MediaFile[]... lists) {
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        RequestBody descr = RequestBody.create(MultipartBody.FORM, "Photos due execution operation.");

        for (MediaFile file : lists[0]) {
            List<MultipartBody.Part> list = new ArrayList<>();

            try {
                File path = new File(extDir.getAbsolutePath() + '/' + file.getPath(), file.getName());
                Uri uri = Uri.fromFile(path);
                String fileUuid = file.getUuid();
                String formId = "file[" + fileUuid + "]";

                list.add(prepareFilePart(formId, uri));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[_id]", String.valueOf(file.get_id())));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[uuid]", fileUuid));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[entityUuid]", file.getEntityUuid()));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[path]", file.getPath()));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[name]", file.getName()));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[createdAt]", String.valueOf(file.getCreatedAt())));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[changedAt]", String.valueOf(file.getChangedAt())));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            // запросы делаем по одному, т.к. может сложиться ситуация когда будет попытка отправить
            // объём данных превышающий ограничения на отправку POST запросом на сервере
            Call<ResponseBody> call = ToirAPIFactory.getMediaFileService().upload(descr, list);
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
                        idUuid.put(_id, uuid);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            long id = idUuid.keyAt(idx);
            String uuid = idUuid.valueAt(idx);
            MediaFile file = realm.where(MediaFile.class).equalTo("_id", id)
                    .equalTo("uuid", uuid).findFirst();
            if (file != null) {
                file.setSent(true);
            }
        }

        realm.commitTransaction();
        realm.close();
    }
}
