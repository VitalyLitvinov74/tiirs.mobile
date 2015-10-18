package ru.toir.mobile.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.OperationActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.TaskServiceHelper;
import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.serverapi.result.EquipmentOperationRes;
import ru.toir.mobile.serverapi.result.TaskRes;
import ru.toir.mobile.utils.DataUtils;
import ru.toir.mobile.db.tables.TaskStatus;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

public class TaskFragment extends Fragment {

	private String TAG = "TaskFragment";
	private int Level = 0;
	private String currentTaskUuid = "";
	private Spinner Spinner_references;
	private Spinner Spinner_type;
	private ListView lv;
	private Button button;

	private SimpleCursorAdapter operationAdapter;
	private ListviewClickListener clickListener = new ListviewClickListener();
	private ListViewLongClickListener longClickListener = new ListViewLongClickListener();
	private ReferenceSpinnerListener referenceSpinnerListener = new ReferenceSpinnerListener();
	private ArrayAdapter<OperationType> operationTypeAdapter;
	private ArrayAdapter<CriticalType> criticalTypeAdapter;
	private ArrayAdapter<TaskStatus> taskStatusAdapter;
	private ArrayAdapter<SortField> sortFieldAdapter;
	private SimpleCursorAdapter taskAdapter;

	private ProgressDialog processDialog;
	private RfidDialog rfidDialog;

	// фильтр для получения сообщений при получении нарядов с сервера
	private IntentFilter mFilterGetTask = new IntentFilter(
			TaskServiceProvider.Actions.ACTION_GET_TASK);
	private BroadcastReceiver mReceiverGetTask = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int provider = intent.getIntExtra(
					ProcessorService.Extras.PROVIDER_EXTRA, 0);
			Log.d(TAG, "" + provider);
			if (provider == ProcessorService.Providers.TASK_PROVIDER) {
				int method = intent.getIntExtra(
						ProcessorService.Extras.METHOD_EXTRA, 0);
				Log.d(TAG, "" + method);
				if (method == TaskServiceProvider.Methods.GET_TASK) {
					boolean result = intent.getBooleanExtra(
							ProcessorService.Extras.RESULT_EXTRA, false);
					Bundle bundle = intent
							.getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
					Log.d(TAG, "boolean result" + result);

					if (result == true) {
						/*
						 * нужно видимо что-то дёрнуть чтоб уведомить о том что
						 * наряд(ы) получены вероятно нужно сделать попытку
						 * отправить на сервер информацию о полученых нарядах
						 * (которые изменили свой статус на "В работе")
						 */

						// сообщаем количество полученных нарядов
						int count = bundle
								.getInt(TaskServiceProvider.Methods.RESULT_GET_TASK_COUNT);
						if (count > 0) {
							Toast.makeText(getActivity(),
									"Количество новых нарядов " + count,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), "Новых нарядов нет.",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						// сообщаем описание неудачи
						String message = bundle
								.getString(IServiceProvider.MESSAGE);
						Toast.makeText(getActivity(),
								"Ошибка при получении нарядов.\r\n" + message,
								Toast.LENGTH_LONG).show();
					}

					// закрываем диалог получения наряда
					processDialog.dismiss();
					getActivity().unregisterReceiver(mReceiverGetTask);
					initView();
				}
			}

		}
	};

	// TODO решить нужны ли фильтры на все возможные варианты отправки
	// состояния/результатов
	// фильтр для получения сообщений при получении нарядов с сервера
	private IntentFilter mFilterSendTask = new IntentFilter(
			TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
	private BroadcastReceiver mReceiverSendTaskResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int provider = intent.getIntExtra(
					ProcessorService.Extras.PROVIDER_EXTRA, 0);
			Log.d(TAG, "" + provider);
			if (provider == ProcessorService.Providers.TASK_PROVIDER) {
				int method = intent.getIntExtra(
						ProcessorService.Extras.METHOD_EXTRA, 0);
				Log.d(TAG, "" + method);
				if (method == TaskServiceProvider.Methods.TASK_SEND_RESULT) {
					boolean result = intent.getBooleanExtra(
							ProcessorService.Extras.RESULT_EXTRA, false);
					Log.d(TAG, "" + result);
					if (result == true) {
						Toast.makeText(getActivity(), "Результаты отправлены.",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(),
								"Ошибка при отправке результатов.",
								Toast.LENGTH_LONG).show();
					}

					// закрываем диалог получения наряда
					processDialog.dismiss();
					getActivity().unregisterReceiver(mReceiverSendTaskResult);
				}
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.tasks_layout, container,
				false);

		lv = (ListView) rootView.findViewById(R.id.tasks_listView);

		button = (Button) rootView.findViewById(R.id.tasks_button_back);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initView();
			}
		});

		Spinner_references = (Spinner) rootView
				.findViewById(R.id.tasks_spinner10);
		Spinner_references.setOnItemSelectedListener(referenceSpinnerListener);

		Spinner_type = (Spinner) rootView.findViewById(R.id.tasks_spinner11);
		Spinner_type.setOnItemSelectedListener(referenceSpinnerListener);

		setHasOptionsMenu(true);
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		rootView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					Log.d("test", "TaskFragment !!! back pressed!!!");
					if (Level == 1) {
						initView();
					}
					return true;
				}
				return false;
			}
		});

		// создаём "пустой" адаптер для отображения операций над оборудованием
		String[] operationFrom = { EquipmentOperationDBAdapter.Projection._ID,
				EquipmentDBAdapter.Projection.TITLE,
				OperationTypeDBAdapter.Projection.TITLE,
				OperationStatusDBAdapter.Projection.TITLE,
				OperationResultDBAdapter.Projection.TITLE };
		int[] operationTo = { R.id.eoi_ImageStatus, R.id.eoi_Equipment,
				R.id.eoi_Operation, R.id.eoi_Status, R.id.eoi_ResultStatus };
		operationAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.equipment_operation_item, null, operationFrom,
				operationTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// это нужно для отображения произвольных изображений
		operationAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				int viewId = view.getId();
				if (viewId == R.id.eoi_ImageStatus) {
					// TODO добавить установку картинки в зависимости от...
					// я так и не понял от чего зависит картинка операции
					// берём значения в диапазоне от 0 до 3
					long row_id = cursor.getLong(cursor.getColumnIndex("_id"));
					int criteria = (int) (row_id % 4);
					int image_id;
					switch (criteria) {
					case 0:
						image_id = R.drawable.img_status_1;
						break;
					case 1:
						image_id = R.drawable.img_status_2;
						break;
					case 2:
						image_id = R.drawable.img_status_3;
						break;
					case 3:
						image_id = R.drawable.img_status_4;
						break;
					default:
						image_id = R.drawable.img_status_1;
					}

					((ImageView) view).setImageResource(image_id);
					return true;
				}

				return false;
			}
		});

		// создаём "пустой" адаптер для отображения нарядов
		String[] taskFrom = { TaskDBAdapter.Projection.TASK_STATUS_UUID,
				TaskDBAdapter.Projection.TASK_NAME,
				TaskDBAdapter.Projection.CREATED_AT,
				TaskStatusDBAdapter.Projection.TITLE };
		int[] taskTo = { R.id.ti_ImageStatus, R.id.ti_Name, R.id.ti_Create,
				R.id.ti_Status };
		taskAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.task_item, null, taskFrom, taskTo,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		// это нужно для отображения произвольных изображений и конвертации в
		// строку дат
		taskAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				int viewId = view.getId();

				if (viewId == R.id.ti_Name) {
					((TextView) view).setText(cursor.getString(columnIndex));
					return true;
				}

				if (viewId == R.id.ti_Create) {
					long lDate = cursor.getLong(columnIndex);
					String sDate = DataUtils.getDate(lDate, "dd.MM.yyyy HH:ss");
					((TextView) view).setText(sDate);
					return true;
				}

				// if (viewId == R.id.ti_Close) {
				// long lDate = cursor.getLong(columnIndex);
				// String sDate;
				// if (lDate != 0) {
				// sDate = DataUtils.getDate(lDate, "dd.MM.yyyy HH:ss");
				// } else {
				// sDate = "нет";
				// }
				// ((TextView) view).setText(sDate);
				// return true;
				// }

				if (viewId == R.id.ti_ImageStatus) {
					int image_id = R.drawable.img_status_3;
					String taskStatus = cursor.getString(columnIndex);

					if (taskStatus
							.equals(TaskStatusDBAdapter.STATUS_UUID_UNCOMPLETED)) {
						image_id = R.drawable.img_status_3;
					}

					if (taskStatus
							.equals(TaskStatusDBAdapter.STATUS_UUID_COMPLETED)) {
						image_id = R.drawable.img_status_1;
					}

					if (taskStatus
							.equals(TaskStatusDBAdapter.STATUS_UUID_RECIEVED)) {
						image_id = R.drawable.img_status_5;
					}

					if (taskStatus
							.equals(TaskStatusDBAdapter.STATUS_UUID_CREATED)) {
						image_id = R.drawable.img_status_4;
					}

					if (taskStatus
							.equals(TaskStatusDBAdapter.STATUS_UUID_ARCHIVED)) {
						image_id = R.drawable.img_status_2;
					}

					((ImageView) view).setImageResource(image_id);

					return true;
				}

				return false;
			}
		});

		// адаптеры для выпадающих списков по типу содержимого
		operationTypeAdapter = new ArrayAdapter<OperationType>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<OperationType>());

		criticalTypeAdapter = new ArrayAdapter<CriticalType>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<CriticalType>());

		taskStatusAdapter = new ArrayAdapter<TaskStatus>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<TaskStatus>());

		sortFieldAdapter = new ArrayAdapter<SortField>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<SortField>());

		// так как обработчики пока одни на всё, ставим их один раз
		lv.setOnItemClickListener(clickListener);
		lv.setOnItemLongClickListener(longClickListener);

		lv.setLongClickable(true);

		initView();

		return rootView;
	}

	private void initView() {

		Level = 0;
		FillListViewTasks(null, null);
		fillSpinnersTasks();

	}

	private void fillSpinnersTasks() {
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<TaskStatus> taskStatusList = taskStatusDBAdapter
				.getAllItems();

		TaskStatus allStatus = new TaskStatus();
		allStatus.setUuid(null);
		allStatus.setTitle("Все статусы");
		taskStatusList.add(0, allStatus);
		taskStatusAdapter.clear();
		taskStatusAdapter.addAll(taskStatusList);
		Spinner_references.setAdapter(taskStatusAdapter);

		sortFieldAdapter.clear();
		sortFieldAdapter.add(new SortField("Сортировка", null));
		sortFieldAdapter.add(new SortField("По дате создания",
				TaskDBAdapter.FIELD_CREATED_AT));
		sortFieldAdapter.add(new SortField("По дате получения",
				TaskDBAdapter.FIELD_CLOSE_DATE));
		sortFieldAdapter.add(new SortField("По дате изменения",
				TaskDBAdapter.FIELD_CHANGED_AT));
		sortFieldAdapter.add(new SortField("По статусу отправки",
				TaskDBAdapter.FIELD_UPDATED));
		Spinner_type.setAdapter(sortFieldAdapter);

	}

	private void FillListViewTasks(String taskStatus, String orderByField) {

		String tagId = AuthorizedUser.getInstance().getTagId();
		UsersDBAdapter users = new UsersDBAdapter(new ToirDatabaseContext(
				getActivity().getApplicationContext()));
		Users user = users.getUserByTagId(tagId);

		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {
			TaskDBAdapter taskDbAdapter = new TaskDBAdapter(
					new ToirDatabaseContext(getActivity()
							.getApplicationContext()));

			taskAdapter.changeCursor(taskDbAdapter.getTaskWithInfo(
					user.getUuid(), taskStatus, orderByField));

			// Integer cnt = 0;
			// List<HashMap<String, String>> aList = new
			// ArrayList<HashMap<String, String>>();
			// while (cnt < ordersList.size()) {
			// HashMap<String, String> hm = new HashMap<String, String>();
			// hm.put("name",
			// "Создан: "
			// + DataUtils.getDate(ordersList.get(cnt)
			// .getCreate_date(), "dd-MM-yyyy hh:mm")
			// + " | Изменен: "
			// + DataUtils.getDate(ordersList.get(cnt)
			// .getModify_date(), "dd-MM-yyyy hh:mm"));
			// hm.put("descr",
			// "Статус: "
			// + taskStatusDBAdapter.getNameByUUID(ordersList
			// .get(cnt).getTask_status_uuid())
			// + " | Отправлялся: "
			// + DataUtils.getDate(ordersList.get(cnt)
			// .getAttempt_send_date(),
			// "dd-MM-yyyy hh:mm") + " [Count="
			// + ordersList.get(cnt).getAttempt_count() + "]");
			// aList.add(hm);
			// cnt++;
			// }

			// Setting the adapter to the listView
			lv.setAdapter(taskAdapter);
			button.setVisibility(View.INVISIBLE);
		}
	}

	private void fillSpinnersEquipment() {

		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<OperationType> operationTypeList = operationTypeDBAdapter
				.getAllItems();
		ArrayList<CriticalType> criticalTypeList = criticalTypeDBAdapter
				.getAllItems();

		OperationType allOperation = new OperationType();
		allOperation.setTitle("Все операции");
		allOperation.setUuid(null);
		operationTypeList.add(0, allOperation);
		operationTypeAdapter.clear();
		operationTypeAdapter.addAll(operationTypeList);
		Spinner_references.setAdapter(operationTypeAdapter);

		CriticalType allCriticalType = new CriticalType();
		allCriticalType.setUuid(null);
		criticalTypeList.add(0, allCriticalType);
		criticalTypeAdapter.clear();
		criticalTypeAdapter.addAll(criticalTypeList);
		Spinner_type.setAdapter(criticalTypeAdapter);

	}

	public class ListviewClickListener implements
			AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View selectedItemView,
				int position, long id) {

			Cursor cursor = (Cursor) parent.getItemAtPosition(position);

			if (Level == 1) {
				initOperationPattern(
						cursor.getString(cursor
								.getColumnIndex(EquipmentOperationDBAdapter.Projection.UUID)),
						cursor.getString(cursor
								.getColumnIndex(EquipmentOperationDBAdapter.Projection.TASK_UUID)),
						cursor.getString(cursor
								.getColumnIndex(EquipmentDBAdapter.Projection.UUID)));
			}

			if (Level == 0) {
				currentTaskUuid = cursor.getString(cursor
						.getColumnIndex(TaskDBAdapter.Projection.UUID));
				FillListViewEquipment(currentTaskUuid, null, null);
				fillSpinnersEquipment();
				Level = 1;
			}
		}
	}

	public class ListViewLongClickListener implements
			AdapterView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			Cursor cursor = (Cursor) parent.getItemAtPosition(position);
			if (Level == 1) {
				final String operation_uuid = cursor
						.getString(cursor
								.getColumnIndex(EquipmentOperationDBAdapter.Projection.UUID));
				final String taskUuid = cursor
						.getString(cursor
								.getColumnIndex(EquipmentOperationDBAdapter.Projection.TASK_UUID));

				EquipmentOperationDBAdapter operationDbAdapter = new EquipmentOperationDBAdapter(
						new ToirDatabaseContext(getActivity()));
				EquipmentOperation operation = operationDbAdapter
						.getItem(operation_uuid);
				String operationStatus = operation.getOperation_status_uuid();
				// менять произвольно статус операции позволяем только если
				// статус операции "Новая"
				if (!operationStatus
						.equals(OperationStatus.Extras.STATUS_UUID_NEW)) {
					Toast.makeText(getActivity(),
							"Изменить статус операции нельзя!",
							Toast.LENGTH_SHORT).show();
					return true;
				}
				// диалог для отмены операции
				final Dialog dialog = new Dialog(getActivity());
				dialog.setContentView(R.layout.operation_cancel_dialog);
				dialog.setTitle("Отмена операции");
				dialog.show();
				Button cancelOK = (Button) dialog.findViewById(R.id.cancelOK);
				cancelOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						View parent = (View) v.getParent();
						Spinner spinner = (Spinner) parent
								.findViewById(R.id.statusSpinner);
						OperationStatus status = (OperationStatus) spinner
								.getSelectedItem();
						// выставляем выбранный статус
						EquipmentOperationDBAdapter dbAdapter = new EquipmentOperationDBAdapter(
								new ToirDatabaseContext(getActivity()));
						dbAdapter.setOperationStatus(operation_uuid,
								status.getUuid());

						// текущие значения фильтров
						OperationType operationType = (OperationType) Spinner_references
								.getSelectedItem();
						CriticalType criticalType = (CriticalType) Spinner_type
								.getSelectedItem();
						// обновляем содержимое курсора
						changeCursorOperations(taskUuid,
								operationType.getUuid(), criticalType.getUuid());

						// закрываем диалог
						dialog.dismiss();
					}
				});
				Button cancelCancel = (Button) dialog
						.findViewById(R.id.cancelCancel);
				cancelCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				// список статусов операций в выпадающем списке для выбора
				OperationStatusDBAdapter statusDBAdapter = new OperationStatusDBAdapter(
						new ToirDatabaseContext(getActivity()));
				ArrayList<OperationStatus> operationStatusList = statusDBAdapter
						.getItems();
				Iterator<OperationStatus> iterator = operationStatusList
						.iterator();
				// удаляем из списка статус "Новая", "Выполнена"
				while (iterator.hasNext()) {
					OperationStatus item = iterator.next();
					if (item.getUuid().equals(
							OperationStatus.Extras.STATUS_UUID_NEW)) {
						iterator.remove();
					} else if (item.getUuid().equals(
							OperationStatus.Extras.STATUS_UUID_COMPLETE)) {
						iterator.remove();
					}
				}

				ArrayAdapter<OperationStatus> adapter = new ArrayAdapter<OperationStatus>(
						getActivity(), android.R.layout.simple_spinner_item,
						operationStatusList);
				Spinner statusSpinner = (Spinner) dialog
						.findViewById(R.id.statusSpinner);
				statusSpinner.setAdapter(adapter);
			} else if (Level == 0) {
				final String taskUuid = cursor.getString(cursor
						.getColumnIndex(TaskDBAdapter.Projection.UUID));

				final AlertDialog.Builder dialog = new AlertDialog.Builder(
						TaskFragment.this.getActivity());
				dialog.setTitle("Закрытие наряда");
				dialog.setPositiveButton("Закрыть наряд",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// закрываем наряд
								// в зависимости от результата выполнения
								// операций выставляем
								// статус наряда
								boolean complete = true;
								TaskRes task = TaskRes.load(
										new ToirDatabaseContext(getActivity()),
										taskUuid);
								ArrayList<EquipmentOperationRes> operations = task
										.getEquipmentOperations();
								for (EquipmentOperationRes operation : operations) {
									if (operation
											.getOperation_status_uuid()
											.equals(OperationStatus.Extras.STATUS_UUID_NEW)) {
										complete = false;
										break;
									}
								}

								if (complete) {
									task.setTask_status_uuid(Task.Extras.STATUS_UUID_COMPLETE);
								} else {
									task.setTask_status_uuid(Task.Extras.STATUS_UUID_NOT_COMPLETE);
								}
								TaskDBAdapter adapter = new TaskDBAdapter(
										new ToirDatabaseContext(getActivity()));
								task.setClose_date(Calendar.getInstance()
										.getTimeInMillis());
								task.setUpdated(true);
								adapter.update(task);
								FillListViewTasks(null, null);
								dialog.dismiss();
							}
						});
				dialog.setNegativeButton("Отмена",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});

				dialog.show();
			}
			return true;
		}

	}

	public class ReferenceSpinnerListener implements
			AdapterView.OnItemSelectedListener {
		boolean userSelect = false;

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {

			Log.d("test", "reference spinner onItemSelected");
			if (Level == 0) {
				String taskStatusUuid = ((TaskStatus) Spinner_references
						.getSelectedItem()).getUuid();

				String orderByField = ((SortField) Spinner_type
						.getSelectedItem()).getField();

				FillListViewTasks(taskStatusUuid, orderByField);
			}

			if (Level == 1) {
				String operationTypeUuid = ((OperationType) Spinner_references
						.getSelectedItem()).getUuid();

				String criticalTypeUuid = ((CriticalType) Spinner_type
						.getSelectedItem()).getUuid();

				FillListViewEquipment(currentTaskUuid, operationTypeUuid,
						criticalTypeUuid);
			}
		}
	}

	private void FillListViewEquipment(String task_uuid,
			String operation_type_uuid, String critical_type_uuid) {

		// обновляем содержимое курсора
		changeCursorOperations(task_uuid, operation_type_uuid,
				critical_type_uuid);

		// Setting the adapter to the listView
		lv.setAdapter(operationAdapter);

		button.setVisibility(View.VISIBLE);

	}

	// init equipment operation information screen
	private void initOperationPattern(String equipment_operation_uuid,
			String task_uuid, String equipment_uuid) {

		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(getActivity()));
		final String equipment_tag = equipmentDBAdapter.getItem(equipment_uuid)
				.getTag_id();
		final Bundle bundle = new Bundle();
		bundle.putString(OperationActivity.OPERATION_UUID_EXTRA,
				equipment_operation_uuid);
		bundle.putString(OperationActivity.TASK_UUID_EXTRA, task_uuid);
		bundle.putString(OperationActivity.EQUIPMENT_UUID_EXTRA, equipment_uuid);

		Handler handler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				if (msg.arg1 == RfidDriverBase.RESULT_RFID_SUCCESS) {
					String tagId = (String) msg.obj;
					Log.d(TAG, "нужна: " + equipment_tag + " считали: " + tagId);
					Intent operationActivity = new Intent(getActivity(),
							OperationActivity.class);
					if (equipment_tag.equals(tagId)) {
						operationActivity.putExtras(bundle);
						startActivity(operationActivity);
					} else {
						Toast.makeText(getActivity(),
								"Не верное оборудование!!!", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					Toast.makeText(getActivity(), "Ошибка чтения метки.",
							Toast.LENGTH_SHORT).show();
				}
				rfidDialog.dismiss();
				return true;
			}
		});

		rfidDialog = new RfidDialog(getActivity().getApplicationContext(),
				handler);
		rfidDialog.readTagId();
		rfidDialog.show(getActivity().getFragmentManager(), "aaaa");
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
		// добавляем элемент меню для получения наряда
		MenuItem getTask = menu.add("Получить наряды");
		getTask.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.d("test", "Получаем наряд.");
				TaskServiceHelper tsh = new TaskServiceHelper(getActivity()
						.getApplicationContext(),
						TaskServiceProvider.Actions.ACTION_GET_TASK);

				getActivity()
						.registerReceiver(mReceiverGetTask, mFilterGetTask);

				tsh.GetTask();

				// показываем диалог получения наряда
				processDialog = new ProgressDialog(getActivity());
				processDialog.setMessage("Получаем наряд");
				processDialog.setIndeterminate(true);
				processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				processDialog.setCancelable(false);
				processDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						"Отмена", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getActivity().unregisterReceiver(
										mReceiverGetTask);
								Toast.makeText(getActivity(),
										"Получение нарядов отменено",
										Toast.LENGTH_SHORT).show();
							}
						});
				processDialog.show();
				return true;
			}
		});

		// добавляем элемент меню для отправки результатов выполнения нарядов
		MenuItem sendTaskResultMenu = menu.add("Отправить результаты");
		sendTaskResultMenu
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {

						TaskDBAdapter adapter = new TaskDBAdapter(
								new ToirDatabaseContext(getActivity()));
						ArrayList<Task> tasks = adapter
								.getTaskByUserAndUpdated(AuthorizedUser
										.getInstance().getUuid());
						if (tasks == null) {
							Toast.makeText(getActivity(),
									"Нет результатов для отправки.",
									Toast.LENGTH_SHORT);
							return true;
						}

						String[] sendTaskUuids = new String[tasks.size()];
						int i = 0;
						for (Task task : tasks) {
							sendTaskUuids[i] = task.getUuid();
							// устанавливаем дату попытки отправки
							// (пока только для наряда)
							task.setAttempt_send_date(Calendar.getInstance()
									.getTime().getTime());
							adapter.replace(task);
							i++;
						}

						getActivity().registerReceiver(mReceiverSendTaskResult,
								mFilterSendTask);

						TaskServiceHelper tsh = new TaskServiceHelper(
								getActivity(),
								TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
						tsh.SendTaskResult(sendTaskUuids);

						// показываем диалог отправки результатов
						processDialog = new ProgressDialog(getActivity());
						processDialog.setMessage("Отправляем результаты");
						processDialog.setIndeterminate(true);
						processDialog
								.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						processDialog.setCancelable(false);
						processDialog.setButton(
								DialogInterface.BUTTON_NEGATIVE, "Отмена",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										getActivity().unregisterReceiver(
												mReceiverGetTask);
										Toast.makeText(
												getActivity(),
												"Отправка результатов отменена",
												Toast.LENGTH_SHORT).show();
									}
								});
						processDialog.show();
						return true;
					}
				});
	}

	/**
	 * 
	 * @author Dmitriy Logachov
	 * 
	 *         Класс для представления элемента выпадающего списка сортировки по
	 *         полю в базе
	 */
	class SortField {
		private String Title;
		private String Field;

		public SortField(String title, String field) {
			Title = title;
			Field = field;
		}

		public String getTitle() {
			return Title;
		}

		public void setTitle(String title) {
			Title = title;
		}

		public String getField() {
			return Field;
		}

		public void setField(String field) {
			Field = field;
		}

		public String toString() {
			return Title;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		// сделано для того чтобы по возвращению из activity выполнения
		// операции, обновить отображение обновившехся данных
		// текущие значения фильтров
		if (Level == 1) {
			OperationType operationType = (OperationType) Spinner_references
					.getSelectedItem();
			CriticalType criticalType = (CriticalType) Spinner_type
					.getSelectedItem();
			changeCursorOperations(currentTaskUuid, operationType.getUuid(),
					criticalType.getUuid());
		}

	}

	private void changeCursorOperations(String taskUuid,
			String operationTypeUuid, String criticalTypeUuid) {
		EquipmentOperationDBAdapter dbAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(getActivity()));
		// обновляем содержимое курсора
		operationAdapter.changeCursor(dbAdapter.getOperationWithInfo(taskUuid,
				operationTypeUuid, criticalTypeUuid));
	}

}
