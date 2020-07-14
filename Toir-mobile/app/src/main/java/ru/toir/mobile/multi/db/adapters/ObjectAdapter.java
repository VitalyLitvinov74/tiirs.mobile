package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
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
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.Objects;

import static ru.toir.mobile.multi.utils.RoundedImageView.getResizedBitmap;

/**
 * @author olejek
 *         Created by olejek on 03.04.17.
 */
public class ObjectAdapter extends RealmBaseAdapter<Objects> implements ListAdapter {

    public static final String TABLE_NAME = "Objects";

    public ObjectAdapter(RealmResults<Objects> data) {
        super(data);
    }

    public ObjectAdapter(RealmList<Objects> data) {
        super(data);
    }

    @Override
    public Objects getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Objects object;
        if (adapterData != null) {
            object = adapterData.get(position);
            return object.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.object_reference_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = convertView.findViewById(R.id.object_image);
            viewHolder.title = convertView.findViewById(R.id.object_title);
            viewHolder.objectType = convertView.findViewById(R.id.object_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Objects object;
        if (adapterData != null) {
            object = adapterData.get(position);
            if (object != null) {
                viewHolder.title.setText(object.getTitle());

                String imgPath = object.getImageFilePath();
                String fileName = object.getImage();
                if (imgPath != null && fileName != null) {
                    File path = context.getExternalFilesDir(imgPath);
                    if (path != null) {
                        Bitmap tmpBitmap = getResizedBitmap(path + File.separator,
                                fileName, 300, 0, object.getChangedAt().getTime());
                        if (tmpBitmap != null) {
                            viewHolder.image.setImageBitmap(tmpBitmap);
                        }
                    }
                }

                viewHolder.title.setText(object.getTitle());
                if (object.getObjectType() != null) {
                    viewHolder.objectType.setText(object.getObjectType().getTitle());
                }

                //viewHolder.descr.setText(object.getDescription());
                //viewHolder.latitude.setText(String.valueOf(object.getLatitude()));
                //viewHolder.longitude.setText(String.valueOf(object.getLongitude()));
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        TextView uuid;
        ImageView image;
        TextView objectType;
        TextView title;
        TextView descr;
        TextView latitude;
        TextView longitude;
    }
}
