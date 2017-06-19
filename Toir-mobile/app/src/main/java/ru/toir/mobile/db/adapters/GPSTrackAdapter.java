package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.GpsTrack;

/**
 * @author olejek
 * Created by olejek on 25.01.17
 */
public class GPSTrackAdapter extends RealmBaseAdapter<GpsTrack> implements ListAdapter {
    public static final String TABLE_NAME = "GPSTrack";

    public GPSTrackAdapter(@NonNull Context context, RealmResults<GpsTrack> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }

        return 0;
    }

    public RealmResults<GpsTrack> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<GpsTrack> result = realm.where(GpsTrack.class).findAll();
        realm.close();
        return result;
    }

    @Override
    public GpsTrack getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        GpsTrack gpsTrack;
        if (adapterData != null) {
            gpsTrack = adapterData.get(position);
            return gpsTrack.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_with_3_columns, parent, false);
            viewHolder.date = (TextView) convertView.findViewById(R.id.row3_date);
            viewHolder.latitude = (TextView) convertView.findViewById(R.id.row3_latitude);
            viewHolder.longitude = (TextView) convertView.findViewById(R.id.row3_longitude);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GpsTrack gpsTrack;
        if (adapterData != null && viewHolder.date != null) {
            gpsTrack = adapterData.get(position);
            if (gpsTrack != null) {
                String sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US).format(gpsTrack.getDate());
                viewHolder.date.setText(sDate);
                viewHolder.latitude.setText(gpsTrack.getLatitude() + "");
                viewHolder.longitude.setText(gpsTrack.getLatitude() + "");
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView date;
        TextView latitude;
        TextView longitude;
    }

}
