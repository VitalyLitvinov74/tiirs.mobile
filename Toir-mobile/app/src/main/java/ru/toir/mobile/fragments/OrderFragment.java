package ru.toir.mobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.MeasureActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.db.adapters.OperationAdapter;
import ru.toir.mobile.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.db.adapters.OrderAdapter;
import ru.toir.mobile.db.adapters.OrderVerdictAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.adapters.StageAdapter;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.ISend;
import ru.toir.mobile.db.realm.Journal;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationType;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.OrderVerdict;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.StageStatus;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rest.ToirAPIResponse;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.MainFunctions;

import static ru.toir.mobile.utils.MainFunctions.addToJournal;
import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

//import android.content.BroadcastReceiver;

//import android.content.BroadcastReceiver;
//import ru.toir.mobile.rest.IServiceProvider;
//import ru.toir.mobile.rest.ProcessorService;
//import ru.toir.mobile.rest.TaskServiceProvider;

public class OrderFragment extends Fragment {
    private Toolbar toolbar;

    private Tasks selectedTask;
    private Orders selectedOrder;
    private TaskStages selectedStage;
    private Operation selectedOperation;

    private OrderAdapter orderAdapter;
    private TaskAdapter taskAdapter;
    private StageAdapter taskStageAdapter;
    private OperationAdapter operationAdapter;

    private String currentOrderUuid = "";
    private String currentTaskUuid = "";
    private String currentOperationUuid = "";
    private String currentTaskStageUuid = "";

    private ListView mainListView;
    private Button submit;
    private Button measure;
    private LinearLayout listLayout;
    private BottomBar bottomBar;
    private String TAG = "OrderFragment";
    private Realm realmDB;
    private int Level = 0;
    private Equipment currentEquipment;
    private Operation currentOperation;
    private ArrayList<Operation> uncompleteOperationList;
    private int totalOperationCount;
    private int currentOperationId = 0;
    private long startTime = 0;
    private boolean firstLaunch = true;
    CountDownTimer taskTimer = new CountDownTimer(1000000000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "Тик таймера...");
            TextView textTime;
            long currentTime = System.currentTimeMillis();
            if (operationAdapter != null && currentOperationId < operationAdapter.getCount()) {
                //textTime = (TextView) mainListView.getChildAt(currentOperationId).findViewById(R.id.op_time);
                if (!operationAdapter.getItem(currentOperationId).getOperationStatus().getUuid().equals(OperationStatus.Status.COMPLETE)) {
                    textTime = (TextView) getViewByPosition(currentOperationId, mainListView).findViewById(R.id.op_time);
                    textTime.setText(getString(R.string.sec_with_value, (int) (currentTime - startTime) / 1000));
                }
                currentOperation = operationAdapter.getItem(currentOperationId);
                if (currentOperation != null) {
                    currentOperationUuid = currentOperation.getUuid();
                    if (firstLaunch) {
                        firstLaunch();
                    }
                }
            }
        }

        void firstLaunch() {
            Log.d(TAG, "Инициализация вьюх для отображения секунд...");
            CheckBox checkBox;
            if (operationAdapter != null && mainListView != null) {
                totalOperationCount = operationAdapter.getCount();
                for (int i = 0; i < totalOperationCount; i++) {
                    if (mainListView.getChildAt(i) != null) {
                        checkBox = (CheckBox) mainListView.getChildAt(i).findViewById(R.id.operation_status);
                        checkBox.setOnClickListener(new onCheckBoxClickListener(i));
                    }
                }
                firstLaunch = false;
            }
        }

        @Override
        public void onFinish() {
        }
    };
    private SharedPreferences sp;
    private ListViewClickListener mainListViewClickListener = new ListViewClickListener();
    private ListViewLongClickListener mainListViewLongClickListener = new ListViewLongClickListener();
    private NumberPicker numberPicker;
    private Spinner spinnerSuffix;
    private ArrayList<OrderFragment.Suffixes> suffixList;
    private Button makePhotoButton;
    private ProgressDialog processDialog;
    private RfidDialog rfidDialog;

    // фильтр для получения сообщений при получении нарядов с сервера
//    private IntentFilter mFilterGetTask = new IntentFilter(TaskServiceProvider.Actions.ACTION_GET_TASK);
    // TODO решить нужны ли фильтры на все возможные варианты отправки состояния/результатов
    // фильтр для получения сообщений при получении нарядов с сервера
//    private IntentFilter mFilterSendTask = new IntentFilter(TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
//    private BroadcastReceiver mReceiverGetTask = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int provider = intent.getIntExtra(ProcessorService.Extras.PROVIDER_EXTRA, 0);
//            Log.d(TAG, "" + provider);
//            if (provider == ProcessorService.Providers.TASK_PROVIDER) {
//                int method = intent.getIntExtra(ProcessorService.Extras.METHOD_EXTRA, 0);
//                Log.d(TAG, "" + method);
//                if (method == TaskServiceProvider.Methods.GET_TASK) {
//                    boolean result = intent.getBooleanExtra(ProcessorService.Extras.RESULT_EXTRA, false);
//                    Bundle bundle = intent.getBundleExtra(ProcessorService.Extras.RESULT_BUNDLE);
//                    Log.d(TAG, "boolean result" + result);
//
//                    if (result) {
//                        /*
//                         * нужно видимо что-то дёрнуть чтоб уведомить о том что
//						 * наряд(ы) получены вероятно нужно сделать попытку
//						 * отправить на сервер информацию о полученых нарядах
//						 * (которые изменили свой статус на "В работе")
//						 */
//
//                        // ообщаем количество полученных нарядов
//                        int count = bundle.getInt(TaskServiceProvider.Methods.RESULT_GET_TASK_COUNT);
//                        if (count > 0) {
//                            Toast.makeText(getActivity(), "Количество нарядов " + count, Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getActivity(), "Нарядов нет.", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        // сообщаем описание неудачи
//                        String message = bundle.getString(IServiceProvider.MESSAGE);
//                        Toast.makeText(getActivity(), "Ошибка при получении нарядов." + message, Toast.LENGTH_LONG).show();
//                    }
//
//                    // закрываем диалог получения наряда
//                    processDialog.dismiss();
//                    getActivity().unregisterReceiver(mReceiverGetTask);
//                    initView();
//                }
//            }
//
//        }
//    };

//    private BroadcastReceiver mReceiverSendTaskResult = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            int provider = intent.getIntExtra(ProcessorService.Extras.PROVIDER_EXTRA, 0);
//            Log.d(TAG, "" + provider);
//            if (provider == ProcessorService.Providers.TASK_PROVIDER) {
//                int method = intent.getIntExtra(ProcessorService.Extras.METHOD_EXTRA, 0);
//                Log.d(TAG, "" + method);
//                if (method == TaskServiceProvider.Methods.TASK_SEND_RESULT) {
//                    boolean result = intent.getBooleanExtra(ProcessorService.Extras.RESULT_EXTRA, false);
//                    Log.d(TAG, "" + result);
//                    if (result) {
//                        Toast.makeText(getActivity(), "Результаты отправлены.", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Ошибка при отправке результатов.", Toast.LENGTH_LONG).show();
//                    }
//
//                    // закрываем диалог получения наряда
//                    processDialog.dismiss();
//                    getActivity().unregisterReceiver(mReceiverSendTaskResult);
//                }
//            }
//        }
//    };

    public static OrderFragment newInstance() {
        return (new OrderFragment());
    }

    @Override
    public void onDestroy() {
        if (taskTimer != null) {
            taskTimer.cancel();
            taskTimer = null;
        }
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.orders_layout, container, false);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        toolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Наряды");
        submit = (Button) rootView.findViewById(R.id.tl_finishButton);
        //submit.setOnClickListener(this);
        submit.setVisibility(View.GONE);

        measure = (Button) rootView.findViewById(R.id.tl_measureButton);
        measure.setVisibility(View.GONE);
        uncompleteOperationList = new ArrayList<>();

        realmDB = Realm.getDefaultInstance();
        //tlButtonLayout = (LinearLayout) rootView.findViewById(R.id.tl_button_layout);
        //resultLayout = (LinearLayout) rootView.findViewById(R.id.tl_resultsLayout);
        //globalLayout = (LinearLayout) rootView.findViewById(R.id.tl_global_layout);
        //resultButtonLayout = (LinearLayout) rootView.findViewById(R.id.tl_resultButtonLayout);
        listLayout = (LinearLayout) rootView.findViewById(R.id.tl_listview_layout);
        bottomBar = (BottomBar) (getActivity()).findViewById(R.id.bottomBar);
        makePhotoButton = (Button) rootView.findViewById(R.id.tl_photoButton);
        makePhotoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File photo = getOutputMediaFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
                // TODO: Олег, объяви константу
                startActivityForResult(intent, 100);
            }
        });
        measure.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent measure = new Intent(getActivity(), MeasureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("operationUuid", currentOperation.getUuid());
                bundle.putString("equipmentUuid", currentEquipment.getUuid());
                measure.putExtras(bundle);
                startActivityForResult(measure, 101);

            }
        });

        mainListView = (ListView) rootView
                .findViewById(R.id.list_view);

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
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
        mainListView.setOnItemLongClickListener(mainListViewLongClickListener);
        //mainListView.setOnItemLongClickListener(mainListViewLongClickListener);

        mainListView.setLongClickable(true);

        initView();
        return rootView;
    }

    private void initView() {

        Level = 0;
        fillListViewOrders();
    }

    // Orders----------------------------------------------------------------------------------------
    private void fillListViewOrders() {
        fillListViewOrders(null, null);
    }

    private void fillListViewOrders(String orderStatus, String orderByField) {
        // !!!!!
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        User user = realmDB.where(User.class)
                .equalTo("tagId", authUser.getTagId())
                .findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
        } else {
            RealmQuery<Orders> query = realmDB.where(Orders.class).equalTo("user.uuid", authUser.getUuid());
            //RealmQuery<Orders> query = realmDB.where(Orders.class);
            if (orderStatus != null) {
                query.equalTo("orderStatus.uuid", orderStatus);
            }

            RealmResults<Orders> orders;
            if (orderByField != null) {
                orders = query.findAllSorted(orderByField);
            } else {
                orders = query.findAll();
            }

            orderAdapter = new OrderAdapter(getContext(), orders);
            mainListView.setAdapter(orderAdapter);
        }

        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.GONE);
        }

        //resultButtonLayout.setVisibility(View.INVISIBLE);
        makePhotoButton.setVisibility(View.INVISIBLE);
        ViewGroup.LayoutParams params = listLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        listLayout.setLayoutParams(params);

        int new_orders = MainFunctions.getActiveOrdersCount();
        if (new_orders > 0) {
            bottomBar.getTabAtPosition(1).setBadgeCount(new_orders);
        }

        //bottomBar.setVisibility(View.VISIBLE);
        //bottomBar.setEnabled(false);
    }

    // Tasks----------------------------------------------------------------------------------------
    private void fillListViewTasks(Orders order, boolean complete_operation) {
        RealmResults<Tasks> tasks;
        RealmQuery<Tasks> q = realmDB.where(Tasks.class);
        boolean first = true;
        boolean all_complete = true;
        toolbar.setSubtitle("Задачи");
        for (Tasks task : order.getTasks()) {
            long id = task.get_id();
            if (!task.getTaskStatus().getUuid().equals(TaskStatus.Status.COMPLETE)) {
                all_complete = false;
            }

            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }
        // задач нет
        if (first) all_complete = false;
        tasks = q.findAll();

        if (complete_operation && all_complete && !order.getOrderStatus().getUuid().equals(OrderStatus.Status.COMPLETE)) {
            final OrderStatus orderStatusComplete = realmDB.where(OrderStatus.class)
                    .equalTo("uuid", OrderStatus.Status.COMPLETE)
                    .findFirst();
            realmDB.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    selectedOrder.setCloseDate(new Date());
                    selectedOrder.setOrderStatus(orderStatusComplete);
                }
            });
            addToJournal("Закончен наряд " +   order.getTitle() + "(" + order.getUuid() + ")");
            Level = 0;
            fillListViewOrders(null, null);
        }

        taskAdapter = new TaskAdapter(getContext(), tasks);
        mainListView.setAdapter(taskAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(order.getTitle());
        }

        submit.setVisibility(View.GONE);
        measure.setVisibility(View.GONE);
    }

    // TaskStages----------------------------------------------------------------------------------------
    private void fillListViewTaskStage(Tasks task, boolean complete_operation) {
        RealmResults<TaskStages> stages;
        RealmQuery<TaskStages> q = realmDB.where(TaskStages.class);
        toolbar.setSubtitle("Этапы задач");
        boolean first = true;
        boolean all_complete = true;
        for (TaskStages stage : task.getTaskStages()) {
            long id = stage.get_id();
            if (!stage.getTaskStageStatus().getUuid().equals(StageStatus.Status.COMPLETE)) {
                all_complete = false;
            }

            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }
        // этапов нет
        if (first) all_complete = false;
        stages = q.findAll();
        taskStageAdapter = new StageAdapter(getContext(), stages);
        mainListView.setAdapter(taskStageAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(task.getTaskTemplate().getTitle());
        }

        if (complete_operation && all_complete && !task.getTaskStatus().equals(TaskStatus.Status.COMPLETE)) {
            final TaskStatus taskStatusComplete = realmDB.where(TaskStatus.class)
                    .equalTo("uuid", TaskStatus.Status.COMPLETE)
                    .findFirst();
            realmDB.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    selectedTask.setEndDate(new Date());
                    selectedTask.setTaskStatus(taskStatusComplete);
                }
            });
            addToJournal("Закончено выполнение задачи " + task.getTaskTemplate().getTitle() + "(" + task.getUuid() + ")");
            Level = 1;
            fillListViewTasks(selectedOrder, true);
        }

        submit.setVisibility(View.GONE);
        measure.setVisibility(View.GONE);
        makePhotoButton.setVisibility(View.GONE);
        ViewGroup.LayoutParams params = listLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        listLayout.setLayoutParams(params);
    }

    // Operations----------------------------------------------------------------------------------------
    private void fillListViewOperations(TaskStages stage) {
        RealmResults<Operation> operations;
        RealmQuery<Operation> q = realmDB.where(Operation.class);
        boolean first = true;
        toolbar.setSubtitle("Операции");
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

        operationAdapter = new OperationAdapter(getContext(), operations, selectedTask.getTaskTemplate().getUuid());
        mainListView.setAdapter(operationAdapter);
        //resultButtonLayout.setVisibility(View.VISIBLE);
        makePhotoButton.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = listLayout.getLayoutParams();
        params.height = 1000;
        listLayout.setLayoutParams(params);

        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(stage.getTaskStageTemplate().getTitle());
        }

        //mainListView.setOnItemClickListener(mainListViewClickListener);
    }

    // Start Operations----------------------------------------------------------------------------------------
    void startOperations() {
        final Operation operation;
        final OperationType operationType;
        boolean isMeasure = false;
        // запрещаем все операции кроме первой
        if (operationAdapter != null) {
            totalOperationCount = operationAdapter.getCount();
            operation = operationAdapter.getItem(0);
            operationAdapter.setItemEnable(0, true);
            // нет решительно никакой возможности выполнять выполненные операции по сто раз
            // только если сбросить все
            if (operation != null) {
                final OperationStatus operationStatusInWork = realmDB.where(OperationStatus.class)
                        .equalTo("uuid", OperationStatus.Status.IN_WORK)
                        .findFirst();
                operationType = operation.getOperationTemplate().getOperationType();
                if (operationType.getUuid().equals(OperationType.Type.MEASURE)) {
                    isMeasure = true;
                }
                // фиксируем начало работы над первой операцией (если у нее нет статуса закончена), меняем ее статус на в процессе
                if (operation.getOperationStatus().getUuid().equals(OperationStatus.Status.NEW) ||
                        operation.getOperationStatus().getUuid().equals(OperationStatus.Status.CANCELED) ||
                        operation.getOperationStatus().getUuid().equals(OperationStatus.Status.UN_COMPLETE)) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            operation.setStartDate(new Date());
                            operation.setOperationStatus(operationStatusInWork);
                        }
                    });
                }
            }

            for (int i = 0; i < totalOperationCount; i++) {
                if (i > 0)
                    operationAdapter.setItemEnable(i, false);
                //checkBox = (CheckBox)operationAdapter.getView(i,null,mainListView);
                //checkBox.setOnClickListener(new onCheckBoxClickListener());
            }
        }

        // время начала работы (приступаем к первой операции и нехрен тормозить)
        startTime = System.currentTimeMillis();
        Log.d(TAG, "Запуск таймера...");
        taskTimer.start();

        // фиксируем начало работы над этапом задачи (если у него статус получен), меняем его статус на в процессе
        final StageStatus taskStageStatus;
        final StageStatus taskStageStatusInWork;
        if (selectedStage != null) {
            taskStageStatus = selectedStage.getTaskStageStatus();
            taskStageStatusInWork = realmDB.where(StageStatus.class)
                    .equalTo("uuid", StageStatus.Status.IN_WORK)
                    .findFirst();
            if (taskStageStatus != null && taskStageStatusInWork != null)
                if (taskStageStatus.getUuid().equals(StageStatus.Status.NEW) || taskStageStatus.getUuid().equals(StageStatus.Status.UN_COMPLETE)) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            selectedStage.setStartDate(new Date());
                            selectedStage.setTaskStageStatus(taskStageStatusInWork);
                        }
                    });
                }
        }
        // фиксируем начало работы над задачей (если у нее статус получен), меняем ее статус на в процессе
        final TaskStatus taskStatus;
        final TaskStatus taskStatusInWork;
        if (selectedTask != null) {
            taskStatus = selectedTask.getTaskStatus();
            taskStatusInWork = realmDB.where(TaskStatus.class)
                    .equalTo("uuid", TaskStatus.Status.IN_WORK)
                    .findFirst();
            if (taskStatus != null && taskStatusInWork != null)
                if (taskStatus.getUuid().equals(TaskStatus.Status.NEW) || taskStatus.getUuid().equals(TaskStatus.Status.UN_COMPLETE)) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            selectedTask.setStartDate(new Date());
                            selectedTask.setTaskStatus(taskStatusInWork);
                        }
                    });
                }
        }
        // фиксируем начало работы над нарядом (если у него статус получен), меняем его статус на в процессе
        final OrderStatus orderStatus;
        final OrderStatus orderStatusInWork;
        if (selectedOrder != null) {
            orderStatus = selectedOrder.getOrderStatus();
            orderStatusInWork = realmDB.where(OrderStatus.class)
                    .equalTo("uuid", OrderStatus.Status.IN_WORK)
                    .findFirst();
            if (orderStatus != null && orderStatusInWork != null)
                if (orderStatus.getUuid().equals(OrderStatus.Status.NEW) || orderStatus.getUuid().equals(OrderStatus.Status.UN_COMPLETE)) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            selectedOrder.setOpenDate(new Date());
                            selectedOrder.setOrderStatus(orderStatusInWork);
                        }
                    });
                }
        }
        if (operationAdapter != null && isMeasure) {
            currentOperationId = 0;
            currentOperation = operationAdapter.getItem(currentOperationId);

            Intent measure = new Intent(getActivity(), MeasureActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("operationUuid", currentOperation.getUuid());
            bundle.putString("equipmentUuid", currentEquipment.getUuid());
            measure.putExtras(bundle);
            //getActivity().startActivity (measure);
            startActivityForResult(measure, 101);
        }
    }

    /**
     * Обёртка к методу получения нарядов.
     *
     * @param status - статус наряда
     */
    private void getOrdersByStatus(String status) {
        List<String> list = new ArrayList<>();
        list.add(status);
        getOrdersByStatus(list);
    }

    /**
     * Получение нарядов с определённым статусом.
     *
     * @param status - статус наряда
     */
    private void getOrdersByStatus(List<String> status) {
        AsyncTask<List<String>, Void, List<Orders>> aTask = new AsyncTask<List<String>, Void, List<Orders>>() {
            @Override
            protected List<Orders> doInBackground(List<String>... params) {
                // обновляем справочники
                ReferenceFragment.updateReferences(null);

                // запрашиваем наряды
                Call<List<Orders>> call = ToirAPIFactory.getOrdersService().ordersByStatus(params[0]);
                List<Orders> result;
                try {
                    Response<List<Orders>> response = call.execute();
                    result = response.body();
                } catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage());
                    return null;
                }

                // список файлов для загрузки
                List<FilePath> files = new ArrayList<>();
                // строим список изображений для загрузки
                for (Orders order : result) {
                    List<Tasks> tasks = order.getTasks();
                    for (Tasks task : tasks) {
                        // UUID шаблона задачи
                        String taskTemplateUuid = task.getTaskTemplate().getUuid();
                        // путь до папки с файлами
                        String equipmentModelUuid = task.getEquipment().getEquipmentModel().getUuid();
                        // общий путь до файлов на сервере
                        String basePath = "/storage/" + equipmentModelUuid + "/";
                        // общий путь до файлов локальный
                        String basePathLocal = "/tasks/" + taskTemplateUuid + "/";
                        // урл изображения задачи
                        files.add(new FilePath(task.getTaskTemplate().getImage(), basePath, basePathLocal));
                        // урл изображения оборудования
                        files.add(new FilePath(task.getEquipment().getImage(), basePath, "/equipment/"));

                        List<TaskStages> stages = task.getTaskStages();
                        for (TaskStages stage : stages) {
                            // урл изображения этапа задачи
                            files.add(new FilePath(stage.getTaskStageTemplate().getImage(),
                                    basePath, basePathLocal));

                            List<Operation> operations = stage.getOperations();
                            for (Operation operation : operations) {
                                // урл изображения операции
                                files.add(new FilePath(operation.getOperationTemplate().getImage(),
                                        basePath, basePathLocal));
                            }
                        }
                    }
                }

                // список файлов документации
                for (Orders order : result) {
                    List<Tasks> tasks = order.getTasks();
                    Realm realm = Realm.getDefaultInstance();
                    for (Tasks task : tasks) {
                        String equipmentUuid = task.getEquipment().getUuid();
                        List<Documentation> docList = realm.where(Documentation.class)
                                .equalTo("equipment.uuid", equipmentUuid)
                                .equalTo("required", true)
                                .findAll();
                        for (Documentation doc : docList) {
                            String docFileName = doc.getPath();
                            String url = "/storage/" + equipmentUuid + "/";
                            String localPath = "/documentation/" + equipmentUuid + "/";
                            files.add(new FilePath(docFileName, url, localPath));
                        }
                    }
                }

                // загружаем файлы
                for (FilePath path : files) {
                    Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().getFile(ToirApplication.serverUrl + path.urlPath + path.fileName);
                    try {
                        Response<ResponseBody> r = call1.execute();
                        ResponseBody trueImgBody = r.body();
                        if (trueImgBody == null) {
                            continue;
                        }

                        // TODO: разобраться почему не возвращает папку!!!
                        File file = new File(getContext().getExternalFilesDir(path.localPath), path.fileName);
                        if (!file.getParentFile().exists()) {
                            if (!file.getParentFile().mkdirs()) {
                                Log.e(TAG, "Не удалось создать папку " +
                                        file.getParentFile().toString() +
                                        " для сохранения файла изображения!");
                                continue;
                            }
                        }

                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(trueImgBody.bytes());
                        fos.close();
//                        equipment.setImage(file.getPath());
//                        equipmentDBAdapter.replace(equipment);
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<Orders> orders) {
                super.onPostExecute(orders);
                if (orders == null) {
                    // сообщаем описание неудачи
                    Toast.makeText(getActivity(), "Ошибка при получении нарядов.", Toast.LENGTH_LONG).show();
                } else {
                    int count = orders.size();
                    // собщаем количество полученных нарядов
                    if (count > 0) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(orders);
                        realm.commitTransaction();
                        Toast.makeText(getActivity(), "Количество нарядов " + count, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Нарядов нет.", Toast.LENGTH_SHORT).show();
                    }
                }

                processDialog.dismiss();
            }
        };
        aTask.execute(status);
    }

    /**
     * Метод для отправки файлов фотографий созданных во время выполнения операций.
     */
    private void sendFiles(List<String> files) {

        AsyncTask<String[], Void, List<String>> task = new AsyncTask<String[], Void, List<String>>() {
            @NonNull
            private RequestBody createPartFromString(String descriptionString) {
                return RequestBody.create(MultipartBody.FORM, descriptionString);
            }

            @NonNull
            private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
                File file = new File(fileUri.getPath());
                String type = null;
                String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
                if (extension != null) {
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }

                MediaType mediaType = MediaType.parse(type);
                RequestBody requestFile = RequestBody.create(mediaType, file);
                MultipartBody.Part part = MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
                return part;
            }

            @Override
            protected List<String> doInBackground(String[]... lists) {
                for (String file : lists[0]) {
                    RequestBody descr = createPartFromString("Photos due execution operation.");
                    Uri uri = null;
                    try {
                        uri = Uri.fromFile(new File(file));
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    List<MultipartBody.Part> list = new ArrayList<>();
                    String[] fileNameParts = file.split("/");
                    String fileName = fileNameParts[fileNameParts.length - 1];
                    fileNameParts = fileName.split("\\.");
                    list.add(prepareFilePart("photo[" + fileNameParts[0] + "]", uri));
                    Call<ResponseBody> call = ToirAPIFactory.getFileDownload().uploadFiles(descr, list);
                    try {
                        Response response = call.execute();
                        ResponseBody result = (ResponseBody) response.body();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "result" + result.contentType());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<String> strings) {
                super.onPostExecute(strings);
            }
        };

        String[] sendFiles = new String[files.size()];
        int i = 0;
        for (String item : files) {
            sendFiles[i++] = item;
        }

        task.execute(sendFiles);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // добавляем элемент меню для получения новых нарядов
        MenuItem getTaskNew = menu.add("Получить новые наряды");
        getTaskNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "Получаем новые наряды.");
//                TaskServiceHelper tsh = new TaskServiceHelper(getActivity().getApplicationContext(),
//                        TaskServiceProvider.Actions.ACTION_GET_TASK);
//                getActivity().registerReceiver(mReceiverGetTask, mFilterGetTask);
//                tsh.GetTaskNew();

                // запускаем поток получения новых нарядов с сервера
                getOrdersByStatus(OrderStatus.Status.NEW);

                // показываем диалог получения нарядов
                processDialog = new ProgressDialog(getActivity());
                processDialog.setMessage("Получаем наряды");
                processDialog.setIndeterminate(true);
                processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                processDialog.setCancelable(false);
                processDialog.setButton(
                        DialogInterface.BUTTON_NEGATIVE, "Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                getActivity().unregisterReceiver(mReceiverGetTask);
                                Toast.makeText(getActivity(), "Получение нарядов отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                processDialog.show();
                return true;
            }
        });

        // добавляем элемент меню для получения "архивных" нарядов
        MenuItem getTaskDone = menu.add("Получить сделанные наряды");
        getTaskDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "Получаем сделанные наряды.");
//                TaskServiceHelper tsh = new TaskServiceHelper( getActivity().getApplicationContext(),
//                        TaskServiceProvider.Actions.ACTION_GET_TASK);
//                getActivity().registerReceiver(mReceiverGetTask, mFilterGetTask);
//                tsh.GetTaskDone();

                // запускаем поток получения выполненных, невыполненных, отменнённых нарядов с сервера
                List<String> stUuids = new ArrayList<>();
                stUuids.add(OrderStatus.Status.CANCELED);
                stUuids.add(OrderStatus.Status.COMPLETE);
                stUuids.add(OrderStatus.Status.UN_COMPLETE);
                getOrdersByStatus(stUuids);

                // показываем диалог получения наряда
                processDialog = new ProgressDialog(getActivity());
                processDialog.setMessage("Получаем наряды");
                processDialog.setIndeterminate(true);
                processDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                processDialog.setCancelable(false);
                processDialog.setButton(
                        DialogInterface.BUTTON_NEGATIVE, "Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                        getActivity().unregisterReceiver(mReceiverGetTask);
                                Toast.makeText(getActivity(), "Получение нарядов отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                processDialog.show();
                return true;
            }
        });

        // добавляем элемент меню для отправки результатов выполнения нарядов
        MenuItem sendTaskResultMenu = menu.add("Отправить результаты");
        MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // проверяем наличие не законченных нарядов
                RealmResults<Orders> ordersInWork = realmDB.where(Orders.class)
                        .equalTo("orderStatus.uuid", OrderStatus.Status.IN_WORK)
                        .findAll();
                int inWorkCount = ordersInWork.size();
                if (inWorkCount > 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                    dialog.setTitle("Внимание!");
                    dialog.setMessage("Есть " + inWorkCount + " наряда в процессе выполнения.\n"
                            + "Отправить выполненные наряды?");
                    dialog.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendCompleteTask();
                                    dialog.dismiss();
                                }
                            });
                    dialog.setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                } else {
                    sendCompleteTask();
                }

                // отправляем данные из журнала и лога GPS
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Call<ToirAPIResponse> call;
                        Response<ToirAPIResponse> response;
                        Realm realm = Realm.getDefaultInstance();

                        // выбираем все неотправленные данные из таблицы journal
                        RealmResults<Journal> journals = realm.where(Journal.class)
                                .equalTo("sent", false)
                                .findAll();
                        List<Journal> journalList = new CopyOnWriteArrayList<>(realm.copyFromRealm(journals));
                        call = ToirAPIFactory.getJournalService().sendJournal(journalList);
                        try {
                            response = call.execute();
                            ToirAPIResponse result = response.body();
                            if (result.isSuccess()) {
                                Log.d(TAG, "Журнал отправлен успешно.");
                            } else {
                                Log.e(TAG, "Журнал отправлен, но не все записи сохранены.");
                                removeNotSaved(journalList, (List<String>) result.getData());
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(journalList);
                                realm.commitTransaction();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            Log.e(TAG, "Ошибка при отправке журнала.");
                        }

                        // выбираем все неотправленные данные из таблицы gpstrack
                        RealmResults<GpsTrack> gpsTracks = realm.where(GpsTrack.class)
                                .equalTo("sent", false)
                                .findAll();
                        List<GpsTrack> gpsTrackList = new CopyOnWriteArrayList<>(realm.copyFromRealm(gpsTracks));
                        call = ToirAPIFactory.getGpsTrackService().sendGpsTrack(gpsTrackList);
                        try {
                            response = call.execute();
                            ToirAPIResponse result = response.body();
                            if (result.isSuccess()) {
                                Log.d(TAG, "GPS лог отправлен успешно.");
                            } else {
                                Log.e(TAG, "GPS лог отправлен, но не все записи сохранены.");
                                removeNotSaved(gpsTrackList, (List<String>) result.getData());
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(gpsTrackList);
                                realm.commitTransaction();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getLocalizedMessage());
                            Log.e(TAG, "Ошибка при отправке GPS лога.");
                        }
                    }
                });
                thread.start();

                return true;
            }
        };
        sendTaskResultMenu.setOnMenuItemClickListener(listener);
    }

    /**
     * Вспомогательный метод для удаления из отправленого списка записей тех которые
     * не сохранены на сервере.
     *
     * @param list Список отправленных записей.
     * @param data Список id записей которые не сохранили.
     */

    private void removeNotSaved(List<? extends ISend> list, List<String> data) {

        List<Long> ids = new ArrayList<>();

        for (String item : data) {
            ids.add(Long.valueOf(item));
        }

        for (Object item : list) {
            if (ids.contains(((ISend) item).get_id())) {
                // удаляем из списка данных для отметки об успешной отправки, те что не сохранил сервер
                list.remove(item);
            } else {
                // меняем статус на "отправлено" для записей которые сохранены сервером
                ((ISend) item).setSent(true);
            }
        }
    }

    private void sendOrders(List<Orders> orders) {
        AsyncTask<List<Orders>, Void, String> task = new AsyncTask<List<Orders>, Void, String>() {
            @Override
            protected String doInBackground(List<Orders>... lists) {
                Call<ResponseBody> call = ToirAPIFactory.getOrdersService().sendOrders(lists[0]);
                try {
                    Response response = call.execute();
                    Log.d(TAG, "response = " + response);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        };
        addToJournal("Отправляем выполненные наряды на сервер");
        task.execute(orders);
    }

    private void sendMeasuredValues(List<MeasuredValue> values) {
        AsyncTask<List<MeasuredValue>, Void, String> task = new AsyncTask<List<MeasuredValue>, Void, String>() {
            @Override
            protected String doInBackground(List<MeasuredValue>... lists) {
                Call<ResponseBody> call = ToirAPIFactory.getOrdersService().sendMeasuredValues(lists[0]);
                try {
                    Response response = call.execute();
                    Log.d(TAG, "response = " + response);
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        };
        task.execute(values);
    }

    /**
     * Отправка всех выполненных нарядов на сервер
     */
    private void sendCompleteTask() {
        AuthorizedUser user = AuthorizedUser.getInstance();
        RealmResults<Orders> ordersList = realmDB.where(Orders.class)
                .equalTo("user.uuid", user.getUuid())
                .equalTo("orderStatus.uuid", OrderStatus.Status.COMPLETE)
                .equalTo("sent", false)
                .findAll();
        if (ordersList.size() == 0) {
            Toast.makeText(getActivity(), "Нет результатов для отправки.", Toast.LENGTH_SHORT).show();
            return;
        }

        // отправляем результат
        sendOrders(realmDB.copyFromRealm(ordersList));

        // строим список фотографий связанных с выполненными операциями
        // раньше список передавался как параметр в сервис отправки данных, сейчас пока не решено
        List<String> photos = new ArrayList<>();
        List<String> operationUuids = new ArrayList<>();
        for (Orders order : ordersList) {
            List<Tasks> tasks = order.getTasks();
            for (Tasks task : tasks) {
                List<TaskStages> stages = task.getTaskStages();
                for (TaskStages stage : stages) {
                    List<Operation> operations = stage.getOperations();
                    for (Operation operation : operations) {
                        operationUuids.add(operation.getUuid());
                        String photoFileName = operation.getUuid() + ".jpg";
                        File operationPhoto = new File(
                                getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                photoFileName);
                        if (operationPhoto.exists()) {
                            photos.add(operationPhoto.getAbsolutePath());
                        }
                    }
                }
            }
        }

        sendFiles(photos);

        String[] opUuidsArray = new String[operationUuids.size()];
        for (int i = 0; i < operationUuids.size(); i++) {
            opUuidsArray[i] = operationUuids.get(i);
        }

        // получаем все измерения связанные с выполненными операциями
        RealmResults<MeasuredValue> measuredValues = realmDB
                .where(MeasuredValue.class)
                .in("operation.uuid", opUuidsArray)
                .findAll();

        sendMeasuredValues(realmDB.copyFromRealm(measuredValues));

//        getActivity().registerReceiver(mReceiverSendTaskResult, mFilterSendTask);
//        TaskServiceHelper tsh = new TaskServiceHelper(getActivity(), TaskServiceProvider.Actions.ACTION_TASK_SEND_RESULT);
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
                        Toast.makeText(getActivity(), "Отправка результатов отменена",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        processDialog.show();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /**
     * Диалог изменения статуса операции
     *
     * @param operation - операция для отмены
     */
    private void closeOperationManual(final Operation operation, AdapterView<?> parent) {
        final OperationStatus operationStatusUnComplete;
        operationStatusUnComplete = realmDB.where(OperationStatus.class).equalTo("uuid", OperationStatus.Status.UN_COMPLETE).findFirst();

        // диалог для отмены операции
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, parent, false);
        final Spinner operationVerdictSpinner;
        // список статусов операций в выпадающем списке для выбора
        RealmResults<OperationVerdict> operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        operationVerdictSpinner = (Spinner) myView.findViewById(R.id.simple_spinner);
        OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(getContext(), operationVerdict);
        operationVerdictAdapter.notifyDataSetChanged();
        operationVerdictSpinner.setAdapter(operationVerdictAdapter);

        dialog.setView(myView);
        dialog.setTitle("Отмена операции");

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OperationVerdict verdict = (OperationVerdict) operationVerdictSpinner.getSelectedItem();
                // выставляем выбранный статус
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        operation.setOperationStatus(operationStatusUnComplete);
                        operation.setOperationVerdict(verdict);
                    }
                });

                // закрываем диалог
                dialog.dismiss();
            }
        };

        dialog.setPositiveButton(android.R.string.ok, okListener);
        dialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        //operationTypeSpinner.setOnItemSelectedListener(new ReferenceSpinnerListener());
        dialog.show();
    }

    /**
     * Диалог ручного закрытия наряда
     *
     * @param order - наряд для закрытия
     */
    private void closeOrderManual(final Orders order) {
        final Spinner orderVerdictSpinner;
        // диалог для отмены операции
        final OrderStatus orderStatusUnComplete;
        final TaskStatus taskStatusUnComplete;
        final StageStatus taskStageStatusUnComplete;
        final OperationStatus operationStatusUnComplete;

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, null);
        // список статусов нарядов в выпадающем списке для выбора
        RealmResults<OrderVerdict> orderVerdict = realmDB.where(OrderVerdict.class).findAll();
        orderVerdictSpinner = (Spinner) myView.findViewById(R.id.simple_spinner);
        OrderVerdictAdapter orderVerdictAdapter = new OrderVerdictAdapter(getContext(), orderVerdict);
        orderVerdictAdapter.notifyDataSetChanged();
        orderVerdictSpinner.setAdapter(orderVerdictAdapter);

        orderStatusUnComplete = realmDB.where(OrderStatus.class)
                .equalTo("uuid", OrderStatus.Status.UN_COMPLETE)
                .findFirst();
        taskStageStatusUnComplete = realmDB.where(StageStatus.class)
                .equalTo("uuid", StageStatus.Status.UN_COMPLETE)
                .findFirst();
        operationStatusUnComplete = realmDB.where(OperationStatus.class)
                .equalTo("uuid", OperationStatus.Status.UN_COMPLETE)
                .findFirst();
        taskStatusUnComplete = realmDB.where(TaskStatus.class)
                .equalTo("uuid", TaskStatus.Status.UN_COMPLETE)
                .findFirst();

        dialog.setView(myView);
        dialog.setTitle("Закрытие наряда");
        dialog.setMessage("Всем не законченным задачам будет установлен статус \"Не выполнена\""
                + "\n" + "Закрыть наряд?");

        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                 * закрываем наряд, в зависимости от статуса выполнения
                 * операции выставляем статус наряда
                 */
                for (final Tasks task : order.getTasks()) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            task.setTaskStatus(taskStatusUnComplete);
                        }
                    });
                    for (final TaskStages taskStages : task.getTaskStages()) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                taskStages.setTaskStageStatus(taskStageStatusUnComplete);
                            }
                        });
                        for (final Operation operation : taskStages.getOperations()) {
                            realmDB.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    operation.setOperationStatus(operationStatusUnComplete);
                                }
                            });
                        }
                    }
                }

                final OrderVerdict verdict = (OrderVerdict) orderVerdictSpinner.getSelectedItem();
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        order.setCloseDate(new Date());
                        order.setUpdate(1);
                        order.setOrderStatus(orderStatusUnComplete);
                        order.setOrderVerdict(verdict);
                    }
                });
                //fillListViewTask(null, null);
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("test", "onPictureTaken - jpeg");
                    File selectedImage = getOutputMediaFile();
                    Uri fileUri = Uri.fromFile(selectedImage);
                    //getActivity().getContentResolver().notifyChange(selectedImage, null);
                    //ContentResolver cr = getActivity().getContentResolver();
                    //Bitmap bitmap;
                    try {
                        //bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, fileUri);
                        String path = getContext().getExternalFilesDir("/Pictures") + File.separator;
                        getResizedBitmap(path, fileUri.getPath().replace(path, ""), 1024, 0, new Date().getTime());
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
                }
                break;
            case 101:
                if (resultCode == Activity.RESULT_OK) {
                    String value = "";
                    if (data != null) {
                        value = data.getStringExtra("value");
                    }
                    CheckBox checkBox = (CheckBox) mainListView.getChildAt(currentOperationId).findViewById(R.id.operation_status);
                    checkBox.setChecked(true);
                    CompleteCurrentOperation(currentOperationId, value);
                }
        }
    }

    private File getOutputMediaFile() {

        File mediaStorageDir;
        mediaStorageDir = new File(getActivity().getApplicationContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator + currentOperationUuid + ".jpg");
    }

    /**
     * Диалог если не выполнены некоторые операции
     */
    private void setOperationsVerdict() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        final OperationStatus operationStatusUnComplete;
        operationStatusUnComplete = realmDB.where(OperationStatus.class).findFirst();
        //final OperationVerdict operationVerdictUnComplete;
        //operationVerdictUnComplete = realmDB.where(OperationVerdict.class).findFirst();
        //Button btnAccept = (Button) myView.findViewById(R.id.odc_accept);
        //Button btnCancel = (Button) myView.findViewById(R.id.odc_cancel);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_dialog_cancel, null);
        // список статусов нарядов в выпадающем списке для выбора
        final ListView listView = (ListView) myView.findViewById(R.id.odc_list_view);
        CheckBox checkBoxAll = (CheckBox) myView.findViewById(R.id.odc_main_status);
        Spinner mainSpinner = (Spinner) myView.findViewById(R.id.simple_spinner);

        RealmResults<OperationVerdict> operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        final OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(getContext(), operationVerdict);
        operationVerdictAdapter.notifyDataSetChanged();
        mainSpinner.setAdapter(operationVerdictAdapter);

        fillListViewUncompleteOperations(listView);

        dialog.setView(myView);
/*
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
*/
        dialog.setPositiveButton(R.string.dialog_accept,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CheckBox checkBox;
                        Spinner spinner;
                        for (int i = 0; i < uncompleteOperationList.size(); i++) {
                            checkBox = (CheckBox) getViewByPosition(i, listView).findViewById(R.id.operation_status);
                            spinner = (Spinner) getViewByPosition(i, listView).findViewById(R.id.operation_verdict_spinner);
                            final OperationVerdict operationVerdict = operationVerdictAdapter.getItem(spinner.getSelectedItemPosition());
                            final Operation operation = uncompleteOperationList.get(i);
                            if (operation != null && checkBox.isChecked()) {
                                realmDB.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        operation.setOperationStatus(operationStatusUnComplete);
                                        operation.setOperationVerdict(operationVerdict);
                                    }
                                });
                            }
                        }
                        Log.d(TAG, "Остановка таймера...");
                        taskTimer.cancel();
                        firstLaunch = true;
                        currentOperationId = 0;

                        if (selectedTask != null) {
                            currentTaskStageUuid = selectedStage.getUuid();
                            currentTaskUuid = selectedTask.getUuid();
                            fillListViewTaskStage(selectedTask, true);
                            submit.setVisibility(View.GONE);
                            measure.setVisibility(View.GONE);
                            Level = 2;
                        }
                    }
                });

        dialog.setNegativeButton(R.string.dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        checkBoxAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox checkBox;
                CheckBox checkBoxAll;
                checkBoxAll = (CheckBox) v.findViewById(R.id.odc_main_status);
                for (int i = 0; i < uncompleteOperationList.size(); i++) {
                    checkBox = (CheckBox) getViewByPosition(i, listView).findViewById(R.id.operation_status);
                    checkBox.setChecked(checkBoxAll.isChecked());
                }
            }
        });
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner;
                for (int j = 0; j < uncompleteOperationList.size(); j++) {
                    spinner = (Spinner) getViewByPosition(j, listView).findViewById(R.id.operation_verdict_spinner);
                    spinner.setSelection(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        dialog.show();
    }

    private void fillListViewUncompleteOperations(ListView listView) {
        RealmResults<Operation> operations;
        RealmQuery<Operation> q = realmDB.where(Operation.class);
        boolean first = true;
        for (Operation operation : uncompleteOperationList) {
            long id = operation.get_id();
            if (first) {
                q = q.equalTo("_id", id);
                first = false;
            } else {
                q = q.or().equalTo("_id", id);
            }
        }
        operations = q.findAll();

        OperationAdapter uncompleteOperationAdapter;
        uncompleteOperationAdapter = new OperationAdapter(getContext(), operations, currentTaskUuid);
        listView.setAdapter(uncompleteOperationAdapter);
    }

    void CompleteCurrentOperation(int position, String measureValue) {
        TextView textTime;
        TextView textValue;
        final long currentTime = System.currentTimeMillis();
        final OperationStatus operationStatusCompleted;
        final OperationVerdict operationVerdictCompleted;

        operationStatusCompleted = realmDB.where(OperationStatus.class)
                .equalTo("uuid", OperationStatus.Status.COMPLETE)
                .findFirst();
        if (operationStatusCompleted == null) {
            Log.d(TAG, "Статус: операция завершена отсутствует в словаре!");
        }

        operationVerdictCompleted = realmDB.where(OperationVerdict.class)
                .equalTo("uuid", OperationVerdict.Verdict.COMPLETE)
                .findFirst();
        if (operationVerdictCompleted == null) {
            Log.d(TAG, "Вердикт: операция завершена отсутствует в словаре!");
        }

        if (operationAdapter != null) {
            textTime = (TextView) mainListView.getChildAt(currentOperationId).findViewById(R.id.op_time);
            textTime.setText(getString(R.string.sec_with_value, (int) (currentTime - startTime) / 1000));

            if (measureValue != null) {
                textValue = (TextView) mainListView.getChildAt(currentOperationId).findViewById(R.id.op_measure_value);
                textValue.setText(measureValue);
            }

            if (currentOperationId + 1 < operationAdapter.getCount()) {
                operationAdapter.setItemEnable(currentOperationId + 1, true);
                operationAdapter.setItemVisibility(currentOperationId);
                operationAdapter.setItemVisibility(currentOperationId + 1);
                startTime = System.currentTimeMillis();
                currentOperationId++;

                // фиксируем начало работы над следующей операцией (если у нее нет статуса закончена), меняем ее статус на в процессе
                final Operation operation = operationAdapter.getItem(currentOperationId);
                final OperationStatus operationStatusInWork;
                operationStatusInWork = realmDB.where(OperationStatus.class)
                        .equalTo("uuid", OperationStatus.Status.IN_WORK)
                        .findFirst();
                if (operation != null) {
                    OperationStatus operationStatus = operation.getOperationStatus();
                    if (!operationStatus.getUuid().equals(OperationStatus.Status.COMPLETE)) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                operation.setStartDate(new Date());
                                operation.setOperationStatus(operationStatusInWork);
                            }
                        });
                    }
                }
            }

            final Operation operation = operationAdapter.getItem(position);
            // если операция уже завершена - то ни статус, ни дату не меняем
            if (operation != null && !operation.getOperationStatus().getUuid().equals(OperationStatus.Status.COMPLETE)) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        operation.setEndDate(new Date(currentTime));
                        //operation.setStartDate(new Date(startTime));
                        operation.setOperationStatus(operationStatusCompleted);
                        operation.setOperationVerdict(operationVerdictCompleted);
                    }
                });
                if (((currentTime - startTime) / 1000) <= operation.getOperationTemplate().getNormative())
                    textTime.setBackgroundColor(Color.GREEN);
                else {
                    textTime.setBackgroundColor(Color.RED);
                }

                // перезапоминаем таймер
                startTime = System.currentTimeMillis();
                operationAdapter.setItemEnable(position, false);
            }
        }
    }

    /**
     * Создание элементов интерфейса для шагов операции с измерениями значений
     *
     * @param measureType - тип осуществляемого измерения
     */
    private void measureUI(String measureType) {
        // выбор значения
        if (numberPicker == null) {
            numberPicker = new NumberPicker(getActivity().getApplicationContext());
        }

        numberPicker.setOrientation(NumberPicker.VERTICAL);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(999);

        // перечень множителей
        if (suffixList == null) {
            suffixList = new ArrayList<>();
        } else {
            suffixList.clear();
        }

        ArrayAdapter<Suffixes> spinnerSuffixAdapter;
        if (measureType.equals(MeasureType.Type.FREQUENCY)) {
            //resultButtonLayout.addView(numberPicker);

            suffixList.add(new Suffixes("Гц", 1));
            suffixList.add(new Suffixes("кГц", 1000));
            suffixList.add(new Suffixes("МГц", 1000000));
            suffixList.add(new Suffixes("ГГц", 1000000000));

            // адаптер для множителей
            spinnerSuffixAdapter = new ArrayAdapter<>(
                    getActivity().getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, suffixList);

            // выпадающий список с множителями
            if (spinnerSuffix == null) {
                spinnerSuffix = new Spinner(getActivity().getApplicationContext());
            }

            spinnerSuffix.setAdapter(spinnerSuffixAdapter);

            //resultButtonLayout.addView(spinnerSuffix);
        } else if (measureType.equals(MeasureType.Type.VOLTAGE)) {
            //resultButtonLayout.addView(numberPicker);

            suffixList.add(new Suffixes("В", 1));
            suffixList.add(new Suffixes("кВ", 1000));
            suffixList.add(new Suffixes("МВ", 1000000));
            suffixList.add(new Suffixes("ГВ", 1000000000));

            // адаптер для множителей
            spinnerSuffixAdapter = new ArrayAdapter<>(
                    getActivity().getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, suffixList);

            // выпадающий список с множителями
            if (spinnerSuffix == null) {
                spinnerSuffix = new Spinner(getActivity().getApplicationContext());
            }

            spinnerSuffix.setAdapter(spinnerSuffixAdapter);

            //resultButtonLayout.addView(spinnerSuffix);
        } else if (measureType.equals(MeasureType.Type.PRESSURE)) {
            //resultButtonLayout.addView(numberPicker);

            suffixList.add(new Suffixes("Па", 1));
            suffixList.add(new Suffixes("кПа", 1000));
            suffixList.add(new Suffixes("МПа", 1000000));
            suffixList.add(new Suffixes("ГПа", 1000000000));

            // адаптер для множителей
            spinnerSuffixAdapter = new ArrayAdapter<>(
                    getActivity().getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, suffixList);

            // выпадающий список с множителями
            if (spinnerSuffix == null) {
                spinnerSuffix = new Spinner(getActivity().getApplicationContext());
            }

            spinnerSuffix.setAdapter(spinnerSuffixAdapter);

            //resultButtonLayout.addView(spinnerSuffix);
        } else if (measureType.equals(MeasureType.Type.PHOTO)) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File photo = getOutputMediaFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            startActivityForResult(intent, 100);
        }
    }

    // обработчик кнопки "завершить все операции"
    public class submitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            int completedOperationCount = 0;
            final long currentTime = System.currentTimeMillis();
            //long totalTimeElapsed = currentTime - startTime;
            CheckBox checkBox;
            final StageStatus taskStageComplete;
            uncompleteOperationList.clear();
            // по умолчанию у нас все выполнено
            taskStageComplete = realmDB.where(StageStatus.class).equalTo("uuid", StageStatus.Status.COMPLETE).findFirst();

            if (operationAdapter != null) {
                totalOperationCount = operationAdapter.getCount();
            }

            for (int i = 0; i < totalOperationCount; i++) {
                checkBox = (CheckBox) getViewByPosition(i, mainListView).findViewById(R.id.operation_status);
                final Operation operation = operationAdapter.getItem(i);
                if (operation != null) {
                    if (checkBox.isChecked()) {
                        completedOperationCount++;
                    } else {
                        uncompleteOperationList.add(operation);
                    }
                } else {
                    Log.d(TAG, "Операция под индексом " + i + " не найдена");
                }
            }

            // все операции выполнены
            if (totalOperationCount == completedOperationCount) {
                if (selectedStage != null && !selectedStage.getTaskStageStatus().getUuid().equals(StageStatus.Status.COMPLETE)) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            selectedStage.setTaskStageStatus(taskStageComplete);
                            //taskStage.setEndDate();
                            selectedStage.setEndDate(new Date());
                        }
                    });
                }

                Log.d(TAG, "Остановка таймера...");
                taskTimer.cancel();
                firstLaunch = true;
                currentOperationId = 0;

                if (selectedTask != null) {
                    currentTaskStageUuid = selectedStage.getUuid();
                    currentTaskUuid = selectedTask.getUuid();
                    Level = 2;
                    fillListViewTaskStage(selectedTask, true);
                    submit.setVisibility(View.GONE);
                    measure.setVisibility(View.GONE);
                }

            } else {
                Log.d("order", "dialog");
                setOperationsVerdict();
            }
        }
    }

    public class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View selectedItemView, int position, long id) {
            // находимся на "экране" нарядов
            if (Level == 0) {
                if (orderAdapter != null) {
                    selectedOrder = orderAdapter.getItem(position);
                    if (selectedOrder != null) {
                        currentOrderUuid = selectedOrder.getUuid();
                        fillListViewTasks(selectedOrder, false);
                        Level = 1;
                    }
                }

                return;
            }

            // Tasks
            if (Level == 1) {
                if (taskAdapter != null) {
                    selectedTask = taskAdapter.getItem(position);
                    if (selectedTask != null) {
                        Toast.makeText(getContext(), "Нужно поднести метку", Toast.LENGTH_LONG).show();
                        final String expectedTagId = selectedTask.getEquipment().getTagId();
                        currentEquipment = selectedTask.getEquipment();
                        Log.d(TAG, "Ожидаемая метка: " + expectedTagId);
                        Handler handler = new Handler(new Handler.Callback() {
                            @Override
                            public boolean handleMessage(Message message) {
                                if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                                    String tagId = ((String) message.obj).substring(4);
                                    Log.d(TAG, "Ид метки получили: " + tagId);
                                    if (expectedTagId.equals(tagId)) {
                                        boolean run_ar_content = sp.getBoolean("run_ar_content_key", false);
                                        if (run_ar_content) {
                                            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("ru.shtrm.toir");
                                            if (intent != null) {
                                                intent.putExtra("hardwareUUID", currentEquipment.getUuid());
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(getContext(),
                                                        "Приложение ТОиР не установлено", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        fillListViewTaskStage(selectedTask, false);
                                        Level = 2;
                                    } else {
                                        Toast.makeText(getContext(),
                                                "Не верное оборудование!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Ошибка чтения метки!");
                                    Toast.makeText(getContext(),
                                            "Ошибка чтения метки.", Toast.LENGTH_SHORT).show();
                                }

                                // закрываем диалог
                                rfidDialog.dismiss();
                                return true;
                            }
                        });

                        rfidDialog = new RfidDialog();
                        rfidDialog.setHandler(handler);
                        rfidDialog.readTagId();
                        rfidDialog.show(getActivity().getFragmentManager(), TAG);

                    }
                }

                return;
            }

            // TaskStage
            if (Level == 2) {
                if (taskStageAdapter != null) {
                    selectedStage = taskStageAdapter.getItem(position);
                    if (selectedStage != null) {
                        currentTaskStageUuid = selectedStage.getUuid();
                        fillListViewOperations(selectedStage);
                        submit.setVisibility(View.VISIBLE);
                        submit.setOnClickListener(new submitOnClickListener());
                        measure.setVisibility(View.VISIBLE);
                        Level = 3;
                        startOperations();
                    }
                }

                return;
            }

            // Operation
            if (Level == 3) {
                if (operationAdapter != null) {
                    selectedOperation = operationAdapter.getItem(position);
                    if (selectedOperation != null) {
                        operationAdapter.setItemVisibility(position);
                        operationAdapter.notifyDataSetChanged();
                        //mainListView.invalidateViews();
                    }
                }
            }
        }
    }

    public class onCheckBoxClickListener implements View.OnClickListener {
        int position;

        onCheckBoxClickListener(int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View arg) {
            if (position != currentOperationId) {
                // показываем меню предупреждения о пропуске операций

                return;
            }

            if (!operationAdapter.getItemEnable(position)) {
                return;
            }
            CompleteCurrentOperation(position, null);
        }
    }

    public class ListViewLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // находимся на "экране" с операциями
            if (Level == 3) {
                //String operationStatus = operation.getOperation_status_uuid();
                // менять произвольно статус операции позволяем только если
                // статус операции "Новая" или "В работе"
                Operation operation = (Operation) parent.getItemAtPosition(position);
                if (!operation.getOperationStatus().getUuid().equals(OperationAdapter.Status.NEW) ||
                        !operation.getOperationStatus().getUuid().equals(OperationAdapter.Status.IN_WORK)) {
                    // показываем диалог изменения статуса
                    closeOperationManual(operation, parent);
                } else {
                    // операция уже выполнена, изменить статус нельзя
                    // сообщаем об этом
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Внимание!");
                    dialog.setMessage("Изменить статус операции нельзя!");
                    AlertDialog.Builder builder = dialog.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            } else if (Level == 0) {
                // находимся на экране с нарядами
                Orders order = orderAdapter.getItem(position);
                OrderStatus orderStatus;
                // проверяем статус наряда
                if (order != null) {
                    orderStatus = order.getOrderStatus();
                    if (orderStatus != null) {
                        if (orderStatus.getUuid().equals(OrderStatus.Status.COMPLETE)
                                || order.getOrderStatus().getUuid().equals(OrderStatus.Status.UN_COMPLETE)) {
                            // наряд уже закрыт, изменить статус нельзя
                            // сообщаем об этом
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            dialog.setTitle("Внимание!");
                            dialog.setMessage("Изменить статус наряда уже нельзя!");
                            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        } else {
                            // наряд можно закрыть принудительно
                            closeOrderManual(order);
                        }
                    } else {
                        closeOrderManual(order);
                    }
                }
            }

            return true;
        }
    }

    /**
     * Класс для представления множителей (частоты, напряжения, тока...)
     *
     * @author Dmitriy Logachov
     */
    protected class Suffixes {
        String title;
        long multiplier;

        Suffixes(String t, int m) {
            title = t;
            multiplier = m;
        }

        public String toString() {
            return title;
        }
    }

    private class FilePath {
        String fileName;
        String urlPath;
        String localPath;

        FilePath(String name, String url, String local) {
            fileName = name;
            urlPath = url;
            localPath = local;
        }
    }
}
