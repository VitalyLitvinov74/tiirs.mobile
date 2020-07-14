package ru.toir.mobile.multi.db.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.StageInfoActivity;
import ru.toir.mobile.multi.db.realm.Stage;
import ru.toir.mobile.multi.db.realm.StageStatus;
import ru.toir.mobile.multi.utils.DataUtils;

/**
 * @author olejek
 * Created by olejek on 14.09.16.
 */
public class StageAdapter extends RealmBaseAdapter<Stage> implements ListAdapter {
    public static final String TABLE_NAME = "Stage";
    EventListener listener;
    private Context mContext;

    public StageAdapter(RealmResults<Stage> data, Context context, EventListener listener) {
        super(data);
        mContext = context;
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.taskstage_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.icon = convertView.findViewById(R.id.ts_ImageStatus);
            viewHolder.title = convertView.findViewById(R.id.ts_Name);
            viewHolder.equipment = convertView.findViewById(R.id.ts_Equipment);
            viewHolder.normative = convertView.findViewById(R.id.ts_normative);
            //viewHolder.status = (TextView) convertView.findViewById(R.id.ts_Status);
            viewHolder.start_date = convertView.findViewById(R.id.ts_StartDate);
            viewHolder.end_date = convertView.findViewById(R.id.ts_EndDate);
            viewHolder.options = convertView.findViewById(R.id.options);
            viewHolder.stage_option = convertView.findViewById(R.id.stage_option);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {

            if (position >= adapterData.size())
                return convertView;

            final Stage stage = adapterData.get(position);
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
                String sn = stage.getEquipment().getSerialNumber();
                if (sn != null && !sn.equals("")) {
                    viewHolder.equipment.setText(stage.getEquipment().getTitle().concat(" [").concat(sn).concat("]"));
                } else {
                    viewHolder.equipment.setText(stage.getEquipment().getTitle());
                }
            }
            String normative = convertView.getContext().getString(R.string.normative);
            //normative += " " + convertView.getContext().getString(R.string.sec_with_value, stage.getStageTemplate().getNormative() / 1000);
            normative += " " + DataUtils.getNormative(stage.getStageTemplate().getNormative());
            viewHolder.normative.setText(normative);

            if (stageStatus != null) {
                if (stageStatus.isNew()) {
                    viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
                }
                if (stageStatus.isInWork()) {
                    viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                }
                if ((!stageStatus.isNew() && !stageStatus.isInWork())) {
                    viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                }
                // не выполнен, но завершен
                if ((stageStatus.isUnComplete())) {
                    viewHolder.icon.setImageResource(R.drawable.status_high_ready);
                }
            }

            if (listener != null) {
                viewHolder.stage_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @SuppressLint("RestrictedApi")
                    public void onClick(View v) {
                        //Display option menu
                        MenuBuilder menuBuilder = new MenuBuilder(mContext);
                        Context wrapper = new ContextThemeWrapper(mContext, R.style.Popup);
                        PopupMenu popupMenu = new PopupMenu(wrapper, viewHolder.options);
                        MenuPopupHelper menuHelper = new MenuPopupHelper(mContext, menuBuilder, viewHolder.options);

                        popupMenu.inflate(R.menu.stage_options);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.stage_cancel:
                                        if (stage.isNew() || stage.isInWork()) {
                                            // показываем диалог изменения статуса
                                            listener.closeStageManual(stage, null);
                                        } else {
                                            // операция уже выполнена, изменить статус нельзя
                                            // сообщаем об этом
                                            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                                            dialog.setTitle("Внимание!");
                                            dialog.setMessage("Изменить статус этапа нельзя!");
                                            dialog.setPositiveButton(android.R.string.ok,
                                                    new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                            dialog.show();
                                        }
                                        break;
                                    case R.id.equipment_info:
                                        String equipment_uuid = stage.getEquipment().getUuid();
                                        Intent equipmentInfo = new Intent(mContext, EquipmentInfoActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("equipment_uuid", equipment_uuid);
                                        equipmentInfo.putExtras(bundle);
                                        mContext.startActivity(equipmentInfo);
                                        break;
                                    case R.id.stage_info:
                                        Intent stageInfo = new Intent(mContext, StageInfoActivity.class);
                                        Bundle bundle_stage = new Bundle();
                                        bundle_stage.putString("stage_uuid", stage.getUuid());
                                        stageInfo.putExtras(bundle_stage);
                                        mContext.startActivity(stageInfo);
                                        break;
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        menuHelper.setForceShowIcon(true);
                        menuHelper.show();

                        popupMenu.show();
                    }
                });
            } else {
                viewHolder.stage_option.setVisibility(View.INVISIBLE);
            }
        }
        return convertView;
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

    public interface EventListener {
        void closeStageManual(final Stage stage, AdapterView<?> parent);
    }

    private static class ViewHolder {
        TextView equipment;
        TextView title;
        ImageView icon;
        TextView normative;
        TextView start_date;
        TextView end_date;
        RelativeLayout stage_option;
        TextView options;
    }
}
