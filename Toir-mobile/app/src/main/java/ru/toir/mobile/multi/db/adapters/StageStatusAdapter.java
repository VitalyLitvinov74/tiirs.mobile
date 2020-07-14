package ru.toir.mobile.multi.db.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.StageStatus;

/**
 * @author olejek
 * Created by olejek on 16.03.17.
 */
public class StageStatusAdapter extends RealmBaseAdapter<StageStatus> implements ListAdapter {
    public static final String TABLE_NAME = "StageStatus";

    public StageStatusAdapter(RealmResults<StageStatus> data) {
        super(data);
    }

    @Override
    public StageStatus getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (parent.getId() == R.id.simple_spinner) {
            TextView textView = (TextView) View.inflate(parent.getContext(), android.R.layout.simple_spinner_item, null);
            StageStatus stageStatus;
            if (adapterData != null) {
                stageStatus = adapterData.get(position);
                textView.setText(stageStatus.getTitle());
            }
            return textView;
        }
        if (parent.getId() == R.id.reference_listView) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.lv_firstLine);
                viewHolder.uuid = convertView.findViewById(R.id.lv_secondLine);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            StageStatus stageStatus;
            if (adapterData != null) {
                stageStatus = adapterData.get(position);
                if (stageStatus != null) {
                    viewHolder.title.setText(stageStatus.getTitle());
                    viewHolder.uuid.setText(stageStatus.getUuid());
                }
            }
            //TODO сопоставление изображений
            //viewHolder.icon.setImageResource(R.drawable.img_3);
            return convertView;
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView uuid;
        TextView title;
    }
}
