package ru.toir.mobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentType;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;

public class EquipmentsFragment extends Fragment {
    private static final String TAG;

    static {
        TAG = EquipmentsFragment.class.getSimpleName();
    }

    private Realm realmDB;
    private boolean isInit;
    private Spinner typeSpinner;
    private ListView equipmentListView;
    private String object_uuid;
    private RfidDialog rfidDialog;

    public static EquipmentsFragment newInstance() {
        return new EquipmentsFragment();
    }

    private static void showEquipmentInfoActivity(Context context, String uuid) {
        Intent equipmentInfo = new Intent(context, EquipmentInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("equipment_uuid", uuid);
        equipmentInfo.putExtras(bundle);
        context.startActivity(equipmentInfo);
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
        toolbar.setSubtitle(getString(R.string.menu_equipment));
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

        RealmResults<Equipment> equipment = realmDB.where(Equipment.class).findAll();
        Set<String> equipmentTypeUuids = new HashSet<>();
        for (Equipment item : equipment) {
            equipmentTypeUuids.add(item.getEquipmentModel().getEquipmentType().getUuid());
        }
        RealmResults<EquipmentType> equipmentTypes = realmDB.where(EquipmentType.class)
                .in("uuid", equipmentTypeUuids.toArray(new String[]{}))
                .findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);
        EquipmentTypeAdapter typeSpinnerAdapter = new EquipmentTypeAdapter(equipmentTypes);
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

        FloatingActionButton readRfidButton = rootView.findViewById(R.id.fab_readRfid);
        readRfidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler(new Handler.Callback() {

                    @Override
                    public boolean handleMessage(Message msg) {
                        Log.d(TAG, "Получили сообщение из драйвера.");

                        if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                            final String tagId = ((String) msg.obj).substring(4);
                            Log.d(TAG, tagId);
                            Toast.makeText(getActivity(),
                                    "Чтение метки успешно.", Toast.LENGTH_SHORT)
                                    .show();
                            Realm realm = Realm.getDefaultInstance();
                            Equipment equipment = realm.where(Equipment.class)
                                    .equalTo("tagId", tagId)
                                    .findFirst();
                            if (equipment != null) {
                                showEquipmentInfoActivity(getActivity(), equipment.getUuid());
                            } else {
                                Call<Equipment> callGetByTagId = ToirAPIFactory.getEquipmentService()
                                        .getByTagId(tagId);
                                Callback<Equipment> callback = new Callback<Equipment>() {
                                    @Override
                                    public void onResponse(Call<Equipment> responseBodyCall, Response<Equipment> response) {
                                        if (response.code() != 200) {
                                            Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                                        }

                                        Equipment equipment = response.body();
                                        if (equipment != null) {
                                            Realm realm = Realm.getDefaultInstance();
                                            realm.beginTransaction();
                                            realm.copyToRealmOrUpdate(equipment);
                                            realm.commitTransaction();
                                            realm.close();
                                            showEquipmentInfoActivity(getActivity(), equipment.getUuid());
                                        } else {
                                            Toast.makeText(getActivity(), getString(R.string.error_equipment_not_found),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Equipment> responseBodyCall, Throwable t) {
                                        Toast.makeText(getActivity(), getString(R.string.error_equipment_read_tag_error),
                                                Toast.LENGTH_LONG).show();
                                        t.printStackTrace();
                                    }
                                };
                                callGetByTagId.enqueue(callback);
                            }

                            realm.close();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.error_read_tag),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // закрываем диалог
                        rfidDialog.dismiss();
                        return true;
                    }
                });

                rfidDialog = new RfidDialog();
                rfidDialog.setHandler(handler);
                rfidDialog.readTagId();
                rfidDialog.show(getActivity().getFragmentManager(), TAG);
            }
        });

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
        if (bundle != null) {
            object_uuid = bundle.getString("object_uuid");
        }
        if (equipmentTypeUuid != null) {
            equipments = realmDB.where(Equipment.class)
                    .equalTo("equipmentModel.equipmentType.uuid", equipmentTypeUuid)
                    .findAll();
            if (object_uuid != null) {
                equipments = realmDB.where(Equipment.class).equalTo("location.uuid", object_uuid)
                        .equalTo("equipmentModel.equipmentType.uuid", equipmentTypeUuid)
                        .findAll();
            }
        } else {
            equipments = realmDB.where(Equipment.class).findAll();
            if (object_uuid != null) {
                equipments = realmDB.where(Equipment.class).equalTo("location.uuid", object_uuid)
                        .findAll();
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
            Equipment equipment = (Equipment) parentView.getItemAtPosition(position);
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
