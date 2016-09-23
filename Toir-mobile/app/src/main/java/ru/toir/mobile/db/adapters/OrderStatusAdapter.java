package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.OrderStatus;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class OrderStatusAdapter extends RealmBaseAdapter<OrderStatus> implements ListAdapter {
    public static final String TABLE_NAME = "OrderStatus";

    private static class ViewHolder{
        TextView uuid;
        TextView title;
    }

    public OrderStatusAdapter(@NonNull Context context, RealmResults<OrderStatus> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<OrderStatus> rows = realm.where(OrderStatus.class).findAll();
        return rows.size();
    }

    @Override
    public OrderStatus getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        else return null;
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
                convertView = inflater.inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
                viewHolder.uuid = (TextView) convertView.findViewById(R.id.lv_secondLine);
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
        if (parent.getId() == R.id.simple_spinner || convertView==null) {
            TextView textView = new TextView(context);
            OrderStatus orderStatus;
            if (adapterData != null) {
                orderStatus = adapterData.get(position);
                textView.setText(orderStatus.getTitle());
                textView.setPadding(10,15,10,20);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setTextColor(Color.WHITE);
            }
            return textView;
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView (position,null,parent);
    }
}
