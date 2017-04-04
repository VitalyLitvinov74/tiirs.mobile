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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.Objects;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;
import static ru.toir.mobile.utils.RoundedImageView.getRoundedBitmap;

/**
 * @author olejek
 * Created by olejek on 03.04.17.
 */
public class ObjectAdapter extends RealmBaseAdapter<Objects> implements ListAdapter {

    public static final String TABLE_NAME = "Objects";

    public ObjectAdapter(@NonNull Context context, RealmResults<Objects> data) {
        super(context, data);
    }
    public ObjectAdapter(@NonNull Context context, RealmList<Objects> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        return adapterData.size();
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.object_reference_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.object_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.object_title);
            viewHolder.objectType = (TextView) convertView.findViewById(R.id.object_type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Objects object;
        if (adapterData != null) {
            object = adapterData.get(position);
            if (object != null) {
                viewHolder.title.setText(object.getTitle());
                String path = context.getExternalFilesDir("/objects") + File.separator;
                Bitmap image_bitmap = getResizedBitmap(path, object.getImage(), 300, 0, object.getChangedAt().getTime());
                if (image_bitmap != null) {
                    viewHolder.image.setImageBitmap(image_bitmap);
                } else {
                    viewHolder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_image));
                }
                viewHolder.title.setText(object.getTitle());
                if (object.getObjectType() != null) {
                    viewHolder.objectType.setText(object.getObjectType().getTitle());
                }
                //viewHolder.descr.setText(object.getDescr());
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
