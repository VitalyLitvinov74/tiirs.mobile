package ru.toir.mobile.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.MainActivity;
import ru.toir.mobile.OperationActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.RFIDActivity;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
//import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.OperationStatus;
//import ru.toir.mobile.db.tables.OperationPattern;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.rest.TaskServiceHelper;
import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.utils.DataUtils;
import ru.toir.mobile.db.tables.TaskStatus;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

public class TaskFragment extends Fragment {
	private int Level = 0;
	private String currentTaskEquipmentUUID = "";
	private String currentEquipmentUUID = "";
	private Spinner Spinner_references;
	private Spinner Spinner_type;
	private ListView lv;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> list2 = new ArrayList<String>();
	ArrayAdapter<String> spinner_reference_adapter;
	ArrayAdapter<String> spinner_type_adapter;
	private Button button;
	ArrayList<String> orders_uuid = new ArrayList<String>();
	ArrayList<String> equipment_uuid = new ArrayList<String>();
	ArrayList<String> tasks_status_uuid = new ArrayList<String>();
	ArrayList<String> tasks_equipment_uuid = new ArrayList<String>();
	ArrayList<String> equipment_critical_uuid = new ArrayList<String>();
	ArrayList<String> equipment_operation_uuid = new ArrayList<String>();
	private SimpleCursorAdapter operationAdapter;
	private ListviewClickListener clickListener = new ListviewClickListener();
	private ListViewLongClickListener longClickListener = new ListViewLongClickListener();
	
	private ProgressDialog getOrderDialog;
	public void cancelGetOrders() {
		if (getOrderDialog != null) {
			getOrderDialog.cancel();
		}
	}


	// private String operationUuidTag = "operation_uuid_tag";
	// private String equipmentUuidTag = "equipment_uuid_tag";

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
		spinner_type_adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), android.R.layout.simple_spinner_item,
				list);
		spinner_reference_adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), android.R.layout.simple_spinner_item,
				list2);
		Spinner_references = (Spinner) rootView
				.findViewById(R.id.tasks_spinner10);
		Spinner_type = (Spinner) rootView.findViewById(R.id.tasks_spinner11);
		
		setHasOptionsMenu(true);
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();
		rootView.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ( keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
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
		String[] operationFrom = {"equipment_title", "operation_title", "operation_status_title"};
		int[] operationTo = {R.id.equipmentItem, R.id.operationItem, R.id.opItemStatus};
		operationAdapter = new SimpleCursorAdapter(getActivity(), R.layout.equipment_operation_item, null, operationFrom, operationTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		lv.setOnItemClickListener(clickListener);
		lv.setOnItemLongClickListener(longClickListener);
		
		initView();

		return rootView;
	}

	private void initView() {
		Level = 0;
		orders_uuid.clear();
		FillListViewTasks("", "");
		FillSpinnersTasks();
	}

	private void FillSpinnersTasks() {
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();
		ArrayList<TaskStatus> taskStatusList = taskStatusDBAdapter
				.getAllItems();
		spinner_reference_adapter.clear();
		int cnt = 0;
		tasks_status_uuid.clear();
		spinner_reference_adapter.add("Все статусы");
		cnt++;
		while (cnt <= taskStatusList.size()) {
			spinner_reference_adapter.add(taskStatusList.get(cnt - 1)
					.getTitle());
			tasks_status_uuid.add(taskStatusList.get(cnt - 1).getUuid());
			cnt++;
		}

		spinner_type_adapter.clear();
		spinner_type_adapter.add("Сортировка");
		spinner_type_adapter.add("По дате создания");
		spinner_type_adapter.add("По дате получения");
		spinner_type_adapter.add("По дате изменения");
		spinner_type_adapter.add("По статусу отправки");

		Spinner_type.setOnItemSelectedListener(new SpinnerListener());
		Spinner_references.setOnItemSelectedListener(new SpinnerListener());

		spinner_reference_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_type_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner_type.setAdapter(spinner_type_adapter);
		Spinner_references.setAdapter(spinner_reference_adapter);
		taskStatusDBAdapter.close();
	}

	private void FillListViewTasks(String type, String sort) {
		String tagId = AuthorizedUser.getInstance().getTagId();
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(
				getActivity().getApplicationContext())).open();
		Users user = users.getUserByTagId(tagId);
		users.close();

		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {
			TaskDBAdapter taskDbAdapter = new TaskDBAdapter(new TOiRDatabaseContext(
					getActivity().getApplicationContext())).open();
			ArrayList<Task> ordersList = taskDbAdapter.getOrdersByUser(
					user.getUuid(), type, sort);
			TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
					new TOiRDatabaseContext(getActivity()
							.getApplicationContext())).open();

			Integer cnt = 0;
			List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
			String[] from = { "name", "descr", "img" };
			int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
			while (cnt < ordersList.size()) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("name",
						"Создан: "
								+ DataUtils.getDate(ordersList.get(cnt)
										.getCreate_date(), "dd-MM-yyyy hh:mm")
								+ " | Изменен: "
								+ DataUtils.getDate(ordersList.get(cnt)
										.getModify_date(), "dd-MM-yyyy hh:mm"));
				hm.put("descr",
						"Статус: "
								+ taskStatusDBAdapter.getNameByUUID(ordersList
										.get(cnt).getTask_status_uuid())
								+ " | Отправлялся: "
								+ DataUtils.getDate(ordersList.get(cnt)
										.getAttempt_send_date(),
										"dd-MM-yyyy hh:mm") + " [Count="
								+ ordersList.get(cnt).getAttempt_count() + "]");
				// hm.put("descr","Статус: " +
				// ordersList.get(cnt).getTask_status_uuid() + " Отправлялся: "
				// +
				// DataUtils.getDate(ordersList.get(cnt).getAttempt_send_date(),"dd-MM-yyyy hh:mm")
				// + " [Count=" + ordersList.get(cnt).getAttempt_count() + "]");
				// default
				hm.put("img", Integer.toString(R.drawable.img_status_1));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_UNCOMPLETED))
					hm.put("img", Integer.toString(R.drawable.img_status_3));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_COMPLETED))
					hm.put("img", Integer.toString(R.drawable.img_status_1));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_RECIEVED))
					hm.put("img", Integer.toString(R.drawable.img_status_5));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_CREATED))
					hm.put("img", Integer.toString(R.drawable.img_status_4));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_ARCHIVED))
					hm.put("img", Integer.toString(R.drawable.img_status_2));
				aList.add(hm);
				orders_uuid.add(cnt, ordersList.get(cnt).getUuid().toString());
				cnt++;
			}
			SimpleAdapter adapter = new SimpleAdapter(getActivity()
					.getApplicationContext(), aList, R.layout.listview, from,
					to);
			// Setting the adapter to the listView
			lv.setAdapter(adapter);
			button.setVisibility(View.INVISIBLE);
		}
	}

	private void FillEquipmentSpinners() {
		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();
		ArrayList<OperationType> operationTypeList = operationTypeDBAdapter
				.getAllItems();
		ArrayList<CriticalType> criticalTypeList = criticalTypeDBAdapter
				.getAllItems();
		spinner_reference_adapter.clear();
		spinner_type_adapter.clear();
		equipment_operation_uuid.clear();
		equipment_critical_uuid.clear();
		spinner_reference_adapter.add("Все операции");

		for (OperationType operationType : operationTypeList) {
			spinner_reference_adapter.add(operationType.getTitle());
			equipment_operation_uuid.add(operationType.getUuid());
		}

		spinner_type_adapter.add("Любая значимость");
		for (CriticalType criticalType : criticalTypeList) {
			spinner_type_adapter.add("Критичность: " + criticalType.getType());
		}

		for (OperationType operationType : operationTypeList) {
			equipment_critical_uuid.add(operationType.getUuid());
		}

		spinner_reference_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_type_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner_type.setOnItemSelectedListener(new SpinnerListener());
		Spinner_references.setOnItemSelectedListener(new SpinnerListener());
		operationTypeDBAdapter.close();
		criticalTypeDBAdapter.close();
	}

	public class ListviewClickListener implements
			AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent,
				View selectedItemView, int position, long id) {
			if (Level == 1) {
				Cursor cursor = (Cursor)parent.getItemAtPosition(position);
				initOperationPattern(cursor.getString(cursor.getColumnIndex("operation_uuid")),
						cursor.getString(cursor.getColumnIndex("task_uuid")),
						cursor.getString(cursor.getColumnIndex("equipment_uuid")));
			}
			if (Level == 0) {
				currentTaskEquipmentUUID = orders_uuid.get(position);
				FillListViewEquipment(orders_uuid.get(position), "", 0);
				FillEquipmentSpinners();
				Level = 1;
			}
		}
	}

	public class ListViewLongClickListener implements
			AdapterView.OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Cursor cursor = (Cursor)parent.getItemAtPosition(position);
			final String operation_uuid = cursor.getString(cursor.getColumnIndex("operation_uuid"));
			final String taskUuid = cursor.getString(cursor.getColumnIndex("task_uuid"));

			// диалог для отмены операции
			final Dialog dialog = new Dialog(getActivity());
			dialog.setContentView(R.layout.operation_cancel_dialog);
			dialog.setTitle("Отмена операции");
			dialog.show();
			Button cancelOK = (Button) dialog.findViewById(R.id.cancelOK);
			cancelOK.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO перед установкой статуса проверить что статус
					// операции либо "новая" либо "не выполнена", нужно уточнить
					// и видимо часть статусов оператору не должна показываться
					// например "Новая"
					View parent = (View) v.getParent();
					Spinner spinner = (Spinner) parent
							.findViewById(R.id.statusSpinner);
					OperationStatus status = (OperationStatus) spinner
							.getSelectedItem();
					// выставляем выбранный статус
					EquipmentOperationDBAdapter dbAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getActivity())).open();
					dbAdapter.setOperationStatus(operation_uuid, status.getUuid());

					// обновляем содержимое курсора
					operationAdapter.changeCursor(dbAdapter.getOperationWithInfo(taskUuid));

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

			OperationStatusDBAdapter statusDBAdapter = new OperationStatusDBAdapter(
					new TOiRDatabaseContext(getActivity())).open();
			ArrayList<OperationStatus> operationStatus = statusDBAdapter
					.getItems();
			statusDBAdapter.close();

			ArrayAdapter<OperationStatus> adapter = new ArrayAdapter<OperationStatus>(
					getActivity(), android.R.layout.simple_spinner_item,
					operationStatus);
			Spinner statusSpinner = (Spinner) dialog
					.findViewById(R.id.statusSpinner);
			statusSpinner.setAdapter(adapter);
			return true;
		}

	}

	public class SpinnerListener implements AdapterView.OnItemSelectedListener {
		boolean userSelect = false;

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			if (Level == 0) {
				TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
						new TOiRDatabaseContext(getActivity()
								.getApplicationContext())).open();
				String type = "";
				String orderBy = "";
				if (Spinner_references.getSelectedItemId() > 0)
					type = tasks_status_uuid.get((int) Spinner_references
							.getSelectedItemId());
				switch ((int) Spinner_type.getSelectedItemId()) {
				case 0:
					orderBy = "";
					break;
				case 1:
					orderBy = "create_date";
					break;
				case 2:
					orderBy = "close_date";
					break;
				case 3:
					orderBy = "modify_date";
					break;
				case 4:
					orderBy = "successfull_send";
					break;
				default:
					orderBy = "";
				}
				FillListViewTasks(type, orderBy);
				taskStatusDBAdapter.close();
			}
			if (Level == 1) {
				String operation_uuid = "";
				int critical_type = 0;
				if (Spinner_references.getSelectedItemId() > 0)
					operation_uuid = equipment_operation_uuid
							.get((int) Spinner_references.getSelectedItemId());
				critical_type = (int) Spinner_type.getSelectedItemId();
				FillListViewEquipment(currentTaskEquipmentUUID, operation_uuid,
						critical_type);
			}
		}
	}

	private void FillListViewEquipment(String task_uuid,
			String operation_type_uuid, int critical_type) {

		EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();
		/*
		int operation_type;
		EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();
		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();
		EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()))
				.open();

		ArrayList<EquipmentOperation> equipmentOperationList = eqOperationDBAdapter
				.getEquipsByOrderId(task_uuid, operation_type_uuid,
						critical_type);

		int cnt = 0;
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		tasks_equipment_uuid.clear();
		equipment_uuid.clear();
		while (cnt < equipmentOperationList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name",
					"Оборудование: "
							+ eqDBAdapter
									.getEquipsNameByUUID(equipmentOperationList
											.get(cnt).getEquipment_uuid()));
			hm.put("descr",
					"Операция: "
							+ operationTypeDBAdapter
									.getOperationTypeByUUID(equipmentOperationList
											.get(cnt).getOperation_type_uuid())
							+ " Критичность: "
							+ criticalTypeDBAdapter.getNameByUUID(eqDBAdapter
									.getCriticalByUUID(equipmentOperationList
											.get(cnt).getEquipment_uuid()))
							+ " ["
							+ DataUtils.getDate(
									equipmentOperationResultDBAdapter
											.getStartDateByUUID(equipmentOperationList
													.get(cnt)
													.getEquipment_uuid()),
									"dd-MM-yyyy hh:mm") + "]" + "STATUS=" + equipmentOperationList.get(cnt).getOperation_status_uuid());
			
			// Creation row
			operation_type = equipmentOperationResultDBAdapter
					.getOperationResultTypeByUUID(equipmentOperationList.get(
							cnt).getOperation_status_uuid());
			switch (operation_type) {
			// нужно что-то сделать
			case 1:
				hm.put("img", Integer.toString(R.drawable.img_status_4));
				break;
			case 2:
				hm.put("img", Integer.toString(R.drawable.img_status_3));
				break;
			case 3:
				hm.put("img", Integer.toString(R.drawable.img_status_1));
				break;
			default:
				hm.put("img", Integer.toString(R.drawable.img_status_1));
			}
			aList.add(hm);
			equipment_uuid.add(cnt, equipmentOperationList.get(cnt)
					.getEquipment_uuid());
			tasks_equipment_uuid.add(cnt, equipmentOperationList.get(cnt)
					.getUuid());
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		*/
		
		// обновляем содержимое курсора
		operationAdapter.changeCursor(eqOperationDBAdapter.getOperationWithInfo(task_uuid));

		// Setting the adapter to the listView
		lv.setAdapter(operationAdapter);

		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initView();
			}
		});

	}

	// init equipment operation information screen
	private void initOperationPattern(String equipment_operation_uuid,
			String task_uuid, String equipment_uuid) {
		Toast.makeText(getActivity(),
				"Проваливаемся на экран с информацией об обслуживании",
				Toast.LENGTH_SHORT).show();

		// запускаем считывание метки оборудования
		// передаём в активити считывания метки все необходимые данные для
		// запуска активити выполнения операции
		// они будут возвращены в MainActivity.onActivityResult и переданны в
		// новое активити
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(getActivity())).open();
		String equipment_tag = equipmentDBAdapter.getItem(equipment_uuid)
				.getTag_id();
		equipmentDBAdapter.close();
		Intent rfidRead = new Intent(getActivity(), RFIDActivity.class);
		Bundle bundle = new Bundle();
		// следующие параметры предаются в OperationActivity
		bundle.putString(OperationActivity.OPERATION_UUID_EXTRA, equipment_operation_uuid);
		bundle.putString(OperationActivity.TASK_UUID_EXTRA, task_uuid);
		bundle.putString(OperationActivity.EQUIPMENT_UUID_EXTRA, equipment_uuid);
		// метка предаётся в MainActivity для проверки правильности оборудования
		bundle.putString(OperationActivity.EQUIPMENT_TAG_EXTRA, equipment_tag);
		bundle.putInt("action", MainActivity.RFIDReadAction.READ_EQUIPMENT_TAG_BEFORE_OPERATION);
		rfidRead.putExtras(bundle);
		getActivity().startActivityForResult(rfidRead,
				MainActivity.RETURN_CODE_READ_RFID);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
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
				tsh.GetTask(AuthorizedUser.getInstance().getToken());
				// показываем диалог получения наряда
				getOrderDialog = new ProgressDialog(getActivity());
				getOrderDialog.setMessage("Получаем наряд");
				getOrderDialog.setIndeterminate(true);
				getOrderDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				getOrderDialog.setCancelable(false);
				getOrderDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						"Отмена", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO необходимые операции для отмены приёма нарядов
							}
						});
				getOrderDialog.show();
				return true;
			}
		});
	}

}
