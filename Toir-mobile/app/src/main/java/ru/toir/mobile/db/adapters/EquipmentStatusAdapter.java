package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.EquipmentStatus;
import ru.toir.mobile.db.realm.MeasureType;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentStatusAdapter extends RealmBaseAdapter<EquipmentStatus> implements ListAdapter {
    public static final String TABLE_NAME = "EquipmentStatus";

    private static class ViewHolder{
        TextView uuid;
        TextView title;
        ImageView icon;
    }

    @Override
    public EquipmentStatus getItem(int position) {
        EquipmentStatus equipmentStatus = null;
        if (adapterData != null) {
            equipmentStatus = adapterData.get(position);
        }
        return equipmentStatus;
    }

    @Override
    public long getItemId(int position) {
        EquipmentStatus equipmentStatus;
        if (adapterData != null) {
            equipmentStatus = adapterData.get(position);
            return equipmentStatus.get_id();
        }
        return 0;
    }


    public EquipmentStatusAdapter(@NonNull Context context, int resId, RealmResults<EquipmentStatus> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EquipmentStatus> rows = realm.where(EquipmentStatus.class).findAll();
        return rows.size();
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
            if (parent.getId() == R.id.simple_spinner || parent.getId() == R.id.spinner_status) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EquipmentStatus equipmentStatus;
        if (adapterData != null && viewHolder.title != null) {
            equipmentStatus = adapterData.get(position);
            if (equipmentStatus != null)
                viewHolder.title.setText(equipmentStatus.getTitle());
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                equipmentStatus = adapterData.get(position);
                if (equipmentStatus != null)
                    textView.setText(equipmentStatus.getTitle());
                    textView.setTextSize(18);
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
}
