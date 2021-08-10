package ru.toir.mobile.multi.rest;

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
import java.util.UUID;

import io.realm.Realm;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.MainActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.ToirApplication;
import ru.toir.mobile.multi.db.realm.Defect;
import ru.toir.mobile.multi.db.realm.Documentation;
import ru.toir.mobile.multi.db.realm.Equipment;
import ru.toir.mobile.multi.db.realm.EquipmentAttribute;
import ru.toir.mobile.multi.db.realm.EquipmentModel;
import ru.toir.mobile.multi.db.realm.Instruction;
import ru.toir.mobile.multi.db.realm.InstructionStageTemplate;
import ru.toir.mobile.multi.db.realm.Message;
import ru.toir.mobile.multi.db.realm.Objects;
import ru.toir.mobile.multi.db.realm.Operation;
import ru.toir.mobile.multi.db.realm.OperationTemplate;
import ru.toir.mobile.multi.db.realm.OrderRepairPart;
import ru.toir.mobile.multi.db.realm.OrderStatus;
import ru.toir.mobile.multi.db.realm.Orders;
import ru.toir.mobile.multi.db.realm.ReferenceUpdate;
import ru.toir.mobile.multi.db.realm.RepairPart;
import ru.toir.mobile.multi.db.realm.Stage;
import ru.toir.mobile.multi.db.realm.StageTemplate;
import ru.toir.mobile.multi.db.realm.Task;
import ru.toir.mobile.multi.db.realm.TaskTemplate;
import ru.toir.mobile.multi.db.realm.User;
import ru.toir.mobile.multi.fragments.ReferenceFragment;

import static ru.toir.mobile.multi.rest.GetOrderAsyncTask.isNeedDownload;
import static ru.toir.mobile.multi.utils.MainFunctions.addToJournal;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 4/12/18.
 */

public class GetOrdersService extends Service {
    public static final String ACTION = ToirApplication.packageName + ".rest.GET_ORDERS";
    public static final String ORDER_STATUS_UUIDS = "orderStatusUuids";
    private static final String TAG = GetOrdersService.class.getSimpleName();
    private boolean isRuning;
    private List<String> statusUuids;
    private Context context;

    /**
     * Метод для выполнения приёма данных с сервера.
     */
    private Runnable task = new Runnable() {
        @Override
        public void run() {

            Log.d(TAG, "run() started...");
            AuthorizedUser authUser = AuthorizedUser.getInstance();
            boolean isValidUser = authUser.getLogin() != null && authUser.getToken() != null;
            if (!isValidUser) {
                finishService();
                return;
            }

            File extDir = context.getExternalFilesDir("");
            if (extDir == null) {
                finishService();
                return;
            }

            // обновляем справочники
            ReferenceFragment.updateReferencesForOrders(context);

            // список оборудования в полученных нарядах (для постройки списка документации)
            Map<String, Equipment> equipmentList = new HashMap<>();
            ArrayList<String> stageTemplates = new ArrayList<>();
            ArrayList<String> instructions = new ArrayList<>();

            // получаем список последних дефектов
            String changedDate = ReferenceUpdate.lastChangedAsStr(Defect.class.getSimpleName());
            Call<List<Defect>> defectCall = ToirAPIFactory.getDefectService().get(changedDate);
            try {
                retrofit2.Response<List<Defect>> r = defectCall.execute();
                List<Defect> list = r.body();
                if (list != null && list.size() > 0) {
                    for (Defect defect : list) {
                        equipmentList.put(defect.getEquipment().getUuid(), defect.getEquipment());
                        GetOrderAsyncTask.getMediaFile(defect.getUuid());
                        defect.setSent(true);
                    }

                    // сохраняем атрибуты
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                    ReferenceUpdate.saveReferenceData("Defect", new Date());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("action", "defectFragment");
                    intent.putExtra("count", list.size());
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "toir")
                            .setSmallIcon(R.drawable.toir_notify)
                            .setAutoCancel(true)
                            .setTicker("Получены новые неисправности")
                            .setContentText("Получено " + list.size() + " дефектов")
                            .setContentIntent(PendingIntent.getActivity(context,
                                    0, intent, PendingIntent.FLAG_CANCEL_CURRENT))
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("Тоирус")
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
                    if (notificationManager != null)
                        notificationManager.notify(1, nb.build());
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении дефектов оборудования.");
                e.printStackTrace();
            }

            // получаем сообщения
            changedDate = ReferenceUpdate.lastChangedAsStr(Message.class.getSimpleName());
            Call<List<Message>> messageCall = ToirAPIFactory.getMessageService().get(changedDate);
            try {
                retrofit2.Response<List<Message>> r = messageCall.execute();
                List<Message> list = r.body();
                if (list != null && list.size() > 0) {
                    // сохраняем атрибуты
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                    ReferenceUpdate.saveReferenceData("Message", new Date());

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("action", "messageFragment");
                    intent.putExtra("count", list.size());
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "toir")
                            .setSmallIcon(R.drawable.toir_notify)
                            .setAutoCancel(true)
                            .setTicker("Получены новые сообщения")
                            .setContentText("Получено " + list.size() + " сообщений")
                            .setContentIntent(PendingIntent.getActivity(context,
                                    0, intent, PendingIntent.FLAG_CANCEL_CURRENT))
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("Тоирус")
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
                    if (notificationManager != null)
                        notificationManager.notify(1, nb.build());
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении сообщений.");
                e.printStackTrace();
            }

            // Временно!! Загрузка пропущенных инструкций
            String userName = authUser.getLogin();
            // список файлов для загрузки
            List<GetOrderAsyncTask.FilePath> files = new ArrayList<>();
            Realm realm = Realm.getDefaultInstance();

            List<Instruction> all_instructions = realm.where(Instruction.class).findAll();
            all_instructions = realm.copyFromRealm(all_instructions);
            realm.close();

            for (Instruction instruction : all_instructions) {
                String localPath = instruction.getImageFilePath(authUser.getDbName()) + "/";
                if (isNeedDownload(extDir, instruction, localPath)) {
                    String url = instruction.getImageFileUrl(userName) + "/";
                    files.add(new GetOrderAsyncTask.FilePath(instruction.getImageFileName(), url,
                            localPath));
                }
            }

            // оборудование после последнего запроса
            Date lastChangedDate = ReferenceUpdate.lastChanged(Equipment.class.getSimpleName());
            Date oldDate = new Date(0);
            if (!lastChangedDate.after(oldDate)) {
                ReferenceUpdate.saveReferenceData(Equipment.class.getSimpleName(), new Date());
            }
            changedDate = ReferenceUpdate.lastChangedAsStr(Equipment.class.getSimpleName());
            Call<List<Equipment>> equipmentCall;
            equipmentCall = ToirAPIFactory.getEquipmentService().get(changedDate);
            try {
                retrofit2.Response<List<Equipment>> r = equipmentCall.execute();
                List<Equipment> list = r.body();
                if (list != null) {
                    // сохраняем оборудование
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении оборудования.");
                e.printStackTrace();
            }

            // запрашиваем наряды
            Call<List<Orders>> call = ToirAPIFactory.getOrdersService().getByStatus(statusUuids);
            Log.d(TAG, "get order call: "+ call.request().url().toString()+" args: "+statusUuids);
            List<Orders> orders;
            try {
                retrofit2.Response<List<Orders>> response = call.execute();
                if (response.code() != 200) {
                    // сообщаем что произошло
                    //addToJournal("Ошибка получения нарядов! Код ответа сервера:" + response.code());
                    finishService();
                    return;
                }

                orders = response.body();
                Log.d(TAG, "respose order call: "+ response.message()+" headers: "+response.headers().toString()+" body: "+response.body().toString());
                if (orders == null) {
                    //addToJournal("Ошибка получения нарядов! Содержимого ответа нет.");
                    finishService();
                    return;
                } else if (orders.size() == 0) {
                    finishService();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                addToJournal("Exception: ".concat(e.getMessage()));
                finishService();
                return;
            }

            // строим список изображений для загрузки
            for (Orders order : orders) {
                // если это не новый наряд, ставим флаг sent в true
                if (!order.isNew()) {
                    order.setSent(true);
                }

                // путь до файлов локальный
                String basePathLocal;
                boolean isNeedDownload;

                // изображение пользователя создавшего наряд
                User orderAuthor = order.getAuthor();
                basePathLocal = orderAuthor.getImageFilePath(authUser.getDbName()) + "/";
                isNeedDownload = isNeedDownload(extDir, orderAuthor, basePathLocal);
                if (isNeedDownload) {
                    String url = orderAuthor.getImageFileUrl(userName) + "/";
                    files.add(new GetOrderAsyncTask.FilePath(orderAuthor.getImageFileName(), url,
                            basePathLocal));
                }

                List<Task> tasks = order.getTasks();
                for (Task task : tasks) {
                    // урл изображения задачи
                    TaskTemplate taskTemplate = task.getTaskTemplate();
                    basePathLocal = taskTemplate.getImageFilePath(authUser.getDbName()) + "/";
                    isNeedDownload = isNeedDownload(extDir, taskTemplate, basePathLocal);
                    if (isNeedDownload) {
                        String url = taskTemplate.getImageFileUrl(userName) + "/";
                        files.add(new GetOrderAsyncTask.FilePath(taskTemplate.getImageFileName(),
                                url, basePathLocal));
                    }

                    List<Stage> stages = task.getStages();
                    for (Stage stage : stages) {
                        // урл изображения этапа задачи
                        StageTemplate stageTemplate = stage.getStageTemplate();
                        stageTemplates.add(stageTemplate.getUuid());
                        basePathLocal = stageTemplate.getImageFilePath(authUser.getDbName()) + "/";
                        isNeedDownload = isNeedDownload(extDir, stageTemplate, basePathLocal);
                        if (isNeedDownload) {
                            String url = stageTemplate.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(stageTemplate.getImageFileName(),
                                    url, basePathLocal));
                        }

                        String fileName;

                        // урл изображения оборудования
                        Equipment equipment = stage.getEquipment();
                        basePathLocal = equipment.getImageFilePath(authUser.getDbName()) + "/";
                        fileName = equipment.getImageFileName();
                        if (!fileName.equals("")) {
                            isNeedDownload = isNeedDownload(extDir, equipment, basePathLocal);
                            if (isNeedDownload) {
                                String url = equipment.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask.FilePath(equipment.getImageFileName(),
                                        url, basePathLocal));
                            }
                        }

                        // урл изображения модели оборудования
                        EquipmentModel equipmentModel = stage.getEquipment().getEquipmentModel();
                        basePathLocal = equipmentModel.getImageFilePath(authUser.getDbName()) + "/";
                        fileName = equipmentModel.getImageFileName();
                        if (!fileName.equals("")) {
                            isNeedDownload = isNeedDownload(extDir, equipmentModel, basePathLocal);
                            if (isNeedDownload) {
                                String url = equipmentModel.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask
                                        .FilePath(equipmentModel.getImageFileName(), url, basePathLocal));
                            }
                        }

                        // урл изображения объекта где расположено оборудование
                        Objects object = stage.getEquipment().getLocation();
                        if (object != null) {
                            basePathLocal = object.getImageFilePath(authUser.getDbName()) + "/";
                            isNeedDownload = isNeedDownload(extDir, object, basePathLocal);
                            if (isNeedDownload) {
                                String url = object.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask.FilePath(object.getImageFileName(),
                                        url, basePathLocal));
                            }
                        }

                        // добавляем для получения документации в дальнейшем
                        equipmentList.put(stage.getEquipment().getUuid(), stage.getEquipment());

                        List<Operation> operations = stage.getOperations();
                        for (Operation operation : operations) {
                            OperationTemplate operationTemplate = operation.getOperationTemplate();
                            basePathLocal = operationTemplate.getImageFilePath(authUser.getDbName()) + "/";
                            // урл изображения операции
                            isNeedDownload = isNeedDownload(extDir, operationTemplate, basePathLocal);
                            if (isNeedDownload) {
                                String url = operationTemplate.getImageFileUrl(userName) + "/";
                                files.add(new GetOrderAsyncTask
                                        .FilePath(operationTemplate.getImageFileName(), url, basePathLocal));
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

            // получаем список необходимых для загрузки атрибутов для оборудования из наряда
            Call<List<EquipmentAttribute>> eqAttrCall;
            eqAttrCall = ToirAPIFactory.getEquipmentAttributeService()
                    .getByEquipment(needEquipmentUuids.toArray(new String[]{}));
            try {
                retrofit2.Response<List<EquipmentAttribute>> r = eqAttrCall.execute();
                List<EquipmentAttribute> list = r.body();
                if (list != null) {
                    for (EquipmentAttribute attribute : list) {
                        attribute.setSent(true);
                    }

                    // сохраняем атрибуты
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении атрибутов оборудования.");
                e.printStackTrace();
            }

            // получаем список дефектов для оборудования из наряда
            changedDate = ReferenceUpdate.lastChangedAsStr(Defect.class.getSimpleName());
            defectCall = ToirAPIFactory.getDefectService()
                    .getByEquipment(needEquipmentUuids.toArray(new String[]{}), changedDate);
            try {
                retrofit2.Response<List<Defect>> r = defectCall.execute();
                List<Defect> list = r.body();
                if (list != null) {
                    for (Defect defect : list) {
                        defect.setSent(true);
                    }

                    // сохраняем атрибуты
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении дефектов оборудования.");
                e.printStackTrace();
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
                        String localPath = doc.getImageFilePath(authUser.getDbName()) + "/";
                        if (isNeedDownload(extDir, doc, localPath) && doc.isRequired()) {
                            String url = doc.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(doc.getImageFileName(), url, localPath));
                        }
                    }

                    // сохраняем информацию о доступной документации для оборудования
                    realm = Realm.getDefaultInstance();
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
                        String localPath = doc.getImageFilePath(authUser.getDbName()) + "/";
                        if (isNeedDownload(extDir, doc, localPath) && doc.isRequired()) {
                            String url = doc.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(doc.getImageFileName(), url, localPath));
                        }
                    }

                    // сохраняем информацию о доступной документации для оборудования
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении документации.");
                e.printStackTrace();
            }

            int count = orders.size();
            final List<String> uuids = new ArrayList<>();
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            // проставляем дату получения нарядов
            for (Orders order : orders) {
                order.setReceivDate(new Date());

                if (order.isNew()) {
                    uuids.add(order.getUuid());
                    // устанавливаем статус "В работе"
                    order.setOrderStatus(OrderStatus.getObjectInWork(realm));
                }
            }

            realm.copyToRealmOrUpdate(orders);
            realm.commitTransaction();
            realm.close();
            addToJournal("Клиент успешно получил " + count + " нарядов");

            for (Orders order : orders) {
                getRepairParts(order.getUuid());
            }

            // запрашиваем связки
            Call<List<InstructionStageTemplate>> callIST = ToirAPIFactory.getInstructionStageTemplate()
                    .getByUuid(stageTemplates.toArray(new String[0]));
            try {
                retrofit2.Response<List<InstructionStageTemplate>> response = callIST.execute();
                // добавляем файлы необходимые для загрузки в список
                List<InstructionStageTemplate> list = response.body();
                if (list != null) {
                    for (InstructionStageTemplate ist : list) {
                        instructions.add(ist.getInstruction().getUuid());
                    }
                    // сохраняем информацию
                    realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(list);
                    realm.commitTransaction();
                    realm.close();
                }
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при получении");
                e.printStackTrace();
            }

            // получаем список документации для моделей оборудования в наряде
            Call<List<Instruction>> instructionCall = ToirAPIFactory.getInstructionService().getByUuid(
                    instructions.toArray(new String[]{}));
            try {
                retrofit2.Response<List<Instruction>> r = instructionCall.execute();
                // добавляем файлы необходимые для загрузки в список
                List<Instruction> list = r.body();
                if (list != null) {
                    for (Instruction instruction : list) {
                        String localPath = instruction.getImageFilePath(authUser.getDbName()) + "/";
                        if (isNeedDownload(extDir, instruction, localPath)) {
                            String url = instruction.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(instruction.getImageFileName(),
                                    url, localPath));
                        }
                    }
                    // сохраняем информацию о доступной документации для оборудования
                    realm = Realm.getDefaultInstance();
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
                Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().get(ToirApplication.serverUrl + key);
                try {
                    retrofit2.Response<ResponseBody> r = call1.execute();
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

            // тестовая реализация штатного уведомления
            // собщаем количество полученных нарядов
            NotificationManager notificationManager = null;
            if (context != null) {
                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            }

            if (notificationManager != null) {
                PackageManager pm = getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(ToirApplication.packageName);
                if (intent != null) {
                    intent.putExtra("action", "orderFragment");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("count", count);

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

            Log.d(TAG, "run() ended...");
            finishService();
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
            Log.d(TAG, "Запускаем поток получения нарядов с сервера...");
            statusUuids = intent.getStringArrayListExtra(ORDER_STATUS_UUIDS);
            isRuning = true;
            new Thread(task).start();
        }

        return START_STICKY;
    }

    private void getRepairParts(String orderUuid) {
        Call<List<RepairPart>> partCall = ToirAPIFactory.getRepairPartService().getByOrderUuid(orderUuid);
        try {
            Realm realm = Realm.getDefaultInstance();
            Orders order = realm.where(Orders.class).equalTo("uuid", orderUuid).findFirst();
            long orderRepairPartCount = realm.where(OrderRepairPart.class).equalTo("order.uuid", orderUuid).count();
            retrofit2.Response<List<RepairPart>> r = partCall.execute();
            List<RepairPart> list = r.body();
            if (list != null && list.size() > 0 && order != null) {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(list);
                realm.commitTransaction();
                if (orderRepairPartCount == 0) {
                    for (RepairPart repairPart : list) {
                        long nextId = OrderRepairPart.getLastId() + 1;
                        OrderRepairPart orderRepairPart = new OrderRepairPart();
                        UUID uuid = UUID.randomUUID();
                        orderRepairPart.set_id(nextId);
                        orderRepairPart.setUuid(uuid.toString().toUpperCase());
                        orderRepairPart.setOrder(order);
                        orderRepairPart.setRepairPart(repairPart);
                        // TODO решить как получать количество
                        orderRepairPart.setQuantity(1);
                        orderRepairPart.setCreatedAt(new Date());
                        orderRepairPart.setChangedAt(new Date());

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(orderRepairPart);
                        realm.commitTransaction();
                    }
                }
            }
            realm.close();
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении запчастей наряда");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        isRuning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void finishService() {
        Log.d(TAG, "finishService()");
        stopSelf();
    }
}
