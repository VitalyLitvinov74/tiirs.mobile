package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.Instruction;

public class InstructionAdapter extends RealmBaseAdapter<Instruction> implements ListAdapter {
    public static final String TABLE_NAME = "Instruction";

    public InstructionAdapter(RealmResults<Instruction> data) {
        super(data);
    }

    @Override
    public Instruction getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        Instruction instruction;
        if (adapterData != null) {
            instruction = adapterData.get(position);
            return instruction.get_id();
        }

        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.instruction_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.lv_firstLine);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Instruction instruction;
        if (adapterData != null) {
            instruction = adapterData.get(position);
            if (instruction != null) {
                viewHolder.title.setText(instruction.getTitle());
                viewHolder.title.setOnClickListener(v -> {
                    final File file = new File(context.getExternalFilesDir(
                            instruction.getImageFilePath(AuthorizedUser.getInstance().getDbName())),
                            instruction.getImageFileName());
                    if (file.exists()) {
                        Intent intent = EquipmentInfoActivity.showDocument(file, context.getApplicationContext());
                        if (intent != null) {
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
    }
}
