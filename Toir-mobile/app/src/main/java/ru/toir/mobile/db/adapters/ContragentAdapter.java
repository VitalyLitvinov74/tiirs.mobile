package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Contragent;

/**
 * @author olejek
 *         Created by olejek on 05.05.17.
 */
public class ContragentAdapter extends RealmBaseAdapter<Contragent> implements ListAdapter {
    public static final String TABLE_NAME = "Contragent";

    public ContragentAdapter(RealmResults<Contragent> data) {
        super(data);
    }

    public ContragentAdapter(RealmList<Contragent> data) {
        super(data);
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            //if (parent.getId() == R.id.gps_listView) {
            if (parent.getId() == R.id.crl_contragents_listView) {
                convertView = inflater.inflate(R.layout.contragent_reference_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = convertView.findViewById(R.id.cril_image);
                viewHolder.name = convertView.findViewById(R.id.cril_title);
                viewHolder.type = convertView.findViewById(R.id.cril_type);
                viewHolder.phone = convertView.findViewById(R.id.cril_phone);
                convertView.setTag(viewHolder);
            } else {
                convertView = inflater.inflate(R.layout.contragent_reference_item_layout, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.icon = convertView.findViewById(R.id.cril_image);
                viewHolder.name = convertView.findViewById(R.id.cril_title);
                viewHolder.type = convertView.findViewById(R.id.cril_type);
                viewHolder.phone = convertView.findViewById(R.id.cril_phone);
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
        TextView parent;
    }

}
