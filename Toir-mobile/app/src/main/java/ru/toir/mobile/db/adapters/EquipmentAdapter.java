package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Equipment;

import static ru.toir.mobile.utils.RoundedImageView.getRoundedBitmap;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class EquipmentAdapter extends RealmBaseAdapter<Equipment> implements ListAdapter {

    public static final String TABLE_NAME = "Equipment";

    public EquipmentAdapter(@NonNull Context context, RealmResults<Equipment> data) {
        super(context, data);
    }
    public EquipmentAdapter(@NonNull Context context, RealmList<Equipment> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        return adapterData.size();
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
            //if (parent.getId() == R.id.gps_listView) {
            if (parent.getId() == R.id.eril_status_label) {
                convertView = inflater.inflate(R.layout.equipment_gps_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.eril_image_critical);
                viewHolder.equipmentStatus = (TextView) convertView.findViewById(R.id.eril_status);
                viewHolder.criticalLevel = (TextView) convertView.findViewById(R.id.eril_critical_level);
                viewHolder.location = (TextView) convertView.findViewById(R.id.eril_place);
                viewHolder.inventoryNumber = (TextView) convertView.findViewById(R.id.eril_inventory_number);
                viewHolder.title = (TextView) convertView.findViewById(R.id.eril_title);
                convertView.setTag(viewHolder);
            }
            else {
                convertView = inflater.inflate(R.layout.equipment_reference_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.eril_image);
                viewHolder.equipmentStatus = (TextView) convertView.findViewById(R.id.eril_status);
                viewHolder.criticalTypeUuid = (TextView) convertView.findViewById(R.id.eril_critical);
                viewHolder.startDate = (TextView) convertView.findViewById(R.id.eril_last_operation_date);
                viewHolder.location = (TextView) convertView.findViewById(R.id.eril_location);
                viewHolder.equipmentModelUuid = (TextView) convertView.findViewById(R.id.eril_type);
                viewHolder.inventoryNumber = (TextView) convertView.findViewById(R.id.eril_inventory_number);
                viewHolder.title = (TextView) convertView.findViewById(R.id.eril_title);
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
                if (parent.getId() == R.id.eril_status_label) {
                    if (equipment.getCriticalType().get_id() == 1)
                        viewHolder.icon.setImageResource(R.drawable.critical_level_1);
                    if (equipment.getCriticalType().get_id() ==  2)
                        viewHolder.icon.setImageResource(R.drawable.critical_level_3);
                    if (equipment.getCriticalType().get_id() ==  2)
                        viewHolder.icon.setImageResource(R.drawable.critical_level_5);
                    //viewHolder.location.setText(equipment.getLocation());
                    if (equipment.get_id()==1)
                        viewHolder.location.setText("Цех изоляторов ПФИ");
                    if (equipment.get_id()==2)
                        viewHolder.location.setText("Котельная №3");

                    viewHolder.equipmentStatus.setText(equipment.getEquipmentStatus().getTitle());
                    viewHolder.inventoryNumber.setText(equipment.getInventoryNumber());
                    viewHolder.criticalLevel.setText(equipment.getCriticalType().getTitle());
                }
                else {
                    // временные фото иконки
                    if (equipment.get_id() == 1)
                        viewHolder.icon.setImageResource(R.drawable.equipment_model_teplogenerator);
                    if (equipment.get_id() == 2)
                        viewHolder.icon.setImageResource(R.drawable.equipment_model_kotelgas);
                    if (equipment.get_id() == 3) {
                        Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gas_counter);
                        //Bitmap crop = getCroppedBitmap(myBitmap, 70);
                        Bitmap crop = getRoundedBitmap(myBitmap, 70);
                        viewHolder.icon.setImageBitmap(crop);
                    }
                    if (equipment.get_id() == 4) {
                         Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pressure);
                         //Bitmap crop = getCroppedBitmap(myBitmap, 80);
                         Bitmap crop = getRoundedBitmap(myBitmap, 70);
                         viewHolder.icon.setImageBitmap(crop);
                        }
                    viewHolder.inventoryNumber.setText(equipment.getInventoryNumber());
                    viewHolder.equipmentModelUuid.setText(equipment.getEquipmentModel().getTitle());
                    viewHolder.location.setText(equipment.getLocation());
                    viewHolder.equipmentStatus.setText(equipment.getEquipmentStatus().getTitle());
                    viewHolder.criticalTypeUuid.setText(equipment.getCriticalType().getTitle());
                    if (equipment.getCriticalType().getTitle().equals("Критичный")) viewHolder.criticalTypeUuid.setBackgroundColor(ContextCompat.getColor(context,R.color.red));
                    if (equipment.getCriticalType().getTitle().equals("Не критичный")) viewHolder.criticalTypeUuid.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
                    if (equipment.getCriticalType().getTitle().equals("Средний")) viewHolder.criticalTypeUuid.setBackgroundColor(ContextCompat.getColor(context,R.color.blue));
                    Date date = equipment.getStartDate();
                    String sDate;
                    if (date != null) {
                        sDate = new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(date);
                    } else {
                        sDate = "none";
                    }
                    viewHolder.startDate.setText(sDate);
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
        TextView criticalTypeUuid;
        TextView criticalLevel;
        TextView startDate;
    }
}
