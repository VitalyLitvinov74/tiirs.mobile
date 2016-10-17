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
import java.util.List;

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
    private List<Long> separates = new ArrayList<>();
    private static final int TYPE_SEPARATOR = -1;

    @Override
    public void notifyDataSetChanged() {
        makeSeparates();
        super.notifyDataSetChanged();
    }

    private void makeSeparates() {
        int i = 0;
        int j = 0;
        Date currentDate = new Date();
        Date orderCloseDate;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        separates.clear();

        if (adapterData != null)
        for(Orders order : adapterData) {
            orderCloseDate = order.getCloseDate();
            if (!fmt.format(orderCloseDate).equals(fmt.format(currentDate))) {
                currentDate = order.getCloseDate();
                separates.add(i + j, Long.valueOf(TYPE_SEPARATOR));
                j++;
                separates.add(i + j, Long.valueOf(i));
            } else {
                separates.add(i + j, Long.valueOf(i));
            }
            i++;
        }
    }

    public OrderAdapter(@NonNull Context context, RealmResults<Orders> data) {
        super(context, data);
        makeSeparates();
    }

    @Override
    public int getCount() {
        return separates.size();
    }

    @Override
    public Orders getItem(int position) {
        Orders order = null;
        long realId = separates.get(position);
        if(realId != TYPE_SEPARATOR) {
            if (adapterData != null) {
                order = adapterData.get((int) realId);
            }
        }
        return order;
    }


    @Override
    public long getItemId(int position) {
        return separates.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Date lDate;
        String sDate = "неизвестно";
        viewHolder = new ViewHolder();
        long type = separates.get(position);
        switch ((int)type) {
            case TYPE_SEPARATOR:
                convertView = inflater.inflate(R.layout.order_item_divider, null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.order_date_divider);
                convertView.setTag(viewHolder);
                long id = separates.get(position + 1);
                if (adapterData == null) break;
                lDate = adapterData.get((int)id).getCloseDate();
                if (lDate != null) {
                    sDate = new SimpleDateFormat("dd MMMM yyyy", myDateFormatSymbols).format(lDate);
                    viewHolder.title.setText(sDate);
                }
                break;
            default:
                convertView = inflater.inflate(R.layout.order_item, parent, false);
                viewHolder.created = (TextView) convertView.findViewById(R.id.order_Create);
                viewHolder.title = (TextView) convertView.findViewById(R.id.order_Name);
                viewHolder.status = (TextView) convertView.findViewById(R.id.order_status);
                viewHolder.icon = (ImageView) convertView.findViewById(R.id.order_ImageStatus);
                convertView.setTag(viewHolder);
                Orders order = getItem(position);
                if (order == null) break;
                lDate = order.getCloseDate();
                if (lDate != null) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", myDateFormatSymbols).format(lDate);
                    viewHolder.created.setText(sDate);
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
        ImageView icon;
    }
}
