package ru.toir.mobile.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.TaskStatus;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class TaskStatusAdapter extends RealmBaseAdapter<TaskStatus> implements ListAdapter {
    public static final String TABLE_NAME = "TaskStatus";

    public TaskStatusAdapter(RealmResults<TaskStatus> data) {
        super(data);
    }

    @Override
    public TaskStatus getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (parent.getId() == R.id.simple_spinner) {
            TextView textView = (TextView) View.inflate(parent.getContext(), android.R.layout.simple_spinner_item, null);
            TaskStatus taskStatus;
            if (adapterData != null) {
                taskStatus = adapterData.get(position);
                textView.setText(taskStatus.getTitle());
            }
            return textView;
        }
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
            TaskStatus taskStatus;
            if (adapterData != null) {
                taskStatus = adapterData.get(position);
                if (taskStatus != null) {
                    viewHolder.title.setText(taskStatus.getTitle());
                    viewHolder.uuid.setText(taskStatus.getUuid());
                }
            }
            //TODO сопоставление изображений
            //viewHolder.icon.setImageResource(R.drawable.img_3);
            return convertView;
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
    }
}
