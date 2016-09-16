package ru.toir.mobile.fragments;

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
import ru.toir.mobile.R;
import ru.toir.mobile.db.SortField;
import ru.toir.mobile.db.adapters.DocumentationAdapter;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.DocumentationType;

public class DocumentationFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private Spinner sortSpinner;
	private Spinner typeSpinner;
	private ListView documentationListView;

	private ArrayAdapter<SortField> sortSpinnerAdapter;
	//private ArrayAdapter<DocumentationType> typeSpinnerAdapter;
    private ArrayAdapter<String> typeSpinnerAdapter;

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
        //typeSpinnerAdapter = new ArrayAdapter<>(getContext(),
		//		android.R.layout.simple_spinner_dropdown_item,
		//		new ArrayList<DocumentationType>());
        typeSpinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<String>());
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

        //DocumentationType allDocumentationTypes = new DocumentationType();
        //allDocumentationTypes.set_id(0);
        //allDocumentationTypes.setTitle("Все типы");
        //allDocumentationTypes.setUuid(null);
        typeSpinnerAdapter.add("Все типы");
        for (int i=0;i<documentationType.size();i++)
            typeSpinnerAdapter.add(documentationType.get(i).getTitle());
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
            //Documentation documentation = (Documentation)parentView.getItemAtPosition(position);
            //documentation = (Documentation)parentView.getSelectedItem();
			//String documentation_uuid = documentation.getUuid();
            // TODO добавить вывод документации на экран
			/*Intent documentationInfo = new Intent(getActivity(),
					EquipmentInfoActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("equipment_uuid", equipment_uuid);
			equipmentInfo.putExtras(bundle);
			getActivity().startActivity(equipmentInfo);*/
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
            /*
			String type = null;
			String orderBy = null;

			DocumentationType typeSelected = (DocumentationType) typeSpinner
					.getSelectedItem();
			if (typeSelected != null) {
				type = typeSelected.getUuid();
			}

			SortField fieldSelected = (SortField) sortSpinner.getSelectedItem();
			if (fieldSelected != null) {
				orderBy = fieldSelected.getField();
			}
			FillListViewDocumentation(type, orderBy);
			*/
		}
	}

	private void FillListViewDocumentation(String documentationTypeUuid,  String sort) {
        RealmResults<Documentation> documentation;
        if (documentationTypeUuid!=null) {
            if (sort!=null)
                //documentation = realmDB.where(Documentation.class).equalTo("documentationTypeUuid", documentationTypeUuid).findAllSorted(sort);
                documentation = realmDB.where(Documentation.class).equalTo("documentationType.uuid", documentationTypeUuid).findAllSorted(sort);
            else
                //documentation = realmDB.where(Documentation.class).equalTo("documentationTypeUuid", documentationTypeUuid).findAll();
                documentation = realmDB.where(Documentation.class).equalTo("documentationType.uuid", documentationTypeUuid).findAll();
        }
        else {
            if (sort!=null)
                documentation = realmDB.where(Documentation.class).findAllSorted(sort);
            else
                documentation = realmDB.where(Documentation.class).findAll();
        }
        documentationAdapter = new DocumentationAdapter(getContext(),R.id.documentation_listView, documentation);
        documentationListView.setAdapter(documentationAdapter);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isInit) {
			initView();
		}
	}

}
