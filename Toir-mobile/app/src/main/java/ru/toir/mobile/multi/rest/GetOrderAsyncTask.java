package ru.toir.mobile.multi.rest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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
import io.realm.RealmObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.MainActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.ToirApplication;
import ru.toir.mobile.multi.db.realm.Contragent;
import ru.toir.mobile.multi.db.realm.Defect;
import ru.toir.mobile.multi.db.realm.Documentation;
import ru.toir.mobile.multi.db.realm.Equipment;
import ru.toir.mobile.multi.db.realm.EquipmentAttribute;
import ru.toir.mobile.multi.db.realm.EquipmentModel;
import ru.toir.mobile.multi.db.realm.IToirDbObject;
import ru.toir.mobile.multi.db.realm.Instruction;
import ru.toir.mobile.multi.db.realm.InstructionStageTemplate;
import ru.toir.mobile.multi.db.realm.MediaFile;
import ru.toir.mobile.multi.db.realm.Objects;
import ru.toir.mobile.multi.db.realm.Operation;
import ru.toir.mobile.multi.db.realm.OperationTemplate;
import ru.toir.mobile.multi.db.realm.OrderStatus;
import ru.toir.mobile.multi.db.realm.Orders;
import ru.toir.mobile.multi.db.realm.ReferenceUpdate;
import ru.toir.mobile.multi.db.realm.Stage;
import ru.toir.mobile.multi.db.realm.StageTemplate;
import ru.toir.mobile.multi.db.realm.Task;
import ru.toir.mobile.multi.db.realm.TaskTemplate;
import ru.toir.mobile.multi.fragments.ReferenceFragment;

import static ru.toir.mobile.multi.utils.MainFunctions.addToJournal;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/21/18.
 */

public class GetOrderAsyncTask extends AsyncTask<String[], Integer, List<Orders>> {

    private static final String TAG = GetOrderAsyncTask.class.getSimpleName();
    private ProgressDialog dialog;
    private File extDir;
    private String message;

    public GetOrderAsyncTask(ProgressDialog d, File e) {
        dialog = d;
        extDir = e;
    }

    /**
     * Проверка на необходимость загрузки файла с сервера.
     *
     * @param obj       {@link RealmObject} Объект. Должен реализовывать {@link IToirDbObject}
     * @param localPath {@link String} Локальный путь к файлу. Относительно папки /files
     * @return boolean
     */
    static boolean isNeedDownload(File extDir, RealmObject obj, String localPath) {
        Realm realm = Realm.getDefaultInstance();
        String uuid = ((IToirDbObject) obj).getUuid();
        RealmObject dbObj = realm.where(obj.getClass()).equalTo("uuid", uuid).findFirst();
        long localChangedAt;

        // есть ли локальная запись
        try {
            localChangedAt = ((IToirDbObject) dbObj).getChangedAt().getTime();
        } catch (Exception e) {
            return true;
        } finally {
            realm.close();
        }

        // есть ли локально файл
        String fileName = ((IToirDbObject) obj).getImageFile();
        if (fileName != null) {
            File file = new File(extDir.getAbsolutePath() + '/' + localPath, fileName);
            if (!file.exists()) {
                return true;
            }
        } else {
            return false;
        }

        // есть ли изменения на сервере
        return localChangedAt < ((IToirDbObject) obj).getChangedAt().getTime();
    }

    public static void getMediaFile(String entityUuid) {
        Call<List<MediaFile>> mediaCall;
        mediaCall = ToirAPIFactory.getMediaFileService().get(entityUuid);
        try {
            retrofit2.Response<List<MediaFile>> r = mediaCall.execute();
            List<MediaFile> list = r.body();
            if (list != null && list.size() > 0) {
                for (MediaFile mediaFile : list) {
                    Call<String> mediaFileCall;
                    mediaFileCall = ToirAPIFactory.getMediaFileService().getUrl(mediaFile.getUuid(), "url");
                    try {
                        retrofit2.Response<String> r1 = mediaFileCall.execute();
                        String url = r1.body();
                        //if (mediaFile.getPath()==null)
                        mediaFile.setPath(url);
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка при получении медиа url");
                        e.printStackTrace();
                    }
                    mediaFile.setSent(true);
                }
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(list);
                realm.commitTransaction();
                realm.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении медиа файла");
            e.printStackTrace();
        }
    }

    @Override
    protected List<Orders> doInBackground(String[]... params) {
        // обновляем справочники
        ReferenceFragment.updateReferencesForOrders(dialog.getContext());

        List<String> args = java.util.Arrays.asList(params[0]);

        // список оборудования в полученных нарядах (для постройки списка документации)
        Map<String, Equipment> equipmentList = new HashMap<>();
        ArrayList<String> stageTemplates = new ArrayList<>();
        ArrayList<String> instructions = new ArrayList<>();

        // получаем список последних дефектов
        Call<List<Defect>> defectCall;
        String changedDate = ReferenceUpdate.lastChangedAsStr(Defect.class.getSimpleName());
        defectCall = ToirAPIFactory.getDefectService().get(changedDate);
        try {
            retrofit2.Response<List<Defect>> r = defectCall.execute();
            List<Defect> list = r.body();
            if (list != null && list.size() > 0) {
                for (Defect defect : list) {
                    equipmentList.put(defect.getEquipment().getUuid(), defect.getEquipment());
                    getMediaFile(defect.getUuid());
                    defect.setSent(true);
                }
                // сохраняем атрибуты
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(list);
                realm.commitTransaction();
                realm.close();

                ReferenceUpdate.saveReferenceData("Defect", new Date());

                Intent intent = new Intent(dialog.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("action", "defectFragment");
                intent.putExtra("count", list.size());
                NotificationManager notificationManager = (NotificationManager) dialog.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder nb = new NotificationCompat.Builder(dialog.getContext(), "toir")
                        .setSmallIcon(R.drawable.toir_notify)
                        .setAutoCancel(true)
                        .setTicker("Получены новые неисправности")
                        .setContentText("Получено " + list.size() + " дефектов")
                        .setContentIntent(PendingIntent.getActivity(dialog.getContext(),
                                0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
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

        changedDate = ReferenceUpdate.lastChangedAsStr(Contragent.class.getSimpleName());
        // запрашиваем контрагентов
        Call<List<Contragent>> callContragent = ToirAPIFactory.getContragentService().get(changedDate);
        List<Contragent> contragent;
        try {
            retrofit2.Response<List<Contragent>> response = callContragent.execute();
            if (response.code() != 200) {
                // сообщаем что произошло
                message = "Ошибка получения контрагентов! Код ответа сервера:" + response.code();
                return null;
            }

            contragent = response.body();
            if (contragent == null) {
                message = "Ошибка получения! Содержимого ответа нет.";
            }
            // сохраняем
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(contragent);
            realm.commitTransaction();
            realm.close();
            ReferenceUpdate.saveReferenceData("Contragent", new Date());

        } catch (Exception e) {
            e.printStackTrace();
            message = "Exception";
            return null;
        }

        // запрашиваем наряды
        Call<List<Orders>> call = ToirAPIFactory.getOrdersService().getByStatus(args);
        List<Orders> result;
        try {
            retrofit2.Response<List<Orders>> response = call.execute();
            if (response.code() != 200) {
                // сообщаем что произошло
                message = "Ошибка получения нарядов! Код ответа сервера:" + response.code();
                return null;
            }

            result = response.body();
            if (result == null) {
                message = "Ошибка получения нарядов! Содержимого ответа нет.";
                return null;
            } else if (result.size() == 0) {
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "Exception";
            return null;
        }

        String userName = AuthorizedUser.getInstance().getLogin();
        // список файлов для загрузки
        List<FilePath> files = new ArrayList<>();
        // строим список изображений для загрузки
        for (Orders order : result) {
            // если это не новый наряд, ставим флаг sent в true
            String orderStatusUuid = order.getOrderStatus().getUuid();
            if (!orderStatusUuid.equals(OrderStatus.Status.NEW)) {
                order.setSent(true);
            }

            // путь до файлов локальный
            String basePathLocal;

            List<Task> tasks = order.getTasks();
            for (Task task : tasks) {
                boolean isNeedDownload;

                // урл изображения задачи
                TaskTemplate taskTemplate = task.getTaskTemplate();
                basePathLocal = taskTemplate.getImageFilePath() + "/";
                isNeedDownload = isNeedDownload(extDir, taskTemplate, basePathLocal);
                if (isNeedDownload) {
                    String url = taskTemplate.getImageFileUrl(userName) + "/";
                    files.add(new FilePath(taskTemplate.getImage(), url, basePathLocal));
                }

                List<Stage> stages = task.getStages();
                for (Stage stage : stages) {
                    // урл изображения этапа задачи
                    StageTemplate stageTemplate = stage.getStageTemplate();
                    stageTemplates.add(stageTemplate.getUuid());
                    basePathLocal = stageTemplate.getImageFilePath() + "/";
                    isNeedDownload = isNeedDownload(extDir, stageTemplate, basePathLocal);
                    if (isNeedDownload) {
                        String url = stageTemplate.getImageFileUrl(userName) + "/";
                        files.add(new FilePath(stageTemplate.getImage(), url, basePathLocal));
                    }

                    // урл изображения оборудования
                    Equipment equipment = stage.getEquipment();
                    basePathLocal = equipment.getImageFilePath() + "/";
                    if (!equipment.getImage().equals("")) {
                        isNeedDownload = isNeedDownload(extDir, equipment, basePathLocal);
                        if (isNeedDownload) {
                            String url = equipment.getImageFileUrl(userName) + "/";
                            files.add(new FilePath(equipment.getImage(), url, basePathLocal));
                        }
                    }

                    // урл изображения модели оборудования
                    EquipmentModel equipmentModel = stage.getEquipment().getEquipmentModel();
                    basePathLocal = equipmentModel.getImageFilePath() + "/";
                    if (!equipmentModel.getImage().equals("")) {
                        isNeedDownload = isNeedDownload(extDir, equipmentModel, basePathLocal);
                        if (isNeedDownload) {
                            String url = equipmentModel.getImageFileUrl(userName) + "/";
                            files.add(new FilePath(equipmentModel.getImage(), url, basePathLocal));
                        }
                    }

                    // урл изображения объекта где расположено оборудование
                    Objects object = stage.getEquipment().getLocation();
                    if (object != null) {
                        basePathLocal = object.getImageFilePath() + "/";
                        isNeedDownload = isNeedDownload(extDir, object, basePathLocal);
                        if (isNeedDownload) {
                            String url = object.getImageFileUrl(userName) + "/";
                            files.add(new FilePath(object.getImage(), url, basePathLocal));
                        }
                    }

                    // добавляем для получения документации в дальнейшем
                    equipmentList.put(stage.getEquipment().getUuid(), stage.getEquipment());

                    List<Operation> operations = stage.getOperations();
                    for (Operation operation : operations) {
                        OperationTemplate operationTemplate = operation.getOperationTemplate();
                        basePathLocal = operationTemplate.getImageFilePath() + "/";
                        // урл изображения операции
                        isNeedDownload = isNeedDownload(extDir, operationTemplate, basePathLocal);
                        if (isNeedDownload) {
                            String url = operationTemplate.getImageFileUrl(userName) + "/";
                            files.add(new FilePath(operationTemplate.getImage(), url, basePathLocal));
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
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(list);
                realm.commitTransaction();
                realm.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении атрибутов оборудования.");
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
                    String localPath = doc.getImageFilePath() + "/";
                    if (isNeedDownload(extDir, doc, localPath) && doc.isRequired()) {
                        String url = doc.getImageFileUrl(userName) + "/";
                        files.add(new FilePath(doc.getPath(), url, localPath));
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
                    if (isNeedDownload(extDir, doc, localPath) && doc.isRequired()) {
                        String url = doc.getImageFileUrl(userName) + "/";
                        files.add(new FilePath(doc.getPath(), url, localPath));
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
                Realm realm = Realm.getDefaultInstance();
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
                    String localPath = instruction.getImageFilePath() + "/";
                    if (isNeedDownload(extDir, instruction, localPath)) {
                        String url = instruction.getImageFileUrl(userName) + "/";
                        files.add(new FilePath(instruction.getPath(), url, localPath));
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
        for (FilePath item : files) {
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
        int filesCount = 0;
        for (String key : requestList.keySet()) {
            Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().get(ToirApplication.serverUrl + key);
            try {
                retrofit2.Response<ResponseBody> r = call1.execute();
                ResponseBody trueImgBody = r.body();
                if (trueImgBody == null) {
                    continue;
                }

                for (String localPath : requestList.get(key)) {
                    filesCount++;
                    publishProgress(filesCount);
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

        // список оборудования в полученных нарядах (для постройки списка документации)
        // Map<String, Equipment> equipmentList = new HashMap<>();
        // получаем дефекты
        // путь до файлов локальный
/*
        String basePathLocal;
        boolean isNeedDownload;

        Call<List<Defect>> defectCall;
        defectCall = ToirAPIFactory.getDefectService().get();
        try {
            retrofit2.Response<List<Defect>> r = defectCall.execute();
            List<Defect> list = r.body();
            if (list != null) {
                for (Defect defect : list) {
                    // урл изображения оборудования
                    Equipment equipment = defect.getEquipment();
                    basePathLocal = equipment.getImageFilePath() + "/";
                    if (!equipment.getImage().equals("")) {
                        isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, equipment, basePathLocal);
                        if (isNeedDownload) {
                            String url = equipment.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(equipment.getImage(), url, basePathLocal));
                        }
                    }

                    // урл изображения модели оборудования
                    EquipmentModel equipmentModel = defect.getEquipment().getEquipmentModel();
                    basePathLocal = equipmentModel.getImageFilePath() + "/";
                    if (!equipmentModel.getImage().equals("")) {
                        isNeedDownload = GetOrderAsyncTask.isNeedDownload(extDir, equipmentModel, basePathLocal);
                        if (isNeedDownload) {
                            String url = equipmentModel.getImageFileUrl(userName) + "/";
                            files.add(new GetOrderAsyncTask.FilePath(equipmentModel.getImage(), url, basePathLocal));
                        }
                    }
                    // строим список оборудования
                    equipmentList.put(defect.getEquipment().getUuid(), defect.getEquipment());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении дефектов");
            e.printStackTrace();
        }
*/
        return result;
    }

    @Override
    protected void onPostExecute(List<Orders> orders) {
        super.onPostExecute(orders);

        Context context = null;
        if (dialog != null) {
            context = dialog.getContext();
        }

        if (orders == null) {
            // сообщаем описание неудачи
            if (context != null && message != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        } else {
            int count = orders.size();
            // собщаем количество полученных нарядов
            if (count > 0) {
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
                if (context != null) {
                    Toast.makeText(context, "Количество нарядов " + count, Toast.LENGTH_SHORT).show();
                }

                // тестовая реализация штатного уведомления
                NotificationManager notificationManager = null;
                if (context != null) {
                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                }

                if (notificationManager != null) {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("action", "orderFragment");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("count", count);

                    NotificationCompat.Builder nb = new NotificationCompat.Builder(context, "toir")
                            .setSmallIcon(R.drawable.toir_notify)
                            .setAutoCancel(true)
                            .setTicker("Получены новые наряды")
                            .setContentText("Получено " + count + " нарядов.")
                            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT))
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("Тоирус")
                            .setDefaults(NotificationCompat.DEFAULT_ALL);
                    notificationManager.notify(1, nb.build());
                }

                // если есть новые наряды, отправляем подтверждение о получении
                if (!uuids.isEmpty()) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            // отправляем запрос на установку статуса IN_WORK на сервере
                            // в случае не успеха, ни каких действий для повторной отправки
                            // не предпринимается (т.к. нет ни каких средств для фиксации этого события)
                            Call<ResponseBody> call = ToirAPIFactory.getOrdersService().setInWork(uuids);
                            try {
                                retrofit2.Response response = call.execute();
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
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            } else {
                if (dialog != null) {
                    Toast.makeText(dialog.getContext(), "Нарядов нет.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // полагаю что здесь нужно отправить уведомление вместо прямого гашения диалога
        // TODO: уведомление !!!
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (dialog != null) {
            dialog.setProgress(values[0]);
        }
    }

    static class FilePath {
        String fileName;
        String urlPath;
        String localPath;

        FilePath(String name, String url, String local) {
            fileName = name;
            urlPath = url;
            localPath = local;
        }
    }

}
