package ru.toir.mobile.rest;

import android.app.ProgressDialog;
import android.os.AsyncTask;
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
import retrofit2.*;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.IToirDbObject;
import ru.toir.mobile.db.realm.Objects;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationTemplate;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.StageTemplate;
import ru.toir.mobile.db.realm.Task;
import ru.toir.mobile.db.realm.TaskTemplate;
import ru.toir.mobile.fragments.ReferenceFragment;

import static ru.toir.mobile.utils.MainFunctions.addToJournal;

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

    @Override
    protected List<Orders> doInBackground(String[]... params) {
        // обновляем справочники
        ReferenceFragment.updateReferencesForOrders();

        List<String> args = java.util.Arrays.asList(params[0]);

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
        // список оборудования в полученных нарядах (для постройки списка документации)
        Map<String, Equipment> equipmentList = new HashMap<>();
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
                isNeedDownload = isNeedDownload(taskTemplate, basePathLocal);
                if (isNeedDownload) {
                    String url = taskTemplate.getImageFileUrl(userName) + "/";
                    files.add(new FilePath(taskTemplate.getImage(), url, basePathLocal));
                }

                List<Stage> stages = task.getStages();
                for (Stage stage : stages) {
                    // урл изображения этапа задачи
                    StageTemplate stageTemplate = stage.getStageTemplate();
                    basePathLocal = stageTemplate.getImageFilePath() + "/";
                    isNeedDownload = isNeedDownload(stageTemplate, basePathLocal);
                    if (isNeedDownload) {
                        String url = stageTemplate.getImageFileUrl(userName) + "/";
                        files.add(new FilePath(stageTemplate.getImage(), url, basePathLocal));
                    }

                    // урл изображения оборудования
                    Equipment equipment = stage.getEquipment();
                    basePathLocal = equipment.getImageFilePath() + "/";
                    if (!equipment.getImage().equals("")) {
                        isNeedDownload = isNeedDownload(equipment, basePathLocal);
                        if (isNeedDownload) {
                            String url = equipment.getImageFileUrl(userName) + "/";
                            files.add(new FilePath(equipment.getImage(), url, basePathLocal));
                        }
                    }

                    // урл изображения модели оборудования
                    EquipmentModel equipmentModel = stage.getEquipment().getEquipmentModel();
                    basePathLocal = equipmentModel.getImageFilePath() + "/";
                    if (!equipmentModel.getImage().equals("")) {
                        isNeedDownload = isNeedDownload(equipmentModel, basePathLocal);
                        if (isNeedDownload) {
                            String url = equipmentModel.getImageFileUrl(userName) + "/";
                            files.add(new FilePath(equipmentModel.getImage(), url, basePathLocal));
                        }
                    }

                    // урл изображения объекта где расположено оборудование
                    Objects object = stage.getEquipment().getLocation();
                    if (object != null) {
                        basePathLocal = object.getImageFilePath() + "/";
                        isNeedDownload = isNeedDownload(object, basePathLocal);
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
                        isNeedDownload = isNeedDownload(operationTemplate, basePathLocal);
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
                    if (isNeedDownload(doc, localPath) && doc.isRequired()) {
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
                    if (isNeedDownload(doc, localPath) && doc.isRequired()) {
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

        return result;
    }

    @Override
    protected void onPostExecute(List<Orders> orders) {
        super.onPostExecute(orders);
        if (orders == null) {
            // сообщаем описание неудачи
            if (dialog != null && message != null) {
                Toast.makeText(dialog.getContext(), message, Toast.LENGTH_LONG).show();
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
                    }
                }

                realm.copyToRealmOrUpdate(orders);
                realm.commitTransaction();
                realm.close();
                addToJournal("Клиент успешно получил " + count + " нарядов");
                if (dialog != null) {
                    Toast.makeText(dialog.getContext(), "Количество нарядов " + count, Toast.LENGTH_SHORT).show();
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

    /**
     * Проверка на необходимость загрузки файла с сервера.
     *
     * @param obj       {@link RealmObject} Объект. Должен реализовывать {@link IToirDbObject}
     * @param localPath {@link String} Локальный путь к файлу. Относительно папки /files
     * @return boolean
     */
    private boolean isNeedDownload(RealmObject obj, String localPath) {
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

    private class FilePath {
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
