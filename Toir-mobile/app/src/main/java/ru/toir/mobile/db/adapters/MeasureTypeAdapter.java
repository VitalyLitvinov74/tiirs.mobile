package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.MeasureType;

public class MeasureTypeAdapter extends RealmBaseAdapter<MeasureType> implements ListAdapter {
    public static final String TABLE_NAME = "MeasureType";

    public MeasureTypeAdapter(@NonNull Context context, RealmResults<MeasureType> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MeasureType> rows = realm.where(MeasureType.class).findAll();
        return rows.size();
    }

    @Override
    public MeasureType getItem(int position) {
        MeasureType measureType = null;
        if (adapterData != null) {
            measureType = adapterData.get(position);
        }
        return measureType;
    }

    @Override
    public long getItemId(int position) {
        MeasureType measureType;
        if (adapterData != null) {
            measureType = adapterData.get(position);
            return measureType.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
          viewHolder = new ViewHolder();
          if (parent.getId() == R.id.reference_listView) {
              convertView = inflater.inflate(R.layout.listview, parent, false);
              viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
              convertView.setTag(viewHolder);
            }
          if (parent.getId() == R.id.simple_spinner) {
              convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
              viewHolder.title = (TextView) convertView.findViewById(android.R.id.text1);
              convertView.setTag(viewHolder);
            }
         } else {
         viewHolder = (ViewHolder) convertView.getTag();
       }

        MeasureType measureType;
        if (adapterData != null && viewHolder.title !=null) {
                measureType = adapterData.get(position);
                if (measureType != null)
                    viewHolder.title.setText(measureType.getTitle());
            }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                measureType = adapterData.get(position);
                if (measureType != null)
                    textView.setText(measureType.getTitle());
                textView.setTextSize(16);
                textView.setPadding(5,5,5,5);
            }
            return textView;
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, null, parent);
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
    }

    public class Type {
        public static final String NONE = "e9ade49a-3c31-42f8-a751-aaeb890c2190";
        public static final String FREQUENCY = "481c2e40-421e-41ab-8bc1-5fb0d01a4cc3";
        public static final String VOLTAGE = "1bec4685-466f-4aa6-95fc-a3c01baf09fe";
        public static final String PRESSURE = "69a71072-7edd-4ff9-b095-0ef145286d79";
        public static final String PHOTO = "8eb1cc6a-fbd5-4a4e-91ee-ca762b94473c";
    }
}
