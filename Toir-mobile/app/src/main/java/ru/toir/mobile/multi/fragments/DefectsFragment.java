package ru.toir.mobile.multi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
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

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.multi.DefectInfoActivity;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.adapters.DefectAdapter;
import ru.toir.mobile.multi.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.multi.db.realm.Defect;
import ru.toir.mobile.multi.db.realm.Equipment;
import ru.toir.mobile.multi.db.realm.EquipmentType;
import ru.toir.mobile.multi.rest.ToirAPIFactory;
import ru.toir.mobile.multi.rfid.RfidDialog;
import ru.toir.mobile.multi.rfid.RfidDriverBase;
import ru.toir.mobile.multi.rfid.RfidDriverMsg;

public class DefectsFragment extends Fragment {
    private static final String TAG;

    static {
        TAG = DefectsFragment.class.getSimpleName();
    }

    private Realm realmDB;
    private boolean isInit;
    private int first;
    private Spinner typeSpinner;
    private ListView defectListView;
    private DefectAdapter defectAdapter;
    private String equipment_uuid;
    private RfidDialog rfidDialog;

    public static DefectsFragment newInstance() {
        return new DefectsFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.defects_layout, container, false);
        final Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setSubtitle(getString(R.string.menu_defects));
        realmDB = Realm.getDefaultInstance();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);
        SpinnerListener spinnerListener = new SpinnerListener();
        defectListView = rootView.findViewById(R.id.erl_defect_listView);
        defectListView.setOnItemClickListener(new ListviewClickListener());
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        FloatingActionButton addDefectButton = rootView.findViewById(R.id.fab_add_defect);
        //  Было просто вызов окна, стал вызов чтения метки
/*
        addDefectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EquipmentInfoActivity.showDialogDefect2((ViewGroup) v.getParent(), inflater, v.getContext(), equipment_uuid);
            }
        });
*/

        addDefectButton.setOnClickListener(v -> {
            Handler handler = new Handler(msg -> {
                Log.d(TAG, "Получили сообщение из драйвера.");
                if (msg.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                    final String tagId = ((RfidDriverMsg) msg.obj).getTagId();
                    Log.d(TAG, tagId);
                    try {
                        Toast.makeText(getActivity(),
                                "Чтение метки успешно.", Toast.LENGTH_SHORT)
                                .show();
                    } catch (Exception e) {
                    }

                    Realm realm = Realm.getDefaultInstance();
                    Equipment equipment = realm.where(Equipment.class)
                            .equalTo("tagId", tagId)
                            .findFirst();
                    if (equipment != null) {
                        EquipmentInfoActivity.showDialogDefect2((ViewGroup) v.getParent(), getLayoutInflater(), v.getContext(), equipment.getUuid(), defectAdapter);
                    } else {
                        Call<Equipment> callGetByTagId = ToirAPIFactory.getEquipmentService()
                                .getByTagId(tagId);
                        Callback<Equipment> callback = new Callback<Equipment>() {
                            @Override
                            public void onResponse(Call<Equipment> responseBodyCall, Response<Equipment> response) {
                                if (response.code() != 200) {
                                    try {
                                        Toast.makeText(getContext(), response.message(), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                    }
                                }

                                Equipment equipment = response.body();
                                if (equipment != null) {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(equipment);
                                    realm.commitTransaction();
                                    realm.close();
                                    EquipmentInfoActivity.showDialogDefect2((ViewGroup) v.getParent(), getLayoutInflater(), v.getContext(), equipment.getUuid(), defectAdapter);
                                } else {
                                    try {
                                        Toast.makeText(getActivity(), getString(R.string.error_equipment_not_found),
                                                Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Equipment> responseBodyCall, Throwable t) {
                                try {
                                    Toast.makeText(getActivity(), getString(R.string.error_equipment_read_tag_error),
                                            Toast.LENGTH_LONG).show();
                                    t.printStackTrace();
                                } catch (Exception e) {
                                }
                            }
                        };
                        callGetByTagId.enqueue(callback);
                    }

                    realm.close();
                } else {
                    try {
                        Toast.makeText(getActivity(), getString(R.string.error_read_tag),
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                }

                // закрываем диалог
                rfidDialog.dismiss();
                return true;
            });

            rfidDialog = new RfidDialog();
            rfidDialog.setHandler(handler);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String driverClass = sp.getString(getActivity()
                    .getString(R.string.default_rfid_driver_key), "");
            rfidDialog.readTagId(driverClass);
            rfidDialog.show(getActivity().getFragmentManager(), TAG);
        });

        initView();
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        isInit = true;

        return rootView;
    }

    private void initView() {
        // обработчик для выпадающих списков у нас один
        FillListViewDefects(null);
    }

    private void FillListViewDefects(String equipmentTypeUuid) {
        RealmResults<Defect> defects;
        List<String> defectEquipmentTypeUuid = new ArrayList<>();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            equipment_uuid = bundle.getString("equipment_uuid");
        }
        if (equipmentTypeUuid != null) {
            defects = realmDB.where(Defect.class)
                    .equalTo("equipment.equipmentModel.equipmentType.uuid", equipmentTypeUuid)
                    .sort("createdAt", Sort.DESCENDING)
                    .findAll();
            if (equipment_uuid != null) {
                defects = realmDB.where(Defect.class).equalTo("equipment.uuid", equipment_uuid)
                        .sort("createdAt", Sort.DESCENDING)
                        .findAll();
            }
        } else {
            defects = realmDB.where(Defect.class).sort("createdAt", Sort.DESCENDING).findAll();
            if (equipment_uuid != null) {
                defects = realmDB.where(Defect.class)
                        .equalTo("equipment.uuid", equipment_uuid)
                        .sort("createdAt", Sort.DESCENDING)
                        .findAll();
            }
            for (Defect item : defects) {
                defectEquipmentTypeUuid.add(item.getEquipment().getEquipmentModel().getEquipmentType().getUuid());
            }
            RealmResults<EquipmentType> equipmentTypes = realmDB.where(EquipmentType.class)
                    .in("uuid", defectEquipmentTypeUuid.toArray(new String[]{}))
                    .findAll();
            EquipmentTypeAdapter typeSpinnerAdapter = new EquipmentTypeAdapter(equipmentTypes);
            typeSpinnerAdapter.notifyDataSetChanged();
            typeSpinner.setAdapter(typeSpinnerAdapter);
        }
        defectAdapter = new DefectAdapter(defects);
        defectListView.setAdapter(defectAdapter);
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
            Defect defect = (Defect) parentView.getItemAtPosition(position);
            if (defect != null) {
                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }

                String defect_uuid = defect.getUuid();
                Intent defectInfo = new Intent(activity, DefectInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("defect_uuid", defect_uuid);
                defectInfo.putExtras(bundle);
                activity.startActivity(defectInfo);
            }
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {

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
            }
            // в первый заход мы строим полный список, второй это выбор по-умолчанию фильтра, его мы игнорируем
            if (first > 0) {
                FillListViewDefects(type);
            } else {
                first++;
            }
        }
    }
}
