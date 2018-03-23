package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.EquipmentModel;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

/**
 * @author olejek
 *         Created by olejek on 06.04.17.
 */
public class EquipmentModelAdapter extends RealmBaseAdapter<EquipmentModel> implements ListAdapter {
    public static final String TABLE_NAME = "EquipmentModel";

    public EquipmentModelAdapter(RealmResults<EquipmentModel> data) {
        super(data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<EquipmentModel> rows = realm.where(EquipmentModel.class).findAll();
        realm.close();
        return rows.size();
    }

    @Override
    public EquipmentModel getItem(int position) {
        EquipmentModel equipmentModel = null;
        if (adapterData != null) {
            equipmentModel = adapterData.get(position);
        }

        return equipmentModel;
    }

    @Override
    public long getItemId(int position) {
        EquipmentModel equipmentModel;
        if (adapterData != null) {
            equipmentModel = adapterData.get(position);
            return equipmentModel.get_id();
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
                convertView = inflater.inflate(R.layout.equipment_model_item_layout, parent, false);
                viewHolder.title = convertView.findViewById(R.id.emi_title);
                viewHolder.uuid = convertView.findViewById(R.id.emi_uuid);
                viewHolder.image = convertView.findViewById(R.id.emi_image);
                viewHolder.type = convertView.findViewById(R.id.emi_type);
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

        EquipmentModel equipmentModel;
        if (adapterData != null && viewHolder.title != null) {
            equipmentModel = adapterData.get(position);
            if (equipmentModel != null) {
                viewHolder.title.setText(equipmentModel.getTitle());
                viewHolder.uuid.setText(equipmentModel.getUuid());
                if (equipmentModel.getEquipmentType() != null) {
                    viewHolder.type.setText(equipmentModel.getEquipmentType().getTitle());
                }

                if (equipmentModel.getImage() != null) {
                    File path = context.getExternalFilesDir(equipmentModel.getImageFilePath());
                    if (path != null) {
                        Bitmap image_bitmap = getResizedBitmap(
                                path.toString() + File.separator,
                                equipmentModel.getImage(), 300, 0,
                                equipmentModel.getChangedAt().getTime());
                        if (image_bitmap != null) {
                            viewHolder.image.setImageBitmap(image_bitmap);
                        } else {
                            viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image));
                        }
                    }
                }

                viewHolder.title.setText(equipmentModel.getTitle());
            }
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                equipmentModel = adapterData.get(position);
                if (equipmentModel != null) {
                    textView.setText(equipmentModel.getTitle());
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
        TextView type;
        ImageView image;
    }
}
