package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.AlertType;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class AlertTypeAdapter extends RealmBaseAdapter<AlertType> implements ListAdapter {
    public static final String TABLE_NAME = "AlertType";

    public AlertTypeAdapter(RealmResults<AlertType> data) {
        super(data);
    }

    @Override
    public AlertType getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (parent.getId() == R.id.simple_spinner) {
            TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
            assert adapterData != null;
            AlertType alertType = adapterData.get(position);
            textView.setText(alertType.getTitle());
            return textView;
        }
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.lv_firstLine);
                viewHolder.uuid = convertView.findViewById(R.id.lv_secondLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            assert adapterData != null;
            AlertType alertType = adapterData.get(position);
            viewHolder.title.setText(alertType.getTitle());
            viewHolder.uuid.setText(alertType.getUuid());
            return convertView;
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
    }
}
