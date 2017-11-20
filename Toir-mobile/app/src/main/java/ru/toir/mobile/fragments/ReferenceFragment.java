package ru.toir.mobile.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import ru.toir.mobile.db.realm.Contragent;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.MeasuredValue;
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
import ru.toir.mobile.db.realm.StageStatus;
import ru.toir.mobile.db.realm.StageTemplate;
import ru.toir.mobile.db.realm.StageType;
import ru.toir.mobile.db.realm.StageVerdict;
import ru.toir.mobile.db.realm.TaskStageList;
import ru.toir.mobile.db.realm.TaskStageOperationList;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.TaskTemplate;
import ru.toir.mobile.db.realm.TaskType;
import ru.toir.mobile.db.realm.TaskVerdict;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.Tool;
import ru.toir.mobile.db.realm.ToolType;
import ru.toir.mobile.rest.ToirAPIFactory;

//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import ru.toir.mobile.rest.IServiceProvider;
//import ru.toir.mobile.rest.ProcessorService;
//import ru.toir.mobile.rest.ReferenceServiceHelper;

public class ReferenceFragment extends Fragment {
    private static final String TAG = "ReferenceFragment";
    private Realm realmDB;

    private ListView contentListView;

//	private IntentFilter mFilterGetReference = new IntentFilter(ToirAPIFactory.Actions.ACTION_GET_ALL_REFERENCE);
//	private BroadcastReceiver mReceiverGetReference = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			getReferencesDialog.dismiss();
//			context.unregisterReceiver(mReceiverGetReference);
//			boolean result = intent.getBooleanExtra(
//					ProcessorService.Extras.RESULT_EXTRA, false);
//			Bundle bundle = intent
//					.getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
//			if (result) {
//				Toast.makeText(context, "Справочники обновлены",
//						Toast.LENGTH_SHORT).show();
//			} else {
//				// сообщаем описание неудачи
//				String message = bundle.getString(IServiceProvider.MESSAGE);
//				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//			}
//		}
//	};

    public static ReferenceFragment newInstance() {
        return (new ReferenceFragment());
    }

    public static void updateReferences(final ProgressDialog dialog) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // получаем справочники, обновляем всё несмотря на то что часть данных будет дублироваться
                final Date currentDate = new Date();
                String changedDate;
                String referenceName;

                // AlertType
                referenceName = AlertType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<AlertType>> response = ToirAPIFactory.getAlertTypeService().alertType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<AlertType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Contragent
                referenceName = Contragent.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Contragent>> response = ToirAPIFactory.getContragentService().contragents(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Contragent> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // CriticalType
                referenceName = CriticalType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<CriticalType>> response = ToirAPIFactory.getCriticalTypeService().criticalType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<CriticalType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                // DefectType
                referenceName = DefectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectType>> response = ToirAPIFactory.getDefectTypeService().defectType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Defect
                referenceName = Defect.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Defect>> response = ToirAPIFactory.getDefectService().defect(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Defect> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Documentation
                // нужно ли вообще таким образом обновлять этот справочник???
                referenceName = Documentation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Documentation>> response = ToirAPIFactory.getDocumentationService().documentation(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Documentation> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // DocumentationType ???
                referenceName = DocumentationType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DocumentationType>> response = ToirAPIFactory.getDocumentationTypeService().documentationType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DocumentationType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Equipment ???
                referenceName = Equipment.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Equipment>> response = ToirAPIFactory.getEquipmentService().equipment(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Equipment> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // EquipmentModel ???
                referenceName = EquipmentModel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentModel>> response = ToirAPIFactory.getEquipmentModelService().equipmentModel(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentModel> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // EquipmentStatus
                referenceName = EquipmentStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentStatus>> response = ToirAPIFactory.getEquipmentStatusService().equipmentStatus(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentStatus> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // EquipmentType ??
                referenceName = EquipmentType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentType>> response = ToirAPIFactory.getEquipmentTypeService().equipmentType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // GpsTrack ???
                // Journal ???

                // MeasuredValue ???
                // TODO: разобраться с тем что при разборе json числовые значения не сохраняются в string !!!
//                referenceName = MeasuredValue.class.getSimpleName();
//                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
//                try {
//                    Response<List<MeasuredValue>> response = ToirAPIFactory.getMeasuredValueService().measuredValue(changedDate).execute();
//                    if (response.isSuccessful()) {
//                        List<MeasuredValue> list = response.body();
//                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                }

                // MeasureType
                referenceName = MeasureType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<MeasureType>> response = ToirAPIFactory.getMeasureTypeService().measureType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<MeasureType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Operation ???
                referenceName = Operation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Operation>> response = ToirAPIFactory.getOperationService().operation(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Operation> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OperationStatus
                referenceName = OperationStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationStatus>> response = ToirAPIFactory.getOperationStatusService().operationStatus(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationStatus> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OperationTemplate
                referenceName = OperationTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationTemplate>> response = ToirAPIFactory.getOperationTemplateService().operationTemplate(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationTemplate> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OperationTool
                referenceName = OperationTool.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationTool>> response = ToirAPIFactory.getOperationToolService().operationTool(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationTool> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OperationType
                referenceName = OperationType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationType>> response = ToirAPIFactory.getOperationTypeService().operationType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // ObjectType
                referenceName = ObjectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<ObjectType>> response = ToirAPIFactory.getObjectTypeService().objectType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<ObjectType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Objects
                referenceName = Objects.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Objects>> response = ToirAPIFactory.getObjectService().objects(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Objects> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OperationVerdict
                referenceName = OperationVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationVerdict>> response = ToirAPIFactory.getOperationVerdictService().operationVerdict(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationVerdict> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OrderLevel
                referenceName = OrderLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderLevel>> response = ToirAPIFactory.getOrderLevelService().orderLevel(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderLevel> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Orders ???
                referenceName = Orders.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Orders>> response = ToirAPIFactory.getOrdersService().orders(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Orders> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OrderStatus
                referenceName = OrderStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderStatus>> response = ToirAPIFactory.getOrderStatusService().orderStatus(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderStatus> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // OrderVerdict
                referenceName = OrderVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderVerdict>> response = ToirAPIFactory.getOrderVerdictService().orderVerdict(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderVerdict> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // RepairPart ???
                referenceName = RepairPart.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<RepairPart>> response = ToirAPIFactory.getRepairPartService().repairPart(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<RepairPart> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // RepairPartType ???
                referenceName = RepairPartType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<RepairPartType>> response = ToirAPIFactory.getRepairPartTypeService().repairPartType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<RepairPartType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Tasks ???
                referenceName = Tasks.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Tasks>> response = ToirAPIFactory.getTasksService().tasks(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Tasks> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStageList ???
                referenceName = TaskStageList.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskStageList>> response = ToirAPIFactory.getTaskStageListService().taskStageList(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskStageList> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStageOperationList ???
                referenceName = TaskStageOperationList.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskStageOperationList>> response = ToirAPIFactory.getTaskStageOperationListService().taskStageOperationList(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskStageOperationList> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStages ???
                referenceName = TaskStages.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskStages>> response = ToirAPIFactory.getTaskStagesService().taskStages(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskStages> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStageStatus
                referenceName = StageStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageStatus>> response = ToirAPIFactory.getTaskStageStatusService().taskStageStatus(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageStatus> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStageTemplate ???
                referenceName = StageTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageTemplate>> response = ToirAPIFactory.getTaskStageTemplateService().taskStageTemplate(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageTemplate> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStageType ???
                referenceName = StageType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageType>> response = ToirAPIFactory.getTaskStageTypeService().taskStageType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStageVerdict
                referenceName = StageVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageVerdict>> response = ToirAPIFactory.getTaskStageVerdictService().taskStageVerdict(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageVerdict> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskStatus
                referenceName = TaskStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskStatus>> response = ToirAPIFactory.getTaskStatusService().taskStatus(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskStatus> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskTemplate ???
                referenceName = TaskTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskTemplate>> response = ToirAPIFactory.getTaskTemplateService().taskTemplate(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskTemplate> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskType ???
                referenceName = TaskType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskType>> response = ToirAPIFactory.getTaskTypeService().taskType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // TaskVerdict
                referenceName = TaskVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskVerdict>> response = ToirAPIFactory.getTaskVerdictService().taskVerdict(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskVerdict> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // Tool ???
                referenceName = Tool.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Tool>> response = ToirAPIFactory.getToolService().tool(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Tool> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // ToolType ???
                referenceName = ToolType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<ToolType>> response = ToirAPIFactory.getToolTypeService().toolType(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<ToolType> list = response.body();
                        ReferenceUpdate.saveReferenceData(referenceName, list, currentDate);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                // User ???

                // гасим диалог обновления справочников
                if (dialog != null) {
                    dialog.dismiss();
                }

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.reference_layout, container, false);
        realmDB = Realm.getDefaultInstance();

        Spinner referenceSpinner = (Spinner) rootView.findViewById(R.id.simple_spinner);
        contentListView = (ListView) rootView.findViewById(R.id.reference_listView);

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

        ArrayAdapter<SortField> referenceSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, referenceList);

        referenceSpinner.setAdapter(referenceSpinnerAdapter);
        ReferenceSpinnerListener referenceSpinnerListener = new ReferenceSpinnerListener();
        referenceSpinner.setOnItemSelectedListener(referenceSpinnerListener);

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private void fillListViewDocumentationType() {
        RealmResults<DocumentationType> documentationType;
        documentationType = realmDB.where(DocumentationType.class).findAll();
        DocumentationTypeAdapter documentationTypeAdapter = new DocumentationTypeAdapter(getActivity().getApplicationContext(), documentationType);
        contentListView.setAdapter(documentationTypeAdapter);
    }

    private void fillListViewEquipmentType() {
        RealmResults<EquipmentType> equipmentType;
        equipmentType = realmDB.where(EquipmentType.class).findAll();
        EquipmentTypeAdapter equipmentTypeAdapter = new EquipmentTypeAdapter(getActivity().getApplicationContext(), equipmentType);
        contentListView.setAdapter(equipmentTypeAdapter);
    }

    private void fillListViewCriticalType() {
        RealmResults<CriticalType> criticalType;
        criticalType = realmDB.where(CriticalType.class).findAll();
        CriticalTypeAdapter criticalTypeAdapter = new CriticalTypeAdapter(getActivity().getApplicationContext(), R.id.reference_listView, criticalType);
        contentListView.setAdapter(criticalTypeAdapter);
    }

    private void fillListViewAlertType() {
        RealmResults<AlertType> alertType;
        alertType = realmDB.where(AlertType.class).findAll();
        AlertTypeAdapter alertTypeAdapter = new AlertTypeAdapter(getActivity().getApplicationContext(), R.id.reference_listView, alertType);
        contentListView.setAdapter(alertTypeAdapter);
    }

    private void fillListViewOperationStatus() {
        RealmResults<OperationStatus> operationStatus;
        operationStatus = realmDB.where(OperationStatus.class).findAll();
        OperationStatusAdapter operationAdapter = new OperationStatusAdapter(getActivity().getApplicationContext(), R.id.reference_listView, operationStatus);
        contentListView.setAdapter(operationAdapter);
    }

    private void fillListViewOperationVerdict() {
        RealmResults<OperationVerdict> operationVerdict;
        operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(getActivity().getApplicationContext(), operationVerdict);
        contentListView.setAdapter(operationVerdictAdapter);
    }

    private void fillListViewObjectType() {
        RealmResults<ObjectType> objectType;
        objectType = realmDB.where(ObjectType.class).findAll();
        ObjectTypeAdapter objectAdapter = new ObjectTypeAdapter(getActivity().getApplicationContext(), objectType);
        contentListView.setAdapter(objectAdapter);
    }

    private void fillListViewDefectType() {
        RealmResults<DefectType> defectType;
        defectType = realmDB.where(DefectType.class).findAll();
        DefectTypeAdapter defectAdapter = new DefectTypeAdapter(getActivity().getApplicationContext(), defectType);
        contentListView.setAdapter(defectAdapter);
    }

    private void fillListViewOperationType() {
        RealmResults<OperationType> operationType;
        operationType = realmDB.where(OperationType.class).findAll();
        OperationTypeAdapter operationAdapter = new OperationTypeAdapter(getActivity().getApplicationContext(), R.id.reference_listView, operationType);
        contentListView.setAdapter(operationAdapter);
    }

    private void fillListViewTaskStatus() {
        RealmResults<TaskStatus> taskStatuses;
        taskStatuses = realmDB.where(TaskStatus.class).findAll();
        TaskStatusAdapter taskStatusAdapter = new TaskStatusAdapter(getActivity().getApplicationContext(), taskStatuses);
        contentListView.setAdapter(taskStatusAdapter);
    }

    private void fillListViewTaskStageStatus() {
        RealmResults<StageStatus> taskStageStatuses;
        taskStageStatuses = realmDB.where(StageStatus.class).findAll();
        StageStatusAdapter taskStageStatusAdapter = new StageStatusAdapter(getActivity().getApplicationContext(), taskStageStatuses);
        contentListView.setAdapter(taskStageStatusAdapter);
    }

    private void fillListViewEquipmentStatus() {
        RealmResults<EquipmentStatus> equipmentStatuses;
        equipmentStatuses = realmDB.where(EquipmentStatus.class).findAll();
        EquipmentStatusAdapter equipmentAdapter = new EquipmentStatusAdapter(getActivity().getApplicationContext(), R.id.reference_listView, equipmentStatuses);
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
                                Toast.makeText(getActivity(), "Обновление справочников отменено", Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Toast.makeText(getContext(), "Справочники обновлены", Toast.LENGTH_SHORT).show();
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
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

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
                    fillListViewTaskStageStatus();
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
