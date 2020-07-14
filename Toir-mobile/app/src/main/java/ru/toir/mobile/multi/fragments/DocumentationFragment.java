package ru.toir.mobile.multi.fragments;

import android.app.Activity;
import android.content.Context;
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

import java.io.File;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.adapters.DocumentationAdapter;
import ru.toir.mobile.multi.db.adapters.DocumentationTypeAdapter;
import ru.toir.mobile.multi.db.adapters.EquipmentModelAdapter;
import ru.toir.mobile.multi.db.realm.Documentation;
import ru.toir.mobile.multi.db.realm.DocumentationType;
import ru.toir.mobile.multi.db.realm.EquipmentModel;

public class DocumentationFragment extends Fragment {
    private Realm realmDB;
    private boolean isInit;

    private Spinner modelSpinner;
    private Spinner typeSpinner;
    private ListView documentationListView;

    public static DocumentationFragment newInstance() {
        return new DocumentationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.documentation_layout, container, false);

        Activity activity = getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setSubtitle(getString(R.string.documentation));
            }
        }

        realmDB = Realm.getDefaultInstance();

        // обработчик для выпадающих списков у нас один
        SpinnerListener spinnerListener = new SpinnerListener();

        RealmResults<DocumentationType> documentationType = realmDB.where(DocumentationType.class).findAll();
        typeSpinner = rootView.findViewById(R.id.documentation_type_sort);
        DocumentationTypeAdapter typeSpinnerAdapter = new DocumentationTypeAdapter(documentationType);
        typeSpinnerAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        RealmResults<EquipmentModel> equipmentModel = realmDB.where(EquipmentModel.class).findAll();
        modelSpinner = rootView.findViewById(R.id.documentation_model_sort);
        EquipmentModelAdapter equipmentModelAdapter = new EquipmentModelAdapter(equipmentModel);
        equipmentModelAdapter.notifyDataSetChanged();
        modelSpinner.setAdapter(equipmentModelAdapter);
        modelSpinner.setOnItemSelectedListener(spinnerListener);

        documentationListView = rootView.findViewById(R.id.documentation_listView);
        documentationListView.setOnItemClickListener(new ListviewClickListener());

        initView();

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        isInit = true;

        return rootView;
    }

    private void initView() {
        FillListViewDocumentation(null, null);
    }

    private void FillListViewDocumentation(String documentationTypeUuid, String equipmentModelUuid) {
        RealmResults<Documentation> documentation;
        RealmQuery<Documentation> query = realmDB.where(Documentation.class);
        if (documentationTypeUuid != null) {
            query.equalTo("documentationType.uuid", documentationTypeUuid);
        }
        if (equipmentModelUuid != null) {
            documentation = query.equalTo("equipmentModel.uuid", equipmentModelUuid).findAll();
        } else {
            documentation = query.findAll();
        }
        DocumentationAdapter documentationAdapter = new DocumentationAdapter(documentation);
        documentationListView.setAdapter(documentationAdapter);
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
            Context context = getContext();
            if (context != null) {
                Documentation documentation = (Documentation) parentView.getItemAtPosition(position);
                File file = new File(context.getExternalFilesDir(documentation.getImageFilePath()),
                        documentation.getPath());
                if (file.exists()) {
                    Intent intent = EquipmentInfoActivity.showDocument(file, getContext());
                    if (intent != null) {
                        startActivity(intent);
                    }
                } else {
                    // TODO: либо сообщить что файла нет, либо запустить какой-то процесс его получения

                }
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
            String model = null;

            DocumentationType typeSelected = (DocumentationType) typeSpinner.getSelectedItem();
            // так как список построен по данным из базы, в выборке нет "Все типы"
            // по этому, принудительно, при выборе первого элемента, показываем все данные из базы
            if (typeSpinner.getSelectedItemPosition() != 0) {
                if (typeSelected != null) {
                    type = typeSelected.getUuid();
                }
            }

            EquipmentModel modelSelected = (EquipmentModel) modelSpinner.getSelectedItem();
            if (modelSpinner.getSelectedItemPosition() != 0) {
                if (modelSelected != null) {
                    model = modelSelected.getUuid();
                }
            }

            FillListViewDocumentation(type, model);
        }
    }
}
