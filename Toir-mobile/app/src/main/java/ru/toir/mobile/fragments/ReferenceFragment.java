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
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.AlertTypeAdapter;
import ru.toir.mobile.db.adapters.CriticalTypeAdapter;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusAdapter;
import ru.toir.mobile.db.adapters.OperationTypeAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentStatus;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.MeasureType;
import ru.toir.mobile.db.tables.OperationResult;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.ReferenceServiceHelper;
import ru.toir.mobile.rest.ReferenceServiceProvider;

public class ReferenceFragment extends Fragment {

	private Spinner referenceSpinner;
	private Spinner typeSpinner;
	private Spinner extraSpinner;

	private ListView contentListView;

	private ArrayList<SortField> referenceList;
	private ArrayList<SortField> typeList;
	private ArrayList<SortField> extraList;

	private ArrayAdapter<SortField> referenceSpinnerAdapter;
	private ArrayAdapter<SortField> typeSpinnerAdapter;
	private ArrayAdapter<SortField> extraSpinnerAdapter;

	private TypeExtraSpinnerListener typeExtraSpinnerListener;
	private ReferenceSpinnerListener referenceSpinnerListener;

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
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		}
	};

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
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.reference_layout, container,
				false);

		referenceSpinner = (Spinner) rootView.findViewById(R.id.spinner1);
		typeSpinner = (Spinner) rootView.findViewById(R.id.spinner2);
		extraSpinner = (Spinner) rootView.findViewById(R.id.spinner3);
		contentListView = (ListView) rootView.findViewById(R.id.listView1);

		typeList = new ArrayList<>();
		typeSpinnerAdapter = new ArrayAdapter<>(getActivity()
				.getApplicationContext(),
				android.R.layout.simple_spinner_dropdown_item, typeList);
		typeSpinner.setAdapter(typeSpinnerAdapter);

		extraList = new ArrayList<>();
		extraSpinnerAdapter = new ArrayAdapter<>(getActivity()
				.getApplicationContext(),
				android.R.layout.simple_spinner_dropdown_item, extraList);
		extraSpinner.setAdapter(extraSpinnerAdapter);

		typeExtraSpinnerListener = new TypeExtraSpinnerListener();
		extraSpinner.setOnItemSelectedListener(typeExtraSpinnerListener);
		typeSpinner.setOnItemSelectedListener(typeExtraSpinnerListener);

		// получаем список справочников, разбиваем его на ключ:значение
		String[] referenceArray = getResources().getStringArray(
				R.array.references_array);
		String[] tmpValue;
		SortField item;
		referenceList = new ArrayList<>();
		for (String value : referenceArray) {
			tmpValue = value.split(":");
			item = new SortField(tmpValue[0], tmpValue[1]);
			referenceList.add(item);
		}

		referenceSpinnerAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, referenceList);

		referenceSpinner.setAdapter(referenceSpinnerAdapter);
		referenceSpinnerListener = new ReferenceSpinnerListener();
		referenceSpinner.setOnItemSelectedListener(referenceSpinnerListener);

		setHasOptionsMenu(true);
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	/**
	 * 
	 * @author Dmitriy Logachov
	 *         <p>
	 *         Класс реализует обработку выбора элемента выпадающего списка
	 *         справочников.
	 *         </p>
	 * 
	 */
	private class ReferenceSpinnerListener implements
			AdapterView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {

			SortField selectedItem = (SortField) parentView
					.getItemAtPosition(position);
			String selected = selectedItem.getField();

			if (selected.equals(EquipmentAdapter.TABLE_NAME)) {

				fillTypeExtraEquipment();
				fillListViewEquipment("", "");

			} else if (selected.equals(DocumentationTypeAdapter.TABLE_NAME)) {

				fillTypeExtraDocumentation();
				fillListViewDocumentation("");

			} else if (selected.equals(EquipmentTypeAdapter.TABLE_NAME)) {

				fillTypeExtraEquipmentType();
				fillListViewEquipmentType();

			} else if (selected.equals(CriticalTypeAdapter.TABLE_NAME)) {

				fillTypeExtraCriticalType();
				fillListViewCriticalType();

			} else if (selected.equals(AlertTypeAdapter.TABLE_NAME)) {

				fillTypeExtraMeasurementType();
				fillListViewMeasurementType();

			} else if (selected.equals(OperationVerdictAdapter.TABLE_NAME)) {

				fillTypeExtraOperationResult();
				fillListViewOperationResult();

			} else if (selected.equals(OperationTypeAdapter.TABLE_NAME)) {

				fillTypeExtraOperationType();
				fillListViewOperationType();

            } else if (selected.equals(OperationStatusAdapter.TABLE_NAME)) {

                fillTypeExtraOperationType();
                fillListViewOperationType();

			} else if (selected.equals(TaskStatusDBAdapter.TABLE_NAME)) {

				fillTypeExtraTaskStatus();
				fillListViewTaskStatus();

			} else if (selected.equals(EquipmentStatusAdapter.TABLE_NAME)) {

				fillTypeExtraEquipmentStatus();
				fillListViewEquipmentStatus();

			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {

		}
	}

	/**
	 * 
	 * @author olejek
	 *         <p>
	 *         Класс реализует обработку выбора элементов выпадающих списков для
	 *         {@code typeSpinner} и {@code extraSpinner}
	 *         </p>
	 * 
	 */
	private class TypeExtraSpinnerListener implements
			AdapterView.OnItemSelectedListener {

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {

			String referenceSelected = EquipmentDBAdapter.TABLE_NAME;
			SortField referenceItem = (SortField) referenceSpinner
					.getSelectedItem();
			if (referenceItem != null) {
				referenceSelected = referenceItem.getField();
			}

			String typeSelected = null;
			SortField typeItem = (SortField) typeSpinner.getSelectedItem();
			if (typeItem != null) {
				typeSelected = typeItem.getField();
			}

			String extraSelected = null;
			SortField extraItem = (SortField) extraSpinner.getSelectedItem();
			if (extraItem != null) {
				extraSelected = extraItem.getField();
			}

			if (referenceSelected.equals(EquipmentDBAdapter.TABLE_NAME)) {

				// в данном случае тип оборудования
				if (typeSelected == null) {
					typeSelected = "";
				}

				// в данном случае тип критичности
				if (extraSelected == null) {
					extraSelected = "";
				}

				fillListViewEquipment(typeSelected, extraSelected);

			} else if (referenceSelected
					.equals(EquipmentDocumentationDBAdapter.TABLE_NAME)) {

				// в данном случае тип документации
				if (typeSelected == null) {
					typeSelected = "";
				}

				fillListViewDocumentation(typeSelected);

			}
			// TODO add this
			// <item>Результаты операций</item>
		}
	}

	private void fillTypeExtraEquipment() {

		EquipmentTypeDBAdapter equipmentTypeDBAdapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<EquipmentType> equipmentTypeList = equipmentTypeDBAdapter
				.getItems();

		typeSpinner.setVisibility(View.VISIBLE);
		extraSpinner.setVisibility(View.VISIBLE);

		SortField item;
		item = new SortField("Все", null);
		typeSpinnerAdapter.clear();
		typeSpinnerAdapter.add(item);

		for (EquipmentType type : equipmentTypeList) {
			item = new SortField(type.getTitle(), type.getUuid());
			typeSpinnerAdapter.add(item);
		}

		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<CriticalType> criticalTypeList = criticalTypeDBAdapter
				.getAllItems();

		item = new SortField("Все", null);
		extraSpinnerAdapter.clear();
		extraSpinnerAdapter.add(item);

		for (CriticalType type : criticalTypeList) {
			item = new SortField("Критичность: " + type.getType(),
					type.getUuid());
			extraSpinnerAdapter.add(item);
		}
	}

	private void fillListViewEquipment(String type, String critical_type) {

		EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<Equipment> equipmentList = eqDBAdapter.getAllItems(type,
				critical_type);
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));

		List<HashMap<String, String>> elementList = new ArrayList<HashMap<String, String>>();
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		HashMap<String, String> element;

		for (Equipment item : equipmentList) {
			element = new HashMap<String, String>();
			element.put("name", item.getTitle());
			element.put(
					"descr",
					"Критичность: "
							+ criticalTypeDBAdapter.getNameByUUID(item
									.getCritical_type_uuid())
							+ " | Тип: "
							+ eqTypeDBAdapter.getNameByUUID(item
									.getEquipment_type_uuid()) + " ["
							+ item.getLatitude() + " " + item.getLongitude()
							+ "]");
			element.put("img", Integer.toString(R.drawable.img_1));
			elementList.add(element);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), elementList, R.layout.listview, from,
				to);

		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraDocumentation() {

		DocumentationTypeDBAdapter DocumentationTypeDBAdapter = new DocumentationTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<DocumentationType> documentationTypeList = DocumentationTypeDBAdapter
				.getAllItems();

		typeSpinner.setVisibility(View.VISIBLE);
		extraSpinner.setVisibility(View.GONE);

		SortField item;
		item = new SortField("Все", null);
		typeSpinnerAdapter.clear();
		typeSpinnerAdapter.add(item);

		for (DocumentationType type : documentationTypeList) {
			item = new SortField(type.getTitle(), type.getUuid());
			typeSpinnerAdapter.add(item);
		}
	}

	private void fillListViewDocumentation(String type) {

		EquipmentDocumentationDBAdapter EquipmentDocumentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		DocumentationTypeDBAdapter DocumentationTypeDBAdapter = new DocumentationTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		List<EquipmentDocumentation> equipmentDocumentationList = EquipmentDocumentationDBAdapter
				.getAllItems(type);
		List<HashMap<String, String>> elementList = new ArrayList<HashMap<String, String>>();
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		HashMap<String, String> element;

		for (EquipmentDocumentation item : equipmentDocumentationList) {
			element = new HashMap<String, String>();
			element.put("name", item.getTitle());
			element.put(
					"descr",
					DocumentationTypeDBAdapter.getNameByUUID(item
							.getDocumentation_type_uuid())
							+ " ["
							+ item.getPath() + "]");
			element.put("img", Integer.toString(R.drawable.img_4));
			elementList.add(element);
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), elementList, R.layout.listview, from,
				to);

		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraEquipmentType() {

		typeSpinner.setVisibility(View.GONE);
		extraSpinner.setVisibility(View.GONE);
	}

	private void fillListViewEquipmentType() {
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<EquipmentType> equipmentTypeList = eqTypeDBAdapter.getItems();

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
		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraCriticalType() {

		typeSpinner.setVisibility(View.GONE);
		extraSpinner.setVisibility(View.GONE);
	}

	private void fillListViewCriticalType() {
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
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
		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraMeasurementType() {

		typeSpinner.setVisibility(View.GONE);
		extraSpinner.setVisibility(View.GONE);
	}

	private void fillListViewMeasurementType() {
		MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
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
		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraOperationResult() {

		typeSpinner.setVisibility(View.GONE);
		extraSpinner.setVisibility(View.GONE);
	}

	private void fillListViewOperationResult() {
		OperationResultDBAdapter opResultTypeDBAdapter = new OperationResultDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		OperationTypeDBAdapter OperationTypeDBAdapter = new OperationTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));

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
		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraOperationType() {

		typeSpinner.setVisibility(View.GONE);
		extraSpinner.setVisibility(View.GONE);
	}

	private void fillListViewOperationType() {
		OperationTypeDBAdapter opTypeDBAdapter = new OperationTypeDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
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
		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraTaskStatus() {

		typeSpinner.setVisibility(View.GONE);
		extraSpinner.setVisibility(View.GONE);
	}

	private void fillListViewTaskStatus() {
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
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
		contentListView.setAdapter(adapter);
	}

	private void fillTypeExtraEquipmentStatus() {

		typeSpinner.setVisibility(View.GONE);
		extraSpinner.setVisibility(View.GONE);
	}

	private void fillListViewEquipmentStatus() {
		EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
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
		contentListView.setAdapter(adapter);
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
