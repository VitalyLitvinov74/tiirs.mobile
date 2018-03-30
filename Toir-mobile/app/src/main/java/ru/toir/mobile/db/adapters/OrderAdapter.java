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
import java.util.Locale;

import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.OrderLevel;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author olejek
 * Created by olejek on 12.09.16.
 */
public class OrderAdapter extends RealmBaseAdapter<Orders> implements ListAdapter, View.OnClickListener {
    public static final String TABLE_NAME = "Orders";
    private static final int TYPE_SEPARATOR = -1;
    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }
    };
    private List<Long> separates = new ArrayList<>();

    public OrderAdapter(@NonNull Context context, RealmResults<Orders> data) {
        super(context, data);
        makeSeparates();
    }

    @Override
    public void notifyDataSetChanged() {
        makeSeparates();
        super.notifyDataSetChanged();
    }

    private void makeSeparates() {
        int i = 0;
        int j = 0;
        Date currentDate = new Date();
        Date separateDate;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        separates.clear();

        if (adapterData != null)
        for(Orders order : adapterData) {
            separateDate = order.getOpenDate();
            if (!fmt.format(separateDate).equals(fmt.format(currentDate))) {
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

    @Override
    public int getCount() {
        return separates.size();
    }

    @Override
    public Orders getItem(int position) {
        Orders order = null;
        long realId = separates.get(position);
        if (realId != TYPE_SEPARATOR) {
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
                //lDate = adapterData.get((int) id).getCreatedAt();
                lDate = adapterData.get((int) id).getStartDate();
                if (lDate != null) {
                    sDate = new SimpleDateFormat("dd MMMM yyyy", myDateFormatSymbols).format(lDate);
                    viewHolder.title.setText(sDate);
                } else {
                    viewHolder.title.setText("");
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
                OrderStatus orderStatus;
                orderStatus = order.getOrderStatus();
                lDate = order.getOpenDate();
                if (lDate != null && lDate.after(new Date(100000))) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", myDateFormatSymbols).format(lDate);
                    viewHolder.created.setText(sDate);
                }
                else {
                    viewHolder.created.setText(R.string.not_started);
                }
                viewHolder.title.setText(order.getTitle());
                if (orderStatus != null) {
                    String orderStatusUuid = orderStatus.getUuid();
                    String orderLevelUuid = order.getOrderLevel().getUuid();
                    viewHolder.status.setText(orderStatus.getTitle());
                    if (orderStatusUuid.equals(OrderStatus.Status.NEW) && (orderLevelUuid.equals(OrderLevel.Level.Level1) || orderLevelUuid.equals(OrderLevel.Level.Level2))) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_receive);
                        viewHolder.created.setText(sDate + " [" + "Получен" + "/" + "Низкая критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.NEW) && orderLevelUuid.equals(OrderLevel.Level.Level3)) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
                        viewHolder.created.setText(sDate + " [" + "Получен" + "/" + "Средняя критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.NEW) && (orderLevelUuid.equals(OrderLevel.Level.Level5) || orderLevelUuid.equals(OrderLevel.Level.Level4))) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_receive);
                        viewHolder.created.setText(sDate + " [" + "Получен" + "/" + "Высокая критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK) && (orderLevelUuid.equals(OrderLevel.Level.Level1) || orderLevelUuid.equals(OrderLevel.Level.Level2))) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_work);
                        viewHolder.created.setText(sDate + " [" + "В работе" + "/" + "Низкая критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK) && orderLevelUuid.equals(OrderLevel.Level.Level3)) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                        viewHolder.created.setText(sDate + " [" + "В работе" + "/" + "Средняя критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK) && (orderLevelUuid.equals(OrderLevel.Level.Level5) || orderLevelUuid.equals(OrderLevel.Level.Level4))) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_work);
                        viewHolder.created.setText(sDate + " [" + "В работе" + "/" + "Высокая критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE) && (orderLevelUuid.equals(OrderLevel.Level.Level1) || orderLevelUuid.equals(OrderLevel.Level.Level2))) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                        viewHolder.created.setText(sDate + " [" + "Выполнен" + "/" + "Низкая критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE) && orderLevelUuid.equals(OrderLevel.Level.Level3)) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_ready);
                        viewHolder.created.setText(sDate + " [" + "Выполнен" + "/" + "Средняя критичность" + "]");
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE) && (orderLevelUuid.equals(OrderLevel.Level.Level5) || orderLevelUuid.equals(OrderLevel.Level.Level4))) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_ready);
                        viewHolder.created.setText(sDate + " [" + "Выполнен" + "/" + "Высокая критичность" + "]");
                    }
                }
        }
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_ImageStatus:
                break;
            default:
                break;
        }
    }

    private static class ViewHolder {
        TextView created;
        TextView title;
        TextView status;
        ImageView icon;
    }
}
