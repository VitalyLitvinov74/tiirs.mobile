package ru.toir.mobile.multi.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.Task;
import ru.toir.mobile.multi.db.realm.TaskStatus;

/**
 * @author olejek
 *         Created by olejek on 13.09.16.
 */
public class TaskAdapter extends RealmBaseAdapter<Task> implements ListAdapter {
    public static final String TABLE_NAME = "Task";

    public TaskAdapter(RealmResults<Task> data) {
        super(data);
    }

    @Override
    public Task getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Task task;
        if (adapterData != null) {
            task = adapterData.get(position);
            return task.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.ti_Name);
            viewHolder.date = convertView.findViewById(R.id.ti_Status);
            viewHolder.icon = convertView.findViewById(R.id.ti_ImageStatus);
            viewHolder.comment = convertView.findViewById(R.id.ti_Comment);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            if (position >= adapterData.size()) {
                return convertView;
            }

            Task task = adapterData.get(position);
            Date lDate = task.getStartDate();
            if (lDate != null && lDate.after(new Date(100000))) {
                String sDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(lDate);
                sDate = "Дата: " + sDate + " " + task.getTaskStatus().getTitle();
                viewHolder.date.setText(sDate);
            } else {
                viewHolder.date.setText("не выполнялся");
            }
            viewHolder.comment.setText(task.getComment());
            viewHolder.title.setText(task.getTaskTemplate().getTitle());
            viewHolder.icon.setImageResource(getIconForStatusAndCriticalType(task.getTaskStatus().getUuid()));
        }

        return convertView;
    }

    /**
     * Вспомогательный метод, возвращает id ресурса в зависимости от сочетания статуса и критичности.
     *
     * @param statusUuid   Uuid статуса задачи.
     * @return Id drawable ресурса.
     */
    private int getIconForStatusAndCriticalType(String statusUuid) {
        int id = R.drawable.status_easy_receive;

        if (statusUuid.equals(TaskStatus.Status.NEW)) {
            id = R.drawable.status_easy_receive;
        }

        if (statusUuid.equals(TaskStatus.Status.IN_WORK)) {
            id = R.drawable.status_easy_work;
        }

        if (statusUuid.equals(TaskStatus.Status.COMPLETE)) {
            id = R.drawable.status_easy_ready;
        }

        return id;
    }

    private static class ViewHolder {
        TextView title;
        TextView date;
        TextView comment;
        ImageView icon;
    }
}
