package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.utils.DataUtils;

/**
 * @author olejek
 * Created by olejek on 13.09.16.
 */
public class OperationAdapter extends RealmBaseAdapter<Operation> implements ListAdapter {
    public static final String TABLE_NAME = "Operation";

    private static class ViewHolder{
        TextView title;
        TextView status;
        TextView verdict;
        TextView startdate;
        ImageView icon;
    }

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
        String pathToImages;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.operation_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.op_Name);
            viewHolder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            viewHolder.status = (TextView) convertView.findViewById(R.id.op_Status);
            viewHolder.status.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            viewHolder.verdict = (TextView) convertView.findViewById(R.id.op_Verdict);
            viewHolder.verdict.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            viewHolder.startdate = (TextView) convertView.findViewById(R.id.op_StartDate);
            viewHolder.startdate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            Operation operation = adapterData.get(position);
            viewHolder.title.setText(operation.getOperationTemplate().getTitle());
            long lDate = operation.getStartDate();
            String sDate = DataUtils.getDate(lDate, "dd.MM.yyyy HH:ss");
            viewHolder.startdate.setText(sDate);
            viewHolder.verdict.setText(operation.getOperationVerdict().getTitle());
            viewHolder.status.setText(operation.getOperationStatus().getTitle());
            // TODO может вынести это на глобальный уровень?
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
                viewHolder.icon.setImageBitmap(mBitmap);
            }
            else {
                imgFile = new File(pathToImages + "help_32.png");
                if (imgFile.exists() && imgFile.isFile()) {
                    Bitmap mBitmap = BitmapFactory.decodeFile(imgFile
                            .getAbsolutePath());
                    viewHolder.icon.setImageBitmap(mBitmap);
                }
            }
        }
        return convertView;
    }
}
