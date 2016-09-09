package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.Realm;
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

    private static class ViewHolder{
        TextView uuid;
        TextView title;
        ImageView icon;
    }

    public TaskStatusAdapter(@NonNull Context context, int resId, RealmResults<TaskStatus> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<TaskStatus> rows = realm.where(TaskStatus.class).findAll();
        return rows.size();
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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.equipment_reference_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.uuid = (TextView) convertView.findViewById(R.id.lv_secondLine);
            viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.lv_icon);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            TaskStatus TaskStatus = adapterData.get(position);
            viewHolder.title.setText(TaskStatus.getTitle());
            viewHolder.uuid.setText(TaskStatus.getUuid());
            //TODO сопоставление изображений
            viewHolder.icon.setImageResource(R.drawable.img_3);
        }
        return convertView;
    }
}
