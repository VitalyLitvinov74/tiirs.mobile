package ru.toir.mobile.db.adapters;

import android.view.LayoutInflater;
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
import ru.toir.mobile.db.realm.OrderLevel;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author olejek
 * Created by olejek on 12.09.16.
 */
public class OrderAdapter extends RealmBaseAdapter<Orders> implements ListAdapter {
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

    public OrderAdapter(RealmResults<Orders> data) {
        super(data);
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
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
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
        String textData;
        switch ((int)type) {
            case TYPE_SEPARATOR:
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_divider, null);
                viewHolder.title = convertView.findViewById(R.id.order_date_divider);
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
                viewHolder.created = convertView.findViewById(R.id.order_Create);
                viewHolder.title = convertView.findViewById(R.id.order_Name);
                viewHolder.status = convertView.findViewById(R.id.order_status);
                viewHolder.icon = convertView.findViewById(R.id.order_ImageStatus);
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
                        textData = sDate + " [" + "Получен" + "/" + "Низкая критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.NEW) && orderLevelUuid.equals(OrderLevel.Level.Level3)) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_receive);
                        textData = sDate + " [" + "Получен" + "/" + "Средняя критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.NEW) && (orderLevelUuid.equals(OrderLevel.Level.Level5) || orderLevelUuid.equals(OrderLevel.Level.Level4))) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_receive);
                        textData = sDate + " [" + "Получен" + "/" + "Высокая критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK) && (orderLevelUuid.equals(OrderLevel.Level.Level1) || orderLevelUuid.equals(OrderLevel.Level.Level2))) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_work);
                        textData = sDate + " [" + "В работе" + "/" + "Низкая критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK) && orderLevelUuid.equals(OrderLevel.Level.Level3)) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                        textData = sDate + " [" + "В работе" + "/" + "Средняя критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK) && (orderLevelUuid.equals(OrderLevel.Level.Level5) || orderLevelUuid.equals(OrderLevel.Level.Level4))) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_work);
                        textData = sDate + " [" + "В работе" + "/" + "Высокая критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE) && (orderLevelUuid.equals(OrderLevel.Level.Level1) || orderLevelUuid.equals(OrderLevel.Level.Level2))) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                        textData = sDate + " [" + "Выполнен" + "/" + "Низкая критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE) && orderLevelUuid.equals(OrderLevel.Level.Level3)) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_ready);
                        textData = sDate + " [" + "Выполнен" + "/" + "Средняя критичность" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE) && (orderLevelUuid.equals(OrderLevel.Level.Level5) || orderLevelUuid.equals(OrderLevel.Level.Level4))) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_ready);
                        textData = sDate + " [" + "Выполнен" + "/" + "Высокая критичность" + "]";
                        viewHolder.created.setText(textData);
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
