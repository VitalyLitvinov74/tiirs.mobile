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

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.TaskStageStatus;
import ru.toir.mobile.db.realm.TaskStages;

/**
 * @author olejek
 * Created by olejek on 14.09.16.
 */
public class TaskStageAdapter extends RealmBaseAdapter<TaskStages> implements ListAdapter {
    public static final String TABLE_NAME = "TaskStages";

    public TaskStageAdapter(@NonNull Context context, RealmResults<TaskStages> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realmDB = Realm.getDefaultInstance();
        RealmResults<TaskStages> rows = realmDB.where(TaskStages.class).findAll();
        return rows.size();
    }

    @Override
    public TaskStages getItem(int position) {
        TaskStages taskStages;
        if (adapterData != null) {
            taskStages = adapterData.get(position);
            return taskStages;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        TaskStages taskStages;
        if (adapterData != null) {
            taskStages = adapterData.get(position);
            return taskStages.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Realm realmDB = Realm.getDefaultInstance();
        TaskStageStatus taskStageStatus;
        String pathToImages;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.taskstage_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.ts_Name);
            viewHolder.status = (TextView) convertView.findViewById(R.id.ts_Status);
            viewHolder.start_date = (TextView) convertView.findViewById(R.id.ts_StartDate);
            viewHolder.end_date = (TextView) convertView.findViewById(R.id.ts_EndDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            TaskStages taskStage = adapterData.get(position);
            viewHolder.title.setText(taskStage.getTaskStageTemplate().getTitle());
            if (taskStage.getTaskStageStatus() != null)
                viewHolder.status.setText("Статус: " + taskStage.getTaskStageStatus().getTitle());
            if (taskStage.getStartDate()!=null) {
                Date lDate = taskStage.getStartDate();
                if (lDate != null) {
                    viewHolder.start_date.setText(new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(lDate));
                } else {
                    viewHolder.start_date.setText("не выполнялся");
                }
            }
            if (taskStage.getEndDate()!=null) {
                Date lDate = taskStage.getEndDate();
                if (lDate != null) {
                    viewHolder.end_date.setText(new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(lDate));
                } else {
                    viewHolder.end_date.setText("не выполнялся");
                }
            }
            else {
                viewHolder.end_date.setText("не закончен");
            }
            if (taskStage.getTaskStageStatus() != null && taskStage.getEquipment() != null) {
                if (taskStage.getTaskStageStatus().getTitle().equals("Получен") && taskStage.getEquipment().getCriticalType().getTitle().equals("Не критичный"))
                    viewHolder.icon.setImageResource(R.drawable.status_easy_receive);
                if (taskStage.getTaskStageStatus().getTitle().equals("Получен") && taskStage.getEquipment().getCriticalType().getTitle().equals("Не критичный"))
                    viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
                if (taskStage.getTaskStageStatus().getTitle().equals("Получен") && taskStage.getEquipment().getCriticalType().getTitle().equals("Не критичный"))
                    viewHolder.icon.setImageResource(R.drawable.status_high_receive);
                if (taskStage.getTaskStageStatus().getTitle().equals("В работе") && taskStage.getEquipment().getCriticalType().getTitle().equals("Средний"))
                    viewHolder.icon.setImageResource(R.drawable.status_easy_work);
                if (taskStage.getTaskStageStatus().getTitle().equals("В работе") && taskStage.getEquipment().getCriticalType().getTitle().equals("Средний"))
                    viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                if (taskStage.getTaskStageStatus().getTitle().equals("В работе") && taskStage.getEquipment().getCriticalType().getTitle().equals("Средний"))
                    viewHolder.icon.setImageResource(R.drawable.status_high_work);
                if (taskStage.getTaskStageStatus().getTitle().equals("Выполнен") && taskStage.getEquipment().getCriticalType().getTitle().equals("Критичный"))
                    viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                if (taskStage.getTaskStageStatus().getTitle().equals("Выполнен") && taskStage.getEquipment().getCriticalType().getTitle().equals("Критичный"))
                    viewHolder.icon.setImageResource(R.drawable.status_mod_ready);
                if (taskStage.getTaskStageStatus().getTitle().equals("Выполнен") && taskStage.getEquipment().getCriticalType().getTitle().equals("Критичный"))
                    viewHolder.icon.setImageResource(R.drawable.status_high_ready);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView equipment;
        TextView title;
        TextView taskTitle;
        TextView description;
        TextView normative;
        TextView comment;
        TextView taskStageType;
        TextView taskStageStatus;
        TextView taskStageVerdict;
        TextView status;
        ImageView icon;
        ImageView image;
        TextView start_date;
        TextView end_date;
    }
}
