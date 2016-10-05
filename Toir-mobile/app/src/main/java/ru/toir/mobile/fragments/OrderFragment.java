package ru.toir.mobile.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.OperationAdapter;
import ru.toir.mobile.db.adapters.OrderAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.adapters.TaskStageAdapter;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.rest.IServiceProvider;
import ru.toir.mobile.rest.ProcessorService;
import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.rfid.RfidDialog;

public class OrderFragment extends Fragment {
    private OrderAdapter orderAdapter;
    private TaskAdapter taskAdapter;
    private TaskStageAdapter taskStageAdapter;
    private OperationAdapter operationAdapter;

    private String TAG = "OrderFragment";
    private Realm realmDB;
    private View rootView;
    private int Level = 0;
    private String currentOrderUuid = "";
    private String currentTaskUuid = "";
    private String currentTaskStageUuid = "";

    //private Spinner sortSpinner;
    //private Spinner statusSpinner;

    private ListView mainListView;
    private ListViewClickListener mainListViewClickListener = new ListViewClickListener();
    //private ListViewLongClickListener mainListViewLongClickListener = new ListViewLongClickListener();
    //private ReferenceSpinnerListener filterSpinnerListener = new ReferenceSpinnerListener();
    //private ArrayAdapter<OperationType> operationTypeAdapter;
    //private ArrayAdapter<CriticalType> criticalTypeAdapter;
    //private OrderStatusAdapter statusSpinnerAdapter;
    //private ArrayAdapter<SortField> sortFieldAdapter;

    private ProgressDialog processDialog;
    private RfidDialog rfidDialog;
    // фильтр для получения сообщений при получении нарядов с сервера
    private IntentFilter mFilterGetTask = new IntentFilter(
            TaskServiceProvider.Actions.ACTION_GET_TASK);
    // TODO решить нужны ли фильтры на все возможные варианты отправки
    // состояния/результатов
    // фильтр для получения сообщений при получении нарядов с сервера
    private IntentFilter mFilterSendTask = new IntentFilter(
            TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
    private BroadcastReceiver mReceiverGetTask = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int provider = intent.getIntExtra(
                    ProcessorService.Extras.PROVIDER_EXTRA, 0);
            Log.d(TAG, "" + provider);
            if (provider == ProcessorService.Providers.TASK_PROVIDER) {
                int method = intent.getIntExtra(
                        ProcessorService.Extras.METHOD_EXTRA, 0);
                Log.d(TAG, "" + method);
                if (method == TaskServiceProvider.Methods.GET_TASK) {
                    boolean result = intent.getBooleanExtra(
                            ProcessorService.Extras.RESULT_EXTRA, false);
                    Bundle bundle = intent
                            .getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
                    Log.d(TAG, "boolean result" + result);

                    if (result) {
                        /*
                         * нужно видимо что-то дёрнуть чтоб уведомить о том что
						 * наряд(ы) получены вероятно нужно сделать попытку
						 * отправить на сервер информацию о полученых нарядах
						 * (которые изменили свой статус на "В работе")
						 */

                        // ообщаем количество полученных нарядов
                        int count = bundle
                                .getInt(TaskServiceProvider.Methods.RESULT_GET_TASK_COUNT);
                        if (count > 0) {
                            Toast.makeText(getActivity(),
                                    "Количество нарядов " + count,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Нарядов нет.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // сообщаем описание неудачи
                        String message = bundle
                                .getString(IServiceProvider.MESSAGE);
                        Toast.makeText(getActivity(),
                                "Ошибка при получении нарядов." + message,
                                Toast.LENGTH_LONG).show();
                    }

                    // закрываем диалог получения наряда
                    processDialog.dismiss();
                    getActivity().unregisterReceiver(mReceiverGetTask);
                    initView();
                }
            }

        }
    };
    private BroadcastReceiver mReceiverSendTaskResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int provider = intent.getIntExtra(
                    ProcessorService.Extras.PROVIDER_EXTRA, 0);
            Log.d(TAG, "" + provider);
            if (provider == ProcessorService.Providers.TASK_PROVIDER) {
                int method = intent.getIntExtra(
                        ProcessorService.Extras.METHOD_EXTRA, 0);
                Log.d(TAG, "" + method);
                if (method == TaskServiceProvider.Methods.TASK_SEND_RESULT) {
                    boolean result = intent.getBooleanExtra(
                            ProcessorService.Extras.RESULT_EXTRA, false);
                    Log.d(TAG, "" + result);
                    if (result) {
                        Toast.makeText(getActivity(), "Результаты отправлены.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),
                                "Ошибка при отправке результатов.",
                                Toast.LENGTH_LONG).show();
                    }

                    // закрываем диалог получения наряда
                    processDialog.dismiss();
                    getActivity().unregisterReceiver(mReceiverSendTaskResult);
                }
            }
        }
    };

    public static OrderFragment newInstance() {
        return (new OrderFragment());
    }

    /*
 * (non-Javadoc)
 *
 * @see
 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
 * android.view.ViewGroup, android.os.Bundle)
 */
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.orders_layout, container,
                false);

        //Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        //toolbar.setSubtitle("Наряды");

        realmDB = Realm.getDefaultInstance();

        mainListView = (ListView) rootView
                .findViewById(R.id.list_view);
        /*
        sortSpinner = (Spinner) rootView
                .findViewById(R.id.ol_sort_spinner);

        statusSpinner = (Spinner) rootView
                .findViewById(R.id.simple_spinner);
        sortSpinner.setOnItemSelectedListener(filterSpinnerListener);
        statusSpinner.setOnItemSelectedListener(filterSpinnerListener); */

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d(TAG, "OrderFragment !!! back pressed!!!");
                    if (Level == 1) {
                        initView();
                    }
                    return true;
                }
                return false;
            }
        });

        // создаём "пустой" адаптер для отображения операций над оборудованием
        // !! адаптер уехал в адаптер

        // адаптеры для выпадающих списков по типу содержимого
        /*
        operationTypeAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<OperationType>());

		criticalTypeAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<CriticalType>());

		taskStatusAdapter = new ArrayAdapter<>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item,
				new ArrayList<TaskStatus>());
        sortFieldAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<SortField>());
        */
        // так как обработчики пока одни на всё, ставим их один раз
        mainListView.setOnItemClickListener(mainListViewClickListener);
        //mainListView.setOnItemLongClickListener(mainListViewLongClickListener);

        mainListView.setLongClickable(true);

        initView();

        return rootView;
    }

    private void initView() {

        Level = 0;
        fillListViewOrders(null, null);
        //fillSpinnersOrders();

    }

    // Orders --------------------------------------------------------------------------------------
    /*
    private void fillSpinnersOrders() {
        RealmResults<OrderStatus> orderStatus = realmDB.where(OrderStatus.class).findAll();
        statusSpinnerAdapter = new OrderStatusAdapter(getContext(), orderStatus);
        statusSpinnerAdapter.notifyDataSetChanged();
        statusSpinner.setAdapter(statusSpinnerAdapter);
        //statusSpinner.setOnItemSelectedListener(spinnerListener);

        sortSpinner = (Spinner) rootView.findViewById(R.id.ol_sort_spinner);
        sortFieldAdapter.clear();
        sortFieldAdapter.add(new SortField("Сортировка", null));
        sortFieldAdapter.add(new SortField("По дате начала", "startDate"));
        sortFieldAdapter.add(new SortField("По дате получения", "recieveDate"));
        sortFieldAdapter.add(new SortField("По дате отправки", "attemptSendDate"));
        // TODO глупо сортировать просто по UUID
        sortFieldAdapter.add(new SortField("По статусу", "orderStatusUuid"));
        sortSpinner.setAdapter(sortFieldAdapter);
        //sortSpinner.setOnItemSelectedListener(FilterSpinnerListener);
    } */
    private void fillListViewOrders(String orderStatus, String orderByField) {
        User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!",
                    Toast.LENGTH_SHORT).show();
        } else {
            /*
            if (orderStatus != null) {
                if (orderByField != null)
                    orders = realmDB.where(Orders.class).equalTo("orderStatusUuid", orderStatus).findAllSorted(orderByField);
                else
                    orders = realmDB.where(Orders.class).equalTo("orderStatusUuid", orderStatus).findAll();
            } else {
                if (orderByField != null)
                    orders = realmDB.where(Orders.class).findAllSorted(orderByField);
                else
                    orders = realmDB.where(Orders.class).findAll();
            }

            String init_date="",current_date="";
            Date lDate;
            RealmResults<Orders> orders;
            orderAdapter = new OrderAdapter(getContext(), orders);
            order.setTitle("---");
            orders = realmDB.where(Orders.class).findAll();
            for (Orders item : orders) {
                lDate = item.getOpenDate();
                if (lDate != null) {
                    current_date = new SimpleDateFormat("dd MMMM").format(lDate);
                    if (!init_date.equals(current_date)) {
                        orderAdapter.addSeparatorItem(item);
                        //orders_list.add(order);
                        init_date = current_date;
                    }
                }
                orderAdapter.addItem(item);
                //orders_list.add(item);
            }*/
            RealmResults<Orders> orders;
            orders = realmDB.where(Orders.class).findAll();
            orderAdapter = new OrderAdapter(getContext(), orders);
            mainListView.setAdapter(orderAdapter);
        }
    }

    // Tasks----------------------------------------------------------------------------------------
    private void fillListViewTasks(String orderUuid) {
        RealmResults<Tasks> tasks;
        tasks = realmDB.where(Tasks.class).equalTo("orderUuid", orderUuid).findAllSorted("startDate");
        taskAdapter = new TaskAdapter(getContext(), tasks);
        mainListView.setAdapter(taskAdapter);
    }

    // Orders----------------------------------------------------------------------------------------
    private void fillListViewTaskStage(String taskUuid) {
        RealmResults<TaskStages> taskStages;
        taskStages = realmDB.where(TaskStages.class).equalTo("taskUuid", taskUuid).findAllSorted("startDate");
        taskStageAdapter = new TaskStageAdapter(getContext(), taskStages);
        mainListView.setAdapter(taskStageAdapter);
    }

    // Operations----------------------------------------------------------------------------------------
    private void fillListViewOperations(String taskStageUuid) {
        RealmResults<Operation> operations;
        operations = realmDB.where(Operation.class).equalTo("taskStageUuid", taskStageUuid).findAllSorted("startDate");
        operationAdapter = new OperationAdapter(getContext(), operations);
        mainListView.setAdapter(operationAdapter);
    }

    public class ListViewClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View selectedItemView,
                                int position, long id) {

            // находимся на "экране" нарядов
            if (Level == 0) {
                if (orderAdapter != null) {
                    currentOrderUuid = orderAdapter.getItem(position).getUuid();
                }
                fillListViewTasks(currentOrderUuid);
                //statusSpinner.setVisibility(View.GONE);
                //sortSpinner.setVisibility(View.GONE);
                Level = 1;
                return;
            }
            // Tasks
            if (Level == 1) {
                if (taskAdapter != null) {
                    currentTaskUuid = taskAdapter.getItem(position).getUuid();
                }
                fillListViewTaskStage(currentTaskUuid);
                //statusSpinner.setVisibility(View.GONE);
                //sortSpinner.setVisibility(View.GONE);
                Level = 2;
                return;
            }
            // TaskStage
            if (Level == 2) {
                if (taskStageAdapter != null) {
                    currentTaskStageUuid = taskStageAdapter.getItem(position).getUuid();
                }
                fillListViewOperations(currentTaskStageUuid);
                //statusSpinner.setVisibility(View.GONE);
                //sortSpinner.setVisibility(View.GONE);
                Level = 3;
                return;
            }
            // Operation
            if (Level == 3) {
                //if (operationAdapter != null)
                //    currentOperationUuid = operationAdapter.getItem(position).getUuid();
                // TODO отмечаем операцию как завершенную ?
                //if (operationStatus.equals(OperationStatusDBAdapter.Status.NEW)	|| operationStatus.equals(OperationStatusDBAdapter.Status.IN_WORK))
                // else dialog.setMessage("Операция уже выполнена. Повторное выполнение невозможно!");
                //initOperationPattern(operationUuid, taskUuid, equipmentUuid);
            }
        }
    }
/*
    private class ReferenceSpinnerListener implements
            AdapterView.OnItemSelectedListener {

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView,
                                   View selectedItemView, int position, long id) {

            Log.d(TAG, "sort spinner onItemSelected");
            if (Level == 0) {
                String orderStatusUuid = ((OrderStatus) statusSpinner
                        .getSelectedItem()).getUuid();

                String orderByField = ((SortField) sortSpinner
                        .getSelectedItem()).getField();
                fillListViewOrders(orderStatusUuid, orderByField);
            }
        }
    }
*/
}



