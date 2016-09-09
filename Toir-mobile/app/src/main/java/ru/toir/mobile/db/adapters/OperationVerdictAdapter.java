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

    private static class ViewHolder{
        TextView uuid;
        TextView title;
    }

    public OperationVerdictAdapter(@NonNull Context context, int resId, RealmResults<OperationVerdict> data) {
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.equipment_reference_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.uuid = (TextView) convertView.findViewById(R.id.lv_secondLine);
            viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            OperationVerdict OperationVerdict = adapterData.get(position);
            viewHolder.title.setText(OperationVerdict.getTitle());
            viewHolder.title.setText(OperationVerdict.getUuid());
        }
        return convertView;
    }
}
