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
import ru.toir.mobile.db.adapters.DocumentationAdapter;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentType;

public class DocumentationFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private Spinner sortSpinner;
	private Spinner typeSpinner;
	private ListView documentationListView;

	private ArrayAdapter<SortField> sortSpinnerAdapter;
	private ArrayAdapter<DocumentationType> typeSpinnerAdapter;

	private SpinnerListener spinnerListener;
    private DocumentationAdapter documentationAdapter;

    public static DocumentationFragment newInstance() {
		return new DocumentationFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.documentation_layout,
				container, false);
        realmDB = Realm.getDefaultInstance();

		// обработчик для выпадающих списков у нас один
		spinnerListener = new SpinnerListener();

		// настраиваем сортировку по типу оборудования
		typeSpinner = (Spinner) rootView.findViewById(R.id.documentation_spinner_type);
        typeSpinnerAdapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<DocumentationType>());
		typeSpinner.setAdapter(typeSpinnerAdapter);
		typeSpinner.setOnItemSelectedListener(spinnerListener);

		// настраиваем сортировку по полям
		sortSpinner = (Spinner) rootView
				.findViewById(R.id.documentation_spinner_sort);
		sortSpinnerAdapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<SortField>());
		sortSpinner.setAdapter(sortSpinnerAdapter);
		sortSpinner.setOnItemSelectedListener(spinnerListener);

		documentationListView = (ListView) rootView
				.findViewById(R.id.documentation_listView);

		documentationListView.setOnItemClickListener(new ListviewClickListener());

		initView();

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		isInit = true;

		return rootView;
	}

	private void initView() {

		FillListViewDocumentation(null, null);
		fillTypeSpinner();
		fillSortFieldSpinner();
	}

	private void fillTypeSpinner() {
        RealmResults<DocumentationType> documentationType = realmDB.where(DocumentationType.class).findAll();
		// TODO стоит наверное добавить запись "любой тип" напрямую в таблицу?
		typeSpinnerAdapter.clear();
		typeSpinnerAdapter.addAll(documentationType);
		typeSpinnerAdapter.notifyDataSetChanged();
	}

	private void fillSortFieldSpinner() {

		sortSpinnerAdapter.clear();
		sortSpinnerAdapter.add(new SortField("Сортировка", null));
		sortSpinnerAdapter.add(new SortField("По типу",
				"documentationTypeUuid"));
		sortSpinnerAdapter.add(new SortField("По оборудованию",
				"equipmentUuid"));

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
