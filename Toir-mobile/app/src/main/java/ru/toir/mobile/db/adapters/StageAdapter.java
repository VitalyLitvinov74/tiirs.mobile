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
import ru.toir.mobile.db.realm.StageStatus;
import ru.toir.mobile.db.realm.Stages;

/**
 * @author olejek
 * Created by olejek on 14.09.16.
 */
public class StageAdapter extends RealmBaseAdapter<Stages> implements ListAdapter {
    public static final String TABLE_NAME = "Stages";

    public StageAdapter(@NonNull Context context, RealmResults<Stages> data) {
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
    public Stages getItem(int position) {
        Stages stages;
        if (adapterData != null) {
            stages = adapterData.get(position);
            return stages;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Stages stages;
        if (adapterData != null) {
            stages = adapterData.get(position);
            return stages.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.taskstage_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.ts_ImageStatus);
            viewHolder.title = (TextView) convertView.findViewById(R.id.ts_Name);
            viewHolder.equipment = (TextView) convertView.findViewById(R.id.ts_Equipment);
            //viewHolder.status = (TextView) convertView.findViewById(R.id.ts_Status);
            viewHolder.start_date = (TextView) convertView.findViewById(R.id.ts_StartDate);
            viewHolder.end_date = (TextView) convertView.findViewById(R.id.ts_EndDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {

            if (position >= adapterData.size())
                return convertView;

            Stages stage = adapterData.get(position);
            StageStatus stageStatus = stage.getStageStatus();
            viewHolder.title.setText(stage.getStageTemplate().getTitle());
            if (stageStatus != null) {
                //viewHolder.status.setText(context.getString(R.string.status, stage.getStageStatus().getTitle()));
                if (!stageStatus.getUuid().equals(StageStatus.Status.NEW)) {
                    if (stage.getStartDate() != null) {
                        Date lDate = stage.getStartDate();
                        if (lDate != null && lDate.after(new Date(100000))) {
                            viewHolder.start_date.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US).format(lDate));
                        } else {
                            viewHolder.start_date.setText(R.string.not_started);
                        }
                    }

                    if (stage.getEndDate() != null) {
                        Date lDate = stage.getEndDate();
                        if (lDate != null && lDate.after(new Date(100000))) {
                            viewHolder.end_date.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US).format(lDate));
                        } else {
                            viewHolder.end_date.setText(R.string.not_finished);
                        }
                    } else {
                        viewHolder.end_date.setText("не закончен");
                    }
                }
            }
            if (stage.getEquipment() != null) {
                viewHolder.equipment.setText(stage.getEquipment().getTitle());
            }

            if (stageStatus != null && stage.getEquipment() != null && stage.getEquipment().getCriticalType() != null) {
                String stageStatusUuid = stageStatus.getUuid();
                String criticalTypeUuid = stage.getEquipment().getCriticalType().getUuid();
                if (stageStatusUuid.equals(StageStatus.Status.NEW) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_3)))
                    viewHolder.icon.setImageResource(R.drawable.status_easy_receive);
                if (stageStatusUuid.equals(StageStatus.Status.NEW) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_2)))
                    viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
                if (stageStatusUuid.equals(StageStatus.Status.NEW) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_1)))
                    viewHolder.icon.setImageResource(R.drawable.status_high_receive);
                if (stageStatusUuid.equals(StageStatus.Status.IN_WORK) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_3)))
                    viewHolder.icon.setImageResource(R.drawable.status_easy_work);
                if (stageStatusUuid.equals(StageStatus.Status.IN_WORK) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_2)))
                    viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                if (stageStatusUuid.equals(StageStatus.Status.IN_WORK) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_1)))
                    viewHolder.icon.setImageResource(R.drawable.status_high_work);
                if (stageStatusUuid.equals(StageStatus.Status.COMPLETE) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_3)))
                    viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                if (stageStatusUuid.equals(StageStatus.Status.COMPLETE) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_2)))
                    viewHolder.icon.setImageResource(R.drawable.status_mod_ready);
                if (stageStatusUuid.equals(StageStatus.Status.COMPLETE) && (criticalTypeUuid.equals(CriticalType.Status.TYPE_1)))
                    viewHolder.icon.setImageResource(R.drawable.status_high_ready);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView equipment;
        TextView title;
        //TextView taskTitle;
        //TextView description;
        //TextView normative;
        //TextView comment;
        //TextView stageType;
        //TextView stageStatus;
        //TextView stageVerdict;
        //TextView status;
        ImageView icon;
        //ImageView image;
        TextView start_date;
        TextView end_date;
    }
}
