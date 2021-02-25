package ru.toir.mobile.multi.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.OrderStatus;

/**
 * @author Dmitriy Logachov
 *         Created by koputo on 08.09.16.
 */
public class OrderStatusAdapter extends RealmBaseAdapter<OrderStatus> implements ListAdapter {
    public static final String TABLE_NAME = "OrderStatus";

    public OrderStatusAdapter(RealmResults<OrderStatus> data) {
        super(data);
    }

    @Override
    public OrderStatus getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        OrderStatus orderStatus;
        if (adapterData != null) {
            orderStatus = adapterData.get(position);
            return orderStatus.get_id();
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

            OrderStatus orderStatus;
            if (adapterData != null) {
                orderStatus = adapterData.get(position);
                viewHolder.title.setText(orderStatus.getTitle());
                viewHolder.uuid.setText(orderStatus.getUuid());
            }

            return convertView;
        }

        if (parent.getId() == R.id.simple_spinner || convertView == null) {
            TextView textView = new TextView(parent.getContext());
            OrderStatus orderStatus;
            if (adapterData != null) {
                orderStatus = adapterData.get(position);
                textView.setText(orderStatus.getTitle());
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
