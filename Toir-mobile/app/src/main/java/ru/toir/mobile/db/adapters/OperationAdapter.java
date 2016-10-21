package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import ru.toir.mobile.db.realm.Operation;

/**
 * @author olejek
 * Created by olejek on 13.09.16.
 */
public class OperationAdapter extends RealmBaseAdapter<Operation> implements ListAdapter {
    public static final String TABLE_NAME = "Operation";
    private boolean[] visibility = new boolean[50];

    public OperationAdapter(@NonNull Context context, RealmResults<Operation> data) {
        super(context, data);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return adapterData.size();
    }

    @Override
    public Operation getItem(int position) {
        Operation operation;
        if (adapterData != null) {
            operation = adapterData.get(position);
            return operation;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return adapterData.get(position).get_id();
    }

    public void setItemVisibility(int position) {
        visibility[position]=!visibility[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Operation operation;
        viewHolder = new ViewHolder();
        if (adapterData==null) return convertView;
        operation = adapterData.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.operation_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.operation_title);
            viewHolder.status = (CheckBox) convertView.findViewById(R.id.operation_status);
            viewHolder.verdict = (ImageView) convertView.findViewById(R.id.operation_verdict);
            viewHolder.start_date = (TextView) convertView.findViewById(R.id.operation_startDate);
            viewHolder.end_date = (TextView) convertView.findViewById(R.id.operation_endDate);
            viewHolder.description = (TextView) convertView.findViewById(R.id.op_description);
            viewHolder.normative = (TextView) convertView.findViewById(R.id.op_normative);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.op_image);
            viewHolder.description_layout = (RelativeLayout) convertView.findViewById(R.id.operation_description_layout);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (visibility[position]) {
            viewHolder.description_layout.setVisibility(View.VISIBLE);
            convertView.setTag(viewHolder);
            viewHolder = (ViewHolder) convertView.getTag();
            notifyDataSetChanged();
           }
        else {
            viewHolder.description_layout.setVisibility(View.GONE);
            convertView.setTag(viewHolder);
            viewHolder = (ViewHolder) convertView.getTag();
            notifyDataSetChanged();
        }

        if (adapterData != null) {
            if (operation != null) {
                String sDate;
                viewHolder.title.setText(operation.getOperationTemplate().getTitle());
                Date lDate = operation.getStartDate();
                if (lDate != null) {
                    sDate = new SimpleDateFormat("dd.MM.yy HH:ss", Locale.US).format(lDate);
                    viewHolder.start_date.setText(sDate);
                }
                lDate = operation.getEndDate();
                if (lDate != null) {
                    sDate = new SimpleDateFormat("dd.MM.yy HH:ss", Locale.US).format(lDate);
                    viewHolder.end_date.setText(sDate);
                }
                if (operation.getOperationStatus().getTitle().equals("Выполнена")) {
                    viewHolder.status.setChecked(true);
                }
                viewHolder.description.setText(operation.getOperationTemplate().getDescription());
                viewHolder.normative.setText("" + operation.getOperationTemplate().getNormative());
            }
        }
      return convertView;
    }

    private static class ViewHolder {
        TextView title;
        CheckBox status;
        ImageView verdict;
        TextView start_date;
        TextView end_date;
        TextView description;
        TextView normative;
        ImageView image;
        RelativeLayout description_layout;
    }
}
