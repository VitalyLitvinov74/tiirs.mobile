package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.MeasureType;

public class MeasuredValueAdapter extends RealmBaseAdapter<MeasureType> implements ListAdapter {
    public static final String TABLE_NAME = "MeasureType";

    public MeasuredValueAdapter(RealmResults<MeasureType> data) {
        super(data);
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.reference_listView) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder.title = convertView.findViewById(R.id.lv_firstLine);
                convertView.setTag(viewHolder);
            }

            if (parent.getId() == R.id.simple_spinner) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MeasureType measureType;
        if (adapterData != null && viewHolder.title != null) {
            measureType = adapterData.get(position);
            if (measureType != null) {
                viewHolder.title.setText(measureType.getTitle());
            }
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                measureType = adapterData.get(position);
                if (measureType != null) {
                    textView.setText(measureType.getTitle());
                }

                textView.setTextSize(16);
                textView.setPadding(5, 5, 5, 5);
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
}
