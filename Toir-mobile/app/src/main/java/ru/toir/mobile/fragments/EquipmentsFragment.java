package ru.toir.mobile.fragments;

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
import android.widget.Spinner;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentType;

//import ru.toir.mobile.db.tables.EquipmentType;

public class EquipmentsFragment extends Fragment {
    private Realm realmDB;
    //private EquipmentType equipmentType;
	private boolean isInit;

	private Spinner sortSpinner;
	private Spinner typeSpinner;
	private ListView equipmentListView;

	private ArrayAdapter<SortField> sortSpinnerAdapter;
	private ArrayAdapter<EquipmentType> typeSpinnerAdapter;

	private SpinnerListener spinnerListener;
    private EquipmentAdapter equipmentAdapter;

    public static EquipmentsFragment newInstance() {
		return new EquipmentsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.equipment_reference_layout,
				container, false);
        realmDB = Realm.getDefaultInstance();

		// обработчик для выпадающих списков у нас один
		spinnerListener = new SpinnerListener();

		// настраиваем сортировку по типу оборудования
		typeSpinner = (Spinner) rootView.findViewById(R.id.erl_type_spinner);
        typeSpinnerAdapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<EquipmentType>());
		typeSpinner.setAdapter(typeSpinnerAdapter);
		typeSpinner.setOnItemSelectedListener(spinnerListener);

		// настраиваем сортировку по полям
		sortSpinner = (Spinner) rootView
				.findViewById(R.id.erl_sort_field_spinner);
		sortSpinnerAdapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<SortField>());
		sortSpinner.setAdapter(sortSpinnerAdapter);
		sortSpinner.setOnItemSelectedListener(spinnerListener);

		equipmentListView = (ListView) rootView
				.findViewById(R.id.erl_equipment_listView);

		// создаём "пустой" адаптер для отображения списка оборудования
        /*
		equipmentAdapter = new SimpleCursorAdapter(getContext(),
				R.layout.equipment_reference_item_layout, null, equipmentFrom,
				equipmentTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		equipmentListView.setAdapter(equipmentAdapter);
        */
		// это нужно для отображения произвольных изображений и конвертации в
		// строку дат
        /*
		equipmentAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				int viewId = view.getId();

				if (viewId == R.id.eril_last_operation_date) {
					long lDate = cursor.getLong(columnIndex);
					String sDate = DataUtils.getDate(lDate, "dd.MM.yyyy HH:ss");
					((TextView) view).setText(sDate);
					return true;
				}

				return false;
			}
		});
        */
		equipmentListView.setOnItemClickListener(new ListviewClickListener());

		initView();

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		isInit = true;

		return rootView;
	}

	private void initView() {

		FillListViewEquipments(null, null);
		fillTypeSpinner();
		fillSortFieldSpinner();
	}

	private void fillTypeSpinner() {
        RealmResults<EquipmentType> equipmentType = realmDB.where(EquipmentType.class).findAll();
        //typeSpinnerAdapter = new ArrayAdapter(equipmentType);
        //typeSpinner.setAdapter(typeSpinnerAdapter);

		//EquipmentType allTypes = new EquipmentType();
		// TODO стоит наверное добавить запись "любой тип" напрямую в таблицу?
		//allTypes.setUuid(null);
		//allTypes.setTitle("Все типы");
		//typeList.add(0, allTypes);
		typeSpinnerAdapter.clear();
		typeSpinnerAdapter.addAll(equipmentType);
		typeSpinnerAdapter.notifyDataSetChanged();
	}

	private void fillSortFieldSpinner() {

		sortSpinnerAdapter.clear();
		sortSpinnerAdapter.add(new SortField("Сортировка", null));
        sortSpinnerAdapter.add(new SortField("По степени критичности",
                "criticalTypeUuid"));
		sortSpinnerAdapter.add(new SortField("По статусу",
				"equipmentStatusUuid"));
		sortSpinnerAdapter.add(new SortField("По дате обслуживания",
				"startDate"));

	}

	public class ListviewClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
            // TODO разобраться как вернуть объект при клике
            Equipment equipment = (Equipment)parentView.getItemAtPosition(position);
            equipment = (Equipment)parentView.getSelectedItem();

			String equipment_uuid = equipment.getUuid();

			Intent equipmentInfo = new Intent(getActivity(),
					EquipmentInfoActivity.class);

			Bundle bundle = new Bundle();
			bundle.putString("equipment_uuid", equipment_uuid);
			equipmentInfo.putExtras(bundle);
			getActivity().startActivity(equipmentInfo);
		}
	}

	public class SpinnerListener implements AdapterView.OnItemSelectedListener {
		//boolean userSelect = false;

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {

			String type = null;
			String orderBy = null;

			EquipmentType typeSelected = (EquipmentType) typeSpinner
					.getSelectedItem();
			if (typeSelected != null) {
				type = typeSelected.getUuid();
			}

			SortField fieldSelected = (SortField) sortSpinner.getSelectedItem();
			if (fieldSelected != null) {
				orderBy = fieldSelected.getField();
			}
			FillListViewEquipments(type, orderBy);
		}
	}

	private void FillListViewEquipments(String equipmentModelUuid,  String sort) {
        RealmResults<Equipment> equipments;
        if (equipmentModelUuid!=null) {
            if (sort!=null)
                equipments = realmDB.where(Equipment.class).equalTo("equipmentModelUuid", equipmentModelUuid).findAllSorted(sort);
            else
                equipments = realmDB.where(Equipment.class).equalTo("equipmentModelUuid", equipmentModelUuid).findAll();
        }
        else {
            if (sort!=null)
                equipments = realmDB.where(Equipment.class).findAllSorted(sort);
            else
                equipments = realmDB.where(Equipment.class).findAll();
        }
        equipmentAdapter = new EquipmentAdapter(getContext(),R.id.erl_equipment_listView, equipments);
        equipmentListView.setAdapter(equipmentAdapter);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isInit) {
			initView();
		}
	}

}
