package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.Tasks;

/**
 * @author olejek
 *         Created by olejek on 13.09.16.
 */
public class TaskAdapter extends RealmBaseAdapter<Tasks> implements ListAdapter {
    public static final String TABLE_NAME = "Tasks";

    public TaskAdapter(@NonNull Context context, RealmResults<Tasks> data) {
        super(context, data);
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
    public Tasks getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Tasks task;
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
            convertView = inflater.inflate(R.layout.task_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.ti_Name);
            viewHolder.date = (TextView) convertView.findViewById(R.id.ti_Status);
            viewHolder.equipment = (TextView) convertView.findViewById(R.id.ti_Equipment);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ti_ImageStatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData != null) {
            Tasks task = adapterData.get(position);
            Date lDate = task.getStartDate();
            if (lDate != null) {
                String sDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(lDate);
                viewHolder.date.setText("Дата: " + sDate + " " + task.getTaskStatus().getTitle());
            } else {
                viewHolder.date.setText("не выполнялся");
            }

            viewHolder.equipment.setText(task.getEquipment().getTitle());
            viewHolder.title.setText(task.getTaskTemplate().getTitle());

            String taskStatusUuid = task.getTaskStatus().getUuid();
            CriticalType criticalType=task.getEquipment().getCriticalType();
            if (criticalType!=null) {
                String criticalTypeUuid = criticalType.getUuid();
                viewHolder.icon.setImageResource(getIconForStatusAndCriticalType(taskStatusUuid, criticalTypeUuid));
            }
        }

        return convertView;
    }

    /**
     * Вспомогательный метод, возвращает id ресурса в зависимости от сочетания статуса и критичности.
     *
     * @param statusUuid   Uuid статуса задачи.
     * @param criticalUuid Uuid типа критичности.
     * @return Id drawable ресурса.
     */
    private int getIconForStatusAndCriticalType(String statusUuid, String criticalUuid) {
        int id = R.drawable.status_easy_receive;

        if (statusUuid.equals(TaskStatus.Status.NEW) && criticalUuid.equals(CriticalType.Status.TYPE_3)) {
            id = R.drawable.status_easy_receive;
        }

        if (statusUuid.equals(TaskStatus.Status.NEW) && criticalUuid.equals(CriticalType.Status.TYPE_2)) {
            id = R.drawable.status_mod_receive;
        }

        if (statusUuid.equals(TaskStatus.Status.NEW) && criticalUuid.equals(CriticalType.Status.TYPE_1)) {
            id = R.drawable.status_high_receive;
        }

        if (statusUuid.equals(TaskStatus.Status.IN_WORK) && criticalUuid.equals(CriticalType.Status.TYPE_3)) {
            id = R.drawable.status_easy_work;
        }

        if (statusUuid.equals(TaskStatus.Status.IN_WORK) && criticalUuid.equals(CriticalType.Status.TYPE_2)) {
            id = R.drawable.status_mod_work;
        }

        if (statusUuid.equals(TaskStatus.Status.IN_WORK) && criticalUuid.equals(CriticalType.Status.TYPE_1)) {
            id = R.drawable.status_high_work;
        }

        if (statusUuid.equals(TaskStatus.Status.COMPLETE) && criticalUuid.equals(CriticalType.Status.TYPE_3)) {
            id = R.drawable.status_easy_ready;
        }

        if (statusUuid.equals(TaskStatus.Status.COMPLETE) && criticalUuid.equals(CriticalType.Status.TYPE_2)) {
            id = R.drawable.status_mod_ready;
        }

        if (statusUuid.equals(TaskStatus.Status.COMPLETE) && criticalUuid.equals(CriticalType.Status.TYPE_1)) {
            id = R.drawable.status_high_ready;
        }

        return id;
    }

    private static class ViewHolder {
        TextView equipment;
        TextView title;
        TextView date;
        //TextView status;
        ImageView icon;
    }
}
