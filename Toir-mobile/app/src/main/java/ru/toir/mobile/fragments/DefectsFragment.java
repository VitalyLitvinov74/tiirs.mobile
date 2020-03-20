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

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.DefectInfoActivity;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.DefectAdapter;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;

public class DefectsFragment extends Fragment {
    private static final String TAG;

    static {
        TAG = DefectsFragment.class.getSimpleName();
    }

    private Realm realmDB;
    private boolean isInit;
    private Spinner typeSpinner;
    private ListView defectListView;
    private String equipment_uuid;
    private RfidDialog rfidDialog;

    public static DefectsFragment newInstance() {
        return new DefectsFragment();
    }

    private static void showDefectInfoActivity(Context context, String uuid) {
        Intent defectInfo = new Intent(context, DefectInfoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("defect_uuid", uuid);
        defectInfo.putExtras(bundle);
        context.startActivity(defectInfo);
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

        // обработчик для выпадающих списков у нас один
        SpinnerListener spinnerListener = new SpinnerListener();
        defectListView = rootView.findViewById(R.id.erl_defect_listView);

/*
        RealmResults<Defect> defects = realmDB.where(Defect.class).findAll();
        Set<String> defectTypeUuids = new HashSet<>();
        for (Defect item : defects) {
            defectTypeUuids.add(item.getDefectType().getUuid());
        }
        RealmResults<DefectType> defectTypes = realmDB.where(DefectType.class).findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);
        DefectTypeAdapter typeSpinnerAdapter = new DefectTypeAdapter(defectTypes);
        typeSpinnerAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);
*/
        defectListView.setOnItemClickListener(new ListviewClickListener());

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

        addDefectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
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
                                EquipmentInfoActivity.showDialogDefect2((ViewGroup) v.getParent(), getLayoutInflater(), v.getContext(), equipment.getUuid());
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
                                            EquipmentInfoActivity.showDialogDefect2((ViewGroup) v.getParent(), getLayoutInflater(), v.getContext(), equipment.getUuid());
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
        FillListViewDefects(null);
    }

    private void FillListViewDefects(String defectTypeUuid) {
        RealmResults<Defect> defects;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            equipment_uuid = bundle.getString("equipment_uuid");
        }
        if (defectTypeUuid != null) {
            defects = realmDB.where(Defect.class)
                    .equalTo("defectType.uuid", defectTypeUuid)
                    .sort("createdAt", Sort.DESCENDING)
                    .findAll();
            if (equipment_uuid != null) {
                defects = realmDB.where(Defect.class).equalTo("equipment.uuid", equipment_uuid)
                        .equalTo("defectType.uuid", defectTypeUuid)
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
        }

        DefectAdapter defectAdapter = new DefectAdapter(defects);
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
            DefectType typeSelected = (DefectType) typeSpinner.getSelectedItem();
            if (typeSelected != null) {
                type = typeSelected.getUuid();
            }
            FillListViewDefects(type);
        }
    }
}
