package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author olejek
 * Created by olejek on 12.09.16.
 */
public class OrderAdapter extends RealmBaseAdapter<Orders> implements ListAdapter {
    public static final String TABLE_NAME = "Orders";

    public OrderAdapter(@NonNull Context context, RealmResults<Orders> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        return adapterData.size();
    }

    @Override
    public Orders getItem(int position) {
        if (adapterData != null) {
            return adapterData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        Orders order;
        if (adapterData != null) {
            order = adapterData.get(position);
            return order.get_id();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        String pathToImages;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.created = (TextView) convertView.findViewById(R.id.orderi_Create);
            viewHolder.title = (TextView) convertView.findViewById(R.id.orderi_Name);
            viewHolder.status = (TextView) convertView.findViewById(R.id.orderi_Status);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.orderi_ImageStatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            Orders order = adapterData.get(position);
            viewHolder.title.setText(order.getTitle());
            Date lDate = order.getReceiveDate();
            if (lDate != null) {
                String sDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(lDate);
                viewHolder.created.setText(sDate);
            }
            else {
                viewHolder.created.setText("неизвестно");
            }
            viewHolder.status.setText(order.getOrderStatus().getTitle());
            pathToImages = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "Android"
                    + File.separator + "data"
                    + File.separator + "ru.toir.mobile"
                    + File.separator + "img"
                    + File.separator;
            File imgFile = new File(pathToImages + order.getOrderStatus().getIcon());
            if (imgFile.exists() && imgFile.isFile()) {
                Bitmap mBitmap = BitmapFactory.decodeFile(imgFile
                        .getAbsolutePath());
                viewHolder.icon.setImageBitmap(mBitmap);
            }
            else {
                imgFile = new File(pathToImages + "help_32.png");
                if (imgFile.exists() && imgFile.isFile()) {
                    Bitmap mBitmap = BitmapFactory.decodeFile(imgFile
                            .getAbsolutePath());
                    viewHolder.icon.setImageBitmap(mBitmap);
                }
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView created;
        TextView title;
        TextView status;
        ImageView icon;
    }
}
