package ru.toir.mobile.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.toir.mobile.R;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.TOiRDatabaseContext;
//import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentType;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class EquipmentsFragment extends Fragment {
	private Spinner Spinner_sort;
	private Spinner Spinner_type;
	private ListView lv;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> list2 = new ArrayList<String>();
	ArrayAdapter<String> spinner_sort_adapter;
	ArrayAdapter<String> spinner_type_adapter;
	ArrayList<String> equipment_uuid = new ArrayList<String>();
	ArrayList<String> equipment_critical_uuid = new ArrayList<String>();
	ArrayList<String> equipment_type_uuid = new ArrayList<String>();

	public static EquipmentsFragment newInstance() {
		EquipmentsFragment f = new EquipmentsFragment();
		return f;
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
		View rootView = inflater.inflate(R.layout.equipments_list_layout,
				container, false);
		lv = (ListView) rootView.findViewById(R.id.equipments_listView);
		spinner_type_adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), android.R.layout.simple_spinner_item,
				list);
		spinner_sort_adapter = new ArrayAdapter<String>(getActivity()
				.getApplicationContext(), android.R.layout.simple_spinner_item,
				list2);
		Spinner_sort = (Spinner) rootView
				.findViewById(R.id.equipments_spinner_sort);
		Spinner_type = (Spinner) rootView
				.findViewById(R.id.equipments_spinner_type);
		initView();

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView() {
		equipment_uuid.clear();
		FillListViewEquipments("", "");
		FillSpinnersEquipments();
	}

	private void FillSpinnersEquipments() {
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<EquipmentType> equipmentTypeList = eqTypeDBAdapter
				.getAllItems();
		spinner_type_adapter.clear();
		int cnt = 0;
		spinner_type_adapter.add("Все типы");
		cnt++;
		while (cnt <= equipmentTypeList.size()) {
			spinner_type_adapter.add(equipmentTypeList.get(cnt - 1).getTitle());
			equipment_type_uuid.add(equipmentTypeList.get(cnt - 1).getUuid());
			// tasks_st_uuid.add(equipmentTypeList.get(cnt-1).getUuid());
			cnt++;
		}

		spinner_sort_adapter.clear();
		spinner_sort_adapter.add("Сортировка");
		spinner_sort_adapter.add("По степени критичности");
		spinner_sort_adapter.add("По статусу");
		spinner_sort_adapter.add("По дате обслуживания");

		Spinner_type.setOnItemSelectedListener(new SpinnerListener());
		Spinner_sort.setOnItemSelectedListener(new SpinnerListener());

		spinner_sort_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_type_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner_type.setAdapter(spinner_type_adapter);
		Spinner_sort.setAdapter(spinner_sort_adapter);
	}

	public class ListviewClickListener implements
			AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			Intent equipmentInfo = new Intent(getActivity(),
					EquipmentInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("equipment_uuid", equipment_uuid.get(position));
			equipmentInfo.putExtras(bundle);
			getActivity().startActivity(equipmentInfo);

			// Create new fragment and transaction
			// Fragment equipmentFragment = new
			// EquipmentInfoFragment(equipment_uuid.get(position).toString());
			// ViewPager pager = (ViewPager)
			// getActivity().findViewById(R.id.pager);
			// FragmentTransaction transaction =
			// getFragmentManager().beginTransaction();
			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack if needed
			// transaction.replace(R.id.pager, equipmentFragment);
			// transaction.addToBackStack(null);
			// Commit the transaction
			// transaction.commit();
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
			String type = "";
			String orderBy = "";
			if (Spinner_type.getSelectedItemId() > 0)
				type = equipment_type_uuid.get((int) Spinner_type
						.getSelectedItemId() - 1);
			switch ((int) Spinner_sort.getSelectedItemId()) {
			case 0:
				orderBy = "";
				break;
			case 1:
				orderBy = "critical";
				break;
			// case 2: orderBy="status"; break;
			case 3:
				orderBy = "start_date";
				break;
			default:
				orderBy = "";
			}
			FillListViewEquipments(type, orderBy);
		}
	}

	private void FillListViewEquipments(String type, String sort) {
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		EquipmentTypeDBAdapter eqTypeDBAdapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(getActivity().getApplicationContext()));
//		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
//				new TOiRDatabaseContext(getActivity().getApplicationContext()));
		ArrayList<Equipment> equipmentList = equipmentDBAdapter.getAllItems(
				type, "");
		equipment_uuid.clear();
		Integer cnt = 0;
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		String[] from = { "name", "descr", "img" };
		int[] to = { R.id.lv_firstLine, R.id.lv_secondLine, R.id.lv_icon };
		while (cnt < equipmentList.size()) {
			HashMap<String, String> hm = new HashMap<String, String>();
			hm.put("name",
					equipmentList.get(cnt).getTitle()
							+ " | ИД: "
							+ equipmentList.get(cnt).getInventoryNumber()
							//+ " | Дата: "
							//+ DataUtils.getDate(equipmentList.get(cnt)
									//.getStart_date(), "dd-MM-yyyy hh:mm")
							);
			hm.put("descr",
					"Тип: "
							+ eqTypeDBAdapter.getNameByUUID(equipmentList.get(
							cnt).getEquipment_type_uuid())
							+ " ["
							+ equipmentList.get(cnt).getLocation()
							+ "]"
							//+ " | Критичность: "
							//+ criticalTypeDBAdapter.getNameByUUID(equipmentList
									//.get(cnt).getCritical_type_uuid())
									);
			// TODO: real image
			// hm.put("img", getActivity().getApplicationInfo().dataDir +
			// equipmentList.get(cnt).getImg());
			hm.put("img", Integer.toString(R.drawable.img_status_4));
			equipment_uuid.add(cnt, equipmentList.get(cnt).getUuid());
			aList.add(hm);
			cnt++;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview, from, to);
		// Setting the adapter to the listView
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new ListviewClickListener());
	}
}
