package ru.toir.mobile.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;
import ru.toir.mobile.R;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.AlertTypeAdapter;
import ru.toir.mobile.db.adapters.CriticalTypeAdapter;
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
import ru.toir.mobile.db.realm.Contragent;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentAttribute;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.ObjectType;
import ru.toir.mobile.db.realm.Objects;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationFile;
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
    public static void updateReferencesForOrders() {

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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        // сразу ставим флаг что они "отправлены", чтоб избежать их повторной отправки
                        for (EquipmentAttribute item : list) {
                            item.setSent(true);
                        }
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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

    /**
     * Обновляет тупо все справочники без разбора необходимости.
     * Неиспользовать!
     *
     * @param dialog    Диалог показывающий процесс обновления справочников
     */
    public static void updateReferences(final ProgressDialog dialog) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // получаем справочники, обновляем всё несмотря на то что часть данных будет дублироваться
                final Date currentDate = new Date();
                String changedDate;
                String referenceName;
                Realm realm = Realm.getDefaultInstance();

                // AlertType
                referenceName = AlertType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<AlertType>> response = ToirAPIFactory.getAlertTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<AlertType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Contragent
                referenceName = Contragent.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Contragent>> response = ToirAPIFactory.getContragentService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Contragent> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // CriticalType
                referenceName = CriticalType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<CriticalType>> response = ToirAPIFactory.getCriticalTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<CriticalType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Defect
                referenceName = Defect.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Defect>> response = ToirAPIFactory.getDefectService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Defect> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Documentation
                // нужно ли вообще таким образом обновлять этот справочник???
                referenceName = Documentation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Documentation>> response = ToirAPIFactory.getDocumentationService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Documentation> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DocumentationType ???
                referenceName = DocumentationType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DocumentationType>> response = ToirAPIFactory
                            .getDocumentationTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DocumentationType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Equipment ???
                referenceName = Equipment.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Equipment>> response = ToirAPIFactory.getEquipmentService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Equipment> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentModel ???
                referenceName = EquipmentModel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentModel>> response = ToirAPIFactory
                            .getEquipmentModelService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentModel> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentType ??
                referenceName = EquipmentType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentType>> response = ToirAPIFactory.getEquipmentTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // MeasuredValue
                referenceName = MeasuredValue.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<MeasuredValue>> response = ToirAPIFactory.getMeasuredValueService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<MeasuredValue> list = response.body();
                        // устанавливаем флаг того данные были уже отправлены на сервер
                        for (MeasuredValue value : list) {
                            value.setSent(true);
                        }

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Operation ???
                referenceName = Operation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Operation>> response = ToirAPIFactory.getOperationService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Operation> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationTemplate
                referenceName = OperationTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationTemplate>> response = ToirAPIFactory
                            .getOperationTemplateService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationTemplate> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationTool
                referenceName = OperationTool.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationTool>> response = ToirAPIFactory
                            .getOperationToolService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationTool> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationType
                referenceName = OperationType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationType>> response = ToirAPIFactory
                            .getOperationTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // ObjectType
                referenceName = ObjectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<ObjectType>> response = ToirAPIFactory
                            .getObjectTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<ObjectType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Objects
                referenceName = Objects.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Objects>> response = ToirAPIFactory.getObjectService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Objects> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderLevel
                referenceName = OrderLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderLevel>> response = ToirAPIFactory.getOrderLevelService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderLevel> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Orders ???
                referenceName = Orders.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Orders>> response = ToirAPIFactory.getOrdersService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Orders> list = response.body();
                        // устанавливаем флаг того данные были уже отправлены на сервер
                        for (Orders order : list) {
                            order.setSent(true);
                        }

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // RepairPart ???
                referenceName = RepairPart.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<RepairPart>> response = ToirAPIFactory.getRepairPartService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<RepairPart> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // RepairPartType ???
                referenceName = RepairPartType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<RepairPartType>> response = ToirAPIFactory
                            .getRepairPartTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<RepairPartType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Task ???
                referenceName = Task.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Task>> response = ToirAPIFactory.getTasksService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Task> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Stages ???
                referenceName = Stage.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Stage>> response = ToirAPIFactory.getStageService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Stage> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageTemplate ???
                referenceName = StageTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageTemplate>> response = ToirAPIFactory
                            .getStageTemplateService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageTemplate> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageType ???
                referenceName = StageType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageType>> response = ToirAPIFactory.getStageTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskTemplate ???
                referenceName = TaskTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskTemplate>> response = ToirAPIFactory.getTaskTemplateService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskTemplate> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskType ???
                referenceName = TaskType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskType>> response = ToirAPIFactory.getTaskTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Tool ???
                referenceName = Tool.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Tool>> response = ToirAPIFactory.getToolService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Tool> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // ToolType ???
                referenceName = ToolType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<ToolType>> response = ToirAPIFactory.getToolTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<ToolType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // User ???

                // OperationFile
                referenceName = OperationFile.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationFile>> response = ToirAPIFactory.getOperationFileService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationFile> list = response.body();
                        // устанавливаем флаг того данные были уже отправлены на сервер
                        for (OperationFile file : list) {
                            file.setSent(true);
                        }

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        // сразу ставим флаг что они "отправлены", чтоб избежать их повторной отправки
                        for (EquipmentAttribute item : list) {
                            item.setSent(true);
                        }
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
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
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // гасим диалог обновления справочников
                if (dialog != null) {
                    dialog.dismiss();
                }

                realm.close();
            }
        });
        thread.start();
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

        // добавляем элемент меню для обновления справочников
        MenuItem getTask = menu.add("Обновить справочники");
        getTask.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "Обновляем справочники.");
                ProgressDialog dialog = new ProgressDialog(getActivity());


//				ReferenceServiceHelper rsh = new ReferenceServiceHelper(getActivity().getApplicationContext(), ToirAPIFactory.Actions.ACTION_GET_ALL_REFERENCE);
//				getActivity().registerReceiver(mReceiverGetReference, mFilterGetReference);
//				rsh.getAll();
                updateReferences(dialog);

                // показываем диалог обновления справочников
                dialog.setMessage("Получаем справочники");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        "Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
//								getActivity().unregisterReceiver(mReceiverGetReference);
                                Toast.makeText(getActivity(), "Обновление справочников отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Toast.makeText(getContext(), "Справочники обновлены", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
                dialog.show();

                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
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
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    }
}
