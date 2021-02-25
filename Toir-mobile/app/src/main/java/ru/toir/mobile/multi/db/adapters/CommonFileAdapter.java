package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.CommonFile;

public class CommonFileAdapter extends RealmBaseAdapter<CommonFile> implements ListAdapter {
    public static final String TABLE_NAME = "Documentation";

    public CommonFileAdapter(RealmResults<CommonFile> data) {
        super(data);
    }

    @Override
    public CommonFile getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        CommonFile documentation;
        if (adapterData != null) {
            documentation = adapterData.get(position);
            return documentation.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.information = convertView.findViewById(R.id.lv_secondLine);
            viewHolder.title = convertView.findViewById(R.id.lv_firstLine);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        CommonFile Documentation;
        if (adapterData != null) {
            Documentation = adapterData.get(position);
            String information = "";
            if (Documentation != null) {
                viewHolder.title.setText(Documentation.getDescription());
                information = information.concat("не определена");
                viewHolder.information.setText(information);
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView information;
        TextView title;
    }
}
