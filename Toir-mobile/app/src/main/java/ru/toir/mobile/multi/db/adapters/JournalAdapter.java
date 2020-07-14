package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.Journal;

/**
 * @author olejek
 * Created by koputo on 25.01.17
 */
public class JournalAdapter extends RealmBaseAdapter<Journal> implements ListAdapter {
    public static final String TABLE_NAME = "Journal";

    public JournalAdapter(RealmResults<Journal> data) {
        super(data);
    }

    public RealmResults<Journal> getAllItems() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Journal> result = realm.where(Journal.class).findAll();
        realm.close();
        return result;
    }

    @Override
    public Journal getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Journal journal;
        if (adapterData != null) {
            journal = adapterData.get(position);
            return journal.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_with_2_columns, parent, false);
            viewHolder.date = convertView.findViewById(R.id.row2_date);
            viewHolder.descr = convertView.findViewById(R.id.row2_description);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Journal journal;
        if (adapterData != null && viewHolder.date != null) {
            String sDate;
            journal = adapterData.get(position);
            if (journal != null) {
                sDate = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.US)
                        .format(journal.getDate());
                viewHolder.date.setText(sDate);
                viewHolder.descr.setText(journal.getDescription());
            }
        }

        if (convertView == null) {
            TextView textView = new TextView(context);
            if (adapterData != null) {
                journal = adapterData.get(position);
                if (journal != null) {
                    textView.setText(journal.getDate().toString());
                }

                textView.setTextSize(16);
                textView.setPadding(5, 5, 5, 5);
            }

            return textView;
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView date;
        TextView descr;
    }

}
