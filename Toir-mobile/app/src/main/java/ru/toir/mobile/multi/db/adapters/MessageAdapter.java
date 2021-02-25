package ru.toir.mobile.multi.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.Message;

import static ru.toir.mobile.multi.utils.RoundedImageView.getResizedBitmap;

public class MessageAdapter extends RealmBaseAdapter<Message> implements ListAdapter {

    public static final String TABLE_NAME = "Message";

    public MessageAdapter(RealmResults<Message> data) {
        super(data);
    }

    @Override
    public Message getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Message message;
        if (adapterData != null) {
            message = adapterData.get(position);
            return message.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.message_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = convertView.findViewById(R.id.avatar_image);
            viewHolder.user = convertView.findViewById(R.id.user_from);
            viewHolder.text = convertView.findViewById(R.id.text);
            viewHolder.date = convertView.findViewById(R.id.date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message message;
        if (adapterData != null) {
            message = adapterData.get(position);
            if (message != null) {
                viewHolder.user.setText(message.getFromUser().getName());
                AuthorizedUser authUser = AuthorizedUser.getInstance();
                String path = message.getFromUser().getImageFilePath(authUser.getDbName()) + "/";
                Bitmap user_bitmap = getResizedBitmap(path,
                        message.getFromUser().getImage(), 0, 70,
                        message.getFromUser().getChangedAt().getTime());
                if (user_bitmap != null) {
                    viewHolder.avatar.setImageBitmap(user_bitmap);
                }
                viewHolder.text.setText(message.getText());
                String sDate = new SimpleDateFormat("dd.MM.yyyy HH:ss", Locale.US).format(message.getDate());
                viewHolder.date.setText(sDate);
                if (message.getStatus() == Message.Status.MESSAGE_READ) {
                    viewHolder.user.setTypeface(null, Typeface.NORMAL);
                    viewHolder.text.setTypeface(null, Typeface.NORMAL);
                } else {
                    viewHolder.user.setTypeface(null, Typeface.BOLD);
                    viewHolder.text.setTypeface(null, Typeface.BOLD);
                }
            }
        }

        return convertView;
    }


    private static class ViewHolder {
        ImageView avatar;
        TextView text;
        TextView date;
        TextView user;
    }
}
