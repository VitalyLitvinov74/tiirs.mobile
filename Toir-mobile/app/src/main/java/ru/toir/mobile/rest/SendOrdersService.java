package ru.toir.mobile.rest;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationFile;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.Task;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 4/11/18.
 */

public class SendOrdersService extends Service {
    public static final String ORDERS_IDS = "ordersIds";
    public static final String ACTION = "ru.toir.mobile.rest.SEND_ORDERS";
    private static final String TAG = SendOrdersService.class.getSimpleName();
    private boolean isRuning;
    private long[] ids;
    private Context context;

    /**
     * Метод для выполнения отправки данных на сервер.
     */
    private Runnable task = new Runnable() {
        @Override
        public void run() {
            AuthorizedUser user = AuthorizedUser.getInstance();
            boolean isValidUser = user.getLogin() != null && user.getToken() != null;
            if (!isValidUser) {
                stopSelf();
                return;
            }

            // массив в который сохраним ид успешно переданных записей (наряды, файлы, измерения)
            LongSparseArray<String> idUuid;

            // выбираем все наряды для отправки
            Realm realm = Realm.getDefaultInstance();

            if (ids != null && ids.length > 0) {

                int count = ids.length;
                Long[] ordersIds = new Long[count];
                for (int i = 0; i < count; i++) {
                    ordersIds[i] = ids[i];
                }

                RealmResults<Orders> orders = realm.where(Orders.class).in("_id", ordersIds)
                        .findAll();
                // отправляем наряды
                idUuid = sendOrders(realm.copyFromRealm(orders));
                // отмечаем успешно отправленные наряды
                setSendOrders(idUuid, realm);

                // список всех uuid операций во всех нарядах
                String[] operationUuids = getAllOperations(orders).toArray(new String[]{});

                // получаем список всех файлов связанных с операциями
                RealmResults<OperationFile> operationFiles = realm.where(OperationFile.class)
                        .in("operation.uuid", operationUuids)
                        .equalTo("sent", false)
                        .findAll();
                // отправляем файлы на сервер
                idUuid = sendOperationFiles(realm.copyFromRealm(operationFiles));
                // отмечаем успешно отправленные файлы
                setSendOperationFiles(idUuid, realm);

                // получаем все измерения связанные с выполненными операциями
                RealmResults<MeasuredValue> measuredValues = realm.where(MeasuredValue.class)
                        .equalTo("sent", false)
                        .in("operation.uuid", operationUuids)
                        .findAll();
                // отправляем данные на сервер
                idUuid = sendMeasuredValues(realm.copyFromRealm(measuredValues));
                // отмечаем успешно отправленные измерения
                setSendMeasuredValues(idUuid, realm);
            }

            // выбираем все неотправленные по каким-то причинам ранее файлы
            // (не обязательно входящие в наряды которые сейчас отправляем)
            List<OperationFile> sendOldFiles = new ArrayList<>();
            RealmResults<OperationFile> oldFiles = realm.where(OperationFile.class)
                    .equalTo("sent", false).findAll();
            for (OperationFile file : oldFiles) {
                Stage stage = realm.where(Stage.class)
                        .equalTo("uuid", file.getOperation().getStageUuid()).findFirst();
                Task task = realm.where(Task.class).equalTo("uuid", stage.getTaskUuid())
                        .findFirst();
                Orders order = realm.where(Orders.class).equalTo("uuid", task.getOrderUuid())
                        .findFirst();
                // для того чтобы не отправлялись данные по выполняемым прямо сейчас нарядам
                // дополнительно проверяем что наряд уже был отправлен
                if (user.getUuid().equals(order.getUser().getUuid()) && order.isSent()) {
                    sendOldFiles.add(realm.copyFromRealm(file));
                }
            }

            if (sendOldFiles.size() > 0) {
                // отправляем файлы на сервер
                idUuid = sendOperationFiles(sendOldFiles);
                // отмечаем успешно отправленные файлы
                setSendOperationFiles(idUuid, realm);
            }

            // выбираем все неотправленные по каким-то причинам ранее измерения
            // (не обязательно входящие в наряды которые сейчас отправляем)
            List<MeasuredValue> sendOldMeasuredValues = new ArrayList<>();
            RealmResults<MeasuredValue> oldMeasuredValues = realm.where(MeasuredValue.class)
                    .equalTo("sent", false).findAll();
            for (MeasuredValue measuredValue : oldMeasuredValues) {
                String stageUuid = measuredValue.getOperation().getStageUuid();
                Stage stage = realm.where(Stage.class).equalTo("uuid", stageUuid)
                        .findFirst();
                Task task = realm.where(Task.class).equalTo("uuid", stage.getTaskUuid())
                        .findFirst();
                Orders order = realm.where(Orders.class).equalTo("uuid", task.getOrderUuid())
                        .findFirst();
                // для того чтобы не отправлялись данные по выполняемым прямо сейчас нарядам
                // дополнительно проверяем что наряд уже был отправлен
                if (user.getUuid().equals(order.getUser().getUuid()) && order.isSent()) {
                    sendOldMeasuredValues.add(realm.copyFromRealm(measuredValue));
                }
            }

            if (sendOldMeasuredValues.size() > 0) {
                // отправляем измерения на сервер
                idUuid = sendMeasuredValues(sendOldMeasuredValues);
                // отмечаем успешно отправленные измерения
                setSendMeasuredValues(idUuid, realm);
            }

            realm.close();

            stopSelf();
        }
    };

    @Override
    public void onCreate() {
        isRuning = false;
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (!isRuning) {
            Log.d(TAG, "Запускаем поток отправки нарядов на сервер...");
            isRuning = true;
            ids = intent.getLongArrayExtra(ORDERS_IDS);
            new Thread(task).start();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRuning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

    private List<MultipartBody.Part> getMultipartBody(OperationFile file, File path) {
        List<MultipartBody.Part> list = new ArrayList<>();
        try {
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
            return null;
        }

        return list;
    }

    /**
     * Отправляем файлы
     *
     * @param files List<OperationFile>
     * @return LongSparseArray<String>
     */
    private LongSparseArray<String> sendOperationFiles(List<OperationFile> files) {
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        RequestBody descr = RequestBody.create(MultipartBody.FORM,
                "Photos due execution operation.");

        // запросы делаем по одному, т.к. может сложиться ситуация когда будет попытка отправить
        // объём данных превышающий ограничения на отправку POST запросом на сервере
        for (OperationFile file : files) {
            File extDir = context.getExternalFilesDir(file.getImageFilePath());
            File operationFile = new File(extDir, file.getFileName());
            if (operationFile.exists()) {
                Call<ResponseBody> call = ToirAPIFactory.getOperationFileService()
                        .upload(descr, getMultipartBody(file, operationFile));
                try {
                    retrofit2.Response response = call.execute();
                    ResponseBody result = (ResponseBody) response.body();
                    if (response.isSuccessful()) {
                        JSONObject jObj = new JSONObject(result.string());
                        // при сохранении данных на сервере произошли ошибки
                        // данный флаг пока не используем
//                        boolean success = (boolean) jObj.get("success");
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
        }

        return idUuid;
    }

    /**
     * Отмечаем успешно отправленные файлы
     *
     * @param idUuid LondSparseArray<String>
     */
    private void setSendOperationFiles(LongSparseArray<String> idUuid, Realm realm) {
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
    }

    /**
     * Отправляем измеренные значения.
     *
     * @param list List<MeasuredValue>
     * @return LongSparseArray<String>
     */
    private LongSparseArray<String> sendMeasuredValues(List<MeasuredValue> list) {
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Call<ResponseBody> call = ToirAPIFactory.getMeasuredValueService().send(list);
        try {
            retrofit2.Response response = call.execute();
            ResponseBody result = (ResponseBody) response.body();
            if (response.isSuccessful()) {
                JSONObject jObj = new JSONObject(result.string());
                // при сохранении данных на сервере произошли ошибки
                // данный флаг пока не используем
//                boolean success = (boolean) jObj.get("success");
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

        return idUuid;
    }

    /**
     * Отмечаем успешно отправленные измерения
     *
     * @param idUuid LongSparseArray<String>
     */
    private void setSendMeasuredValues(LongSparseArray<String> idUuid, Realm realm) {
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
    }

    /**
     * Отправляем наряды
     *
     * @param items List<Orders>
     * @return LongSparseArray<String>
     */
    private LongSparseArray<String> sendOrders(List<Orders> items) {
        LongSparseArray<String> idUuid = new LongSparseArray<>();
        Call<ResponseBody> call = ToirAPIFactory.getOrdersService().send(items);
        try {
            retrofit2.Response response = call.execute();
            ResponseBody result = (ResponseBody) response.body();
            if (response.isSuccessful()) {
                JSONObject jObj = new JSONObject(result.string());
                // при сохранении данных на сервере произошли ошибки
                // данный флаг пока не используем
//                boolean success = (boolean) jObj.get("success");
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

    /**
     * Отмечаем успешно отправленные наряды
     *
     * @param idUuid LongSparseArray<String>
     */
    private void setSendOrders(LongSparseArray<String> idUuid, Realm realm) {
        realm.beginTransaction();
        for (int idx = 0; idx < idUuid.size(); idx++) {
            long _id = idUuid.keyAt(idx);
            String uuid = idUuid.valueAt(idx);
            Orders value = realm.where(Orders.class)
                    .equalTo("_id", _id)
                    .equalTo("uuid", uuid)
                    .findFirst();
            if (value != null) {
                value.setSent(true);
            }
        }

        realm.commitTransaction();
    }

    /**
     * Строим список uuid всех операций
     *
     * @param orders List<Orders>
     * @return List<Operation>
     */
    private List<String> getAllOperations(List<Orders> orders) {
        List<String> operationUuids = new ArrayList<>();
        for (Orders order : orders) {
            List<Task> tasks = order.getTasks();
            for (Task task : tasks) {
                List<Stage> stages = task.getStages();
                for (Stage stage : stages) {
                    List<Operation> operations = stage.getOperations();
                    for (Operation operation : operations) {
                        operationUuids.add(operation.getUuid());
                    }
                }
            }
        }

        return operationUuids;
    }
}
