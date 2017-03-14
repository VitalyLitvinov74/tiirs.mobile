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
        OperationVerdict operationVerdict = null;
        if (adapterData != null) {
            operationVerdict = adapterData.get(position);
        }
        return operationVerdict;
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            if (parent.getId() == R.id.reference_listView) {
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                viewHolder.uuid = (TextView) convertView.findViewById(R.id.lv_secondLine);
                convertView.setTag(viewHolder);
            }
            if (parent.getId() == R.id.simple_spinner || parent.getId() == R.id.operation_verdict_spinner) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        OperationVerdict operationVerdict;
        if (adapterData != null && viewHolder.title != null) {
            operationVerdict = adapterData.get(position);
            if (operationVerdict != null)
                viewHolder.title.setText(operationVerdict.getTitle());
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                operationVerdict = adapterData.get(position);
                if (operationVerdict != null)
                    textView.setText(operationVerdict.getTitle());
                textView.setTextSize(16);
                //textView.setPadding(5,5,5,5);
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
        TextView uuid;
        TextView title;
    }
}
