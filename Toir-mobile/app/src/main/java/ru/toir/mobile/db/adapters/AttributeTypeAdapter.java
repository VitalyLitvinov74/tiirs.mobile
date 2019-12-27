package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.db.realm.AttributeType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 18.03.19.
 */
public class AttributeTypeAdapter extends RealmBaseAdapter<AttributeType> implements ListAdapter {
    public static final String TABLE_NAME = "AttributeType";

    public AttributeTypeAdapter(RealmResults<AttributeType> data) {
        super(data);
    }

    @Override
    public AttributeType getItem(int position) {
        AttributeType attributeType = null;
        if (adapterData != null) {
            attributeType = adapterData.get(position);
        }

        return attributeType;
    }

    @Override
    public long getItemId(int position) {
        AttributeType attributeType;
        if (adapterData != null) {
            attributeType = adapterData.get(position);
            return attributeType.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;

        if (adapterData == null) {
            return convertView;
        }

        if (convertView == null) {
//            TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.text = convertView.findViewById(android.R.id.text1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AttributeType attributeType = adapterData.get(position);
        if (attributeType != null) {
            viewHolder.text.setText(attributeType.getName());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView text;
    }
}
