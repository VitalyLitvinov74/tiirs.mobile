package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.MediaController;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.MediaFile;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

/**
 * @author olejek
 *         Created by olejek on 03.05.17.
 */
public class DefectAdapter extends RealmBaseAdapter<Defect> implements ListAdapter {

    public static final String TABLE_NAME = "Defect";

    public DefectAdapter(RealmResults<Defect> data) {
        super(data);
    }

    @Override
    public Defect getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        Defect defect;
        if (adapterData != null) {
            defect = adapterData.get(position);
            return defect.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        viewHolder = new DefectAdapter.ViewHolder();
        if (convertView == null) {
            if (parent.getId() == R.id.spinner_defect_type) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            } else {
                convertView = inflater.inflate(R.layout.defect_item_layout, parent, false);
                viewHolder = new DefectAdapter.ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.defect_title);
                viewHolder.equipment = convertView.findViewById(R.id.defect_equipment);
                viewHolder.date = convertView.findViewById(R.id.defect_date);
                viewHolder.status = convertView.findViewById(R.id.defect_status);
                viewHolder.image_status = convertView.findViewById(R.id.defect_status_image);
                viewHolder.image_defect = convertView.findViewById(R.id.defect_image);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Defect defect;
        if (adapterData != null) {
            defect = adapterData.get(position);
            if (defect != null) {
                if (parent.getId() == R.id.spinner_defect_type) {
                    viewHolder.title.setText(defect.getComment());
                } else {
                    if (defect.getDefectType() != null) {
                        viewHolder.title.setText(defect.getDefectType().getTitle());
                    } else {
                        String textData = "новый: " + defect.getComment();
                        viewHolder.title.setText(textData);
                        //viewHolder.title.setText("новый");
                    }
                    if (defect.isProcess()) {
                        viewHolder.image_status.setImageResource(R.drawable.status_easy_ready);
                        viewHolder.status.setText(R.string.defect_status_processed);
                    } else {
                        viewHolder.status.setText(R.string.defect_status_non_processed);
                    }
                    java.util.Date date = defect.getDate();
                    if (date != null) {
                        String sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US)
                                .format(date);
                        viewHolder.date.setText(defect.getUser().getName().concat(" [").concat(sDate).concat("]"));
                    }

                    if (defect.getUser() != null) {
                        viewHolder.equipment.setText(defect.getEquipment().getTitle());
                    }
                    MediaFile mediaFile = defect.getMediaFile();
                    if (mediaFile != null) {
                        File path = context.getExternalFilesDir(mediaFile.getPath());
                        String fileName = mediaFile.getName();
                        if (path != null && fileName.contains("jpg")) {
                            Bitmap bm = getResizedBitmap(path + File.separator,
                                    fileName, 200, 0, mediaFile.getChangedAt().getTime());
                            if (bm != null) {
                                viewHolder.image_defect.setImageBitmap(bm);
                            }
                        }
                    }
                }
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView uuid;
        TextView title;
        TextView date;
        TextView equipment;
        TextView status;
        ImageView image_status;
        ImageView image_defect;
    }
}
