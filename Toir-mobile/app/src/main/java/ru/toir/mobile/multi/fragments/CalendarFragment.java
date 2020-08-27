package ru.toir.mobile.multi.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import static com.prolificinteractive.materialcalendarview.MaterialCalendarView.SELECTION_MODE_NONE;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.multi.MainActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.adapters.OrderAdapter;
import ru.toir.mobile.multi.db.realm.OrderStatus;
import ru.toir.mobile.multi.db.realm.Orders;
import ru.toir.mobile.multi.utils.OneDayDecorator;

public class CalendarFragment extends Fragment {
    MaterialCalendarView simpleCalendarView;
    private Realm realmDB;
    private Context mainActivityConnector = null;
    private ListView ordersListView;
    private OrderAdapter orderAdapter;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.calendar, container, false);
        Activity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Toolbar toolbar = ((AppCompatActivity) mainActivityConnector).findViewById(R.id.toolbar);
        simpleCalendarView = rootView.findViewById(R.id.calendarView);
        simpleCalendarView.setSelectionMode(SELECTION_MODE_NONE);
        simpleCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {

            }
        });
        toolbar.setSubtitle(R.string.menu_calendar_description);
        realmDB = Realm.getDefaultInstance();

        ordersListView = rootView.findViewById(R.id.trainings_listView);
        ordersListView.setOnItemClickListener(new ListViewClickListener());

        //onDateLongClickListener
        simpleCalendarView.setOnDateLongClickListener((OnDateLongClickListener) (widget, date) -> {
            Fragment fr = OrderFragment.newInstance();
            Bundle bundle = new Bundle();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(date.getDate().getTime());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            bundle.putLong("dateSelected", cal.getTimeInMillis());
            fr.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fr);
            fragmentTransaction.commit();
        });

        initView();

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private void initView() {
        FillListViewOrders();
    }

    private void FillListViewOrders() {
        RealmResults<Orders> orders = realmDB.where(Orders.class).findAll();
        orderAdapter = new OrderAdapter(orders, getContext(), null);
        ordersListView.setAdapter(orderAdapter);
        Date orderDate;
        simpleCalendarView.setDateSelected(new Date(), true);
        for (Orders order : orders) {
            orderDate = order.getStartDate();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(orderDate.getTime());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            orderDate = cal.getTime();
            simpleCalendarView.setDateSelected(orderDate, true);
            if (order.getOrderStatus().getUuid().equals(OrderStatus.Status.COMPLETE)) {
                simpleCalendarView.addDecorator(new OneDayDecorator(orderDate, ContextCompat.getDrawable(mainActivityConnector, R.drawable.order_complete)));
            }
            if (order.getOrderStatus().getUuid().equals(OrderStatus.Status.NEW)) {
                simpleCalendarView.addDecorator(new OneDayDecorator(orderDate, ContextCompat.getDrawable(mainActivityConnector, R.drawable.order_new)));
            }
            if (order.getOrderStatus().getUuid().equals(OrderStatus.Status.IN_WORK)) {
                simpleCalendarView.addDecorator(new OneDayDecorator(orderDate, ContextCompat.getDrawable(mainActivityConnector, R.drawable.order_progress)));
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivityConnector = getActivity();
        // TODO решить что делать если контекст не приехал
        if (mainActivityConnector == null)
            onDestroyView();
    }

    /*
        private class ListviewClickListener implements
                AdapterView.OnItemClickListener {

            @Override
            public void onItemClick(AdapterView<?> parentView,
                                    View selectedItemView, int position, long id) {
                Orders order = (Orders) parentView.getItemAtPosition(position);
                if (order != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("uuid", order.getUuid());
                    OrderFragment orderFragment = OrderFragment.newInstance();
                    orderFragment.setArguments(bundle);
                    ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                            replace(R.id.frame_container, orderFragment).commit();
                }
            }
        }
    */
    private class ListViewClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, View selectedItemView, final int position, long id) {
            Orders order = orderAdapter.getItem(position);
            if (order != null) {
                Bundle bundle = new Bundle();
                bundle.putString("uuid", order.getUuid());
                OrderFragment orderFragment = OrderFragment.newInstance();
                orderFragment.setArguments(bundle);
                ((MainActivity) mainActivityConnector).getSupportFragmentManager().beginTransaction().
                        replace(R.id.frame_container, orderFragment).commit();
            }
        }
    }
}
