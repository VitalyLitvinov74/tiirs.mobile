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
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationType;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.ReferenceUpdate;
import ru.toir.mobile.db.realm.TaskStatus;
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
                // Equipment
                // EquipmentModel
                // EquipmentStatus
                // EquipmentType
                // GpsTrack
                // Journal
                // MeasuredValue
                // MeasureType
                // Operation
                // OperationStatus
                // OperationTemplate
                // OperationTool
                // OperationType
                // OperationVerdict
                // OrderLevel
                // Orders
                // OrderStatus
                // OrderVerdict
                // ReferenceUpdate
                // RepairPart
                // RepairPartType
                // Tasks
                // TaskStageList
                // TaskStageOperationList
                // TaskStages
                // TaskStageStatus
                // TaskStageTemplate
                // TaskStageType
                // TaskStageVerdict
                // TaskStatus
                // TaskTemplate
                // TaskType
                // TaskVerdict
                // Tool
                // ToolType
                // User



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

    /**
     *
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
}
