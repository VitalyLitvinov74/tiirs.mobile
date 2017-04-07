package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Documentation;

/**
 * @author koputo
 * Created by koputo on 08.09.16.
 */
public class DocumentationAdapter extends RealmBaseAdapter<Documentation> implements ListAdapter {
    public static final String TABLE_NAME = "Documentation";

    public DocumentationAdapter(@NonNull Context context, RealmResults<Documentation> data) {
        super(context, data);
    }

    @Override
    public int getCount() {

        if (adapterData != null) {
            return adapterData.size();
        }
        return 0;
    }

    @Override
    public Documentation getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Documentation documentation;
        if (adapterData != null) {
            documentation = adapterData.get(position);
            return documentation.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.information = (TextView) convertView.findViewById(R.id.lv_secondLine);
            viewHolder.title = (TextView) convertView.findViewById(R.id.lv_firstLine);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Documentation Documentation;
        if (adapterData != null) {
            Documentation = adapterData.get(position);
            String information="";
            if (Documentation != null) {
                viewHolder.title.setText(Documentation.getTitle());
                if (Documentation.getDocumentationType() != null) {
                    information = information.concat(Documentation.getDocumentationType().getTitle());
                    information = information.concat(" на ");
                    if (Documentation.getEquipmentModel() != null) {
                        information = information.concat(Documentation.getEquipmentModel().getTitle());
                    }
                    if (Documentation.getEquipment() != null) {
                        information = information.concat(Documentation.getEquipment().getTitle());
                    }
                }
                else {
                    information = information.concat("не определена");
                }
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
