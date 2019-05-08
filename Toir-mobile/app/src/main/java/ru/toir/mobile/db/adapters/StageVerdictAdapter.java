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
import ru.toir.mobile.db.realm.StageVerdict;

/**
 * @author koputo
 *         Created by koputo on 08.09.16.
 */
public class StageVerdictAdapter extends RealmBaseAdapter<StageVerdict> implements ListAdapter {
    public static final String TABLE_NAME = "OperationVerdict";

    public StageVerdictAdapter(RealmResults<StageVerdict> data) {
        super(data);
    }

    @Override
    public StageVerdict getItem(int position) {
        StageVerdict stageVerdict = null;
        if (adapterData != null) {
            stageVerdict = adapterData.get(position);
        }
        return stageVerdict;
    }

    @Override
    public long getItemId(int position) {
        StageVerdict stageVerdict;
        if (adapterData != null) {
            stageVerdict = adapterData.get(position);
            return stageVerdict.get_id();
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
                viewHolder.uuid = convertView.findViewById(R.id.lv_secondLine);
                convertView.setTag(viewHolder);
            }
            if (parent.getId() == R.id.simple_spinner || parent.getId() == R.id.operation_verdict_spinner) {
                convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.title = convertView.findViewById(android.R.id.text1);
                convertView.setTag(viewHolder);
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        StageVerdict stageVerdict;
        if (adapterData != null && viewHolder.title != null) {
            stageVerdict = adapterData.get(position);
            if (stageVerdict != null)
                viewHolder.title.setText(stageVerdict.getTitle());
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                stageVerdict = adapterData.get(position);
                if (stageVerdict != null)
                    textView.setText(stageVerdict.getTitle());
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
