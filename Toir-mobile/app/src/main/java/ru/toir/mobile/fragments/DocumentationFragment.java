package ru.toir.mobile.fragments;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.DocumentationAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeAdapter;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;

public class DocumentationFragment extends Fragment {
    private Realm realmDB;
    private boolean isInit;

    private Spinner sortSpinner;
    private Spinner typeSpinner;
    private ListView documentationListView;

    private ArrayAdapter<SortField> sortSpinnerAdapter;

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
                toolbar.setSubtitle("Документация");
            }
        }

        realmDB = Realm.getDefaultInstance();

        // обработчик для выпадающих списков у нас один
        SpinnerListener spinnerListener = new SpinnerListener();

        RealmResults<DocumentationType> documentationType = realmDB.where(DocumentationType.class).findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);
        DocumentationTypeAdapter typeSpinnerAdapter = new DocumentationTypeAdapter(documentationType);
        typeSpinnerAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        // настраиваем сортировку по полям
        sortSpinner = rootView.findViewById(R.id.documentation_spinner_sort);
        sortSpinnerAdapter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<SortField>());
        sortSpinner.setAdapter(sortSpinnerAdapter);
        sortSpinner.setOnItemSelectedListener(spinnerListener);

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
        fillSortFieldSpinner();
    }

    private void fillSortFieldSpinner() {

        sortSpinnerAdapter.clear();
        sortSpinnerAdapter.add(new SortField("Без сортировки", null));
        sortSpinnerAdapter.add(new SortField("По типу", "documentationType.title"));
        sortSpinnerAdapter.add(new SortField("По оборудованию", "equipment.title"));
        sortSpinnerAdapter.add(new SortField("По модели", "equipmentModel.title"));

    }

    private void FillListViewDocumentation(String documentationTypeUuid, String sort) {
        RealmResults<Documentation> documentation;
        RealmQuery<Documentation> query = realmDB.where(Documentation.class);
        if (documentationTypeUuid != null) {
            query.equalTo("documentationType.uuid", documentationTypeUuid);
            if (sort != null) {
                documentation = query.findAllSorted(sort);
            } else {
                documentation = query.findAll();
            }
        } else {
            if (sort != null) {
                documentation = query.findAllSorted(sort);
            } else {
                documentation = query.findAll();
            }
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
            String orderBy = null;

            DocumentationType typeSelected = (DocumentationType) typeSpinner.getSelectedItem();
            // так как список построен по данным из базы, в выборке нет "Все типы"
            // по этому, принудительно, при выборе первого элемента, показываем все данные из базы
            if (typeSpinner.getSelectedItemPosition() != 0) {
                if (typeSelected != null) {
                    type = typeSelected.getUuid();
                }
            }

            // сортировка указывается, даже работает, но в самом списке с файлами документации
            // не отображаются поля по которым идёт сортировка.
            SortField fieldSelected = (SortField) sortSpinner.getSelectedItem();
            if (fieldSelected != null) {
                orderBy = fieldSelected.getField();
            }

            FillListViewDocumentation(type, orderBy);
        }
    }
}
