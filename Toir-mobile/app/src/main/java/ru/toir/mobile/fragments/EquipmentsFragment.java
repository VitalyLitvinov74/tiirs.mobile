package ru.toir.mobile.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentType;

public class EquipmentsFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private Spinner typeSpinner;
	private ListView equipmentListView;

    private String object_uuid;

    public static EquipmentsFragment newInstance() {
		return new EquipmentsFragment();
	}

	@Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.equipment_reference_layout, container, false);
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setSubtitle("Оборудование");
        realmDB = Realm.getDefaultInstance();

		// обработчик для выпадающих списков у нас один
        SpinnerListener spinnerListener = new SpinnerListener();

		// настраиваем сортировку по полям
        /*
		sortSpinner = (Spinner) rootView
				.findViewById(R.id.erl_sort_field_spinner);
		sortSpinnerAdapter = new ArrayAdapter<>(getContext(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<SortField>());
		sortSpinner.setAdapter(sortSpinnerAdapter);
		sortSpinner.setOnItemSelectedListener(spinnerListener);
*/
        equipmentListView = rootView.findViewById(R.id.erl_equipment_listView);

        RealmResults<EquipmentType> equipmentType = realmDB.where(EquipmentType.class).findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);
        EquipmentTypeAdapter typeSpinnerAdapter = new EquipmentTypeAdapter(equipmentType);
        typeSpinnerAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

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

		FillListViewEquipments(null);
//        fillSortFieldSpinner();
    }
/*
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
*/
	private void FillListViewEquipments(String equipmentTypeUuid) {
        RealmResults<Equipment> equipments;
        //String object_uuid = intent.getStringExtra("object_uuid");
        Bundle bundle = this.getArguments();
        if(bundle != null) {
            object_uuid = bundle.getString("object_uuid");
        }
        if (equipmentTypeUuid != null) {
            equipments = realmDB.where(Equipment.class).equalTo("equipmentModel.equipmentType.uuid", equipmentTypeUuid).findAll();
            if (object_uuid != null) {
                equipments = realmDB.where(Equipment.class).equalTo("location.uuid", object_uuid).equalTo("equipmentModel.equipmentType.uuid", equipmentTypeUuid).findAll();
            }
        } else {
            equipments = realmDB.where(Equipment.class).findAll();
            if (object_uuid != null) {
                equipments = realmDB.where(Equipment.class).equalTo("location.uuid", object_uuid).findAll();
            }
        }

        EquipmentAdapter equipmentAdapter = new EquipmentAdapter(equipments);
        equipmentListView.setAdapter(equipmentAdapter);
    }

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isInit) {
			initView();
		}
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    private class ListviewClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parentView,
                                View selectedItemView, int position, long id) {
            Equipment equipment = (Equipment)parentView.getItemAtPosition(position);
            if (equipment != null) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }

                String equipment_uuid = equipment.getUuid();
                Intent equipmentInfo = new Intent(activity, EquipmentInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("equipment_uuid", equipment_uuid);
                equipmentInfo.putExtras(bundle);
                activity.startActivity(equipmentInfo);
            }
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {
        //boolean userSelect = false;

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView,
                                   View selectedItemView, int position, long id) {

            String type = null;

            EquipmentType typeSelected = (EquipmentType) typeSpinner.getSelectedItem();
            if (typeSelected != null) {
                type = typeSelected.getUuid();
                //if (typeSelected.get_id() == 1) type = null;
            }

            FillListViewEquipments(type);
        }
    }
}
