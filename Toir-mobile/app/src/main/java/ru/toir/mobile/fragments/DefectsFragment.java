package ru.toir.mobile.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import io.realm.Sort;
import ru.toir.mobile.DefectInfoActivity;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.DefectAdapter;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;

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

        View rootView = inflater.inflate(R.layout.defects_layout, container, false);
        Activity activity = getActivity();
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
        addDefectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EquipmentInfoActivity.showDialogDefect2((ViewGroup) v.getParent(), inflater, v.getContext(), equipment_uuid);
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
