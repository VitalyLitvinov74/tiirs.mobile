package ru.toir.mobile.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.CriticalType;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.StageStatus;

/**
 * @author olejek
 * Created by olejek on 14.09.16.
 */
public class StageAdapter extends RealmBaseAdapter<Stage> implements ListAdapter {
    public static final String TABLE_NAME = "Stage";

    public StageAdapter(RealmResults<Stage> data) {
        super(data);
    }

    @Override
    public Stage getItem(int position) {
        Stage stages;
        if (adapterData != null) {
            stages = adapterData.get(position);
            return stages;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Stage stages;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskstage_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.linearLayout = convertView.findViewById(R.id.stage_linear_layout);
            viewHolder.icon = convertView.findViewById(R.id.ts_ImageStatus);
            viewHolder.title = convertView.findViewById(R.id.ts_Name);
            viewHolder.equipment = convertView.findViewById(R.id.ts_Equipment);
            //viewHolder.status = (TextView) convertView.findViewById(R.id.ts_Status);
            viewHolder.start_date = convertView.findViewById(R.id.ts_StartDate);
            viewHolder.end_date = convertView.findViewById(R.id.ts_EndDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {

            if (position >= adapterData.size())
                return convertView;

            Stage stage = adapterData.get(position);
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

                    Date endDate = stage.getEndDate();
                    if (endDate != null) {
                        viewHolder.end_date.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.US)
                                .format(endDate));
                    } else {
                        viewHolder.end_date.setText("не закончен");
                    }
                }
            }
            if (stage.getEquipment() != null) {
                viewHolder.equipment.setText(stage.getEquipment().getTitle());
            }

            if (stageStatus != null && stage.getEquipment() != null && stage.getEquipment().getCriticalType() != null) {
                CriticalType criticalType = stage.getEquipment().getCriticalType();

                if (stageStatus.isNew() && criticalType.isType3()) {
                    viewHolder.icon.setImageResource(R.drawable.status_easy_receive);
                }

                if (stageStatus.isNew() && criticalType.isType2()) {
                    viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
                }

                if (stageStatus.isNew() && criticalType.isType1()) {
                    viewHolder.icon.setImageResource(R.drawable.status_high_receive);
                }

                if (stageStatus.isInWork() && criticalType.isType3()) {
                    viewHolder.icon.setImageResource(R.drawable.status_easy_work);
                }

                if (stageStatus.isInWork() && criticalType.isType2()) {
                    viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                }

                if (stageStatus.isInWork() && criticalType.isType1()) {
                    viewHolder.icon.setImageResource(R.drawable.status_high_work);
                }

                if ((!stageStatus.isNew() && !stageStatus.isInWork()) && criticalType.isType3()) {
                    viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                    //viewHolder.linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(R.color.md_green_50));
                }

                if ((!stageStatus.isNew() && !stageStatus.isInWork()) && criticalType.isType2()) {
                    viewHolder.icon.setImageResource(R.drawable.status_mod_ready);
                    //viewHolder.linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(R.color.md_green_50));
                }

                if ((!stageStatus.isNew() && !stageStatus.isInWork()) && criticalType.isType1()) {
                    viewHolder.icon.setImageResource(R.drawable.status_high_ready);
                    //viewHolder.linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(R.color.md_green_50));
                }
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView equipment;
        TextView title;
        ImageView icon;
        RelativeLayout linearLayout;

        TextView start_date;
        TextView end_date;
    }
}
