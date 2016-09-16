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

    private static class ViewHolder{
        TextView title;
    }

    public DocumentationTypeAdapter(@NonNull Context context, int resId, RealmResults<DocumentationType> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DocumentationType> rows = realm.where(DocumentationType.class).findAll();
        return rows.size();
    }

    @Override
    public DocumentationType getItem(int position) {
        return null;
    }

    public RealmResults<DocumentationType> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<DocumentationType> rows = realm.where(DocumentationType.class).findAll();
        return rows;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (parent.getId() == R.id.simple_spinner) {
            TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
            DocumentationType documentationType = adapterData.get(position);
            textView.setText(documentationType.getTitle());
            return textView;
        }
        if (parent.getId() == R.id.reference_listView) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            DocumentationType documentationType = adapterData.get(position);
            viewHolder.title.setText(documentationType.getTitle());
            return convertView;
        }
        return null;
    }
}
