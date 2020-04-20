package ru.toir.mobile.db.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.OrderLevel;
import ru.toir.mobile.db.realm.OrderRepairPart;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.Task;
import ru.toir.mobile.db.realm.TaskVerdict;

import static java.lang.Long.valueOf;

/**
 * @author olejek
 * Created by olejek on 12.09.16.
 */
public class OrderAdapter extends RealmBaseAdapter<Orders> implements ListAdapter {
    public static final String TABLE_NAME = "Orders";
    private Context mContext;
    EventListener listener;

    private static final int TYPE_SEPARATOR = -1;
    private static DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                    "июля", "августа", "сентября", "октября", "ноября", "декабря"};
        }
    };
    private List<Long> separates = new ArrayList<>();

    public OrderAdapter(RealmResults<Orders> data, Context context, EventListener listener) {
        super(data);
        makeSeparates();
        mContext = context;
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;
        Date lDate;
        String sDate = "неизвестно";
        viewHolder = new ViewHolder();
        long type = separates.get(position);
        String textData;
        switch ((int) type) {
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
                viewHolder.options = convertView.findViewById(R.id.options);
                viewHolder.order_row = convertView.findViewById(R.id.order_row);
                viewHolder.order_option = convertView.findViewById(R.id.order_option);

                convertView.setTag(viewHolder);
                final Orders order = getItem(position);
                if (order == null) break;
                OrderStatus orderStatus;
                orderStatus = order.getOrderStatus();
                lDate = order.getOpenDate();
                if (lDate != null) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", myDateFormatSymbols)
                            .format(lDate);
                    viewHolder.created.setText(sDate);
                } else {
                    viewHolder.created.setText(R.string.not_started);
                }

                viewHolder.title.setText(order.getTitle());
                if (orderStatus != null) {
                    String orderStatusUuid = orderStatus.getUuid();
                    viewHolder.status.setText(orderStatus.getTitle());
                    if (orderStatusUuid.equals(OrderStatus.Status.NEW)) {
                        viewHolder.icon.setImageResource(R.drawable.status_high_receive);
                        textData = sDate + " [" + "Получен" + "]";
                        viewHolder.created.setText(textData);
                    }
                    if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK)) {
                        viewHolder.icon.setImageResource(R.drawable.status_mod_work);
                        textData = sDate + " [" + "В работе" + "]";
                        viewHolder.created.setText(textData);
                    }

                    if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE)) {
                        viewHolder.icon.setImageResource(R.drawable.status_easy_ready);
                        textData = sDate + " [" + "Выполнен" + "]";
                        viewHolder.created.setText(textData);
                        if (!isAllTaskFullComplete(order)) {
                            viewHolder.icon.setImageResource(R.drawable.status_high_ready);
                            textData = sDate + " [" + "Выполнен с проблемами" + "]";
                            viewHolder.created.setText(textData);
                        }
                    }
                }
                viewHolder.order_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.checkClickItem(position);
                    }
                });
                viewHolder.order_option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    @SuppressLint("RestrictedApi")
                    public void onClick(View v) {
                        //Display option menu
                        MenuBuilder menuBuilder = new MenuBuilder(mContext);
                        PopupMenu popupMenu = new PopupMenu(mContext, viewHolder.options);
                        MenuPopupHelper menuHelper = new MenuPopupHelper(mContext, menuBuilder, viewHolder.options);

                        popupMenu.inflate(R.menu.orders_options);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.order_info:
                                        showInformation(order, parent);
                                        break;
                                    case R.id.order_parts:
                                        showRepairParts(order, parent);
                                        break;
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        menuHelper.setForceShowIcon(true);
                        menuHelper.show();

                        popupMenu.show();
                    }
                });
        }
        return convertView;
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

        if (adapterData != null) {
            for (Orders order : adapterData) {
                separateDate = order.getStartDate();
                if (separateDate == null) {
                    separateDate = new Date();
                }

                if (!fmt.format(separateDate).equals(fmt.format(currentDate))) {
                    currentDate = separateDate;
                    separates.add(i + j, valueOf(TYPE_SEPARATOR));
                    j++;
                    separates.add(i + j, valueOf(i));
                } else {
                    separates.add(i + j, valueOf(i));
                }
                i++;
            }
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

    /**
     * Диалог с общей информацией по наряду/задаче/этапу/операции
     */
    private void showInformation(Orders order, ViewGroup parent) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView level, status, title, reason, author, worker, recieve, start, open, close, comment, verdict;
        View myView = LayoutInflater.from(mContext).inflate(R.layout.order_full_information, parent, false);
        String sDate;
        level = myView.findViewById(R.id.order_dialog_level);
        status = myView.findViewById(R.id.order_dialog_status);
        title = myView.findViewById(R.id.order_dialog_title);
        reason = myView.findViewById(R.id.order_dialog_reason);
        author = myView.findViewById(R.id.order_dialog_author);
        worker = myView.findViewById(R.id.order_dialog_worker);
        recieve = myView.findViewById(R.id.order_dialog_recieve);
        start = myView.findViewById(R.id.order_dialog_start);
        open = myView.findViewById(R.id.order_dialog_open);
        close = myView.findViewById(R.id.order_dialog_close);
        comment = myView.findViewById(R.id.order_dialog_comment);
        verdict = myView.findViewById(R.id.order_dialog_verdict);

        if (order != null) {
            if (order.getOrderLevel() != null) {
                level.setText(order.getOrderLevel().getTitle());
                ((GradientDrawable) level.getBackground()).setColor(Color.GREEN);
            } else
                level.setText(order.getOrderLevel().getTitle());
            if (order.getOrderStatus() != null) {
                status.setText(order.getOrderStatus().getTitle());
                ((GradientDrawable) status.getBackground()).setColor(Color.BLUE);
            } else {
                status.setText(order.getOrderStatus().getTitle());
            }
            title.setText(mContext.getString(R.string.order_title, order.get_id(), order.getTitle()));
            reason.setText(mContext.getString(R.string.order_reason, order.getReason()));
            author.setText(mContext.getString(R.string.order_author, order.getAuthor().getName()));
            worker.setText(mContext.getString(R.string.order_worker, order.getUser().getName()));
            Date startDate = order.getStartDate();
            if (startDate != null) {
                sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                        .format(startDate);
            } else {
                sDate = "не назначен";
            }

            start.setText(mContext.getString(R.string.order_start, sDate));
            Date receivDate = order.getReceivDate();
            if (receivDate != null) {
                sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                        .format(receivDate);
            } else {
                sDate = "не получен";
            }

            recieve.setText(mContext.getString(R.string.order_recieved, sDate));

            Date openDate = order.getOpenDate();
            if (openDate != null) {
                sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                        .format(openDate);
            } else {
                sDate = "не начат";
            }

            open.setText(mContext.getString(R.string.order_open, sDate));

            Date closeDate = order.getCloseDate();
            if (closeDate != null) {
                sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                        .format(closeDate);
            } else {
                sDate = "не закрыт";
            }

            close.setText(mContext.getString(R.string.order_close, sDate));
            comment.setText(mContext.getString(R.string.order_comment, order.getComment()));
            verdict.setText(mContext.getString(R.string.order_verdict, order.getOrderVerdict().getTitle()));
        }

        dialog.setView(myView);
        dialog.show();
    }

    /**
     * Диалог с списком запчастей по наряду
     */
    private void showRepairParts(Orders order, ViewGroup parent) {
        final Dialog dialog = new Dialog(mContext);
        Realm realmDB;
        RepairPartAdapter repairPartAdapter;
        dialog.setContentView(R.layout.order_parts);
        dialog.setTitle("Перечень запчастей");
        ListView lv;
        lv = dialog.findViewById(R.id.List);
        realmDB = Realm.getDefaultInstance();
        RealmResults<OrderRepairPart> orderRepairParts = realmDB.where(OrderRepairPart.class).
                equalTo("order.uuid", order.getUuid()).findAll();
        repairPartAdapter = new RepairPartAdapter(orderRepairParts);
        lv.setAdapter(repairPartAdapter);
        dialog.show();
        realmDB.close();
    }

    public interface EventListener {
        void checkClickItem(final int position);
    }

    private static class ViewHolder {
        RelativeLayout order_option;
        RelativeLayout order_row;
        TextView created;
        TextView title;
        TextView status;
        TextView options;
        ImageView icon;
    }

    /**
     * Проверяет все ли задачи в текущем наряде были выполнены без проблем
     */
    private boolean isAllTaskFullComplete(Orders order) {
        boolean good = true;
        List<Task> tasks = order.getTasks();
        for (Task task : tasks) {
            if (task.getTaskVerdict() != null && task.getTaskVerdict().getUuid().equals(TaskVerdict.Verdict.UN_COMPLETE)) {
                good = false;
            }
        }
        return good;
    }
}
