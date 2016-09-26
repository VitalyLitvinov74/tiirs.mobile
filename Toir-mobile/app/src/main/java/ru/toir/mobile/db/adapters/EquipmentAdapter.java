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
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.utils.DataUtils;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentAdapter extends RealmBaseAdapter<Equipment> implements ListAdapter {

    public static final String TABLE_NAME = "Equipment";

    public EquipmentAdapter(@NonNull Context context, RealmResults<Equipment> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Equipment> rows = realm.where(Equipment.class).findAll();
        return rows.size();
    }

    @Override
    public Equipment getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Equipment equipment;
        if (adapterData != null) {
            equipment = adapterData.get(position);
            return equipment.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.equipment_reference_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.equipmentStatusUuid = (TextView) convertView.findViewById(R.id.eril_status);
            viewHolder.criticalTypeUuid = (TextView) convertView.findViewById(R.id.eril_critical);
            viewHolder.startDate = (TextView) convertView.findViewById(R.id.eril_last_operation_date);
            //viewHolder.location = (TextView) convertView.findViewById(R.id.eril_location);
            viewHolder.equipmentModelUuid = (TextView) convertView.findViewById(R.id.eril_type);
            viewHolder.inventoryNumber = (TextView) convertView.findViewById(R.id.eril_inventory_number);
            viewHolder.title = (TextView) convertView.findViewById(R.id.eril_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Equipment equipment;
        if (adapterData != null) {
            equipment = adapterData.get(position);
            viewHolder.title.setText(equipment.getTitle());
            viewHolder.inventoryNumber.setText(equipment.getInventoryNumber());
            viewHolder.equipmentModelUuid.setText(equipment.getEquipmentModel().getTitle());
            //viewHolder.location.setText(equipment.getLocation());
            viewHolder.equipmentStatusUuid.setText(equipment.getEquipmentStatus().getTitle());
            viewHolder.criticalTypeUuid.setText(equipment.getCriticalType().getTitle());
            String sDate = DataUtils.getDate(equipment.getStartDate(), "dd.MM.yyyy HH:ss");
            viewHolder.startDate.setText(sDate);
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uuid;
        TextView equipmentModelUuid;
        TextView equipmentStatusUuid;
        TextView title;
        TextView location;
        TextView inventoryNumber;
        TextView criticalTypeUuid;
        TextView startDate;
    }
}
