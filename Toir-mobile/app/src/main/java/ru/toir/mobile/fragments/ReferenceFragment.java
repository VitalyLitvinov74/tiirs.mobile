package ru.toir.mobile.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

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
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.AlertTypeAdapter;
import ru.toir.mobile.db.adapters.CriticalTypeAdapter;
import ru.toir.mobile.db.adapters.DefectLevelAdapter;
import ru.toir.mobile.db.adapters.DefectTypeAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.db.adapters.ObjectTypeAdapter;
import ru.toir.mobile.db.adapters.OperationStatusAdapter;
import ru.toir.mobile.db.adapters.OperationTypeAdapter;
import ru.toir.mobile.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.db.adapters.StageStatusAdapter;
import ru.toir.mobile.db.adapters.TaskStatusAdapter;
import ru.toir.mobile.db.realm.AlertType;
import ru.toir.mobile.db.realm.AttributeType;
import ru.toir.mobile.db.realm.CommonFile;
import ru.toir.mobile.db.realm.Contragent;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectLevel;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentAttribute;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.db.realm.IToirDbObject;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.MediaFile;
import ru.toir.mobile.db.realm.ObjectType;
import ru.toir.mobile.db.realm.Objects;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationTemplate;
import ru.toir.mobile.db.realm.OperationTool;
import ru.toir.mobile.db.realm.OperationType;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.OrderLevel;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.OrderVerdict;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.ReferenceUpdate;
import ru.toir.mobile.db.realm.RepairPart;
import ru.toir.mobile.db.realm.RepairPartType;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.StageStatus;
import ru.toir.mobile.db.realm.StageTemplate;
import ru.toir.mobile.db.realm.StageType;
import ru.toir.mobile.db.realm.StageVerdict;
import ru.toir.mobile.db.realm.Task;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.TaskTemplate;
import ru.toir.mobile.db.realm.TaskType;
import ru.toir.mobile.db.realm.TaskVerdict;
import ru.toir.mobile.db.realm.Tool;
import ru.toir.mobile.db.realm.ToolType;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rest.ToirAPIResponse;

public class ReferenceFragment extends Fragment {
    private static final String TAG = "ReferenceFragment";
    private Realm realmDB;

    private ListView contentListView;

    public static ReferenceFragment newInstance() {
        return (new ReferenceFragment());
    }

    /**
     * Метод для обновления справочников необходимых для работы с нарядом.
     */
    public static void updateReferencesForOrders(final Context context) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Date currentDate = new Date();
                String changedDate;
                String referenceName;
                Realm realm = Realm.getDefaultInstance();

                // OrderLevel
                referenceName = OrderLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderLevel>> response = ToirAPIFactory.getOrderLevelService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderLevel> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderStatus
                referenceName = OrderStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderStatus>> response = ToirAPIFactory.getOrderStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderVerdict
                referenceName = OrderVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderVerdict>> response = ToirAPIFactory.getOrderVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskVerdict
                referenceName = TaskVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskVerdict>> response = ToirAPIFactory.getTaskVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskStatus
                referenceName = TaskStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskStatus>> response = ToirAPIFactory.getTaskStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentStatus
                referenceName = EquipmentStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentStatus>> response = ToirAPIFactory
                            .getEquipmentStatusService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageVerdict
                referenceName = StageVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageVerdict>> response = ToirAPIFactory.getStageVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageStatus
                referenceName = StageStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageStatus>> response = ToirAPIFactory.getStageStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationVerdict
                referenceName = OperationVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationVerdict>> response = ToirAPIFactory
                            .getOperationVerdictService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationStatus
                referenceName = OperationStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationStatus>> response = ToirAPIFactory
                            .getOperationStatusService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // MeasureType
                referenceName = MeasureType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<MeasureType>> response = ToirAPIFactory.getMeasureTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<MeasureType> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // AttributeType
                referenceName = AttributeType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<AttributeType>> response = ToirAPIFactory.getAttributeTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<AttributeType> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentAttribute
                referenceName = EquipmentAttribute.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentAttribute>> response = ToirAPIFactory.getEquipmentAttributeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        // TODO: реализовать механизм проверки наличия изменённых данных локально
                        // при необходимости отбрасывать данные с сервера
                        List<EquipmentAttribute> list = response.body();
                        if (list.size() > 0) {
                            // сразу ставим флаг что они "отправлены", чтоб избежать их повторной отправки
                            for (EquipmentAttribute item : list) {
                                item.setSent(true);
                            }

                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DefectType
                referenceName = DefectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectType>> response = ToirAPIFactory.getDefectTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectType> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DefectLevel
                referenceName = DefectLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectLevel>> response = ToirAPIFactory.getDefectLevelService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectLevel> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Documentation
                referenceName = Documentation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Documentation>> response = ToirAPIFactory.getDocumentationService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Documentation> list = response.body();
                        List<FilePath> files = new ArrayList<>();
                        File extDir = context.getExternalFilesDir("");
                        AuthorizedUser user = AuthorizedUser.getInstance();
                        String userName = user.getLogin();
                        if (extDir == null) {
                            throw new Exception("Unable get extDir!!!");
                        }

                        for (Documentation item : list) {
                            String localPath = item.getImageFilePath() + "/";
                            if (isNeedDownload(extDir, item, localPath, item.isRequired())) {
                                String url = item.getImageFileUrl(userName) + "/";
                                files.add(new FilePath(item.getPath(), url, localPath));
                            }
                        }

                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }

                        Map<String, Set<String>> requestList = new HashMap<>();
                        // тестовый вывод для принятия решения о группировке файлов для минимизации количества загружаемых данных
                        for (FilePath item : files) {
                            String key = item.urlPath + item.fileName;
                            if (!requestList.containsKey(key)) {
                                Set<String> listOfDoc = new HashSet<>();
                                listOfDoc.add(item.localPath);
                                requestList.put(key, listOfDoc);
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // CommonFile
                referenceName = CommonFile.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<CommonFile>> response = ToirAPIFactory.getCommonFileService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<CommonFile> list = response.body();
                        File extDir = context.getExternalFilesDir("");
                        AuthorizedUser user = AuthorizedUser.getInstance();
                        String userName = user.getLogin();
                        if (extDir == null) {
                            throw new Exception("Unable get extDir!!!");
                        }

                        for (CommonFile item : list) {
                            String localPath = CommonFile.getImageRoot();
                            item.setPath(localPath);
                        }

                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }

                        // загружаем файлы
                        for (CommonFile item : list) {
                            if (!isNeedDownload(extDir, item, item.getPath(), item.isRequire())) {
                                continue;
                            }

                            String url = null;
                            Response<ToirAPIResponse> urlResponse = ToirAPIFactory.getCommonFileService()
                                    .getUrl(item.getUuid()).execute();
                            if (response.isSuccessful()) {
                                ToirAPIResponse data = urlResponse.body();
                                url = (String) data.getData();
                                if (url == null || url.equals("")) {
                                    continue;
                                }

                                url = ToirApplication.serverUrl + data.getData();
                            }

                            Call<ResponseBody> callFile = ToirAPIFactory.getFileDownload().get(url);
                            try {
                                retrofit2.Response<ResponseBody> r = callFile.execute();
                                ResponseBody trueImgBody = r.body();
                                if (trueImgBody == null) {
                                    continue;
                                }

                                File file = new File(extDir.getAbsolutePath() + '/' + item.getPath(), item.getName());
                                if (!file.getParentFile().exists()) {
                                    if (!file.getParentFile().mkdirs()) {
                                        Log.e(TAG, "Не удалось создать папку " +
                                                file.getParentFile().toString() +
                                                " для сохранения файла!");
                                        continue;
                                    }
                                }

                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(trueImgBody.bytes());
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                realm.close();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public static boolean isNeedDownload(File extDir, RealmObject obj, String localPath, boolean isRequery) {
        Realm realm = Realm.getDefaultInstance();
        String uuid = ((IToirDbObject) obj).getUuid();
        RealmObject dbObj = realm.where(obj.getClass()).equalTo("uuid", uuid).findFirst();
        long localChangedAt;

        // есть ли локальная запись
        if (dbObj != null) {
            localChangedAt = ((IToirDbObject) dbObj).getChangedAt().getTime();
            realm.close();
        } else {
            realm.close();
            return isRequery;
        }

        // есть ли локально файл
        String fileName = ((IToirDbObject) obj).getImageFile();
        if (fileName != null) {
            File file = new File(extDir.getAbsolutePath() + '/' + localPath, fileName);
            if (!file.exists()) {
                return isRequery;
            }
        } else {
            return false;
        }

        // есть ли изменения на сервере
        return localChangedAt < ((IToirDbObject) obj).getChangedAt().getTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.reference_layout, container, false);
        realmDB = Realm.getDefaultInstance();

        Spinner referenceSpinner = rootView.findViewById(R.id.simple_spinner);
        contentListView = rootView.findViewById(R.id.reference_listView);

        // получаем список справочников, разбиваем его на ключ:значение
        String[] referenceArray = getResources().getStringArray(R.array.references_array);
        String[] tmpValue;
        SortField item;
        ArrayList<SortField> referenceList = new ArrayList<>();
        for (String value : referenceArray) {
            tmpValue = value.split(":");
            item = new SortField(tmpValue[0], tmpValue[1]);
            referenceList.add(item);
        }

        Activity activity = getActivity();
        if (activity != null) {
            ArrayAdapter<SortField> referenceSpinnerAdapter = new ArrayAdapter<>(activity,
                    android.R.layout.simple_spinner_dropdown_item, referenceList);

            referenceSpinner.setAdapter(referenceSpinnerAdapter);
            ReferenceSpinnerListener referenceSpinnerListener = new ReferenceSpinnerListener();
            referenceSpinner.setOnItemSelectedListener(referenceSpinnerListener);
        }

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private void fillListViewDocumentationType() {
        RealmResults<DocumentationType> documentationType;
        documentationType = realmDB.where(DocumentationType.class).findAll();
        DocumentationTypeAdapter documentationTypeAdapter = new DocumentationTypeAdapter(documentationType);
        contentListView.setAdapter(documentationTypeAdapter);
    }

    private void fillListViewEquipmentType() {
        RealmResults<EquipmentType> equipmentType;
        equipmentType = realmDB.where(EquipmentType.class).findAll();
        EquipmentTypeAdapter equipmentTypeAdapter = new EquipmentTypeAdapter(equipmentType);
        contentListView.setAdapter(equipmentTypeAdapter);
    }

    private void fillListViewCriticalType() {
        RealmResults<CriticalType> criticalType;
        criticalType = realmDB.where(CriticalType.class).findAll();
        CriticalTypeAdapter criticalTypeAdapter = new CriticalTypeAdapter(criticalType);
        contentListView.setAdapter(criticalTypeAdapter);
    }

    private void fillListViewAlertType() {
        RealmResults<AlertType> alertType;
        alertType = realmDB.where(AlertType.class).findAll();
        AlertTypeAdapter alertTypeAdapter = new AlertTypeAdapter(alertType);
        contentListView.setAdapter(alertTypeAdapter);
    }

    private void fillListViewOperationStatus() {
        RealmResults<OperationStatus> operationStatus;
        operationStatus = realmDB.where(OperationStatus.class).findAll();
        OperationStatusAdapter operationAdapter = new OperationStatusAdapter(operationStatus);
        contentListView.setAdapter(operationAdapter);
    }

    private void fillListViewOperationVerdict() {
        RealmResults<OperationVerdict> operationVerdict;
        operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(operationVerdict);
        contentListView.setAdapter(operationVerdictAdapter);
    }

    private void fillListViewObjectType() {
        RealmResults<ObjectType> objectType;
        objectType = realmDB.where(ObjectType.class).findAll();
        ObjectTypeAdapter objectAdapter = new ObjectTypeAdapter(objectType);
        contentListView.setAdapter(objectAdapter);
    }

    private void fillListViewDefectType() {
        RealmResults<DefectType> defectType;
        defectType = realmDB.where(DefectType.class).findAll();
        DefectTypeAdapter defectAdapter = new DefectTypeAdapter(defectType);
        contentListView.setAdapter(defectAdapter);
    }

    private void fillListViewDefectLevel() {
        RealmResults<DefectLevel> defectLevel;
        defectLevel = realmDB.where(DefectLevel.class).findAll();
        DefectLevelAdapter defectAdapter = new DefectLevelAdapter(defectLevel);
        contentListView.setAdapter(defectAdapter);
    }

    private void fillListViewOperationType() {
        RealmResults<OperationType> operationType;
        operationType = realmDB.where(OperationType.class).findAll();
        OperationTypeAdapter operationAdapter = new OperationTypeAdapter(operationType);
        contentListView.setAdapter(operationAdapter);
    }

    private void fillListViewTaskStatus() {
        RealmResults<TaskStatus> taskStatuses;
        taskStatuses = realmDB.where(TaskStatus.class).findAll();
        TaskStatusAdapter taskStatusAdapter = new TaskStatusAdapter(taskStatuses);
        contentListView.setAdapter(taskStatusAdapter);
    }

    private void fillListViewStageStatus() {
        RealmResults<StageStatus> stageStatuses;
        stageStatuses = realmDB.where(StageStatus.class).findAll();
        StageStatusAdapter stageStatusAdapter = new StageStatusAdapter(stageStatuses);
        contentListView.setAdapter(stageStatusAdapter);
    }

    private void fillListViewEquipmentStatus() {
        RealmResults<EquipmentStatus> equipmentStatuses;
        equipmentStatuses = realmDB.where(EquipmentStatus.class).findAll();
        EquipmentStatusAdapter equipmentAdapter = new EquipmentStatusAdapter(equipmentStatuses);
        contentListView.setAdapter(equipmentAdapter);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
     * android.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
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

    /**
     * @author Dmitriy Logachov
     *         <p>
     *         Класс реализует обработку выбора элемента выпадающего списка
     *         справочников.
     *         </p>
     */
    private class ReferenceSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position,
                                   long id) {

            SortField selectedItem = (SortField) parentView.getItemAtPosition(position);
            String selected = selectedItem.getField();

            switch (selected) {
                case DocumentationTypeAdapter.TABLE_NAME:
                    fillListViewDocumentationType();
                    break;
                case EquipmentTypeAdapter.TABLE_NAME:
                    fillListViewEquipmentType();
                    break;
                case CriticalTypeAdapter.TABLE_NAME:
                    fillListViewCriticalType();
                    break;
                case AlertTypeAdapter.TABLE_NAME:
                    fillListViewAlertType();
                    break;
                case OperationVerdictAdapter.TABLE_NAME:
                    fillListViewOperationVerdict();
                    break;
                case OperationTypeAdapter.TABLE_NAME:
                    fillListViewOperationType();
                    break;
                case OperationStatusAdapter.TABLE_NAME:
                    fillListViewOperationStatus();
                    break;
                case TaskStatusAdapter.TABLE_NAME:
                    fillListViewTaskStatus();
                    break;
                case StageStatusAdapter.TABLE_NAME:
                    fillListViewStageStatus();
                    break;
                case EquipmentStatusAdapter.TABLE_NAME:
                    fillListViewEquipmentStatus();
                    break;
                case ObjectTypeAdapter.TABLE_NAME:
                    fillListViewObjectType();
                    break;
                case DefectTypeAdapter.TABLE_NAME:
                    fillListViewDefectType();
                    break;
                case DefectLevelAdapter.TABLE_NAME:
                    fillListViewDefectLevel();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    }
}
