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
import ru.toir.mobile.multi.db.realm.EquipmentType;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentTypeAdapter extends RealmBaseAdapter<EquipmentType> implements ListAdapter {
    public static final String TABLE_NAME = "EquipmentType";

    public EquipmentTypeAdapter(RealmResults<EquipmentType> data) {
        super(data);
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
              //convertView = inflater.inflate(R.layout.simple_spinner_item, parent, false);
              //convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
              convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
              //viewHolder.title = (TextView) convertView.findViewById(R.id.spinner_item);
              viewHolder.title = convertView.findViewById(android.R.id.text1);
              convertView.setTag(viewHolder);
            }
         } else {
         viewHolder = (ViewHolder) convertView.getTag();
       }

        EquipmentType equipmentType;
        if (adapterData != null && viewHolder.title !=null) {
                equipmentType = adapterData.get(position);
                if (equipmentType != null)
                    viewHolder.title.setText(equipmentType.getTitle());
            }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                equipmentType = adapterData.get(position);
                if (equipmentType != null)
                    textView.setText(equipmentType.getTitle());
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
}
