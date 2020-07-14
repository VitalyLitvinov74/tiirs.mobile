package ru.toir.mobile.multi.db.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.Operation;
import ru.toir.mobile.multi.db.realm.OperationVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 16.05.18.
 */
public class OperationCancelAdapter extends RealmBaseAdapter<Operation> implements ListAdapter {
    private static final int MAX_OPERATIONS = 100;

    public OperationCancelAdapter(RealmResults<Operation> data) {
        super(data);
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
            operation = adapterData.get(position);
            return operation;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.operation_cancel_item, parent, false);
            viewHolder.title = convertView.findViewById(R.id.operation_title);
            viewHolder.status = convertView.findViewById(R.id.operation_status);
            viewHolder.spinner = convertView.findViewById(R.id.operation_verdict_spinner);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Operation operation = adapterData.get(position);
        if (operation != null) {
            Realm realm = Realm.getDefaultInstance();
            viewHolder.title.setText(operation.getOperationTemplate().getTitle());
            RealmResults<OperationVerdict> operationVerdict;
            operationVerdict = realm.where(OperationVerdict.class).findAll();
            OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(operationVerdict);
            viewHolder.spinner.setAdapter(operationVerdictAdapter);
            realm.close();
        }

        convertView.setTag(viewHolder);

        return convertView;
    }

    public static class ViewHolder {
        TextView title;
        CheckBox status;
        Spinner spinner;
    }
}
