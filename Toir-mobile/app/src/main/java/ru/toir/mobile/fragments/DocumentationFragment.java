package ru.toir.mobile.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
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
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.documentation_layout,
				container, false);
        realmDB = Realm.getDefaultInstance();

		// обработчик для выпадающих списков у нас один
		SpinnerListener spinnerListener = new SpinnerListener();

		RealmResults<DocumentationType> documentationType = realmDB.where(DocumentationType.class).findAll();
		typeSpinner = (Spinner) rootView.findViewById(R.id.simple_spinner);
        DocumentationTypeAdapter typeSpinnerAdapter = new DocumentationTypeAdapter(getContext(), documentationType);
		typeSpinnerAdapter.notifyDataSetChanged();
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
		fillSortFieldSpinner();
	}

	private void fillSortFieldSpinner() {

		sortSpinnerAdapter.clear();
		sortSpinnerAdapter.add(new SortField("Сортировка", null));
		sortSpinnerAdapter.add(new SortField("По типу",
				"documentationTypeUuid"));
		sortSpinnerAdapter.add(new SortField("По оборудованию",
				"equipmentUuid"));

	}

	private void FillListViewDocumentation(String documentationTypeUuid, String sort) {
		RealmResults<Documentation> documentation;
		if (documentationTypeUuid != null) {
			if (sort != null)
				documentation = realmDB.where(Documentation.class).equalTo("documentationTypeUuid", documentationTypeUuid).findAllSorted(sort);
			else
				documentation = realmDB.where(Documentation.class).equalTo("documentationTypeUuid", documentationTypeUuid).findAll();
		} else {
			if (sort != null)
				documentation = realmDB.where(Documentation.class).findAllSorted(sort);
			else
				documentation = realmDB.where(Documentation.class).findAll();
		}
        DocumentationAdapter documentationAdapter = new DocumentationAdapter(getContext(), documentation);
        documentationListView.setAdapter(documentationAdapter);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {

		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && isInit) {
			initView();
		}
	}

	public class ListviewClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
            Documentation documentation = (Documentation)parentView.getItemAtPosition(position);
            MimeTypeMap mt = MimeTypeMap.getSingleton();
            // TODO добавить путь
            File file = new File(documentation.getUuid());
            String[] patternList = file.getName().split("\\.");
            String extension = patternList[patternList.length - 1];

            if (mt.hasExtension(extension)) {
                String mimeType = mt.getMimeTypeFromExtension(extension);
                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(new File(documentation.getUuid())),
                        mimeType);
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                Intent viewFileIntent = Intent.createChooser(target,
                        "Open File");
                try {
                    startActivity(viewFileIntent);
                } catch (ActivityNotFoundException e) {
                    // сообщить пользователю установить подходящее
                    // приложение
                }
            }
		}
	}

	public class SpinnerListener implements AdapterView.OnItemSelectedListener {

		@Override
		public void onNothingSelected(AdapterView<?> parentView) {
		}

		@Override
		public void onItemSelected(AdapterView<?> parentView,
				View selectedItemView, int position, long id) {
			String type = null;
			String orderBy = null;

			DocumentationType typeSelected = (DocumentationType) typeSpinner
					.getSelectedItem();
			if (typeSelected != null) {
				type = typeSelected.getUuid();
                // временно неопределенный тип
                if (typeSelected.get_id() == 1) type = null;
			}

			SortField fieldSelected = (SortField) sortSpinner.getSelectedItem();
			if (fieldSelected != null) {
				orderBy = fieldSelected.getField();
			}
			FillListViewDocumentation(type, orderBy);
		}
	}

}
