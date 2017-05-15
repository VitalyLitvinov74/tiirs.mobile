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
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Contragent;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Equipment;

import static org.slf4j.MDC.clear;
import static ru.toir.mobile.utils.MainFunctions.getEquipmentImage;
import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

/**
 * @author olejek
 *         Created by olejek on 05.05.17.
 */
public class ContragentAdapter extends RealmBaseAdapter<Contragent> implements ListAdapter {
    public static final String TABLE_NAME = "Contragent";

    public ContragentAdapter(@NonNull Context context, RealmResults<Contragent> data) {
        super(context, data);
    }

    public ContragentAdapter(@NonNull Context context, RealmList<Contragent> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    public void setFilter(String text, Realm realmDB) {
        if (adapterData != null) {
            adapterData = realmDB.where(Contragent.class).equalTo("name",text).or().contains("name",text, Case.INSENSITIVE).findAll();
            notifyDataSetChanged();
        }
    }

    @Override
    public Contragent getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Contragent contragent;
        if (adapterData != null) {
            contragent = adapterData.get(position);
            return contragent.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            //if (parent.getId() == R.id.gps_listView) {
            if (parent.getId() == R.id.crl_contragents_listView) {
                convertView = inflater.inflate(R.layout.contragent_reference_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.cril_image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.cril_title);
                viewHolder.type = (TextView) convertView.findViewById(R.id.cril_type);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.cril_phone);
                convertView.setTag(viewHolder);
            } else {
                convertView = inflater.inflate(R.layout.contragent_reference_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.cril_image);
                viewHolder.name = (TextView) convertView.findViewById(R.id.cril_title);
                viewHolder.type = (TextView) convertView.findViewById(R.id.cril_type);
                viewHolder.phone = (TextView) convertView.findViewById(R.id.cril_phone);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Contragent contragent;
        if (adapterData != null) {
            contragent = adapterData.get(position);
            if (contragent != null) {
                viewHolder.name.setText(contragent.getName());
                viewHolder.phone.setText(contragent.getPhone());
                //viewHolder.description.setText(contragent.getDescription());
                if (contragent.getContragentType()==2)
                    viewHolder.type.setText("Исполнитель");
                if (contragent.getContragentType()==1)
                    viewHolder.type.setText("Клиент");
            }
        }
        return convertView;
    }


    private static class ViewHolder {
        ImageView icon;
        TextView uuid;
        TextView name;
        TextView description;
        TextView phone;
        TextView type;
        TextView latitude;
        TextView longitude;
        TextView parent;
    }

}
