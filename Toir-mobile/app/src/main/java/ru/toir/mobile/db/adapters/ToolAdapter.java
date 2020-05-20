package ru.toir.mobile.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Tool;

public class ToolAdapter extends RealmBaseAdapter<Tool> implements ListAdapter {

    ToolAdapter(RealmResults<Tool> data) {
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
    public Tool getItem(int position) {
        Tool tool;
        if (adapterData != null) {
            if (position < getCount()) {
                tool = adapterData.get(position);
                return tool;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tool_item, parent, false);
            viewHolder.title = convertView.findViewById(R.id.tool_title);
            viewHolder.type = convertView.findViewById(R.id.tool_description);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Tool tool = adapterData.get(position);
        if (tool != null) {
            viewHolder.title.setText(tool.getTitle());
            viewHolder.type.setText(tool.getToolType().getTitle());
        }

        convertView.setTag(viewHolder);
        return convertView;
    }

    public static class ViewHolder {
        TextView title;
        TextView type;
    }
}
