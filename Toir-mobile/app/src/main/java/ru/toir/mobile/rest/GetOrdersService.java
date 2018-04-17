package ru.toir.mobile.rest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.Objects;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationTemplate;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.StageTemplate;
import ru.toir.mobile.db.realm.Task;
import ru.toir.mobile.db.realm.TaskTemplate;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.fragments.ReferenceFragment;

import static ru.toir.mobile.utils.MainFunctions.addToJournal;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 4/12/18.
 */

public class GetOrdersService extends Service {
    public static final String ACTION = "ru.toir.mobile.rest.GET_ORDERS";
    public static final String ORDER_STATUS_UUIDS = "orderStatusUuids";
    private static final String TAG = GetOrdersService.class.getSimpleName();
    private boolean isRuning;
    private Thread thread;
    private List<String> statusUuids;
    private Context context;

    /**
     * Метод для выполнения приёма данных с сервера.
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

            File extDir = context.getExternalFilesDir("");
            if (extDir == null) {
                stopSelf();
                return;
            }

            // обновляем справочники
            ReferenceFragment.updateReferencesForOrders();

            // запрашиваем наряды
            Call<List<Orders>> call = ToirAPIFactory.getOrdersService().getByStatus(statusUuids);
            List<Orders> orders;
            try {
                retrofit2.Response<List<Orders>> response = call.execute();
                if (response.code() != 200) {
                    // сообщаем что произошло
                    addToJournal("Ошибка получения нарядов! Код ответа сервера:" + response.code());
                    stopSelf();
                    return;
                }

                orders = response.body();
                if (orders == null) {
                    addToJournal("Ошибка получения нарядов! Содержимого ответа нет.");
                    stopSelf();
                    return;
                } else if (orders.size() == 0) {
                    stopSelf();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                addToJournal("Exception");
                stopSelf();
                return;
            }

            String userName = user.getLogin();
            // список оборудования в полученных нарядах (для постройки списка документации)
            Map<String, Equipment> equipmentList = new HashMap<>();
            // список файлов для загрузки
            List<GetOrderAsyncTask.FilePath> files = new ArrayList<>();
            // строим список изображений для загрузки
            for (Orders order : orders) {
                // если это не новый наряд, ставим флаг sent в true
                String orderStatusUuid = order.getOrderStatus().getUuid();
                if (!orderStatusUuid.equals(OrderStatus.Status.NEW)) {
                    order.setSent(true);
                }

                // путь до файлов локальный
                String basePathLocal;
                boolean isNeedDownload;

                // изображение пользователя создавшего наряд
                User orderAuthor = order.getAuthor();
                basePathLocal = orderAuthor.getImageFilePath() + "/";
                isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, orderAuthor, basePathLocal);
                if (isNeedDownload) {
                    String url = orderAuthor.getImageFileUrl(userName) + "/";
                    files.add(new GetOrderAsyncTask.FilePath(orderAuthor.getImage(), url, basePathLocal));
                }

                List<Task> tasks = order.getTasks();
                for (Task task : tasks) {
                    // урл изображения задачи
                    TaskTemplate taskTemplate = task.getTaskTemplate();
                    basePathLocal = taskTemplate.getImageFilePath() + "/";
                    isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, taskTemplate, basePathLocal);
                    if (isNeedDownload) {
                        String url = taskTemplate.getImageFileUrl(userName) + "/";
                        files.add(new GetOrderAsyncTask.FilePath(taskTemplate.getImage(), url, basePathLocal));
                    }

                    List<Stage> stages = task.getStages();
                    for (Stage stage : stages) {
                        // урл изображения этапа задачи
                        StageTemplate stageTemplate = stage.getStageTemplate();
                        basePathLocal = stageTemplate.getImageFilePath() + "/";
                        isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, stageTemplate, basePathLocal);
                        if (isNeedDownload) {
                            String url = stageTemplate.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(stageTemplate.getImage(), url, basePathLocal));
                        }

                        // урл изображения оборудования
                        Equipment equipment = stage.getEquipment();
                        basePathLocal = equipment.getImageFilePath() + "/";
                        if (!equipment.getImage().equals("")) {
                            isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, equipment, basePathLocal);
                            if (isNeedDownload) {
                                String url = equipment.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask.FilePath(equipment.getImage(), url, basePathLocal));
                            }
                        }

                        // урл изображения модели оборудования
                        EquipmentModel equipmentModel = stage.getEquipment().getEquipmentModel();
                        basePathLocal = equipmentModel.getImageFilePath() + "/";
                        if (!equipmentModel.getImage().equals("")) {
                            isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, equipmentModel, basePathLocal);
                            if (isNeedDownload) {
                                String url = equipmentModel.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask.FilePath(equipmentModel.getImage(), url, basePathLocal));
                            }
                        }

                        // урл изображения объекта где расположено оборудование
                        Objects object = stage.getEquipment().getLocation();
                        if (object != null) {
                            basePathLocal = object.getImageFilePath() + "/";
                            isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, object, basePathLocal);
                            if (isNeedDownload) {
                                String url = object.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask.FilePath(object.getImage(), url, basePathLocal));
                            }
                        }

                        // добавляем для получения документации в дальнейшем
                        equipmentList.put(stage.getEquipment().getUuid(), stage.getEquipment());

                        List<Operation> operations = stage.getOperations();
                        for (Operation operation : operations) {
                            OperationTemplate operationTemplate = operation.getOperationTemplate();
                            basePathLocal = operationTemplate.getImageFilePath() + "/";
                            // урл изображения операции
                            isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, operationTemplate, basePathLocal);
                            if (isNeedDownload) {
                                String url = operationTemplate.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask.FilePath(operationTemplate.getImage(), url, basePathLocal));
                            }
                        }
                    }
                }
            }

            Set<String> needEquipmentUuids = new HashSet<>();
            Set<String> needEquipmentModelUuids = new HashSet<>();
            for (Map.Entry<String, Equipment> entry : equipmentList.entrySet()) {
                needEquipmentUuids.add(entry.getValue().getUuid());
                needEquipmentModelUuids.add(entry.getValue().getEquipmentModel().getUuid());
            }

            // получаем список документации для оборудования в наряде
            Call<List<Documentation>> docCall;
            docCall = ToirAPIFactory.getDocumentationService().getByEquipment(
                    needEquipmentUuids.toArray(new String[]{}));
            try {
                retrofit2.Response<List<Documentation>> r = docCall.execute();
                // добавляем файлы необходимые для загрузки в список
                List<Documentation> list = r.body();
                if (list != null) {
                    for (Documentation doc : list) {
                        String localPath = doc.getImageFilePath() + "/";
                        if (GetOrderAsyncTask.isNeedDownload(extDir, doc, localPath) && doc.isRequired()) {
                            String url = doc.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(doc.getPath(), url, localPath));
                        }
                    }

                    // сохраняем информацию о доступной документации для оборудования
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении документации.");
                e.printStackTrace();
            }

            // получаем список документации для моделей оборудования в наряде
            docCall = ToirAPIFactory.getDocumentationService().getByEquipmentModel(
                    needEquipmentModelUuids.toArray(new String[]{}));
            try {
                retrofit2.Response<List<Documentation>> r = docCall.execute();
                // добавляем файлы необходимые для загрузки в список
                List<Documentation> list = r.body();
                if (list != null) {
                    for (Documentation doc : list) {
                        String localPath = doc.getImageFilePath() + "/";
                        if (GetOrderAsyncTask.isNeedDownload(extDir, doc, localPath) && doc.isRequired()) {
                            String url = doc.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(doc.getPath(), url, localPath));
                        }
                    }

                    // сохраняем информацию о доступной документации для оборудования
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении документации.");
                e.printStackTrace();
            }

            Map<String, Set<String>> requestList = new HashMap<>();
            // тестовый вывод для принятия решения о группировке файлов для минимизации количества загружаемых данных
            for (GetOrderAsyncTask.FilePath item : files) {
                String key = item.urlPath + item.fileName;
                if (!requestList.containsKey(key)) {
                    Set<String> list = new HashSet<>();
                    list.add(item.localPath);
                    requestList.put(key, list);
                } else {
                    requestList.get(key).add(item.localPath);
                }
            }

            // загружаем файлы
            for (String key : requestList.keySet()) {
                Call<ResponseBody> callFile = ToirAPIFactory.getFileDownload().get(ToirApplication.serverUrl + key);
                try {
                    retrofit2.Response<ResponseBody> r = callFile.execute();
                    ResponseBody trueImgBody = r.body();
                    if (trueImgBody == null) {
                        continue;
                    }

                    for (String localPath : requestList.get(key)) {
                        String fileName = key.substring(key.lastIndexOf("/") + 1);
                        File file = new File(extDir.getAbsolutePath() + '/' + localPath, fileName);
                        if (!file.getParentFile().exists()) {
                            if (!file.getParentFile().mkdirs()) {
                                Log.e(TAG, "Не удалось создать папку " +
                                        file.getParentFile().toString() +
                                        " для сохранения файла изображения!");
                                continue;
                            }
                        }

                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(trueImgBody.bytes());
                        fos.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int count = orders.size();
            final List<String> uuids = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            // проставляем дату получения нарядов
            for (Orders order : orders) {
                order.setReceivDate(new Date());

                if (order.getOrderStatus().getUuid().equals(OrderStatus.Status.NEW)) {
                    uuids.add(order.getUuid());
                    // устанавливаем статус "В работе"
                    OrderStatus inWorkStatus = realm.where(OrderStatus.class)
                            .equalTo("uuid", OrderStatus.Status.IN_WORK).findFirst();
                    order.setOrderStatus(inWorkStatus);
                }
            }

            realm.copyToRealmOrUpdate(orders);
            realm.commitTransaction();
            realm.close();
            addToJournal("Клиент успешно получил " + count + " нарядов");

            // тестовая реализация штатного уведомления
            // собщаем количество полученных нарядов
            NotificationManager notificationManager = null;
            if (context != null) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (notificationManager != null) {
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("ru.toir.mobile");
                if (intent != null) {
                    intent.putExtra("action", "orderFragment");

                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "toir")
                            .setSmallIcon(R.drawable.toir_notify)
                            .setAutoCancel(true)
                            .setTicker("Получены новые наряды")
                            .setContentText("Полученно " + count + " нарядов.")
                            .setContentIntent(contentIntent)
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("Тоирус")
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
                    notificationManager.notify(1, nb.build());
                }
            }

            // если есть новые наряды, отправляем подтверждение о получении
            if (!uuids.isEmpty()) {
                // отправляем запрос на установку статуса IN_WORK на сервере
                // в случае не успеха, ни каких действий для повторной отправки
                // не предпринимается (т.к. нет ни каких средств для фиксации этого события)
                Call<ResponseBody> callInWork = ToirAPIFactory.getOrdersService().setInWork(uuids);
                try {
                    retrofit2.Response response = callInWork.execute();
                    if (response.code() != 200) {
                        // TODO: нужно реализовать механизм повторной попытки установки статуса
                        addToJournal("Не удалось отправить запрос на установку статуса нарядов IN_WORK");
                    } else {
                        addToJournal("Успешно отправили статус для полученных нарядов IN_WORK");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    addToJournal("Исключение при запросе на установку статуса нарядов IN_WORK");
                }
            }
        }
    };

    @Override
    public void onCreate() {
        isRuning = false;
        context = getApplicationContext();
        thread = new Thread(task);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        if (!isRuning) {
            Log.d(TAG, "Запускаем поток получения нарядов с сервера...");
            statusUuids = intent.getStringArrayListExtra(ORDER_STATUS_UUIDS);
            isRuning = true;
            thread.start();
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
}
