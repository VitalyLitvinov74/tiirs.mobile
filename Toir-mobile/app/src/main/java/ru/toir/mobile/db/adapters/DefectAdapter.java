package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Defect;

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
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        } else {
            return 0;
        }
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
                viewHolder.user_name = convertView.findViewById(R.id.defect_user);
                viewHolder.date = convertView.findViewById(R.id.defect_date);
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

                    java.util.Date date = defect.getDate();
                    if (date != null) {
                        String sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US)
                                .format(date);
                        viewHolder.date.setText(sDate);
                    }

                    if (defect.getUser() != null) {
                        viewHolder.user_name.setText(defect.getUser().getName());
                    }
                }
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView uuid;
        TextView title;
        TextView user_name;
        TextView date;
        //TextView equipment;
    }
}
