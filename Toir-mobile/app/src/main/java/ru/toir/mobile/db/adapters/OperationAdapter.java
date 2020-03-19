package ru.toir.mobile.db.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationTemplate;
import ru.toir.mobile.fragments.OrderFragment;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

/**
 * @author olejek
 *         Created by olejek on 13.09.16.
 */
public class OperationAdapter extends RealmBaseAdapter<Operation> implements ListAdapter {
    //    private static final String TABLE_NAME = "Operation";
    private static final int MAX_OPERATIONS = 100;

    private boolean[] visibility = new boolean[MAX_OPERATIONS];
    private boolean[] completed = new boolean[MAX_OPERATIONS];

    private OrderFragment.OnCheckBoxClickListener gCBlistener;

    public OperationAdapter(RealmResults<Operation> data) {
        super(data);
    }

    public void setGBListener(OrderFragment.OnCheckBoxClickListener l) {
        gCBlistener = l;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (adapterData != null) {
            if (adapterData.size() < MAX_OPERATIONS) {
                return adapterData.size();
            } else {
                Log.d("System error", "Превышено максимальное число операций для одного этапа");
                return MAX_OPERATIONS;
            }
        } else {
            return 0;
        }
    }

    @Override
    public Operation getItem(int position) {
        Operation operation;
        if (adapterData != null) {
            if (position < getCount()) {
                operation = adapterData.get(position);
                return operation;
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

    public void setItemVisibility(int position, boolean visible) {
        visibility[position] = visible;
    }

    public boolean getItemVisibility(int position) {
        return visibility[position];
    }

    public void setItemEnable(int position, boolean enable) {
        if (position < MAX_OPERATIONS) {
            completed[position] = enable;
        }
    }

    public boolean isItemEnabled(int position) {
        return completed[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (adapterData == null) {
            return convertView;
        }

        if (position > MAX_OPERATIONS) {
            Log.d("Debug", "position > max_operations");
            return convertView;
        }

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.operation_item, parent, false);
            viewHolder.linearLayout = convertView.findViewById(R.id.operation_linear_layout);
            viewHolder.title = convertView.findViewById(R.id.operation_title);
            viewHolder.status = convertView.findViewById(R.id.operation_status);
            viewHolder.verdict = convertView.findViewById(R.id.operation_verdict);
            viewHolder.start_date = convertView.findViewById(R.id.operation_startDate);
            viewHolder.end_date = convertView.findViewById(R.id.operation_endDate);
            viewHolder.description = convertView.findViewById(R.id.op_description);
            viewHolder.normative = convertView.findViewById(R.id.op_normative);
            viewHolder.measure = convertView.findViewById(R.id.op_measure_label);
            viewHolder.measure_value = convertView.findViewById(R.id.op_measure_value);
            viewHolder.time = convertView.findViewById(R.id.op_time);
            //viewHolder.image = convertView.findViewById(R.id.op_image);
            viewHolder.description_layout = convertView.findViewById(R.id.operation_description_layout);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.status.setOnClickListener(gCBlistener);

        Operation operation = adapterData.get(position);
        if (operation != null) {
            viewHolder.status.setEnabled(completed[position]);

            int showMode = visibility[position] ? View.VISIBLE : View.GONE;
            viewHolder.description_layout.setVisibility(showMode);

            String sDate;
            OperationStatus operationStatus;
            operationStatus = operation.getOperationStatus();
            viewHolder.title.setText(operation.getOperationTemplate().getTitle());
            Date lDate = operation.getStartDate();
            if (lDate != null && lDate.after(new Date(100000))) {
                sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US).format(lDate);
                viewHolder.start_date.setText(sDate);
            } else {
                viewHolder.start_date.setText(R.string.not_started);
            }

            lDate = operation.getEndDate();
            if (lDate != null) {
                sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US).format(lDate);
                viewHolder.end_date.setText(sDate);
            } else {
                viewHolder.end_date.setText(R.string.not_finished);
            }

            if (operationStatus != null) {
                if (operationStatus.isNew()) {
                    viewHolder.verdict.setImageResource(R.drawable.status_high_receive);
                    viewHolder.status.setChecked(false);
                }

                if (operationStatus.isInWork()) {
                    viewHolder.verdict.setImageResource(R.drawable.status_mod_work);
                    viewHolder.status.setChecked(false);
                }

                if (operationStatus.isComplete()) {
                    viewHolder.status.setChecked(true);
                    viewHolder.verdict.setImageResource(R.drawable.status_easy_ready);
                    viewHolder.linearLayout.setBackgroundColor(parent.getContext().getResources().getColor(R.color.md_green_50));
                }
            }

            if (!operation.getOperationVerdict().isNotDefined()) {
                viewHolder.status.setEnabled(false);
            }

            viewHolder.description.setText(operation.getOperationTemplate().getDescription());
            OperationTemplate operationTemplate;
            operationTemplate = operation.getOperationTemplate();
            viewHolder.normative.setText(String.valueOf(operationTemplate.getNormative()));
            Date startDate = operation.getStartDate();
            Date endDate = operation.getEndDate();
            if (startDate != null && endDate != null) {
                int diffTime = (int) (endDate.getTime() - startDate.getTime());
                viewHolder.time.setText(convertView.getContext().getString(R.string.sec_with_value,
                        diffTime / 1000));
            }

            Realm realmDB = Realm.getDefaultInstance();
            MeasuredValue lastValue = realmDB.where(MeasuredValue.class)
                    .equalTo("operation.uuid", operation.getUuid()).findFirst();
            if (lastValue != null) {
                String result = lastValue.getValue() + " (" +
                        new SimpleDateFormat("dd.MM.yy HH:ss", Locale.US).format(lastValue.getDate())
                        + ")";
                viewHolder.measure_value.setText(result);
            } else {
                viewHolder.measure.setVisibility(View.GONE);
                viewHolder.measure_value.setVisibility(View.GONE);
            }

            realmDB.close();

            OperationTemplate opTemplate = operation.getOperationTemplate();
/*
            String imgPath = opTemplate.getImageFilePath();
            String fileName = opTemplate.getImage();
            if (imgPath != null && fileName != null) {
                File path = convertView.getContext().getExternalFilesDir(imgPath);
                if (path != null) {
                    Bitmap tmpBitmap = getResizedBitmap(path + File.separator,
                            fileName, 300, 0, operation.getChangedAt().getTime());
                    if (tmpBitmap != null) {
                        viewHolder.image.setImageBitmap(tmpBitmap);
                    }
                }
            }
*/
        }

        convertView.setTag(viewHolder);

        return convertView;
    }

    public static class ViewHolder {
        TextView title;
        CheckBox status;
        ImageView verdict;
        TextView start_date;
        TextView end_date;
        TextView description;
        TextView normative;
        TextView time;
        TextView measure;
        TextView measure_value;
        //ImageView image;
        LinearLayout linearLayout;
        RelativeLayout description_layout;
    }
}
