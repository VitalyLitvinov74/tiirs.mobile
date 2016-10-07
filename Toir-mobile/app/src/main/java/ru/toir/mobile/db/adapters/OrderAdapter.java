package ru.toir.mobile.db.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TreeSet;

import io.realm.RealmBaseAdapter;
import io.realm.RealmList;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author olejek
 * Created by olejek on 12.09.16.
 */
public class OrderAdapter extends RealmBaseAdapter<Orders> implements ListAdapter {
    public static final String TABLE_NAME = "Orders";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    private ArrayList mData = new ArrayList();
    private TreeSet mSeparatorsSet = new TreeSet();

    public OrderAdapter(@NonNull Context context, RealmResults<Orders> data) {
        super(context, data);
    }

    public OrderAdapter(@NonNull Context context, RealmList<Orders> data) {
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
    /*
        public int getItemType(int position) {
            if (adapterData != null) {
                if (adapterData.get(position).getTitle().equals("---")) return TYPE_SEPARATOR;
                else return TYPE_ITEM;
            }
            return -1;
        }
        public void addItem(final Orders item) {
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final Orders item) {
            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }
    */
    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.order_item, parent, false);
            viewHolder.created = (TextView) convertView.findViewById(R.id.order_Create);
            viewHolder.title = (TextView) convertView.findViewById(R.id.order_Name);
            viewHolder.status = (TextView) convertView.findViewById(R.id.order_status);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.order_ImageStatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (adapterData!=null) {
            Orders order = adapterData.get(position);
            Date lDate = order.getOpenDate();
            String sDate;
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
