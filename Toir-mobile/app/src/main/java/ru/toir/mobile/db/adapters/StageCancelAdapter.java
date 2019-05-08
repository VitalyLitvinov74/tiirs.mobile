package ru.toir.mobile.db.adapters;

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
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.StageVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 16.05.18.
 */
public class StageCancelAdapter extends RealmBaseAdapter<Stage> implements ListAdapter {
    private static final int MAX_OPERATIONS = 100;

    public StageCancelAdapter(RealmResults<Stage> data) {
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
    public Stage getItem(int position) {
        Stage stage;
        if (adapterData != null) {
            stage = adapterData.get(position);
            return stage;
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

        Stage stage = adapterData.get(position);
        if (stage != null) {
            Realm realm = Realm.getDefaultInstance();
            viewHolder.title.setText(stage.getStageTemplate().getTitle());
            RealmResults<StageVerdict> stageVerdicts;
            stageVerdicts = realm.where(StageVerdict.class).findAll();
            StageVerdictAdapter operationVerdictAdapter = new StageVerdictAdapter(stageVerdicts);
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
