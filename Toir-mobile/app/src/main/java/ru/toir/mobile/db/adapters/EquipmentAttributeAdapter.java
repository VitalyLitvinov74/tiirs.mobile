package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.EquipmentAttribute;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 18.03.19.
 */
public class EquipmentAttributeAdapter extends RealmBaseAdapter<EquipmentAttribute> implements ListAdapter {
    public static final String TABLE_NAME = "Documentation";

    public EquipmentAttributeAdapter(RealmResults<EquipmentAttribute> data) {
        super(data);
    }

    @Override
    public EquipmentAttribute getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        EquipmentAttribute equipmentAttribute;
        if (adapterData != null) {
            equipmentAttribute = adapterData.get(position);
            return equipmentAttribute.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;

        if (adapterData == null) {
            return convertView;
        }

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.firstLine = convertView.findViewById(R.id.lv_firstLine);
            viewHolder.secondLine = convertView.findViewById(R.id.lv_secondLine);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        EquipmentAttribute equipmentAttribute = adapterData.get(position);
        if (equipmentAttribute != null) {
            String firstLine = equipmentAttribute.getAttributeType().getName();
            firstLine = firstLine.concat(": ");
            firstLine = firstLine.concat(equipmentAttribute.getValue());
            viewHolder.firstLine.setText(firstLine);

            String secondLine = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US).format(equipmentAttribute.getDate());
            viewHolder.secondLine.setText(secondLine);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView firstLine;
        TextView secondLine;
    }
}
