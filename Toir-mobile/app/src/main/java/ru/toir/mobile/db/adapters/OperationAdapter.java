package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
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
    private Context context;
    private boolean[] visibility = new boolean[50];
    private boolean[] completed = new boolean[50];

    public class Status {
        public static final String NEW = "1e9b4d73-044c-471b-a08d-26f36ebb22ba";
        public static final String IN_WORK = "9f980db5-934c-4ddb-999a-04c6c3daca59";
        public static final String COMPLETE = "dc6dca37-2cc9-44da-aff9-19bf143e611a";
        public static final String UN_COMPLETE = "363c08ec-89d9-47df-b7cf-63a05d56594c";
    }

    public OperationAdapter(@NonNull Context context, RealmResults<Operation> data) {
        super(context, data);
        this.context = context;
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

    public void setItemEnable(int position, boolean enable)
    {
        completed[position]=enable;
    }
    public boolean getItemEnable(int position)
    {
        return completed[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        File image;
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

        viewHolder.status.setEnabled(completed[position]);

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
                image=getOutputMediaFile(operation.getUuid());
                    Bitmap imageBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                if (imageBitmap!=null) {
                    int width = imageBitmap.getWidth();
                    int height = imageBitmap.getHeight();
                    int newWidth = 300;
                    float scaleWidth = (float)newWidth / (float)width;
                    int newHeight = (int)(height * scaleWidth);
                    viewHolder.image.setImageBitmap(Bitmap.createScaledBitmap (imageBitmap, newWidth, newHeight, false));
                    //viewHolder.image.setImageBitmap(imageBitmap);

                }
            }
        }
      return convertView;
    }

    private File getOutputMediaFile(String operationUuid) {
        File mediaStorageDir = new File(context
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera", "Required media storage does not exist");
                return null;
            }
        }
        String fileName;
        fileName = operationUuid + ".jpg";
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + fileName);
        return mediaFile;
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
