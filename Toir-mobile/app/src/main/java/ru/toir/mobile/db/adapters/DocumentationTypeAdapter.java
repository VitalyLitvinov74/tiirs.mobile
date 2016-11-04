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

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class DocumentationTypeAdapter extends RealmBaseAdapter<DocumentationType> implements ListAdapter {
    public static final String TABLE_NAME = "DocumentationType";

    public DocumentationTypeAdapter(@NonNull Context context, RealmResults<DocumentationType> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    public RealmResults<DocumentationType> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(DocumentationType.class).findAll();
    }

    @Override
    public DocumentationType getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        DocumentationType documentationType;
        if (adapterData != null) {
            documentationType = adapterData.get(position);
            return documentationType.get_id();
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

        DocumentationType documentationType;
        if (adapterData != null && viewHolder.title != null) {
            documentationType = adapterData.get(position);
            if (documentationType != null)
                viewHolder.title.setText(documentationType.getTitle());
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                documentationType = adapterData.get(position);
                if (documentationType != null)
                    textView.setText(documentationType.getTitle());
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
