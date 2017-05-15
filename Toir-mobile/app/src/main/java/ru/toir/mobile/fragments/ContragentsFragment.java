package ru.toir.mobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.EquipmentInfoActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.ContragentAdapter;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.db.realm.Contragent;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentType;

public class ContragentsFragment extends Fragment {
    private Realm realmDB;
	private boolean isInit;

	private EditText filter;
	private ListView contragentsListView;

    private String object_uuid;

    public static ContragentsFragment newInstance() {
		return new ContragentsFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.contragent_reference_layout, container, false);
        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Клиенты");
        realmDB = Realm.getDefaultInstance();

        contragentsListView = (ListView) rootView.findViewById(R.id.crl_contragents_listView);
        contragentsListView.setOnItemClickListener(new ListviewClickListener());
        contragentsListView.setTextFilterEnabled(true);

        RealmResults<Contragent> contragents;
        contragents = realmDB.where(Contragent.class).findAll();
        final ContragentAdapter contragentAdapter = new ContragentAdapter(getContext(), contragents);
        contragentsListView.setAdapter(contragentAdapter);

        final EditText contragentFilter = (EditText) rootView.findViewById(R.id.contragent_filter);
        contragentFilter.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String text = contragentFilter.getText().toString().toLowerCase(Locale.getDefault());
                contragentAdapter.setFilter(text, realmDB);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		isInit = true;

		return rootView;
	}

    private class ListviewClickListener implements
            AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parentView,
                                View selectedItemView, int position, long id) {
            Contragent contragent = (Contragent) parentView.getItemAtPosition(position);
            if (contragent != null) {
                String contragent_uuid = contragent.getUuid();
                Intent contragentInfo = new Intent(getActivity(),
                        EquipmentInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("contragent_uuid", contragent_uuid);
                contragentInfo.putExtras(bundle);
                getActivity().startActivity(contragentInfo);
            }
        }
    }
}
