package ru.toir.mobile.fragments;

import java.util.ArrayList;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.MainActivity;
import ru.toir.mobile.OperationActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.RFIDActivity;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.rest.TaskServiceHelper;
import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.utils.DataUtils;
import ru.toir.mobile.db.tables.TaskStatus;
import android.app.Dialog;
import android.app.ProgressDialog;
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

	private ProgressDialog getOrderDialog;

	private String dateFormat = "dd.MM.yyyy hh:mm";

	public void cancelGetOrders() {
		if (getOrderDialog != null) {
			getOrderDialog.cancel();
		}
	}
	
	public void refreshTaskList() {
		initView();
	}

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
		String[] operationFrom = { "_id", "equipment_title", "operation_title",
				"operation_status_title" };
		int[] operationTo = { R.id.eoi_ImageStatus, R.id.eoi_Equipment,
				R.id.eoi_Operation, R.id.eoi_Status };
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
		String[] taskFrom = { "_id", "task_name", "create_date" };
		int[] taskTo = { R.id.ti_ImageStatus, R.id.ti_Name, R.id.ti_Create };
		taskAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.task_item, null, taskFrom, taskTo,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		// это нужно для отображения произвольных изображений и конвертации в строку дат
		taskAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				int viewId = view.getId();

				if (viewId == R.id.ti_Name) {
					((TextView) view).setText(cursor.getString(cursor
							.getColumnIndex(TaskDBAdapter.FIELD_TASK_NAME_NAME)));
					return true;
				}

				if (viewId == R.id.ti_Create) {
					((TextView) view).setText(DataUtils.getDate(
							cursor.getLong(columnIndex), dateFormat));
					return true;
				}

				if (viewId == R.id.ti_ImageStatus) {
					int image_id = R.drawable.img_status_3;
					String taskStatus = cursor.getString(cursor
							.getColumnIndex(TaskDBAdapter.FIELD_TASK_STATUS_UUID_NAME));

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
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<TaskStatus> taskStatusList = taskStatusDBAdapter
				.getAllItems();

		taskStatusList.add(0, new TaskStatus(0, null, "Все статусы", 0, 0));
		taskStatusAdapter.clear();
		taskStatusAdapter.addAll(taskStatusList);
		Spinner_references.setAdapter(taskStatusAdapter);

		sortFieldAdapter.clear();
		sortFieldAdapter.add(new SortField("Сортировка", null));
		sortFieldAdapter.add(new SortField("По дате создания",
				TaskDBAdapter.FIELD_CREATE_DATE_NAME));
		sortFieldAdapter.add(new SortField("По дате получения",
				TaskDBAdapter.FIELD_CLOSE_DATE_NAME));
		sortFieldAdapter.add(new SortField("По дате изменения",
				TaskDBAdapter.FIELD_MODIFY_DATE_NAME));
		sortFieldAdapter.add(new SortField("По статусу отправки",
				TaskDBAdapter.FIELD_UPDATED_NAME));
		Spinner_type.setAdapter(sortFieldAdapter);

	}

	private void FillListViewTasks(String taskStatus, String orderByField) {

		String tagId = AuthorizedUser.getInstance().getTagId();
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(
				getActivity().getApplicationContext()));
		Users user = users.getUserByTagId(tagId);

		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {
			TaskDBAdapter taskDbAdapter = new TaskDBAdapter(
					new TOiRDatabaseContext(getActivity()
							.getApplicationContext()));

			taskAdapter.changeCursor(taskDbAdapter.getTaskWithInfo(
					user.getUuid(), taskStatus, orderByField));

			/*
			Integer cnt = 0;
			List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
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
				aList.add(hm);
				cnt++;
			}
			*/
			// Setting the adapter to the listView
			lv.setAdapter(taskAdapter);
			button.setVisibility(View.INVISIBLE);
		}
	}

	private void fillSpinnersEquipment() {

		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<OperationType> operationTypeList = operationTypeDBAdapter
				.getAllItems();
		ArrayList<CriticalType> criticalTypeList = criticalTypeDBAdapter
				.getAllItems();

		operationTypeList.add(0, new OperationType(0, null, "Все операции", 0,
				0));
		operationTypeAdapter.clear();
		operationTypeAdapter.addAll(operationTypeList);
		Spinner_references.setAdapter(operationTypeAdapter);

		criticalTypeList.add(0, new CriticalType(0, null, 0, 0, 0));
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
				initOperationPattern(cursor.getString(cursor
						.getColumnIndex("operation_uuid")),
						cursor.getString(cursor.getColumnIndex("task_uuid")),
						cursor.getString(cursor
								.getColumnIndex("equipment_uuid")));
			}

			if (Level == 0) {
				currentTaskUuid = cursor.getString(cursor
						.getColumnIndex(TaskDBAdapter.FIELD_UUID_NAME));
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

			if (Level == 1) {
				Cursor cursor = (Cursor) parent.getItemAtPosition(position);
				final String operation_uuid = cursor.getString(cursor
						.getColumnIndex("operation_uuid"));
				final String taskUuid = cursor.getString(cursor
						.getColumnIndex("task_uuid"));

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
						EquipmentOperationDBAdapter dbAdapter = new EquipmentOperationDBAdapter(
								new TOiRDatabaseContext(getActivity()));
						dbAdapter.setOperationStatus(operation_uuid,
								status.getUuid());

						// текущие значения фильтров
						OperationType operationType = (OperationType) Spinner_references
								.getSelectedItem();
						CriticalType criticalType = (CriticalType) Spinner_type
								.getSelectedItem();
						// обновляем содержимое курсора
						operationAdapter.changeCursor(dbAdapter
								.getOperationWithInfo(taskUuid,
										operationType.getUuid(),
										criticalType.getUuid()));

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
						new TOiRDatabaseContext(getActivity()));
				ArrayList<OperationStatus> operationStatus = statusDBAdapter
						.getItems();

				ArrayAdapter<OperationStatus> adapter = new ArrayAdapter<OperationStatus>(
						getActivity(), android.R.layout.simple_spinner_item,
						operationStatus);
				Spinner statusSpinner = (Spinner) dialog
						.findViewById(R.id.statusSpinner);
				statusSpinner.setAdapter(adapter);
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

		EquipmentOperationDBAdapter eqOperationDBAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));

		// обновляем содержимое курсора
		operationAdapter.changeCursor(eqOperationDBAdapter
				.getOperationWithInfo(task_uuid, operation_type_uuid,
						critical_type_uuid));

		// Setting the adapter to the listView
		lv.setAdapter(operationAdapter);

		button.setVisibility(View.VISIBLE);

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
				new TOiRDatabaseContext(getActivity()));
		String equipment_tag = equipmentDBAdapter.getItem(equipment_uuid)
				.getTag_id();

		Intent rfidRead = new Intent(getActivity(), RFIDActivity.class);
		Bundle bundle = new Bundle();
		// следующие параметры предаются в OperationActivity
		bundle.putString(OperationActivity.OPERATION_UUID_EXTRA,
				equipment_operation_uuid);
		bundle.putString(OperationActivity.TASK_UUID_EXTRA, task_uuid);
		bundle.putString(OperationActivity.EQUIPMENT_UUID_EXTRA, equipment_uuid);
		// метка предаётся в MainActivity для проверки правильности оборудования
		bundle.putString(OperationActivity.EQUIPMENT_TAG_EXTRA, equipment_tag);
		bundle.putInt("action",
				MainActivity.RFIDReadAction.READ_EQUIPMENT_TAG_BEFORE_OPERATION);
		rfidRead.putExtras(bundle);
		getActivity().startActivityForResult(rfidRead,
				MainActivity.RETURN_CODE_READ_RFID);
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
								// TODO необходимые операции для отмены приёма
								// нарядов
							}
						});
				getOrderDialog.show();
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

}
