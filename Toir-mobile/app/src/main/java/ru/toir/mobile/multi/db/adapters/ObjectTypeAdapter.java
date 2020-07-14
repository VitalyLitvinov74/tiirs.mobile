package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.ObjectType;

/**
 * @author olejek
 * Created by olejek on 03.04.17.
 */
public class ObjectTypeAdapter extends RealmBaseAdapter<ObjectType> implements ListAdapter {
    public static final String TABLE_NAME = "ObjectType";

    public ObjectTypeAdapter(RealmResults<ObjectType> data) {
        super(data);
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.reference_listView) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder.title = convertView.findViewById(R.id.lv_firstLine);
                convertView.setTag(viewHolder);
            }

            if (parent.getId() == R.id.simple_spinner) {
                //convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = convertView.findViewById(android.R.id.text1);
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
