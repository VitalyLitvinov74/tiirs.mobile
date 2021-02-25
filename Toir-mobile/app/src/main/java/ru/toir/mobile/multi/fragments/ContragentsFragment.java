package ru.toir.mobile.multi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.adapters.ContragentAdapter;
import ru.toir.mobile.multi.db.realm.Contragent;

public class ContragentsFragment extends Fragment {
    private Realm realmDB;
    private Activity mainActivityConnector = null;

    public static ContragentsFragment newInstance() {
		return new ContragentsFragment();
	}

	@Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainActivityConnector = getActivity();
        if (mainActivityConnector == null)
            return null;
        View rootView = inflater.inflate(R.layout.contragent_reference_layout, container, false);
        Toolbar toolbar = mainActivityConnector.findViewById(R.id.toolbar);
        toolbar.setSubtitle(getString(R.string.contragents));
        realmDB = Realm.getDefaultInstance();

        ListView contragentsListView = rootView.findViewById(R.id.crl_contragents_listView);
        contragentsListView.setOnItemClickListener(new ListviewClickListener());
        contragentsListView.setTextFilterEnabled(true);

        RealmResults<Contragent> contragents;
        contragents = realmDB.where(Contragent.class).findAll();
        final ContragentAdapter contragentAdapter = new ContragentAdapter(contragents);
        contragentsListView.setAdapter(contragentAdapter);

        final EditText contragentFilter = rootView.findViewById(R.id.contragent_filter);
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

		return rootView;
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
            Contragent contragent = (Contragent) parentView.getItemAtPosition(position);
            if (contragent != null) {
                String contragent_uuid = contragent.getUuid();
                Intent contragentInfo = new Intent(getActivity(),
                        EquipmentInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("contragent_uuid", contragent_uuid);
                contragentInfo.putExtras(bundle);
                mainActivityConnector.startActivity(contragentInfo);
            }
        }
    }
}
