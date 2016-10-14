package ru.toir.mobile.db.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author olejek
 * Created by olejek on 12.09.16.
 */
public class OrderAdapterD extends ArrayAdapter<Orders> implements ListAdapter {
    public static final String TABLE_NAME = "Orders";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    ArrayList<Orders> adapterData = null;
    Context mContext;

    public OrderAdapterD(@NonNull Context context, ArrayList<Orders> data) {
        super(context,R.layout.listview,data);
        this.mContext = context;
        this.adapterData=data;
    }

    public int getItemType(int position) {
            if (adapterData != null) {
                if (getItem(position).getTitle().equals("0000")) return TYPE_SEPARATOR;
                else return TYPE_ITEM;
            }
            return -1;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Date lDate;
        String sDate;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        int type = getItemType(position);
        viewHolder = new ViewHolder();
        switch (type) {
                case TYPE_ITEM:
                    convertView = inflater.inflate(R.layout.order_item, parent, false);
                    viewHolder.created = (TextView) convertView.findViewById(R.id.order_Create);
                    viewHolder.title = (TextView) convertView.findViewById(R.id.order_Name);
                    viewHolder.status = (TextView) convertView.findViewById(R.id.order_status);
                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.order_ImageStatus);
                    convertView.setTag(viewHolder);
                    Orders order = getItem(position);
                    lDate = order.getOpenDate();
                    if (lDate != null) {
                        sDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(lDate);
                        viewHolder.created.setText(sDate);
                    } else {
                        sDate = "неизвестно";
                    }
                    viewHolder.title.setText(order.getTitle());
                    viewHolder.status.setText(order.getOrderStatus().getTitle());
                    if (order.getOrderStatus().getTitle().equals("Получен") && order.getOrderLevel().getTitle().equals("Низкий")) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_receive);
                        viewHolder.created.setText(sDate + " [" + "Получен" + "/" + "Низкая критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("Получен") && order.getOrderLevel().getTitle().equals("Средний")) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
                        viewHolder.created.setText(sDate + " [" + "Получен" + "/" + "Средняя критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("Получен") && order.getOrderLevel().getTitle().equals("Высокий")) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_receive);
                        viewHolder.created.setText(sDate + " [" + "Получен" + "/" + "Высокая критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("В работе") && order.getOrderLevel().getTitle().equals("Низкий")) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_work);
                        viewHolder.created.setText(sDate + " [" + "В работе" + "/" + "Низкая критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("В работе") && order.getOrderLevel().getTitle().equals("Средний")) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                        viewHolder.created.setText(sDate + " [" + "В работе" + "/" + "Средняя критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("В работе") && order.getOrderLevel().getTitle().equals("Высокий")) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_work);
                        viewHolder.created.setText(sDate + " [" + "В работе" + "/" + "Высокая критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("Выполнен") && order.getOrderLevel().getTitle().equals("Низкий"))  {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                        viewHolder.created.setText(sDate + " [" + "Выполнен" + "/" + "Низкая критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("Выполнен") && order.getOrderLevel().getTitle().equals("Средний")) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_ready);
                        viewHolder.created.setText(sDate + " [" + "Выполнен" + "/" + "Средняя критичность" + "]");
                    }
                    if (order.getOrderStatus().getTitle().equals("Выполнен") && order.getOrderLevel().getTitle().equals("Высокий")) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_ready);
                        viewHolder.created.setText(sDate + " [" + "Выполнен" + "/" + "Высокая критичность" + "]");
                    }
                    break;

            case TYPE_SEPARATOR:
                    convertView = inflater.inflate(R.layout.order_item_divider, null);
                    viewHolder.title = (TextView) convertView.findViewById(R.id.order_date_divider);
                    convertView.setTag(viewHolder);
                    lDate = new Date();
                    if (lDate != null) {
                        sDate = new SimpleDateFormat("dd.MM.yyyy", Locale.US).format(lDate);
                        viewHolder.title.setText(sDate);
                    }
                    break;
                }
        return convertView;
    }

    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols(){
        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }
    };

    private static class ViewHolder {
        TextView created;
        TextView title;
        TextView status;
        //TextView separator;
        ImageView icon;
    }
}
