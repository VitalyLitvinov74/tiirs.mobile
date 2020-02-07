package ru.toir.mobile.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.OrderRepairPart;

public class RepairPartAdapter extends RealmBaseAdapter<OrderRepairPart> implements ListAdapter {

    RepairPartAdapter(RealmResults<OrderRepairPart> data) {
        super(data);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            return adapterData.size();
        } else {
            return 0;
        }
    }

    @Override
    public OrderRepairPart getItem(int position) {
        OrderRepairPart orderRepairPart;
        if (adapterData != null) {
            if (position < getCount()) {
                orderRepairPart = adapterData.get(position);
                return orderRepairPart;
            }
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        if (adapterData != null) {
            return adapterData.get(position).get_id();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (adapterData == null) {
            return convertView;
        }

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.repair_part_item, parent, false);
            viewHolder.title = convertView.findViewById(R.id.parts_title);
            viewHolder.type = convertView.findViewById(R.id.parts_description);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        OrderRepairPart orderRepairPart = adapterData.get(position);
        if (orderRepairPart != null) {
            viewHolder.title.setText(orderRepairPart.getRepairPart().getTitle());
            viewHolder.type.setText(orderRepairPart.getRepairPart().getRepairPartType().getTitle());
        }
        convertView.setTag(viewHolder);
        return convertView;
    }

    public static class ViewHolder {
        TextView title;
        TextView type;
    }
}
