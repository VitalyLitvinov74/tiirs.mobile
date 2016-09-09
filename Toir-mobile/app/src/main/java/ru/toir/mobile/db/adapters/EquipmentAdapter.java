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

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentAdapter extends RealmBaseAdapter<Equipment> implements ListAdapter {
    public static final String TABLE_NAME = "equipment";

    private static class ViewHolder{
        TextView uuid;
        TextView equipmentModelUuid;
        TextView equipmentStatusUuid;
        TextView title;
        TextView inventoryNumber;
        TextView location;
        TextView criticalTypeUuid;
        TextView userUuid;
        TextView startDate;
        TextView latitude;
        TextView longitude;
        TextView tagId;
        TextView image;
    }

    public EquipmentAdapter(@NonNull Context context, int resId, RealmResults<Equipment> data) {
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
        return null;
    }

    @Override
    public long getItemId(int position) {
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

        Equipment equipment = adapterData.get(position);
        viewHolder.title.setText(equipment.getTitle());
        viewHolder.inventoryNumber.setText(equipment.getInventoryNumber());
        viewHolder.equipmentModelUuid.setText(equipment.getEquipmentModelUuid());
        //viewHolder.location.setText(equipment.getLocation());
        viewHolder.equipmentStatusUuid.setText(equipment.getEquipmentStatusUuid());
        viewHolder.criticalTypeUuid.setText(equipment.getCriticalTypeUuid());
        viewHolder.startDate.setText(""+equipment.getStartDate());
        return convertView;
    }
}
