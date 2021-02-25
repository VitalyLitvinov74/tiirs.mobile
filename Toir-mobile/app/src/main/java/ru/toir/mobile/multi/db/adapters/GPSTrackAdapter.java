package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.GpsTrack;

/**
 * @author olejek
 * Created by olejek on 25.01.17
 */
public class GPSTrackAdapter extends RealmBaseAdapter<GpsTrack> implements ListAdapter {
    public static final String TABLE_NAME = "GPSTrack";

    public GPSTrackAdapter(RealmResults<GpsTrack> data) {
        super(data);
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_with_3_columns, parent, false);
            viewHolder.date = convertView.findViewById(R.id.row3_date);
            viewHolder.latitude = convertView.findViewById(R.id.row3_latitude);
            viewHolder.longitude = convertView.findViewById(R.id.row3_longitude);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GpsTrack gpsTrack;
        if (adapterData != null && viewHolder.date != null) {
            gpsTrack = adapterData.get(position);
            if (gpsTrack != null) {
                String sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US)
                        .format(gpsTrack.getDate());
                viewHolder.date.setText(sDate);
                viewHolder.latitude.setText(String.valueOf(gpsTrack.getLatitude()));
                viewHolder.longitude.setText(String.valueOf(gpsTrack.getLatitude()));
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
