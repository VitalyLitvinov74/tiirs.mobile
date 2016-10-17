package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import ru.toir.mobile.db.realm.Operation;

/**
 * @author olejek
 * Created by olejek on 13.09.16.
 */
public class OperationAdapter extends RealmBaseAdapter<Operation> implements ListAdapter {
    public static final String TABLE_NAME = "Operation";

    public OperationAdapter(@NonNull Context context, RealmResults<Operation> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realmDB = Realm.getDefaultInstance();
        RealmResults<Operation> rows = realmDB.where(Operation.class).findAll();
        return rows.size();
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
        Operation operations;
        if (adapterData != null) {
            operations = adapterData.get(position);
            return operations.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //String pathToImages;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.operation_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.operation_title);
            viewHolder.status = (CheckBox) convertView.findViewById(R.id.operation_status);
            viewHolder.verdict = (ImageView) convertView.findViewById(R.id.operation_verdict);
            viewHolder.start_date = (TextView) convertView.findViewById(R.id.operation_startDate);
            viewHolder.end_date = (TextView) convertView.findViewById(R.id.operation_endDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            Operation operation = adapterData.get(position);
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
                // TODO может вынести это на глобальный уровень?
                // тут берем изображение или из иконки или из ресурсов по заранее выстроенной логике
                //viewHolder.verdict.setImageResource();
                /*
                pathToImages = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "Android"
                    + File.separator + "data"
                    + File.separator + "ru.toir.mobile"
                    + File.separator + "img"
                    + File.separator;
                File imgFile = new File(pathToImages + operation.getOperationStatus().getIcon());
                if (imgFile.exists() && imgFile.isFile()) {
                    Bitmap mBitmap = BitmapFactory.decodeFile(imgFile
                            .getAbsolutePath());
                    viewHolder.icon.setImageBitmap(mBitmap);*/
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
    }
}
