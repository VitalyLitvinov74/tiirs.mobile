package ru.toir.mobile.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.OrderVerdict;

/**
 * @author koputo
 *         Created by koputo on 08.09.16.
 */
public class OrderVerdictAdapter extends RealmBaseAdapter<OrderVerdict> implements ListAdapter {
    public static final String TABLE_NAME = "OrderVerdict";

    public OrderVerdictAdapter(RealmResults<OrderVerdict> data) {
        super(data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrderVerdict> rows = realm.where(OrderVerdict.class).findAll();
        realm.close();
        return rows.size();
    }

    @Override
    public OrderVerdict getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        } else return null;
    }

    @Override
    public long getItemId(int position) {
        OrderVerdict orderVerdict;
        if (adapterData != null) {
            orderVerdict = adapterData.get(position);
            return orderVerdict.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.lv_firstLine);
                viewHolder.uuid = convertView.findViewById(R.id.lv_secondLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            OrderVerdict orderVerdict;
            if (adapterData != null) {
                orderVerdict = adapterData.get(position);
                viewHolder.title.setText(orderVerdict.getTitle());
                viewHolder.uuid.setText(orderVerdict.getUuid());
            }
            return convertView;
        }
        if (parent.getId() == R.id.simple_spinner || convertView == null) {
            TextView textView = new TextView(parent.getContext());
            OrderVerdict orderVerdict;
            if (adapterData != null) {
                orderVerdict = adapterData.get(position);
                textView.setText(orderVerdict.getTitle());
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

    public class Status {
        public static final String COMPLETE = "886dca37-2cc9-44da-aff9-19bf143e611a";
        public static final String UN_COMPLETE = "883c08ec-89d9-47df-b7cf-63a05d56594c";
        public static final String CANNOT_COMPLETE = "893c08ec-89d9-47df-b7cf-63a05d56594c";
    }
}
