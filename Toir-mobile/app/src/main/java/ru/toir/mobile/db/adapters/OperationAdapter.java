package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
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
import ru.toir.mobile.db.realm.OperationVerdict;

import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

/**
 * @author olejek
 * Created by olejek on 13.09.16.
 */
public class OperationAdapter extends RealmBaseAdapter<Operation> implements ListAdapter {
    public static final String TABLE_NAME = "Operation";
    Realm realm = Realm.getDefaultInstance();
    private Context context;
    private int counter=0;
    private String taskTemplateUuid;
    private boolean[] visibility = new boolean[50];
    private boolean[] completed = new boolean[50];

    public OperationAdapter(@NonNull Context context, RealmResults<Operation> data, String taskTemplateUuid) {
        super(context, data);
        this.context = context;
        this.taskTemplateUuid = taskTemplateUuid;
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
        File image2;
        Operation operation;
        viewHolder = new ViewHolder();
        if (adapterData==null) return convertView;
        operation = adapterData.get(position);
        if (convertView == null) {
            if (parent.getId() == R.id.list_view) {
                convertView = inflater.inflate(R.layout.operation_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.operation_title);
                viewHolder.status = (CheckBox) convertView.findViewById(R.id.operation_status);
                viewHolder.verdict = (ImageView) convertView.findViewById(R.id.operation_verdict);
                viewHolder.start_date = (TextView) convertView.findViewById(R.id.operation_startDate);
                viewHolder.end_date = (TextView) convertView.findViewById(R.id.operation_endDate);
                viewHolder.description = (TextView) convertView.findViewById(R.id.op_description);
                viewHolder.normative = (TextView) convertView.findViewById(R.id.op_normative);
                viewHolder.measure = (TextView) convertView.findViewById(R.id.op_measure_value);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.op_image);
                viewHolder.description_layout = (RelativeLayout) convertView.findViewById(R.id.operation_description_layout);
            } else {
                convertView = inflater.inflate(R.layout.operation_cancel_item, parent, false);
                viewHolder.title = (TextView) convertView.findViewById(R.id.operation_title);
                viewHolder.status = (CheckBox) convertView.findViewById(R.id.operation_status);
                viewHolder.spinner = (Spinner) convertView.findViewById(R.id.operation_verdict_spinner);
            }
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (parent.getId() == R.id.list_view) {

            viewHolder.status.setEnabled(completed[position]);
            if (visibility[position]) {
                viewHolder.description_layout.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
                viewHolder = (ViewHolder) convertView.getTag();
                //notifyDataSetChanged();
            } else {
                viewHolder.description_layout.setVisibility(View.GONE);
                convertView.setTag(viewHolder);
                viewHolder = (ViewHolder) convertView.getTag();
                //notifyDataSetChanged();
            }

            if (adapterData != null) {
                if (operation != null) {
                    String sDate;
                    OperationStatus operationStatus;
                    operationStatus = operation.getOperationStatus();
                    viewHolder.title.setText(operation.getOperationTemplate().getTitle());
                    Date lDate = operation.getStartDate();
                    if (lDate != null && lDate.after(new Date(100000))) {
                        sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US).format(lDate);
                        viewHolder.start_date.setText(sDate);
                    }
                    else {
                        viewHolder.start_date.setText(R.string.not_started);
                    }
                    lDate = operation.getEndDate();
                    if (lDate != null && lDate.after(new Date(100000))) {
                        sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US).format(lDate);
                        viewHolder.end_date.setText(sDate);
                    }
                    else {
                        viewHolder.end_date.setText(R.string.not_finished);
                    }

                    if (operationStatus != null) {
                        if (operationStatus.getTitle().equals("Не выполнена")) {
                            viewHolder.verdict.setImageResource(R.drawable.status_easy_receive);
                        }
                        if (operationStatus.getTitle().equals("Выполнена")) {
                            viewHolder.status.setChecked(true);
                            viewHolder.verdict.setImageResource(R.drawable.status_easy_ready);
                        }
                    }
                    viewHolder.description.setText(operation.getOperationTemplate().getDescription());
                    viewHolder.normative.setText("" + operation.getOperationTemplate().getNormative());

                    image = getOutputMediaFile(operation.getUuid(), 1);
                    Bitmap imageBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    //counter++;
                    //Toast toast = Toast.makeText(context,""+counter,Toast.LENGTH_SHORT);
                    //toast.show();
                    Realm realmDB = Realm.getDefaultInstance();
                    MeasuredValue lastValue = realmDB.where(MeasuredValue.class).equalTo("operation.uuid", operation.getUuid()).findFirst();
                    if (lastValue != null)
                        viewHolder.measure.setText(lastValue.getValue() + " (" + new SimpleDateFormat("dd.MM.yy HH:ss", Locale.US).format(lastValue.getDate()) + ")");

                    //String path = getPicturesDirectory(context) + "tasks" + File.separator + taskStageTemplateUuid + File.separator;
                    String path = context.getExternalFilesDir("/tasks") + File.separator + taskTemplateUuid + File.separator;
                    Bitmap image_bitmap = getResizedBitmap(path, operation.getOperationTemplate().getImage(), 500, 0, operation.getChangedAt().getTime());
                    if (image_bitmap != null) {
                        viewHolder.image.setImageBitmap(image_bitmap);
                    }
                }
            }
        } else {
            if (adapterData != null) {
                if (operation != null) {
                    viewHolder.title.setText(operation.getOperationTemplate().getTitle());
                    RealmResults<OperationVerdict> operationVerdict;
                    operationVerdict = realm.where(OperationVerdict.class).findAll();
                    OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(context, operationVerdict);
                    viewHolder.spinner.setAdapter(operationVerdictAdapter);
                }
            }
        }
      return convertView;
    }

    private File getOutputMediaFile(String operationUuid, int mini) {
        File mediaStorageDir = new File(context
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath());
        /*
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera", "Required media storage does not exist");
                return null;
            }
        }*/
        File mediaFile = null;
        if (mini==1)
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + operationUuid + "_m.jpg");
        else mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + operationUuid + ".jpg");
        return mediaFile;
    }

    private static class ViewHolder {
        TextView title;
        CheckBox status;
        Spinner spinner;
        ImageView verdict;
        TextView start_date;
        TextView end_date;
        TextView description;
        TextView normative;
        TextView measure;
        ImageView image;
        RelativeLayout description_layout;
    }

    public class Status {
        public static final String NEW = "1e9b4d73-044c-471b-a08d-26f36ebb22ba";
        public static final String IN_WORK = "9f980db5-934c-4ddb-999a-04c6c3daca59";
        public static final String COMPLETE = "dc6dca37-2cc9-44da-aff9-19bf143e611a";
        public static final String UN_COMPLETE = "363c08ec-89d9-47df-b7cf-63a05d56594c";
    }

}
