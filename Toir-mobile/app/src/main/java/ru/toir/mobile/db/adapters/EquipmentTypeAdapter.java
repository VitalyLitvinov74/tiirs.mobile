package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.EquipmentType;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentTypeAdapter extends RealmBaseAdapter<EquipmentType> implements ListAdapter {
    public static final String TABLE_NAME = "EquipmentType";

    public EquipmentTypeAdapter(@NonNull Context context, RealmResults<EquipmentType> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EquipmentType> rows = realm.where(EquipmentType.class).findAll();
        return rows.size();
    }

    @Override
    public EquipmentType getItem(int position) {
        EquipmentType equipmentType = null;
        if (adapterData != null) {
            equipmentType = adapterData.get(position);
        }
        return equipmentType;
    }

    @Override
    public long getItemId(int position) {
        EquipmentType equipmentType;
        if (adapterData != null) {
            equipmentType = adapterData.get(position);
            return equipmentType.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            EquipmentType equipmentType;
            if (adapterData != null) {
                equipmentType = adapterData.get(position);
                viewHolder.title.setText(equipmentType.getTitle());
            }
            return convertView;
        }
        if (parent.getId() == R.id.simple_spinner || convertView == null) {
            TextView textView = new TextView(context);
            EquipmentType equipmentType;
            if (adapterData != null) {
                equipmentType = adapterData.get(position);
                textView.setText(equipmentType.getTitle());
                textView.setPadding(10, 20, 10, 20);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setTextColor(Color.WHITE);
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
