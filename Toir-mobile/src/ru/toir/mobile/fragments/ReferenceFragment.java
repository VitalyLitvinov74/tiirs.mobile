package ru.toir.mobile.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentStatus;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.MeasureType;
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.OperationResult;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.ReferenceServiceHelper;
import ru.toir.mobile.rest.ReferenceServiceProvider;
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
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.AdapterView;
import android.widget.Toast;

public class ReferenceFragment extends Fragment {
	private Spinner Spinner_references;
	private Spinner Spinner_type;
	private Spinner Spinner_addict;
	private ListView lv;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> list2 = new ArrayList<String>();
	ArrayAdapter<String> spinner_type_adapter;
	ArrayAdapter<String> spinner_addict_adapter;
	private ProgressDialog getReferencesDialog;

	private IntentFilter mFilterGetReference = new IntentFilter(
			ReferenceServiceProvider.Actions.ACTION_GET_ALL);
	private BroadcastReceiver mReceiverGetReference = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			getReferencesDialog.dismiss();
			context.unregisterReceiver(mReceiverGetReference);
			boolean result = intent.getBooleanExtra(
					ProcessorService.Extras.RESULT_EXTRA, false);
			Bundle bundle = intent
					.getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
			if (result) {
				Toast.makeText(context, "Справочники обновлены",
						Toast.LENGTH_SHORT).show();
			} else {
				// сообщаем описание неудачи
				String message = bundle.getString(IServiceProvider.MESSAGE);
				Toast.makeText(context, message,
						Toast.LENGTH_SHORT).show();
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
		View rootView = inflater.inflate(R.layout.reference_layout, container,
				false);
		Spinner_references = (Spinner) rootView.findViewById(R.id.spinner1);
		Spinner_type = (Spinner) rootView.findViewById(R.id.spinner2);
		Spinner_addict = (Spinner) rootView.findViewById(R.id.spinner3);
		lv = (ListView) rootView.findViewById(R.id.listView1);
		initView();

		setHasOptionsMenu(true);
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView() {
		FillSpinners();
		FillListViewEquipment("", "");
	}

	private void FillSpinners() {
		ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter
				.createFromResource(getActivity().getApplicationContext(),
						R.array.references_array,
						android.R.layout.simple_spinner_item);
		spinner_type_adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), android.R.layout.simple_spinner_item,
				list);
		spinner_addict_adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), android.R.layout.simple_spinner_item,
				list2);

		spinner_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner_references.setAdapter(spinner_adapter);

		Spinner_addict.setOnItemSelectedListener(new SpinnerListener());
		Spinner_type.setOnItemSelectedListener(new SpinnerListener());

		Spinner_references
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parentView,
							View selectedItemView, int position, long id) {
						// String selected =
						// parentView.getItemAtPosition(position).toString();
						spinner_type_adapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						Spinner_type.setAdapter(spinner_type_adapter);
						spinner_addict_adapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						Spinner_addict.setAdapter(spinner_addict_adapter);

						switch (position) {
						// устройства
						case 0:
							FillReferencesEquipment();
							FillListViewEquipment("", "");
							break;
						// Документация
						case 1:
							FillListViewDocumentation("");
							FillReferencesDocumentation();
							break;
						// Типы оборудования
						case 2:
							FillListViewEquipmentType();
							break;
						// Степени важности
						case 3:
							FillListViewCriticalType();
							ClearReferences();
							break;
						// Типы измерений
						case 4:
							FillListViewMeasurementType();
							ClearReferences();
							break;
						// Результаты операций
						case 5:
							FillListViewOperationResult();
							ClearReferences();
							break;
						// Типы операций
						case 6:
							FillListViewOperationType();
							ClearReferences();
							break;
						// Статусы нарядов
						case 7:
							FillListViewTaskStatus();
							ClearReferences();
							break;
						// Статусы состояния оборудования
						case 8:
							FillListViewEquipmentStatus();
							ClearReferences();
							break;
						default:
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parentView) {
						//
					}
				});
	}

	public class SpinnerListener implements AdapterView.OnItemSelectedListener {
		boolean userSelect = false;

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			// String reference=Spinner_references.getSelectedItem().toString();
			long current_ref = Spinner_references.getSelectedItemId();
			EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
					new TOiRDatabaseContext(getActivity()
							.getApplicationContext()));
			CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
					new TOiRDatabaseContext(getActivity()
							.getApplicationContext()));
			DocumentationTypeDBAdapter DocumentationTypeDBAdapter = new DocumentationTypeDBAdapter(
					new TOiRDatabaseContext(getActivity()
							.getApplicationContext()));
			String type = "";
			String critical_type = "";
			if (current_ref == 0) {
				if (Spinner_type.getSelectedItemId() > 0)
					type = eqTypeDBAdapter.getUUIDByName(Spinner_type
							.getSelectedItem().toString());
				if (Spinner_addict.getSelectedItemId() > 0) {
					critical_type = criticalTypeDBAdapter
							.getUUIDByName(Spinner_addict.getSelectedItem()
									.toString().charAt(13)
									+ "");
				}
				FillListViewEquipment(type, critical_type);
			}
			if (current_ref == 1) {
				if (Spinner_type.getSelectedItemId() > 0)
					type = DocumentationTypeDBAdapter
							.getUUIDByName(Spinner_type.getSelectedItem()
									.toString());
				FillListViewDocumentation(type);
			}
			// TODO add this
			// <item>Результаты операций</item>
		}
	}

	private void FillReferencesEquipment() {
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<EquipmentType> equipmentTypeList = eqTypeDBAdapter
				.getAllItems();
		spinner_type_adapter.clear();
		Integer cnt = 0;
		spinner_type_adapter.add("Все");
		cnt++;
		while (cnt <= equipmentTypeList.size()) {
			spinner_type_adapter.add(equipmentTypeList.get(cnt - 1).getTitle());
			cnt++;
		}

		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<CriticalType> criticalTypeList = criticalTypeDBAdapter
				.getAllItems();
		spinner_addict_adapter.clear();
		cnt = 0;
		spinner_addict_adapter.add("Все");
		cnt++;
		while (cnt <= criticalTypeList.size()) {
			spinner_addict_adapter.add("Критичность: "
					+ criticalTypeList.get(cnt - 1).getType());
			cnt++;
		}
	}

	private void FillListViewEquipment(String type, String critical_type) {
		EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<Equipment> equipmentList = eqDBAdapter.getAllItems(type,
				critical_type);
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));

		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		// keys used in Hashmap
		String[] from = { "name", "descr", "img" };
		// id of views in listview_layout
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < equipmentList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", equipmentList.get(cnt).getTitle());
			hm.put("descr",
					"Критичность: "
							+ criticalTypeDBAdapter.getNameByUUID(equipmentList
									.get(cnt).getCritical_type_uuid())
							+ " | Тип: "
							+ eqTypeDBAdapter.getNameByUUID(equipmentList.get(
									cnt).getEquipment_type_uuid()) + " ["
							+ equipmentList.get(cnt).getLatitude() + " "
							+ equipmentList.get(cnt).getLongitude() + "]");
			hm.put("img", Integer.toString(R.drawable.img_1));
			aList.add(hm);
			cnt++;
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		// Setting the adapter to the listView
		lv.setAdapter(adapter);
	}

	private void FillListViewEquipmentType() {
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<EquipmentType> equipmentTypeList = eqTypeDBAdapter
				.getAllItems();

		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < equipmentTypeList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", equipmentTypeList.get(cnt).getTitle());
			hm.put("descr", "uuid: " + equipmentTypeList.get(cnt).getUuid());
			hm.put("img", Integer.toString(R.drawable.img_4));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		lv.setAdapter(adapter);
	}

	private void FillReferencesDocumentation() {
		DocumentationTypeDBAdapter DocumentationTypeDBAdapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<DocumentationType> documentationTypeList = DocumentationTypeDBAdapter
				.getAllItems();
		spinner_type_adapter.clear();
		spinner_addict_adapter.clear();
		Integer cnt = 0;
		spinner_type_adapter.add("Все");
		cnt++;
		while (cnt <= documentationTypeList.size()) {
			spinner_type_adapter.add(documentationTypeList.get(cnt - 1)
					.getTitle());
			cnt++;
		}
	}

	private void FillListViewDocumentation(String type) {
		EquipmentDocumentationDBAdapter EquipmentDocumentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		DocumentationTypeDBAdapter DocumentationTypeDBAdapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<EquipmentDocumentation> equipmentDocumentationList = EquipmentDocumentationDBAdapter
				.getAllItems(type);
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < equipmentDocumentationList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", equipmentDocumentationList.get(cnt).getTitle());
			hm.put("descr",
					DocumentationTypeDBAdapter
							.getNameByUUID(equipmentDocumentationList.get(cnt)
									.getDocumentation_type_uuid())
							+ " ["
							+ equipmentDocumentationList.get(cnt).getPath()
							+ "]");
			hm.put("img", Integer.toString(R.drawable.img_4));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		// Setting the adapter to the listView
		lv.setAdapter(adapter);
	}

	private void FillListViewCriticalType() {
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<CriticalType> criticalTypeList = criticalTypeDBAdapter
				.getAllItems();

		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		// keys used in Hashmap
		String[] from = { "name", "descr", "img" };
		// id of views in listview_layout
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < criticalTypeList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", "Уровень критичности: "
					+ criticalTypeList.get(cnt).getType());
			hm.put("descr", "uuid: " + criticalTypeList.get(cnt).getUuid());
			hm.put("img", Integer.toString(R.drawable.img_2));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		lv.setAdapter(adapter);
	}

	private void FillListViewMeasurementType() {
		MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<MeasureType> measureTypeList = measureTypeDBAdapter
				.getAllItems();
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < measureTypeList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", measureTypeList.get(cnt).getTitle());
			hm.put("descr", "uuid: " + measureTypeList.get(cnt).getUuid());
			hm.put("img", Integer.toString(R.drawable.img_2));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		lv.setAdapter(adapter);
	}

	private void FillListViewOperationResult() {
		OperationResultDBAdapter opResultTypeDBAdapter = new OperationResultDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		OperationTypeDBAdapter OperationTypeDBAdapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));

		ArrayList<OperationResult> opResultList = opResultTypeDBAdapter
				.getAllItems();
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < opResultList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", opResultList.get(cnt).getTitle());
			hm.put("descr",
					"Тип операции:"
							+ OperationTypeDBAdapter
									.getOperationTypeByUUID(opResultList.get(
											cnt).getOperation_type_uuid())
							+ " | uuid: " + opResultList.get(cnt).getUuid());
			hm.put("img", Integer.toString(R.drawable.img_2));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		lv.setAdapter(adapter);
	}

	private void FillListViewOperationType() {
		OperationTypeDBAdapter opTypeDBAdapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<OperationType> opTypeList = opTypeDBAdapter.getAllItems();
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < opTypeList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", opTypeList.get(cnt).getTitle());
			hm.put("descr", "uuid: " + opTypeList.get(cnt).getUuid());
			hm.put("img", Integer.toString(R.drawable.img_2));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		lv.setAdapter(adapter);
	}

	private void FillListViewTaskStatus() {
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<TaskStatus> taskStatusList = taskStatusDBAdapter
				.getAllItems();
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < taskStatusList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", taskStatusList.get(cnt).getTitle());
			hm.put("descr", "uuid: " + taskStatusList.get(cnt).getUuid());
			hm.put("img", Integer.toString(R.drawable.img_3));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		lv.setAdapter(adapter);
	}

	private void FillListViewEquipmentStatus() {
		EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<EquipmentStatus> equipmentStatusList = equipmentStatusDBAdapter
				.getAllItems();
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		Integer cnt = 0;
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < equipmentStatusList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name", equipmentStatusList.get(cnt).getTitle());
			hm.put("descr", "uuid: " + equipmentStatusList.get(cnt).getUuid());
			hm.put("img", Integer.toString(R.drawable.img_3));
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		lv.setAdapter(adapter);
	}

	private void ClearReferences() {
		spinner_type_adapter.clear();
		spinner_addict_adapter.clear();
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
		MenuItem getTask = menu.add("Обновить справочники");
		getTask.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

			@Override
			public boolean onMenuItemClick(MenuItem item) {
				Log.d("test", "Обновляем справочники.");

				ReferenceServiceHelper rsh = new ReferenceServiceHelper(
						getActivity().getApplicationContext(),
						ReferenceServiceProvider.Actions.ACTION_GET_ALL);

				getActivity().registerReceiver(mReceiverGetReference,
						mFilterGetReference);

				rsh.getAll();

				// показываем диалог обновления справочников
				getReferencesDialog = new ProgressDialog(getActivity());
				getReferencesDialog.setMessage("Получаем справочники");
				getReferencesDialog.setIndeterminate(true);
				getReferencesDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				getReferencesDialog.setCancelable(false);
				getReferencesDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
						"Отмена", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getActivity().unregisterReceiver(
										mReceiverGetReference);
								Toast.makeText(getActivity(),
										"Обновление справочников отменено",
										Toast.LENGTH_SHORT).show();
							}
						});
				getReferencesDialog.show();

				return true;
			}
		});
	}
}
