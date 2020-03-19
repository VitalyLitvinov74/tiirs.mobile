package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.EquipmentStatus;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentAdapter extends RealmBaseAdapter<Equipment> implements ListAdapter {

    public static final String TABLE_NAME = "Equipment";

    public EquipmentAdapter(RealmResults<Equipment> data) {
        super(data);
    }

    public EquipmentAdapter(RealmList<Equipment> data) {
        super(data);
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            //if (parent.getId() == R.id.gps_listView) {
            if (parent.getId() == R.id.eril_status) {
                convertView = inflater.inflate(R.layout.equipment_gps_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = convertView.findViewById(R.id.eril_image_critical);
                viewHolder.equipmentStatus = convertView.findViewById(R.id.eril_status);
                viewHolder.criticalTypeUuid = convertView.findViewById(R.id.eril_critical_level);
                viewHolder.location = convertView.findViewById(R.id.eril_place);
                viewHolder.inventoryNumber = convertView.findViewById(R.id.eril_inventory_number);
                viewHolder.title = convertView.findViewById(R.id.eril_title);
                convertView.setTag(viewHolder);
            } else {
                convertView = inflater.inflate(R.layout.equipment_reference_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = convertView.findViewById(R.id.eril_image);
                viewHolder.equipmentStatus = convertView.findViewById(R.id.eril_status);
                viewHolder.serialNumber = convertView.findViewById(R.id.eril_serial);
                viewHolder.location = convertView.findViewById(R.id.eril_location);
                viewHolder.equipmentModelUuid = convertView.findViewById(R.id.eril_type);
                viewHolder.inventoryNumber = convertView.findViewById(R.id.eril_inventory_number);
                viewHolder.title = convertView.findViewById(R.id.eril_title);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Equipment equipment;
        if (adapterData != null) {
            equipment = adapterData.get(position);
            if (equipment != null) {
                viewHolder.title.setText(equipment.getTitle());
                //if (parent.getId() == R.id.gps_listView) {
                if (parent.getId() == R.id.eril_status) {
                    if (equipment.getCriticalType().get_id() == 1) {
                        viewHolder.icon.setImageResource(R.drawable.critical_level_1);
                    }

                    if (equipment.getCriticalType().get_id() == 2) {
                        viewHolder.icon.setImageResource(R.drawable.critical_level_3);
                    }

                    if (equipment.getCriticalType().get_id() == 2) {
                        viewHolder.icon.setImageResource(R.drawable.critical_level_5);
                    }

                    //viewHolder.location.setText(equipment.getLocation());
                    viewHolder.equipmentStatus.setText(context.getString(R.string.status, equipment.getEquipmentStatus().getTitle()));
                    viewHolder.inventoryNumber.setText(context.getString(R.string.inventory_number, equipment.getInventoryNumber()));
                    viewHolder.criticalLevel.setText(equipment.getCriticalType().getTitle());
                } else {
                    String imgPath = equipment.getAnyImageFilePath();
                    String fileName = equipment.getAnyImage();
                    if (imgPath != null && fileName != null) {
                        File path = context.getExternalFilesDir(imgPath);
                        if (path != null) {
                            Bitmap tmpBitmap = getResizedBitmap(path + File.separator,
                                    fileName, 300, 0, equipment.getChangedAt().getTime());
                            if (tmpBitmap != null) {
                                viewHolder.icon.setImageBitmap(tmpBitmap);
                            }
                        }
                    }
                    String inventoryNumber = context.getString(R.string.inventory_number, equipment.getInventoryNumber());
                    viewHolder.inventoryNumber.setText(inventoryNumber);
                    String serialNumber = context.getString(R.string.serial_number) + ": ";
                    if (equipment.getSerialNumber() != null)
                        serialNumber += equipment.getSerialNumber();
                    else
                        serialNumber += "-";
                    viewHolder.serialNumber.setText(serialNumber);
                    viewHolder.equipmentModelUuid.setText(equipment.getEquipmentModel().getTitle());
                    if (equipment.getLocation() != null) {
                        viewHolder.location.setText(equipment.getLocation().getTitle());
                    }

                    viewHolder.equipmentStatus.setText(equipment.getEquipmentStatus().getTitle());
                    if (equipment.getEquipmentStatus().getUuid() != null) {
                        if (equipment.getEquipmentStatus().getUuid().equals(EquipmentStatus.Status.WORK)) {
                            viewHolder.equipmentStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.green));
                        } else {
                            viewHolder.equipmentStatus.setBackgroundColor(ContextCompat.getColor(context, R.color.md_deep_orange_300));
                        }
                    }

/*
                    Date date = equipment.getStartDate();
                    String sDate;
                    if (date != null && date.after(new Date(100000))) {
                        sDate = new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(date);
                    } else {
                        sDate = "неизвестна";
                    }
                    viewHolder.startDate.setText(sDate);
*/
                }
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        ImageView icon;
        TextView uuid;
        TextView equipmentModelUuid;
        TextView equipmentStatus;
        TextView title;
        TextView location;
        TextView inventoryNumber;
        TextView serialNumber;
        TextView criticalTypeUuid;
        TextView criticalLevel;
        TextView status;
        //TextView startDate;
        TextView checkDate;
    }
}
