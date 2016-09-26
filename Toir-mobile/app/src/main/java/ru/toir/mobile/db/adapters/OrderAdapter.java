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

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author olejek
 * Created by olejek on 12.09.16.
 */
public class OrderAdapter extends RealmBaseAdapter<Orders> implements ListAdapter {
    public static final String TABLE_NAME = "Orders";

    public OrderAdapter(@NonNull Context context, int resId, RealmResults<Orders> data) {
        super(context, data);
    }

    @Override
    public int getCount() {
        Realm realmDB = Realm.getDefaultInstance();
        RealmResults<Orders> rows = realmDB.where(Orders.class).findAll();
        return rows.size();
    }

    @Override
    public Orders getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Realm realmDB = Realm.getDefaultInstance();
        OrderStatus orderStatus;
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
            String sDate = new SimpleDateFormat("dd.MM.YYYY", Locale.US).format(lDate);
            viewHolder.created.setText(sDate);

            orderStatus = realmDB.where(OrderStatus.class).equalTo("uuid",order.getOrderStatusUuid()).findFirst();
            viewHolder.status.setText(order.getOrderStatusUuid());
            pathToImages = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "Android"
                    + File.separator + "data"
                    + File.separator + "ru.toir.mobile"
                    + File.separator + "img"
                    + File.separator;
            File imgFile = new File(pathToImages + orderStatus.getIcon());
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
