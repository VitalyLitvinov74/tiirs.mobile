package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.Date;

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Contragent;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.Objects;
import ru.toir.mobile.db.realm.Tasks;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

/**
 * @author olejek
 * Created by olejek on 03.05.17.
 */
public class DefectAdapter extends RealmBaseAdapter<Defect> implements ListAdapter {

    public static final String TABLE_NAME = "Defect";

    public DefectAdapter(@NonNull Context context, RealmResults<Defect> data) {
        super(context, data);
    }
    public DefectAdapter(@NonNull Context context, RealmList<Defect> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        return adapterData.size();
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
        ViewHolder viewHolder;
        viewHolder = new DefectAdapter.ViewHolder();
        if (convertView == null) {
            if (parent.getId() == R.id.spinner_defects) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Defect defect;
        if (adapterData != null) {
            defect = adapterData.get(position);
            if (defect != null) {
                viewHolder.title.setText(defect.getComment());
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        TextView uuid;
        TextView title;
        TextView contragent_name;
        TextView date;
        TextView equipment;
        TextView defectType;

    }
}
