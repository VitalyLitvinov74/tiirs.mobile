package ru.toir.mobile.rest;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.util.LongSparseArray;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.*;
import ru.toir.mobile.db.realm.OperationFile;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/21/18.
 */

public class SendFiles extends AsyncTask<OperationFile[], Void, LongSparseArray<String>> {

    private File extDir;

    public SendFiles(File e) {
        extDir = e;
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
    protected LongSparseArray<String> doInBackground(OperationFile[]... lists) {
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Realm realm = Realm.getDefaultInstance();
        RequestBody descr = RequestBody.create(MultipartBody.FORM, "Photos due execution operation.");

        for (OperationFile file : lists[0]) {
            List<MultipartBody.Part> list = new ArrayList<>();

            try {
                File path = new File(extDir.getAbsolutePath() + '/' + file.getImageFilePath(),
                        file.getFileName());
                Uri uri = Uri.fromFile(path);
                String fileUuid = file.getUuid();
                String formId = "file[" + fileUuid + "]";

                list.add(prepareFilePart(formId, uri));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[_id]", String.valueOf(file.get_id())));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[uuid]", fileUuid));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[operationUuid]", file.getOperation().getUuid()));
                list.add(MultipartBody.Part
                        .createFormData(formId + "[fileName]", file.getFileName()));
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
            Call<ResponseBody> call = ToirAPIFactory.getOperationFileService().upload(descr, list);
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

        realm.close();

        return idUuid;
    }

    @Override
    protected void onPostExecute(LongSparseArray<String> idUuid) {
        super.onPostExecute(idUuid);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int idx = 0; idx < idUuid.size(); idx++) {
            long id = idUuid.keyAt(idx);
            String uuid = idUuid.valueAt(idx);
            OperationFile file = realm.where(OperationFile.class).equalTo("_id", id)
                    .equalTo("uuid", uuid).findFirst();
            if (file != null) {
                file.setSent(true);
            }
        }

        realm.commitTransaction();
        realm.close();
    }
}
