package ru.toir.mobile.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import io.realm.RealmObject;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import ru.toir.mobile.AuthorizedUser;
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
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.ReferenceServiceHelper;
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
        CriticalTypeAdapter criticalTypeAdapter = new CriticalTypeAdapter(getActivity().getApplicationContext(),R.id.reference_listView, criticalType);
        contentListView.setAdapter(criticalTypeAdapter);
	}

	private void fillListViewAlertType() {
        RealmResults<AlertType> alertType;
        alertType = realmDB.where(AlertType.class).findAll();
        AlertTypeAdapter alertTypeAdapter = new AlertTypeAdapter(getActivity().getApplicationContext(),R.id.reference_listView, alertType);
        contentListView.setAdapter(alertTypeAdapter);
	}

	private void fillListViewOperationStatus() {
        RealmResults<OperationStatus> operationStatus;
        operationStatus = realmDB.where(OperationStatus.class).findAll();
        OperationStatusAdapter operationAdapter = new OperationStatusAdapter(getActivity().getApplicationContext(),R.id.reference_listView, operationStatus);
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
        OperationTypeAdapter operationAdapter = new OperationTypeAdapter(getActivity().getApplicationContext(),R.id.reference_listView, operationType);
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
        EquipmentStatusAdapter equipmentAdapter = new EquipmentStatusAdapter(getActivity().getApplicationContext(),R.id.reference_listView, equipmentStatuses);
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
                String bearer = AuthorizedUser.getInstance().getBearer();
                final ProgressDialog dialog = new ProgressDialog(getActivity());


//				ReferenceServiceHelper rsh = new ReferenceServiceHelper(getActivity().getApplicationContext(), ToirAPIFactory.Actions.ACTION_GET_ALL_REFERENCE);
//				getActivity().registerReceiver(mReceiverGetReference, mFilterGetReference);
//				rsh.getAll();

                // получаем справочники, обновляем всё несмотря на то что часть данных будет дублироваться
                final Date currentDate = new Date();
                String changedDate;

                // TODO: нужен механизм проверки получения данных по всем справочникам для "отключения" диалога!!!
                // AlertType
                changedDate = ReferenceUpdate.lastChangedAsStr(AlertType.class.getSimpleName());
                ToirAPIFactory.getAlertTypeService().alertType(bearer, changedDate)
                        .enqueue(new Callback<List<AlertType>>() {
                            @Override
                            public void onResponse(Response<List<AlertType>> response, Retrofit retrofit) {
                                List<AlertType> list = response.body();
                                saveReferenceData(AlertType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                        dialog.dismiss();
                    }
                        });

                // Clients
                changedDate = ReferenceUpdate.lastChangedAsStr(Clients.class.getSimpleName());
                ToirAPIFactory.getClientsService().clients(bearer, changedDate)
                        .enqueue(new Callback<List<Clients>>() {
                            @Override
                            public void onResponse(Response<List<Clients>> response, Retrofit retrofit) {
                                List<Clients> list = response.body();
                                saveReferenceData(AlertType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // CriticalType
                changedDate = ReferenceUpdate.lastChangedAsStr(CriticalType.class.getSimpleName());
                ToirAPIFactory.getCriticalTypeService().criticalType(bearer, changedDate)
                        .enqueue(new Callback<List<CriticalType>>() {
                            @Override
                            public void onResponse(Response<List<CriticalType>> response, Retrofit retrofit) {
                                List<CriticalType> list = response.body();
                                saveReferenceData(CriticalType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // Documentation
                // нужно ли вообще таким образом обновлять этот справочник???
                changedDate = ReferenceUpdate.lastChangedAsStr(Documentation.class.getSimpleName());
                ToirAPIFactory.getDocumentationService().documentation(bearer, changedDate)
                        .enqueue(new Callback<List<Documentation>>() {
                            @Override
                            public void onResponse(Response<List<Documentation>> response, Retrofit retrofit) {
                                List<Documentation> list = response.body();
                                saveReferenceData(Documentation.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // DocumentationType
                changedDate = ReferenceUpdate.lastChangedAsStr(DocumentationType.class.getSimpleName());
                ToirAPIFactory.getDocumentationTypeService().documentationType(bearer, changedDate)
                        .enqueue(new Callback<List<DocumentationType>>() {
                            @Override
                            public void onResponse(Response<List<DocumentationType>> response, Retrofit retrofit) {
                                List<DocumentationType> list = response.body();
                                saveReferenceData(DocumentationType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // Equipment
                changedDate = ReferenceUpdate.lastChangedAsStr(Equipment.class.getSimpleName());
                ToirAPIFactory.getEquipmentService().equipment(bearer, changedDate)
                        .enqueue(new Callback<List<Equipment>>() {
                            @Override
                            public void onResponse(Response<List<Equipment>> response, Retrofit retrofit) {
                                List<Equipment> list = response.body();
                                saveReferenceData(Equipment.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // EquipmentModel
                changedDate = ReferenceUpdate.lastChangedAsStr(EquipmentModel.class.getSimpleName());
                ToirAPIFactory.getEquipmentModelService().equipmentModel(bearer, changedDate)
                        .enqueue(new Callback<List<EquipmentModel>>() {
                            @Override
                            public void onResponse(Response<List<EquipmentModel>> response, Retrofit retrofit) {
                                List<EquipmentModel> list = response.body();
                                saveReferenceData(EquipmentModel.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // EquipmentStatus
                changedDate = ReferenceUpdate.lastChangedAsStr(EquipmentStatus.class.getSimpleName());
                ToirAPIFactory.getEquipmentStatusService().equipmentStatus(bearer, changedDate)
                        .enqueue(new Callback<List<EquipmentStatus>>() {
                            @Override
                            public void onResponse(Response<List<EquipmentStatus>> response, Retrofit retrofit) {
                                List<EquipmentStatus> list = response.body();
                                saveReferenceData(EquipmentStatus.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // EquipmentType
                changedDate = ReferenceUpdate.lastChangedAsStr(EquipmentType.class.getSimpleName());
                ToirAPIFactory.getEquipmentTypeService().equipmentType(bearer, changedDate)
                        .enqueue(new Callback<List<EquipmentType>>() {
                            @Override
                            public void onResponse(Response<List<EquipmentType>> response, Retrofit retrofit) {
                                List<EquipmentType> list = response.body();
                                saveReferenceData(EquipmentType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // GpdTrack ???
                // Journal ???

                // MeasuredValue
                changedDate = ReferenceUpdate.lastChangedAsStr(MeasuredValue.class.getSimpleName());
                ToirAPIFactory.getMeasuredValueService().measuredValue(bearer, changedDate)
                        .enqueue(new Callback<List<MeasuredValue>>() {
                            @Override
                            public void onResponse(Response<List<MeasuredValue>> response, Retrofit retrofit) {
                                List<MeasuredValue> list = response.body();
                                saveReferenceData(MeasuredValue.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // MeasureType
                changedDate = ReferenceUpdate.lastChangedAsStr(MeasureType.class.getSimpleName());
                ToirAPIFactory.getMeasureTypeService().measureType(bearer, changedDate)
                        .enqueue(new Callback<List<MeasureType>>() {
                            @Override
                            public void onResponse(Response<List<MeasureType>> response, Retrofit retrofit) {
                                List<MeasureType> list = response.body();
                                saveReferenceData(MeasureType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // Operation
                changedDate = ReferenceUpdate.lastChangedAsStr(Operation.class.getSimpleName());
                ToirAPIFactory.getOperationService().operation(bearer, changedDate)
                .enqueue(new Callback<List<Operation>>() {
                    @Override
                    public void onResponse(Response<List<Operation>> response, Retrofit retrofit) {
                        List<Operation> list = response.body();
                        saveReferenceData(Operation.class.getSimpleName(), list, currentDate);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        dialog.dismiss();
                    }
                });

                // OperationStatus
                changedDate = ReferenceUpdate.lastChangedAsStr(OperationStatus.class.getSimpleName());
                ToirAPIFactory.getOperationStatusService().operationStatus(bearer, changedDate)
                        .enqueue(new Callback<List<OperationStatus>>() {
                            @Override
                            public void onResponse(Response<List<OperationStatus>> response, Retrofit retrofit) {
                                List<OperationStatus> list = response.body();
                                saveReferenceData(OperationStatus.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // OperationTemplate
                changedDate = ReferenceUpdate.lastChangedAsStr(OperationTemplate.class.getSimpleName());
                ToirAPIFactory.getOperationTemplateService().operationTemplate(bearer, changedDate)
                        .enqueue(new Callback<List<OperationTemplate>>() {
                            @Override
                            public void onResponse(Response<List<OperationTemplate>> response, Retrofit retrofit) {
                                List<OperationTemplate> list = response.body();
                                saveReferenceData(OperationTemplate.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // OperationTool
                changedDate = ReferenceUpdate.lastChangedAsStr(OperationTool.class.getSimpleName());
                ToirAPIFactory.getOperationToolService().operationTool(bearer, changedDate)
                        .enqueue(new Callback<List<OperationTool>>() {
                            @Override
                            public void onResponse(Response<List<OperationTool>> response, Retrofit retrofit) {
                                List<OperationTool> list = response.body();
                                saveReferenceData(OperationTool.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // OperationType
                changedDate = ReferenceUpdate.lastChangedAsStr(OperationType.class.getSimpleName());
                ToirAPIFactory.getOperationTypeService().operationType(bearer, changedDate)
                        .enqueue(new Callback<List<OperationType>>() {
                            @Override
                            public void onResponse(Response<List<OperationType>> response, Retrofit retrofit) {
                                List<OperationType> list = response.body();
                                saveReferenceData(OperationType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // OperationVerdict
                changedDate = ReferenceUpdate.lastChangedAsStr(OperationVerdict.class.getSimpleName());
                ToirAPIFactory.getOperationVerdictService().operationVerdict(bearer, changedDate)
                        .enqueue(new Callback<List<OperationVerdict>>() {
                            @Override
                            public void onResponse(Response<List<OperationVerdict>> response, Retrofit retrofit) {
                                List<OperationVerdict> list = response.body();
                                saveReferenceData(OperationVerdict.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // OrderLevel
                changedDate = ReferenceUpdate.lastChangedAsStr(OrderLevel.class.getSimpleName());
                ToirAPIFactory.getOrderLevelService().orderLevel(bearer, changedDate)
                        .enqueue(new Callback<List<OrderLevel>>() {
                            @Override
                            public void onResponse(Response<List<OrderLevel>> response, Retrofit retrofit) {
                                List<OrderLevel> list = response.body();
                                saveReferenceData(OrderLevel.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // Orders
                changedDate = ReferenceUpdate.lastChangedAsStr(Orders.class.getSimpleName());
                ToirAPIFactory.getOrdersService().orders(bearer, changedDate)
                        .enqueue(new Callback<List<Orders>>() {
                            @Override
                            public void onResponse(Response<List<Orders>> response, Retrofit retrofit) {
                                List<Orders> list = response.body();
                                saveReferenceData(Orders.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // OrderStatus
                changedDate = ReferenceUpdate.lastChangedAsStr(OrderStatus.class.getSimpleName());
                ToirAPIFactory.getOrderStatusService().orderStatus(bearer, changedDate)
                        .enqueue(new Callback<List<OrderStatus>>() {
                            @Override
                            public void onResponse(Response<List<OrderStatus>> response, Retrofit retrofit) {
                                List<OrderStatus> list = response.body();
                                saveReferenceData(OrderStatus.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // OrderVerdict
                changedDate = ReferenceUpdate.lastChangedAsStr(OrderVerdict.class.getSimpleName());
                ToirAPIFactory.getOrderVerdictService().orderVerdict(bearer, changedDate)
                        .enqueue(new Callback<List<OrderVerdict>>() {
                            @Override
                            public void onResponse(Response<List<OrderVerdict>> response, Retrofit retrofit) {
                                List<OrderVerdict> list = response.body();
                                saveReferenceData(OrderVerdict.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // RepairPart
                changedDate = ReferenceUpdate.lastChangedAsStr(RepairPart.class.getSimpleName());
                ToirAPIFactory.getRepairPartService().repairPart(bearer, changedDate)
                        .enqueue(new Callback<List<RepairPart>>() {
                            @Override
                            public void onResponse(Response<List<RepairPart>> response, Retrofit retrofit) {
                                List<RepairPart> list = response.body();
                                saveReferenceData(RepairPart.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // RepairPartType
                changedDate = ReferenceUpdate.lastChangedAsStr(RepairPartType.class.getSimpleName());
                ToirAPIFactory.getRepairPartTypeService().repairPartType(bearer, changedDate)
                        .enqueue(new Callback<List<RepairPartType>>() {
                            @Override
                            public void onResponse(Response<List<RepairPartType>> response, Retrofit retrofit) {
                                List<RepairPartType> list = response.body();
                                saveReferenceData(RepairPartType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // Tasks
                changedDate = ReferenceUpdate.lastChangedAsStr(Tasks.class.getSimpleName());
                ToirAPIFactory.getTasksService().tasks(bearer, changedDate)
                        .enqueue(new Callback<List<Tasks>>() {
                            @Override
                            public void onResponse(Response<List<Tasks>> response, Retrofit retrofit) {
                                List<Tasks> list = response.body();
                                saveReferenceData(Tasks.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStageList
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageList.class.getSimpleName());
                ToirAPIFactory.getTaskStageListService().taskStageList(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStageList>>() {
                            @Override
                            public void onResponse(Response<List<TaskStageList>> response, Retrofit retrofit) {
                                List<TaskStageList> list = response.body();
                                saveReferenceData(TaskStageList.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStageOperationList
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageOperationList.class.getSimpleName());
                ToirAPIFactory.getTaskStageOperationListService().taskStageOperationList(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStageOperationList>>() {
                            @Override
                            public void onResponse(Response<List<TaskStageOperationList>> response, Retrofit retrofit) {
                                List<TaskStageOperationList> list = response.body();
                                saveReferenceData(TaskStageOperationList.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStages
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStages.class.getSimpleName());
                ToirAPIFactory.getTaskStagesService().taskStages(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStages>>() {
                            @Override
                            public void onResponse(Response<List<TaskStages>> response, Retrofit retrofit) {
                                List<TaskStages> list = response.body();
                                saveReferenceData(TaskStages.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStageStatus
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageStatus.class.getSimpleName());
                ToirAPIFactory.getTaskStageStatusService().taskStageStatus(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStageStatus>>() {
                            @Override
                            public void onResponse(Response<List<TaskStageStatus>> response, Retrofit retrofit) {
                                List<TaskStageStatus> list = response.body();
                                saveReferenceData(TaskStageStatus.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStageTemplate
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageTemplate.class.getSimpleName());
                ToirAPIFactory.getTaskStageTemplateService().taskStageTemplate(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStageTemplate>>() {
                            @Override
                            public void onResponse(Response<List<TaskStageTemplate>> response, Retrofit retrofit) {
                                List<TaskStageTemplate> list = response.body();
                                saveReferenceData(TaskStageTemplate.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStageType
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageType.class.getSimpleName());
                ToirAPIFactory.getTaskStageTypeService().taskStageType(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStageType>>() {
                            @Override
                            public void onResponse(Response<List<TaskStageType>> response, Retrofit retrofit) {
                                List<TaskStageType> list = response.body();
                                saveReferenceData(TaskStageType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStageVerdict
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStageVerdict.class.getSimpleName());
                ToirAPIFactory.getTaskStageVerdictService().taskStageVerdict(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStageVerdict>>() {
                            @Override
                            public void onResponse(Response<List<TaskStageVerdict>> response, Retrofit retrofit) {
                                List<TaskStageVerdict> list = response.body();
                                saveReferenceData(TaskStageVerdict.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskStatus
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskStatus.class.getSimpleName());
                ToirAPIFactory.getTaskStatusService().taskStatus(bearer, changedDate)
                        .enqueue(new Callback<List<TaskStatus>>() {
                            @Override
                            public void onResponse(Response<List<TaskStatus>> response, Retrofit retrofit) {
                                List<TaskStatus> list = response.body();
                                saveReferenceData(TaskStatus.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskTemplate
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskTemplate.class.getSimpleName());
                ToirAPIFactory.getTaskTemplateService().taskTemplate(bearer, changedDate)
                        .enqueue(new Callback<List<TaskTemplate>>() {
                            @Override
                            public void onResponse(Response<List<TaskTemplate>> response, Retrofit retrofit) {
                                List<TaskTemplate> list = response.body();
                                saveReferenceData(TaskTemplate.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskType
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskType.class.getSimpleName());
                ToirAPIFactory.getTaskTypeService().taskType(bearer, changedDate)
                        .enqueue(new Callback<List<TaskType>>() {
                            @Override
                            public void onResponse(Response<List<TaskType>> response, Retrofit retrofit) {
                                List<TaskType> list = response.body();
                                saveReferenceData(TaskType.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // TaskVerdict
                changedDate = ReferenceUpdate.lastChangedAsStr(TaskVerdict.class.getSimpleName());
                ToirAPIFactory.getTaskVerdictService().taskVerdict(bearer, changedDate)
                        .enqueue(new Callback<List<TaskVerdict>>() {
                            @Override
                            public void onResponse(Response<List<TaskVerdict>> response, Retrofit retrofit) {
                                List<TaskVerdict> list = response.body();
                                saveReferenceData(TaskVerdict.class.getSimpleName(), list, currentDate);
                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                dialog.dismiss();
                            }
                        });

                // Tool
                // ToolType

                // User ???



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
     * @param referenceName
     * @param list
     * @param updateDate
     */
    private void saveReferenceData(String referenceName, List list, Date updateDate) {
        Realm realm = Realm.getDefaultInstance();

        ReferenceUpdate item = new ReferenceUpdate();
        item.setReferenceName(referenceName);
        item.setUpdateDate(updateDate);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(list);
        realm.copyToRealmOrUpdate(item);
        realm.commitTransaction();
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
