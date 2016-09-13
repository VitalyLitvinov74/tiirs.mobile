package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationTemplate;
import ru.toir.mobile.db.realm.OperationVerdict;
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

    public OperationAdapter(@NonNull Context context, int resId, RealmResults<Operation> data) {
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Realm realmDB = Realm.getDefaultInstance();
        OperationStatus operationStatus;
        OperationVerdict operationVerdict;
        String pathToImages;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.operation_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.op_Name);
            viewHolder.status = (TextView) convertView.findViewById(R.id.op_Status);
            viewHolder.verdict = (TextView) convertView.findViewById(R.id.op_Verdict);
            viewHolder.startdate = (TextView) convertView.findViewById(R.id.op_StartDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            Operation operation = adapterData.get(position);
            OperationTemplate operationTemplate = realmDB.where(OperationTemplate.class).equalTo("operationTemplateUuid",operation.getOperationTemplateUuid()).findFirst();
            viewHolder.title.setText(operationTemplate.getTitle());
            long lDate = operation.getStartDate();
            String sDate = DataUtils.getDate(lDate, "dd.MM.yyyy HH:ss");
            viewHolder.startdate.setText(sDate);
            operationVerdict = realmDB.where(OperationVerdict.class).equalTo("operationVerdictUuid",operation.getOperationVerdictUuid()).findFirst();
            viewHolder.verdict.setText(operationVerdict.getTitle());
            operationStatus = realmDB.where(OperationStatus.class).equalTo("operationStatusUuid",operation.getOperationStatusUuid()).findFirst();
            viewHolder.status.setText(operation.getOperationStatusUuid());
            // TODO может вынести это на глобальный уровень?
            pathToImages = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "Android"
                    + File.separator + "data"
                    + File.separator + "ru.toir.mobile"
                    + File.separator + "img"
                    + File.separator;
            File imgFile = new File(pathToImages + operationStatus.getIcon());
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
