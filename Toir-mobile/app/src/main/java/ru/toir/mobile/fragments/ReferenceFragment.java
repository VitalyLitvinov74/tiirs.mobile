package ru.toir.mobile.fragments;

import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
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
import retrofit.Response;
import ru.toir.mobile.R;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.AlertTypeAdapter;
import ru.toir.mobile.db.adapters.CriticalTypeAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.db.adapters.OperationStatusAdapter;
import ru.toir.mobile.db.adapters.OperationTypeAdapter;
import ru.toir.mobile.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.db.adapters.TaskStatusAdapter;
import ru.toir.mobile.db.realm.AlertType;
import ru.toir.mobile.db.realm.Clients;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentModel;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.MeasuredValue;
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
import ru.toir.mobile.db.realm.TaskStageList;
import ru.toir.mobile.db.realm.TaskStageOperationList;
import ru.toir.mobile.db.realm.TaskStageStatus;
import ru.toir.mobile.db.realm.TaskStageTemplate;
import ru.toir.mobile.db.realm.TaskStageType;
import ru.toir.mobile.db.realm.TaskStageVerdict;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.TaskTemplate;
import ru.toir.mobile.db.realm.TaskType;
import ru.toir.mobile.db.realm.TaskVerdict;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.Tool;
import ru.toir.mobile.db.realm.ToolType;
//import ru.toir.mobile.rest.IServiceProvider;
//import ru.toir.mobile.rest.ProcessorService;
//import ru.toir.mobile.rest.ReferenceServiceHelper;
import ru.toir.mobile.rest.ToirAPIFactory;

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
                final ProgressDialog dialog = new ProgressDialog(getActivity());


//				ReferenceServiceHelper rsh = new ReferenceServiceHelper(getActivity().getApplicationContext(), ToirAPIFactory.Actions.ACTION_GET_ALL_REFERENCE);
//				getActivity().registerReceiver(mReceiverGetReference, mFilterGetReference);
//				rsh.getAll();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // получаем справочники, обновляем всё несмотря на то что часть данных будет дублироваться
                        final Date currentDate = new Date();
                        String changedDate;

                        // AlertType
                        changedDate = ReferenceUpdate.lastChangedAsStr(AlertType.class.getSimpleName());
                        try {
                            Response<List<AlertType>> response = ToirAPIFactory.getAlertTypeService().alertType(changedDate).execute();
                            List<AlertType> list = response.body();
                            ReferenceUpdate.saveReferenceData(AlertType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // Clients
                        changedDate = ReferenceUpdate.lastChangedAsStr(Clients.class.getSimpleName());
                        try {
                            Response<List<Clients>> response = ToirAPIFactory.getClientsService().clients(changedDate).execute();
                            List<Clients> list = response.body();
                            ReferenceUpdate.saveReferenceData(Clients.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // CriticalType
                        changedDate = ReferenceUpdate.lastChangedAsStr(CriticalType.class.getSimpleName());
                        try {
                            Response<List<CriticalType>> response = ToirAPIFactory.getCriticalTypeService().criticalType(changedDate).execute();
                            List<CriticalType> list = response.body();
                            ReferenceUpdate.saveReferenceData(CriticalType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // Documentation
                        // нужно ли вообще таким образом обновлять этот справочник???
                        changedDate = ReferenceUpdate.lastChangedAsStr(Documentation.class.getSimpleName());
                        try {
                            Response<List<Documentation>> response = ToirAPIFactory.getDocumentationService().documentation(changedDate).execute();
                            List<Documentation> list = response.body();
                            ReferenceUpdate.saveReferenceData(Documentation.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // DocumentationType ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(DocumentationType.class.getSimpleName());
                        try {
                            Response<List<DocumentationType>> response = ToirAPIFactory.getDocumentationTypeService().documentationType(changedDate).execute();
                            List<DocumentationType> list = response.body();
                            ReferenceUpdate.saveReferenceData(DocumentationType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // Equipment ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(Equipment.class.getSimpleName());
                        try {
                            Response<List<Equipment>> response = ToirAPIFactory.getEquipmentService().equipment(changedDate).execute();
                            List<Equipment> list = response.body();
                            ReferenceUpdate.saveReferenceData(Equipment.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // EquipmentModel ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(EquipmentModel.class.getSimpleName());
                        try {
                            Response<List<EquipmentModel>> response = ToirAPIFactory.getEquipmentModelService().equipmentModel(changedDate).execute();
                            List<EquipmentModel> list = response.body();
                            ReferenceUpdate.saveReferenceData(EquipmentModel.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // EquipmentStatus
                        changedDate = ReferenceUpdate.lastChangedAsStr(EquipmentStatus.class.getSimpleName());
                        try {
                            Response<List<EquipmentStatus>> response = ToirAPIFactory.getEquipmentStatusService().equipmentStatus(changedDate).execute();
                            List<EquipmentStatus> list = response.body();
                            ReferenceUpdate.saveReferenceData(EquipmentStatus.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // EquipmentType ??
                        changedDate = ReferenceUpdate.lastChangedAsStr(EquipmentType.class.getSimpleName());
                        try {
                            Response<List<EquipmentType>> response = ToirAPIFactory.getEquipmentTypeService().equipmentType(changedDate).execute();
                            List<EquipmentType> list = response.body();
                            ReferenceUpdate.saveReferenceData(EquipmentType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // GpdTrack ???
                        // Journal ???

                        // MeasuredValue ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(MeasuredValue.class.getSimpleName());
                        try {
                            Response<List<MeasuredValue>> response = ToirAPIFactory.getMeasuredValueService().measuredValue(changedDate).execute();
                            List<MeasuredValue> list = response.body();
                            ReferenceUpdate.saveReferenceData(MeasuredValue.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // MeasureType
                        changedDate = ReferenceUpdate.lastChangedAsStr(MeasureType.class.getSimpleName());
                        try {
                            Response<List<MeasureType>> response = ToirAPIFactory.getMeasureTypeService().measureType(changedDate).execute();
                            List<MeasureType> list = response.body();
                            ReferenceUpdate.saveReferenceData(MeasureType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // Operation ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(Operation.class.getSimpleName());
                        try {
                            Response<List<Operation>> response = ToirAPIFactory.getOperationService().operation(changedDate).execute();
                            List<Operation> list = response.body();
                            ReferenceUpdate.saveReferenceData(Operation.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OperationStatus
                        changedDate = ReferenceUpdate.lastChangedAsStr(OperationStatus.class.getSimpleName());
                        try {
                            Response<List<OperationStatus>> response = ToirAPIFactory.getOperationStatusService().operationStatus(changedDate).execute();
                            List<OperationStatus> list = response.body();
                            ReferenceUpdate.saveReferenceData(OperationStatus.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OperationTemplate
                        changedDate = ReferenceUpdate.lastChangedAsStr(OperationTemplate.class.getSimpleName());
                        try {
                            Response<List<OperationTemplate>> response = ToirAPIFactory.getOperationTemplateService().operationTemplate(changedDate).execute();
                            List<OperationTemplate> list = response.body();
                            ReferenceUpdate.saveReferenceData(OperationTemplate.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OperationTool
                        changedDate = ReferenceUpdate.lastChangedAsStr(OperationTool.class.getSimpleName());
                        try {
                            Response<List<OperationTool>> response = ToirAPIFactory.getOperationToolService().operationTool(changedDate).execute();
                            List<OperationTool> list = response.body();
                            ReferenceUpdate.saveReferenceData(OperationTool.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OperationType
                        changedDate = ReferenceUpdate.lastChangedAsStr(OperationType.class.getSimpleName());
                        try {
                            Response<List<OperationType>> response = ToirAPIFactory.getOperationTypeService().operationType(changedDate).execute();
                            List<OperationType> list = response.body();
                            ReferenceUpdate.saveReferenceData(OperationType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OperationVerdict
                        changedDate = ReferenceUpdate.lastChangedAsStr(OperationVerdict.class.getSimpleName());
                        try {
                            Response<List<OperationVerdict>> response = ToirAPIFactory.getOperationVerdictService().operationVerdict(changedDate).execute();
                            List<OperationVerdict> list = response.body();
                            ReferenceUpdate.saveReferenceData(OperationVerdict.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OrderLevel
                        changedDate = ReferenceUpdate.lastChangedAsStr(OrderLevel.class.getSimpleName());
                        try {
                            Response<List<OrderLevel>> response = ToirAPIFactory.getOrderLevelService().orderLevel(changedDate).execute();
                            List<OrderLevel> list = response.body();
                            ReferenceUpdate.saveReferenceData(OrderLevel.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // Orders ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(Orders.class.getSimpleName());
                        try {
                            Response<List<Orders>> response = ToirAPIFactory.getOrdersService().orders(changedDate).execute();
                            List<Orders> list = response.body();
                            ReferenceUpdate.saveReferenceData(Orders.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OrderStatus
                        changedDate = ReferenceUpdate.lastChangedAsStr(OrderStatus.class.getSimpleName());
                        try {
                            Response<List<OrderStatus>> response = ToirAPIFactory.getOrderStatusService().orderStatus(changedDate).execute();
                            List<OrderStatus> list = response.body();
                            ReferenceUpdate.saveReferenceData(OrderStatus.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // OrderVerdict
                        changedDate = ReferenceUpdate.lastChangedAsStr(OrderVerdict.class.getSimpleName());
                        try {
                            Response<List<OrderVerdict>> response = ToirAPIFactory.getOrderVerdictService().orderVerdict(changedDate).execute();
                            List<OrderVerdict> list = response.body();
                            ReferenceUpdate.saveReferenceData(OrderVerdict.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // RepairPart ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(RepairPart.class.getSimpleName());
                        try {
                            Response<List<RepairPart>> response = ToirAPIFactory.getRepairPartService().repairPart(changedDate).execute();
                            List<RepairPart> list = response.body();
                            ReferenceUpdate.saveReferenceData(RepairPart.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // RepairPartType ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(RepairPartType.class.getSimpleName());
                        try {
                            Response<List<RepairPartType>> response = ToirAPIFactory.getRepairPartTypeService().repairPartType(changedDate).execute();
                            List<RepairPartType> list = response.body();
                            ReferenceUpdate.saveReferenceData(RepairPartType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // Tasks ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(Tasks.class.getSimpleName());
                        try {
                            Response<List<Tasks>> response = ToirAPIFactory.getTasksService().tasks(changedDate).execute();
                            List<Tasks> list = response.body();
                            ReferenceUpdate.saveReferenceData(Tasks.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStageList ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageList.class.getSimpleName());
                        try {
                            Response<List<TaskStageList>> response = ToirAPIFactory.getTaskStageListService().taskStageList(changedDate).execute();
                            List<TaskStageList> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStageList.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStageOperationList ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageOperationList.class.getSimpleName());
                        try {
                            Response<List<TaskStageOperationList>> response = ToirAPIFactory.getTaskStageOperationListService().taskStageOperationList(changedDate).execute();
                            List<TaskStageOperationList> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStageOperationList.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStages ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStages.class.getSimpleName());
                        try {
                            Response<List<TaskStages>> response = ToirAPIFactory.getTaskStagesService().taskStages(changedDate).execute();
                            List<TaskStages> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStages.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStageStatus
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageStatus.class.getSimpleName());
                        try {
                            Response<List<TaskStageStatus>> response = ToirAPIFactory.getTaskStageStatusService().taskStageStatus(changedDate).execute();
                            List<TaskStageStatus> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStageStatus.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStageTemplate ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageTemplate.class.getSimpleName());
                        try {
                            Response<List<TaskStageTemplate>> response = ToirAPIFactory.getTaskStageTemplateService().taskStageTemplate(changedDate).execute();
                            List<TaskStageTemplate> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStageTemplate.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStageType ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageType.class.getSimpleName());
                        try {
                            Response<List<TaskStageType>> response = ToirAPIFactory.getTaskStageTypeService().taskStageType(changedDate).execute();
                            List<TaskStageType> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStageType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStageVerdict
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageVerdict.class.getSimpleName());
                        try {
                            Response<List<TaskStageVerdict>> response = ToirAPIFactory.getTaskStageVerdictService().taskStageVerdict(changedDate).execute();
                            List<TaskStageVerdict> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStageVerdict.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskStatus
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskStatus.class.getSimpleName());
                        try {
                            Response<List<TaskStatus>> response = ToirAPIFactory.getTaskStatusService().taskStatus(changedDate).execute();
                            List<TaskStatus> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskStatus.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskTemplate ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskTemplate.class.getSimpleName());
                        try {
                            Response<List<TaskTemplate>> response = ToirAPIFactory.getTaskTemplateService().taskTemplate(changedDate).execute();
                            List<TaskTemplate> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskTemplate.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskType ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskType.class.getSimpleName());
                        try {
                            Response<List<TaskType>> response = ToirAPIFactory.getTaskTypeService().taskType(changedDate).execute();
                            List<TaskType> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // TaskVerdict
                        changedDate = ReferenceUpdate.lastChangedAsStr(TaskVerdict.class.getSimpleName());
                        try {
                            Response<List<TaskVerdict>> response = ToirAPIFactory.getTaskVerdictService().taskVerdict(changedDate).execute();
                            List<TaskVerdict> list = response.body();
                            ReferenceUpdate.saveReferenceData(TaskVerdict.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // Tool ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(Tool.class.getSimpleName());
                        try {
                            Response<List<Tool>> response = ToirAPIFactory.getToolService().tool(changedDate).execute();
                            List<Tool> list = response.body();
                            ReferenceUpdate.saveReferenceData(Tool.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // ToolType ???
                        changedDate = ReferenceUpdate.lastChangedAsStr(ToolType.class.getSimpleName());
                        try {
                            Response<List<ToolType>> response = ToirAPIFactory.getToolTypeService().toolType(changedDate).execute();
                            List<ToolType> list = response.body();
                            ReferenceUpdate.saveReferenceData(ToolType.class.getSimpleName(), list, currentDate);
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                        }

                        // User ???

                        // гасим диалог обновления справочников
                        dialog.dismiss();

                    }
                });
                thread.start();

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
                case EquipmentStatusAdapter.TABLE_NAME:
                    fillListViewEquipmentStatus();
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
