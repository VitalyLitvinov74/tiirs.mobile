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
import ru.toir.mobile.db.realm.OperationVerdict;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class OperationVerdictAdapter extends RealmBaseAdapter<OperationVerdict> implements ListAdapter {
    public static final String TABLE_NAME = "OperationVerdict";

    public OperationVerdictAdapter(@NonNull Context context, RealmResults<OperationVerdict> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OperationVerdict> rows = realm.where(OperationVerdict.class).findAll();
        return rows.size();
    }

    @Override
    public OperationVerdict getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        OperationVerdict operationVerdict;
        if (adapterData != null) {
            operationVerdict = adapterData.get(position);
            return operationVerdict.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (parent.getId() == R.id.simple_spinner) {
            TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
            OperationVerdict operationVerdict;
            if (adapterData != null) {
                operationVerdict = adapterData.get(position);
                textView.setText(operationVerdict.getTitle());
            }
            return textView;
        }
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                viewHolder.uuid = (TextView) convertView.findViewById(R.id.lv_secondLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OperationVerdict operationVerdict;
            if (adapterData != null) {
                operationVerdict = adapterData.get(position);
                viewHolder.title.setText(operationVerdict.getTitle());
                viewHolder.uuid.setText(operationVerdict.getUuid());
            }
            return convertView;
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
    }
}
