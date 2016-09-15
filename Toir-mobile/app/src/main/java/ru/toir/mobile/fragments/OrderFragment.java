package ru.toir.mobile.fragments;

import android.app.AlertDialog;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.OperationActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.OperationAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.OrderAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStageAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.TaskServiceHelper;
import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.serverapi.result.EquipmentOperationRes;
import ru.toir.mobile.serverapi.result.TaskRes;

//import ru.toir.mobile.db.tables.OperationStatus;

public class OrderFragment extends Fragment {
    OrderAdapter orderAdapter;
    TaskAdapter taskAdapter;
    TaskStageAdapter taskStageAdapter;
	private String TAG = "OrderFragment";
    private Realm realmDB;
    private View rootView;
	private int Level = 0;
	private String currentOrderUuid = "";
    private String currentTaskUuid = "";
    private String currentTaskStageUuid = "";
    private String currentOperationUuid = "";
	private Spinner referenceSpinner;
	private Spinner typeSpinner;
	private ListView mainListView;
	private SimpleCursorAdapter operationAdapter;
	private ListViewClickListener mainListViewClickListener = new ListViewClickListener();
	private ListViewLongClickListener mainListViewLongClickListener = new ListViewLongClickListener();
	private ReferenceSpinnerListener filterSpinnerListener = new ReferenceSpinnerListener();
	private ArrayAdapter<OperationType> operationTypeAdapter;
	private ArrayAdapter<CriticalType> criticalTypeAdapter;
	private ArrayAdapter<OrderStatus> orderStatusAdapter;
	private ArrayAdapter<SortField> sortFieldAdapter;

	private ProgressDialog processDialog;
	private RfidDialog rfidDialog;
    // фильтр для получения сообщений при получении нарядов с сервера
	private IntentFilter mFilterGetTask = new IntentFilter(
			TaskServiceProvider.Actions.ACTION_GET_TASK);
	// TODO решить нужны ли фильтры на все возможные варианты отправки
	// состояния/результатов
	// фильтр для получения сообщений при получении нарядов с сервера
	private IntentFilter mFilterSendTask = new IntentFilter(
			TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);

    public static OrderFragment newInstance() {
        return (new OrderFragment());
    }	private BroadcastReceiver mReceiverGetTask = new BroadcastReceiver() {
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

						// ообщаем количество полученных нарядов
						int count = bundle
								.getInt(TaskServiceProvider.Methods.RESULT_GET_TASK_COUNT);
						if (count > 0) {
							Toast.makeText(getActivity(),
									"Количество нарядов " + count,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getActivity(), "Нарядов нет.",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						// сообщаем описание неудачи
						String message = bundle
								.getString(IServiceProvider.MESSAGE);
						Toast.makeText(getActivity(),
								"Ошибка при получении нарядов." + message,
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

		rootView = inflater.inflate(R.layout.orders_layout, container,
				false);
        realmDB = Realm.getDefaultInstance();

		mainListView = (ListView) rootView
				.findViewById(R.id.ol_orders_list_view);

		referenceSpinner = (Spinner) rootView
				.findViewById(R.id.ol_reference_spinner);
		referenceSpinner.setOnItemSelectedListener(filterSpinnerListener);

		typeSpinner = (Spinner) rootView.findViewById(R.id.ol_type_spinner);
		typeSpinner.setOnItemSelectedListener(filterSpinnerListener);

		setHasOptionsMenu(true);
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		rootView.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					Log.d(TAG, "OrderFragment !!! back pressed!!!");
					if (Level == 1) {
						initView();
					}
					return true;
				}
				return false;
			}
		});

		// создаём "пустой" адаптер для отображения операций над оборудованием
        // !! адаптер уехал в адаптер

		// адаптеры для выпадающих списков по типу содержимого
        /*
		operationTypeAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<OperationType>());

		criticalTypeAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<CriticalType>());

		taskStatusAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<TaskStatus>());

		sortFieldAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<SortField>());
        */
		// так как обработчики пока одни на всё, ставим их один раз
		mainListView.setOnItemClickListener(mainListViewClickListener);
		mainListView.setOnItemLongClickListener(mainListViewLongClickListener);

		mainListView.setLongClickable(true);

		initView();

		return rootView;
	}

	private void initView() {

		Level = 0;
		fillListViewOrders(null, null);
		fillSpinnersOrders();

	}	private BroadcastReceiver mReceiverSendTaskResult = new BroadcastReceiver() {
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

    // Orders --------------------------------------------------------------------------------------
	private void fillSpinnersOrders() {
		//TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
		//		new ToirDatabaseContext(getActivity().getApplicationContext()));
		//ArrayList<TaskStatus> taskStatusList = taskStatusDBAdapter
		//		.getAllItems();
        RealmResults<OrderStatus> orderStatus = realmDB.where(OrderStatus.class).findAll();
        ArrayAdapter<OrderStatus> statusSpinnerAdapter;
        statusSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<OrderStatus>());

        statusSpinnerAdapter.clear();
        statusSpinnerAdapter.addAll(orderStatus);
        statusSpinnerAdapter.notifyDataSetChanged();

		sortFieldAdapter.clear();
		sortFieldAdapter.add(new SortField("Сортировка", null));
		sortFieldAdapter.add(new SortField("По дате начала", "startDate"));
		sortFieldAdapter.add(new SortField("По дате получения", "recieveDate"));
		sortFieldAdapter.add(new SortField("По дате отправки", "attemptSendDate"));
        // TODO глупо сортировать просто по UUID
		sortFieldAdapter.add(new SortField("По статусу", "orderStatusUuid"));
		typeSpinner.setAdapter(sortFieldAdapter);

	}

	private void fillListViewOrders(String orderStatus, String orderByField) {
        User user = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!",
                    Toast.LENGTH_SHORT).show();
        } else {
            RealmResults<Orders> orders;
            if (orderStatus!=null) {
                if (orderByField!=null)
                    orders = realmDB.where(Orders.class).equalTo("orderStatusUuid", orderStatus).findAllSorted(orderByField);
                else
                    orders = realmDB.where(Orders.class).equalTo("orderStatusUuid", orderStatus).findAll();
            }
            else {
                if (orderByField!=null)
                    orders = realmDB.where(Orders.class).findAllSorted(orderByField);
                else
                    orders = realmDB.where(Orders.class).findAll();
            }
            orderAdapter = new OrderAdapter(getContext(),R.id.ol_orders_list_view, orders);
            mainListView.setAdapter(orderAdapter);
		}
	}

    // Tasks----------------------------------------------------------------------------------------
    private void fillListViewTasks(String orderUuid) {
       RealmResults<Tasks> tasks;
       mainListView = (ListView) rootView
                .findViewById(R.id.tl_tasks_list_view);

       tasks = realmDB.where(Tasks.class).equalTo("orderUuid", orderUuid).findAllSorted("startDate");
       TaskAdapter taskAdapter = new TaskAdapter(getContext(),R.id.tl_tasks_list_view, tasks);
       mainListView.setAdapter(taskAdapter);
    }

    // Orders----------------------------------------------------------------------------------------
    private void fillListViewTaskStage(String taskUuid) {
        RealmResults<TaskStages> taskStages;
        taskStages = realmDB.where(TaskStages.class).equalTo("taskUuid", taskUuid).findAllSorted("startDate");
        TaskStageAdapter taskStageAdapter = new TaskStageAdapter(getContext(),R.id.tsl_taskstage_list_view, taskStages);
        mainListView.setAdapter(taskStageAdapter);
    }

    // Operations----------------------------------------------------------------------------------------
    private void fillListViewOperations(String taskStageUuid) {
        RealmResults<Operation> operations;
        mainListView = (ListView) rootView
                .findViewById(R.id.op_operation_list_view);
        operations = realmDB.where(Operation.class).equalTo("taskStageUuid", taskStageUuid).findAllSorted("startDate");
        OperationAdapter operationAdapter = new OperationAdapter(getContext(),R.id.op_operation_list_view, operations);
        mainListView.setAdapter(operationAdapter);
    }

	public class ListViewClickListener implements
			AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View selectedItemView,
				int position, long id) {

			Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            // Orders
            // находимся на "экране" нарядов
            if (Level == 0) {
                currentOrderUuid  = orderAdapter.getItem(position).getUuid();
                fillListViewTasks(currentOrderUuid);
                mainListView = (ListView) rootView
                        .findViewById(R.id.ol_orders_list_view);
                Level = 1;
                return;
            }
            // Tasks
			if (Level == 1) {
                currentTaskUuid = taskAdapter.getItem(position).getUuid();
                mainListView = (ListView) rootView
                        .findViewById(R.id.tsl_taskstage_list_view);
                fillListViewTaskStage(currentTaskUuid);
                Level = 2;
                return;
            }
            // TaskStage
            if (Level == 2) {
                currentTaskStageUuid = taskStageAdapter.getItem(position).getUuid();
                mainListView = (ListView) rootView
                        .findViewById(R.id.op_operation_list_view);
                fillListViewOperations(currentTaskStageUuid);
                Level = 3;
                return;
            }
            // Operation
            if (Level == 3) {
                currentOperationUuid = operationAdapter.getItem(position).getUuid();
                // TODO отмечаем операцию как завершенную ?
                //if (operationStatus.equals(OperationStatusDBAdapter.Status.NEW)	|| operationStatus.equals(OperationStatusDBAdapter.Status.IN_WORK))
                // else dialog.setMessage("Операция уже выполнена. Повторное выполнение невозможно!");
                //initOperationPattern(operationUuid, taskUuid, equipmentUuid);
                return;
            }
	}

	/**
	 * Диалог изменения статуса операции
	 *
	 * @param operationUuid - uuid операции
	 * @param taskUuid - uuid задачи
	 */
	private void closeOperationManual(final String operationUuid,
			final String taskUuid) {

		// диалог для отмены операции
		AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View myView = inflater.inflate(R.layout.operation_cancel_dialog, null);

		dialog.setView(myView);

		dialog.setTitle("Отмена операции");

		DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				Spinner spinner = (Spinner) ((AlertDialog) dialog)
						.findViewById(R.id.statusSpinner);
				OperationStatus status = (OperationStatus) spinner
						.getSelectedItem();

				// выставляем выбранный статус
                OperationAdapter operationAdapter = new OperationAdapter(getContext(),R.id.op_operation_list_view, operations);
				dbAdapter.setOperationStatus(operationUuid, status.getUuid());

				// текущие значения фильтров
				OperationType operationType = (OperationType) referenceSpinner
						.getSelectedItem();
				CriticalType criticalType = (CriticalType) typeSpinner
						.getSelectedItem();

				// обновляем содержимое курсора
				changeCursorOperations(taskUuid, operationType.getUuid(),
						criticalType.getUuid());

				// закрываем диалог
				dialog.dismiss();
			}
		};

		dialog.setPositiveButton(android.R.string.ok, okListener);

		dialog.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		// список статусов операций в выпадающем списке для выбора
		OperationStatusDBAdapter statusDBAdapter = new OperationStatusDBAdapter(
				new ToirDatabaseContext(getActivity()));
		List<OperationStatus> operationStatusList = statusDBAdapter.getItems();
		Iterator<OperationStatus> iterator = operationStatusList.iterator();
		// удаляем из списка статус "Новая", "Выполнена"
		while (iterator.hasNext()) {
			OperationStatus item = iterator.next();
			if (item.getUuid().equals(OperationStatusDBAdapter.Status.NEW)) {
				iterator.remove();
			} else if (item.getUuid().equals(
					OperationStatusDBAdapter.Status.COMPLETE)) {
				iterator.remove();
			}
		}

		ArrayAdapter<OperationStatus> adapter = new ArrayAdapter<>(
				getActivity(), android.R.layout.simple_spinner_dropdown_item,
				operationStatusList);
		Spinner statusSpinner = (Spinner) myView
				.findViewById(R.id.statusSpinner);
		statusSpinner.setAdapter(adapter);

		dialog.show();
	}

	/**
	 * Диалог ручного закрытия наряда
	 *
	 * @param taskUuid - - uuid задачи
	 */
	private void closeTaskManual(final String taskUuid) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(
				OrderFragment.this.getActivity());
		dialog.setTitle("Закрытие наряда");
		dialog.setMessage("Всем не законченным операциям будет установлен статус \"Не выполнена\""
				+ "\n" + "Закрыть наряд?");
		dialog.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						/*
						 * закрываем наряд, в зависимости от статуса выполнения
						 * операции выставляем статус наряда
						 */
						boolean complete = true;
						TaskRes task = TaskRes.load(getContext(), taskUuid);
						List<EquipmentOperationRes> operations = task
								.getEquipmentOperations();
						EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
								new ToirDatabaseContext(getContext()));

						String operationStatusUuid;
						for (EquipmentOperationRes operation : operations) {
							operationStatusUuid = operation
									.getOperation_status_uuid();
							if (operationStatusUuid
									.equals(OperationStatusDBAdapter.Status.NEW)
									|| operationStatusUuid
											.equals(OperationStatusDBAdapter.Status.IN_WORK)) {
								complete = false;
								// выставляем статус операции
								// "Не выполнена"
								operation
										.setOperation_status_uuid(OperationStatusDBAdapter.Status.UNCOMPLETE);
								operationDBAdapter.update(operation);
								break;
							}
						}

						if (complete) {
							task.setTask_status_uuid(TaskStatusDBAdapter.Status.COMPLETE);
						} else {
							task.setTask_status_uuid(TaskStatusDBAdapter.Status.UNCOMPLETE);
						}

						TaskDBAdapter adapter = new TaskDBAdapter(
								new ToirDatabaseContext(getActivity()));
						task.setClose_date(Calendar.getInstance()
								.getTimeInMillis());
						task.setUpdated(true);
						adapter.update(task);
						fillListViewTask(null, null);
						dialog.dismiss();
					}
				});

		dialog.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		dialog.show();

	}

	private void fillListViewOperation(String task_uuid,
			String operation_type_uuid, String critical_type_uuid) {

		// обновляем содержимое курсора
		changeCursorOperations(task_uuid, operation_type_uuid,
                critical_type_uuid);

		mainListView.setAdapter(operationAdapter);

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
				if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
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

		rfidDialog = new RfidDialog();
		rfidDialog.setHandler(handler);
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
		// добавляем элемент меню для получения новых нарядов
		MenuItem getTaskNew = menu.add("Получить новые наряды");
		getTaskNew
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Log.d(TAG, "Получаем новые наряды.");
						TaskServiceHelper tsh = new TaskServiceHelper(
								getActivity().getApplicationContext(),
								TaskServiceProvider.Actions.ACTION_GET_TASK);

						getActivity().registerReceiver(mReceiverGetTask,
								mFilterGetTask);

						tsh.GetTaskNew();

						// показываем диалог получения наряда
						processDialog = new ProgressDialog(getActivity());
						processDialog.setMessage("Получаем наряды");
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
										Toast.makeText(getActivity(),
												"Получение нарядов отменено",
												Toast.LENGTH_SHORT).show();
									}
								});
						processDialog.show();
						return true;
					}
				});

		// добавляем элемент меню для получения "архивных" нарядов
		MenuItem getTaskDone = menu.add("Получить сделанные наряды");
		getTaskDone
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						Log.d(TAG, "Получаем сделанные наряды.");
						TaskServiceHelper tsh = new TaskServiceHelper(
								getActivity().getApplicationContext(),
								TaskServiceProvider.Actions.ACTION_GET_TASK);

						getActivity().registerReceiver(mReceiverGetTask,
								mFilterGetTask);

						tsh.GetTaskDone();

						// показываем диалог получения наряда
						processDialog = new ProgressDialog(getActivity());
						processDialog.setMessage("Получаем наряды");
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
						List<Task> tasks;
						String currentUserUuid = AuthorizedUser.getInstance()
								.getUuid();

						// проверяем наличие не законченных нарядов
						tasks = adapter.getOrdersByUser(currentUserUuid,
								TaskStatusDBAdapter.Status.IN_WORK, "");
						if (tasks.size() > 0) {
							AlertDialog.Builder dialog = new AlertDialog.Builder(
									getContext());

							dialog.setTitle("Внимание!");
							dialog.setMessage("Есть " + tasks.size()
									+ " наряда в процессе выполнения.\n"
									+ "Отправить выполненные наряды?");
							dialog.setPositiveButton(android.R.string.ok,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											sendCompleteTask();
											dialog.dismiss();
										}
									});
							dialog.setNegativeButton(android.R.string.cancel,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											dialog.dismiss();
										}
									});
							dialog.show();
						}

						return true;
					}
				});
	}

	/**
	 * Отправка всех выполненных нарядов на сервер
	 */
	private void sendCompleteTask() {

		TaskDBAdapter adapter = new TaskDBAdapter(new ToirDatabaseContext(
				getActivity()));
		List<Task> tasks;
		String currentUserUuid = AuthorizedUser.getInstance().getUuid();

		tasks = adapter.getTaskByUserAndUpdated(currentUserUuid);

		if (tasks == null) {
			Toast.makeText(getActivity(), "Нет результатов для отправки.",
					Toast.LENGTH_SHORT);
			return;
		}

		String[] sendTaskUuids = new String[tasks.size()];
		int i = 0;
		for (Task task : tasks) {
			sendTaskUuids[i] = task.getUuid();
			// устанавливаем дату попытки отправки
			// (пока только для наряда)
			task.setAttempt_send_date(Calendar.getInstance().getTime()
					.getTime());
			adapter.replace(task);
			i++;
		}

		getActivity()
				.registerReceiver(mReceiverSendTaskResult, mFilterSendTask);

		TaskServiceHelper tsh = new TaskServiceHelper(getActivity(),
				TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
		tsh.SendTaskResult(sendTaskUuids);

		// показываем диалог отправки результатов
		processDialog = new ProgressDialog(getActivity());
		processDialog.setMessage("Отправляем результаты");
		processDialog.setIndeterminate(true);
		processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		processDialog.setCancelable(false);
		processDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().unregisterReceiver(mReceiverGetTask);
                        Toast.makeText(getActivity(),
                                "Отправка результатов отменена",
                                Toast.LENGTH_SHORT).show();
                    }
                });
		processDialog.show();
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
			OperationType operationType = (OperationType) referenceSpinner
					.getSelectedItem();
			CriticalType criticalType = (CriticalType) typeSpinner
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

	public class ListViewLongClickListener implements
			AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			Cursor cursor = (Cursor) parent.getItemAtPosition(position);

			if (Level == 1) {
				// находимся на "экране" с операциями
				String operationUuid = cursor
						.getString(cursor
								.getColumnIndex(EquipmentOperationDBAdapter.Projection.UUID));
				String taskUuid = cursor
						.getString(cursor
								.getColumnIndex(EquipmentOperationDBAdapter.Projection.TASK_UUID));

				EquipmentOperationDBAdapter operationDbAdapter = new EquipmentOperationDBAdapter(
						new ToirDatabaseContext(getActivity()));
				EquipmentOperation operation = operationDbAdapter
						.getItem(operationUuid);
				String operationStatus = operation.getOperation_status_uuid();

				// менять произвольно статус операции позволяем только если
				// статус операции "Новая" или "В работе"
				if (operationStatus.equals(OperationStatusDBAdapter.Status.NEW)
						|| operationStatus
								.equals(OperationStatusDBAdapter.Status.IN_WORK)) {

					// показываем диалог изменения статуса
					closeOperationManual(operationUuid, taskUuid);
				} else {

					// операция уже выполнена, изменить статус нельзя
					// сообщаем об этом
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							getContext());
					dialog.setTitle("Внимание!");
					dialog.setMessage("Изменить статус операции нельзя!");
					dialog.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.dismiss();
								}
							});
					dialog.show();

				}

			} else if (Level == 0) {

				// находимся на экране с нарядами
				String taskUuid = cursor.getString(cursor
						.getColumnIndex(TaskDBAdapter.Projection.UUID));

				TaskDBAdapter adapter = new TaskDBAdapter(
						new ToirDatabaseContext(getContext()));
				Task task = adapter.getItem(taskUuid);

				// проверяем статус наряда
				String taskStatusUuid = task.getTask_status_uuid();
				if (taskStatusUuid.equals(TaskStatusDBAdapter.Status.COMPLETE)
						|| taskStatusUuid
								.equals(TaskStatusDBAdapter.Status.UNCOMPLETE)) {

					// наряд уже закрыт, изменить статус нельзя
					// сообщаем об этом
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							getContext());
					dialog.setTitle("Внимание!");
					dialog.setMessage("Изменить статус наряда уже нельзя!");
					dialog.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.dismiss();
								}
							});
					dialog.show();

				} else {

					// наряд можно закрыть принудительно
					closeTaskManual(taskUuid);
				}

			}

			return true;
		}

	}

	private class ReferenceSpinnerListener implements
			AdapterView.OnItemSelectedListener {

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {

			Log.d(TAG, "reference spinner onItemSelected");
			if (Level == 0) {
				String taskStatusUuid = ((TaskStatus) referenceSpinner
						.getSelectedItem()).getUuid();

				String orderByField = ((SortField) typeSpinner
						.getSelectedItem()).getField();

				fillListViewTask(taskStatusUuid, orderByField);
			}

			if (Level == 1) {
				String operationTypeUuid = ((OperationType) referenceSpinner
						.getSelectedItem()).getUuid();

				String criticalTypeUuid = ((CriticalType) typeSpinner
						.getSelectedItem()).getUuid();

				fillListViewOperation(currentTaskUuid, operationTypeUuid,
						criticalTypeUuid);
			}
		}
	}

}



