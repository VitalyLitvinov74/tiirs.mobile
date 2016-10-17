package ru.toir.mobile.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
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
import ru.toir.mobile.rest.ToirAPIFactory;
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

        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Наряды");

        realmDB = Realm.getDefaultInstance();

        mainListView = (ListView) rootView
                .findViewById(R.id.list_view);

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
    }

    private void fillListViewOrders(String orderStatus, String orderByField) {
        User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!",
                    Toast.LENGTH_SHORT).show();
        } else {
            RealmResults<Orders> orders;
            orders = realmDB.where(Orders.class).findAll();
            orderAdapter = new OrderAdapter(getContext(), orders);
            mainListView.setAdapter(orderAdapter);
        }
    }

    // Tasks----------------------------------------------------------------------------------------
    private void fillListViewTasks(Orders order) {
        RealmResults<Tasks> tasks;
        RealmQuery<Tasks> q = realmDB.where(Tasks.class);
        boolean first = true;
        for (Tasks task : order.getTasks()) {
            long id = task.get_id();
            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }

        tasks = q.findAll();

        taskAdapter = new TaskAdapter(getContext(), tasks);
        mainListView.setAdapter(taskAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(order.getTitle());
        }
    }

    // Orders----------------------------------------------------------------------------------------
    private void fillListViewTaskStage(Tasks task) {
        RealmResults<TaskStages> stages;
        RealmQuery<TaskStages> q = realmDB.where(TaskStages.class);
        boolean first = true;
        for (TaskStages stage : task.getTaskStages()) {
            long id = stage.get_id();
            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }

        stages = q.findAll();

        taskStageAdapter = new TaskStageAdapter(getContext(), stages);
        mainListView.setAdapter(taskStageAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(task.getTaskTemplate().getTitle());
        }
    }

    // Operations----------------------------------------------------------------------------------------

    private void fillListViewOperations(TaskStages stage) {
        RealmResults<Operation> operations;
        RealmQuery<Operation> q = realmDB.where(Operation.class);
        boolean first = true;
        for (Operation operation : stage.getOperations()) {
            long id = operation.get_id();
            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }

        operations = q.findAll();

        operationAdapter = new OperationAdapter(getContext(), operations);
        mainListView.setAdapter(operationAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(stage.getTaskStageTemplate().getTitle());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // добавляем элемент меню для получения новых нарядов
        MenuItem getTaskNew = menu.add("Получить новые наряды");
        getTaskNew
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d(TAG, "Получаем новые наряды.");
//                        TaskServiceHelper tsh = new TaskServiceHelper(
//                                getActivity().getApplicationContext(),
//                                TaskServiceProvider.Actions.ACTION_GET_TASK);
//
//                        getActivity().registerReceiver(mReceiverGetTask,
//                                mFilterGetTask);
//
//                        tsh.GetTaskNew();

                        AuthorizedUser user = AuthorizedUser.getInstance();
                        Call<List<Orders>> call = ToirAPIFactory.getOrderService()
                                .order("bearer " + user.getToken(), user.getUuid(), "");
                        call.enqueue(new Callback<List<Orders>>() {
                            @Override
                            public void onResponse(Response<List<Orders>> response, Retrofit retrofit) {
                                // ообщаем количество полученных нарядов
                                List<Orders> orders = response.body();
                                int count = orders.size();
                                if (count > 0) {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    realm.copyToRealmOrUpdate(orders);
                                    realm.commitTransaction();
                                    Toast.makeText(getActivity(),
                                            "Количество нарядов " + count,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Нарядов нет.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                processDialog.dismiss();
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                // сообщаем описание неудачи
                                Toast.makeText(getActivity(),
                                        "Ошибка при получении нарядов.",
                                        Toast.LENGTH_LONG).show();
                                processDialog.dismiss();
                            }
                        });

                        // показываем диалог получения наряда
                        processDialog = new ProgressDialog(getActivity());
                        processDialog.setMessage("Получаем наряды");
                        processDialog.setIndeterminate(true);
                        processDialog
                                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        processDialog.setCancelable(false);
                        processDialog.setButton(
                                DialogInterface.BUTTON_NEGATIVE, "Отмена",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        getActivity().unregisterReceiver(mReceiverGetTask);
                                        Toast.makeText(getActivity(),
                                                "Получение нарядов отменено",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        processDialog.show();
                        return true;
                    }
                });

        // добавляем элемент меню для получения "архивных" нарядов
        MenuItem getTaskDone = menu.add("Получить сделанные наряды");
        getTaskDone
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Log.d(TAG, "Получаем сделанные наряды.");
//                        TaskServiceHelper tsh = new TaskServiceHelper(
//                                getActivity().getApplicationContext(),
//                                TaskServiceProvider.Actions.ACTION_GET_TASK);
//
//                        getActivity().registerReceiver(mReceiverGetTask,
//                                mFilterGetTask);
//
//                        tsh.GetTaskDone();

                        // показываем диалог получения наряда
                        processDialog = new ProgressDialog(getActivity());
                        processDialog.setMessage("Получаем наряды");
                        processDialog.setIndeterminate(true);
                        processDialog
                                .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        processDialog.setCancelable(false);
                        processDialog.setButton(
                                DialogInterface.BUTTON_NEGATIVE, "Отмена",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
//                                        getActivity().unregisterReceiver(mReceiverGetTask);
                                        Toast.makeText(getActivity(),
                                                "Получение нарядов отменено",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                        processDialog.show();
                        return true;
                    }
                });

        // добавляем элемент меню для отправки результатов выполнения нарядов
        MenuItem sendTaskResultMenu = menu.add("Отправить результаты");
        sendTaskResultMenu
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

//                        TaskDBAdapter adapter = new TaskDBAdapter(
//                                new ToirDatabaseContext(getActivity()));
//                        List<Task> tasks;
//                        String currentUserUuid = AuthorizedUser.getInstance()
//                                .getUuid();
//
//                        // проверяем наличие не законченных нарядов
//                        tasks = adapter.getOrdersByUser(currentUserUuid,
//                                TaskStatusDBAdapter.Status.IN_WORK, "");
//                        if (tasks.size() > 0) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(
                                getContext());

                        dialog.setTitle("Внимание!");
                        dialog.setMessage("Есть "/* + tasks.size()*/
                                + " наряда в процессе выполнения.\n"
                                + "Отправить выполненные наряды?");
                        dialog.setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {

                                        sendCompleteTask();
                                        dialog.dismiss();
                                    }
                                });
                        dialog.setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {

                                        dialog.dismiss();
                                    }
                                });
                        dialog.show();
//                        }

                        return true;
                    }
                });
    }

    /**
     * Отправка всех выполненных нарядов на сервер
     */
    private void sendCompleteTask() {

//        TaskDBAdapter adapter = new TaskDBAdapter(new ToirDatabaseContext(
//                getActivity()));
//        List<Task> tasks;
//        String currentUserUuid = AuthorizedUser.getInstance().getUuid();
//
//        tasks = adapter.getTaskByUserAndUpdated(currentUserUuid);
//
//        if (tasks == null) {
//            Toast.makeText(getActivity(), "Нет результатов для отправки.",
//                    Toast.LENGTH_SHORT);
//            return;
//        }
//
//        String[] sendTaskUuids = new String[tasks.size()];
//        int i = 0;
//        for (Task task : tasks) {
//            sendTaskUuids[i] = task.getUuid();
//            // устанавливаем дату попытки отправки
//            // (пока только для наряда)
//            task.setAttempt_send_date(Calendar.getInstance().getTime()
//                    .getTime());
//            adapter.replace(task);
//            i++;
//        }
//
//        getActivity()
//                .registerReceiver(mReceiverSendTaskResult, mFilterSendTask);
//
//        TaskServiceHelper tsh = new TaskServiceHelper(getActivity(),
//                TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
//        tsh.SendTaskResult(sendTaskUuids);

        // показываем диалог отправки результатов
        processDialog = new ProgressDialog(getActivity());
        processDialog.setMessage("Отправляем результаты");
        processDialog.setIndeterminate(true);
        processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        processDialog.setCancelable(false);
        processDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        getActivity().unregisterReceiver(mReceiverGetTask);
                        Toast.makeText(getActivity(),
                                "Отправка результатов отменена",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        processDialog.show();
    }

    public class ListViewClickListener implements
            AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View selectedItemView,
                                int position, long id) {

            Orders order;
            Tasks task;
            TaskStages stage;

            // находимся на "экране" нарядов
            if (Level == 0) {
                if (orderAdapter != null) {
                    order = orderAdapter.getItem(position);
                    if (order != null) {
                        currentOrderUuid = order.getUuid();
                        fillListViewTasks(order);
                        Level = 1;
                    }
                }

                return;
            }
            // Tasks
            if (Level == 1) {
                if (taskAdapter != null) {
                    task = taskAdapter.getItem(position);
                    if (task != null) {
                        currentTaskUuid = task.getUuid();
                        fillListViewTaskStage(task);
                        Level = 2;
                    }
                }

                return;
            }
            // TaskStage
            if (Level == 2) {
                if (taskStageAdapter != null) {
                    stage = taskStageAdapter.getItem(position);
                    if (stage != null) {
                        currentTaskStageUuid = stage.getUuid();
                        fillListViewOperations(stage);
                        Level = 3;
                    }
                }

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
}



