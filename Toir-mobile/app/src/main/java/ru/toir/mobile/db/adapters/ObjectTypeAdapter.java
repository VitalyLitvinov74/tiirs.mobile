package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.DocumentationType;
import ru.toir.mobile.db.realm.ObjectType;

/**
 * @author olejek
 * Created by olejek on 03.04.17.
 */
public class ObjectTypeAdapter extends RealmBaseAdapter<ObjectType> implements ListAdapter {
    public static final String TABLE_NAME = "ObjectType";

    public ObjectTypeAdapter(@NonNull Context context, RealmResults<ObjectType> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    public RealmResults<ObjectType> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ObjectType> result = realm.where(ObjectType.class).findAll();
        realm.close();
        return result;
    }

    @Override
    public ObjectType getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        ObjectType objectType;
        if (adapterData != null) {
            objectType = adapterData.get(position);
            return objectType.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.reference_listView) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                convertView.setTag(viewHolder);
            }

            if (parent.getId() == R.id.simple_spinner) {
                //convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ObjectType objectType;
        if (adapterData != null && viewHolder.title != null) {
            objectType = adapterData.get(position);
            if (objectType != null) {
                viewHolder.title.setText(objectType.getTitle());
            }
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                objectType = adapterData.get(position);
                if (objectType != null) {
                    textView.setText(objectType.getTitle());
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
        TextView title;
    }

}
