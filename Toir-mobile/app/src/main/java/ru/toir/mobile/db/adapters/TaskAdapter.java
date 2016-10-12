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
import ru.toir.mobile.db.realm.Tasks;

/**
 * @author olejek
 * Created by olejek on 13.09.16.
 */
public class TaskAdapter extends RealmBaseAdapter<Tasks> implements ListAdapter {
    public static final String TABLE_NAME = "Tasks";

    public TaskAdapter(@NonNull Context context, RealmResults<Tasks> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        return adapterData.size();
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

        Tasks task = adapterData.get(position);
        Date lDate = task.getStartDate();
        if (lDate != null) {
                String sDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(lDate);
                viewHolder.date.setText(task.getTaskStatus().getTitle() + " " + sDate);
            } else {
                viewHolder.date.setText("не выполнялся");
            }

        viewHolder.equipment.setText(task.getEquipment().getTitle());
        viewHolder.title.setText(task.getTaskTemplate().getTitle());

            if (task.getTaskStatus().getTitle().equals("Получен") && task.getEquipment().getCriticalType().getTitle().equals("Не критичный"))
                viewHolder.icon.setImageResource(R.drawable.status_easy_receive);
            if (task.getTaskStatus().getTitle().equals("Получен") && task.getEquipment().getCriticalType().getTitle().equals("Не критичный"))
                viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
            if (task.getTaskStatus().getTitle().equals("Получен") && task.getEquipment().getCriticalType().getTitle().equals("Не критичный"))
                viewHolder.icon.setImageResource(R.drawable.status_high_receive);
            if (task.getTaskStatus().getTitle().equals("В работе") && task.getEquipment().getCriticalType().getTitle().equals("Средний"))
                viewHolder.icon.setImageResource(R.drawable.status_easy_work);
            if (task.getTaskStatus().getTitle().equals("В работе") && task.getEquipment().getCriticalType().getTitle().equals("Средний"))
                viewHolder.icon.setImageResource(R.drawable.status_mod_work);
            if (task.getTaskStatus().getTitle().equals("В работе") && task.getEquipment().getCriticalType().getTitle().equals("Средний"))
                viewHolder.icon.setImageResource(R.drawable.status_high_work);
            if (task.getTaskStatus().getTitle().equals("Выполнен") && task.getEquipment().getCriticalType().getTitle().equals("Критичный"))
                viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
            if (task.getTaskStatus().getTitle().equals("Выполнен") && task.getEquipment().getCriticalType().getTitle().equals("Критичный"))
                viewHolder.icon.setImageResource(R.drawable.status_mod_ready);
            if (task.getTaskStatus().getTitle().equals("Выполнен") && task.getEquipment().getCriticalType().getTitle().equals("Критичный"))
                viewHolder.icon.setImageResource(R.drawable.status_high_ready);
        return convertView;
    }

    private static class ViewHolder {
        TextView equipment;
        TextView title;
        TextView date;
        //TextView status;
        ImageView icon;
    }
}
