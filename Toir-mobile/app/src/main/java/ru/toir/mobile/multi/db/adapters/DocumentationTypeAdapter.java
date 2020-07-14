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
import ru.toir.mobile.multi.db.realm.DocumentationType;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class DocumentationTypeAdapter extends RealmBaseAdapter<DocumentationType> implements ListAdapter {
    public static final String TABLE_NAME = "DocumentationType";

    public DocumentationTypeAdapter(RealmResults<DocumentationType> data) {
        super(data);
    }

    public RealmResults<DocumentationType> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DocumentationType> result = realm.where(DocumentationType.class).findAll();
        realm.close();
        return result;
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

            if (parent.getId() == R.id.simple_spinner || parent.getId() == R.id.documentation_type_sort) {
                //convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        DocumentationType documentationType;
        if (adapterData != null && viewHolder.title != null) {
            documentationType = adapterData.get(position);
            if (documentationType != null) {
                viewHolder.title.setText(documentationType.getTitle());
            }
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                documentationType = adapterData.get(position);
                if (documentationType != null) {
                    textView.setText(documentationType.getTitle());
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
