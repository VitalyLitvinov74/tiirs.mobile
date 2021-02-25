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
import ru.toir.mobile.multi.db.realm.DefectType;

/**
 * @author olejek
 *         Created by olejek on 03.05.17.
 */
public class DefectTypeAdapter extends RealmBaseAdapter<DefectType> implements ListAdapter {
    public static final String TABLE_NAME = "DefectType";

    public DefectTypeAdapter(RealmResults<DefectType> data) {
        super(data);
    }

    @Override
    public DefectType getItem(int position) {
        DefectType defectType = null;
        if (adapterData != null) {
            defectType = adapterData.get(position);
        }
        return defectType;
    }

    @Override
    public long getItemId(int position) {
        if (adapterData != null && position < adapterData.size()) {
            return adapterData.get(position).get_id();
        }

        return -1;
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
            } else if (parent.getId() == R.id.spinner_defect_type) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DefectType defectType;
        if (adapterData != null && viewHolder.title != null && adapterData.size() > 0) {
            defectType = adapterData.get(position);
            if (defectType != null)
                viewHolder.title.setText(defectType.getTitle());
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                defectType = adapterData.get(position);
                if (defectType != null) {
                    textView.setText(defectType.getTitle());
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
