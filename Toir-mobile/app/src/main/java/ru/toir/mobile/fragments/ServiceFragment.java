package ru.toir.mobile.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.GPSTrackAdapter;
import ru.toir.mobile.db.adapters.JournalAdapter;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.Journal;

public class ServiceFragment extends Fragment {
    private Realm realmDB;
    private ListView journalListView;
    private ListView gpsListView;

    public static ServiceFragment newInstance() {
        return new ServiceFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_service,
                container, false);

        journalListView = (ListView) rootView
                .findViewById(R.id.service_list_view_journal);
        gpsListView = (ListView) rootView
                .findViewById(R.id.service_list_view_gps);

        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Сервис");
        realmDB = Realm.getDefaultInstance();

        RealmResults<Journal> journal;
        journal = realmDB.where(Journal.class).findAll().sort("date", Sort.DESCENDING);
        JournalAdapter journalAdapter = new JournalAdapter(getContext(), journal);
        journalListView.setAdapter(journalAdapter);

        RealmResults<GpsTrack> gpsTrack;
        gpsTrack = realmDB.where(GpsTrack.class).findAll().sort("date", Sort.DESCENDING);
        GPSTrackAdapter gpsAdapter = new GPSTrackAdapter(getContext(), gpsTrack);
        gpsListView.setAdapter(gpsAdapter);

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }
}
