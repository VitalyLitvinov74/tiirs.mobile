package ru.toir.mobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.LongSparseArray;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmObject;
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
import ru.toir.mobile.db.adapters.StageAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.realm.Documentation;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.IToirDbObject;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.Objects;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationFile;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationType;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.OrderVerdict;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.Stage;
import ru.toir.mobile.db.realm.StageStatus;
import ru.toir.mobile.db.realm.Task;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.rest.ToirAPIFactory;
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
    private static final int ORDER_LEVEL = 0;
    private static final int TASK_LEVEL = 1;
    private static final int STAGE_LEVEL = 2;
    private static final int OPERATION_LEVEL = 3;

    private static final int ACTIVITY_PHOTO = 100;
    private static final int ACTIVITY_MEASURE = 101;
    FloatingActionButton fab_check;
    FloatingActionButton fab_camera;
    private Toolbar toolbar;
    private Task selectedTask;
    private Orders selectedOrder;
    private Stage selectedStage;
    private OrderAdapter orderAdapter;
    private TaskAdapter taskAdapter;
    private StageAdapter stageAdapter;
    private OperationAdapter operationAdapter;
    //    private String currentOrderUuid = "";
    private String currentTaskUuid = "";
    private String currentOperationUuid = "";
    //    private String currentStageUuid = "";
    private ListView mainListView;
    private LinearLayout listLayout;
    private BottomBar bottomBar;
    private String TAG = "OrderFragment";
    private Realm realmDB;
    private int Level = ORDER_LEVEL;
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
            currentOperation = operationAdapter.getItem(currentOperationId);
            if (operationAdapter != null && currentOperation != null && currentOperationId < operationAdapter.getCount()) {
                //textTime = (TextView) mainListView.getChildAt(currentOperationId).findViewById(R.id.op_time);
                if (!currentOperation.getOperationStatus().getUuid().equals(OperationStatus.Status.COMPLETE)) {
                    textTime = (TextView) getViewByPosition(currentOperationId, mainListView).findViewById(R.id.op_time);
                    textTime.setText(getString(R.string.sec_with_value, (int) (currentTime - startTime) / 1000));
                }

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
    //private NumberPicker numberPicker;
    //private Spinner spinnerSuffix;
    //private ArrayList<OrderFragment.Suffixes> suffixList;
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

        uncompleteOperationList = new ArrayList<>();

        realmDB = Realm.getDefaultInstance();
        listLayout = (LinearLayout) rootView.findViewById(R.id.tl_listview_layout);
        bottomBar = (BottomBar) (getActivity()).findViewById(R.id.bottomBar);

        fab_check = (FloatingActionButton) rootView.findViewById(R.id.fab_check);
        fab_camera = (FloatingActionButton) rootView.findViewById(R.id.fab_photo);
        fab_check.setVisibility(View.INVISIBLE);
        fab_camera.setVisibility(View.INVISIBLE);

        fab_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ACTIVITY_PHOTO);
            }
        });
        fab_check.setOnClickListener(new submitOnClickListener());

        mainListView = (ListView) rootView.findViewById(R.id.list_view);

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d(TAG, "OrderFragment !!! back pressed!!!");
                    if (Level == TASK_LEVEL || Level == 0) {
                        initView();
                    }

                    if (Level == STAGE_LEVEL) {
                        Level = TASK_LEVEL;
                        fillListViewTasks(selectedOrder);
                    }

                    if (Level == OPERATION_LEVEL) {
                        taskTimer.cancel();
                        firstLaunch = true;
                        currentOperationId = 0;
                        if (selectedTask != null) {
//                            currentStageUuid = selectedStage.getUuid();
                            currentTaskUuid = selectedTask.getUuid();
                            fillListViewStage(selectedTask);
                            Level = STAGE_LEVEL;
                            fab_camera.setVisibility(View.INVISIBLE);
                            //fab_check.setVisibility(View.INVISIBLE);
                        }
                    }

                    return true;
                }

                return false;
            }
        });

        // так как обработчики пока одни на всё, ставим их один раз
        mainListView.setOnItemClickListener(mainListViewClickListener);
        mainListView.setOnItemLongClickListener(mainListViewLongClickListener);

        mainListView.setLongClickable(true);

        initView();
        return rootView;
    }

    private void initView() {

        Level = ORDER_LEVEL;
        toolbar.setSubtitle("Наряды");
        fillListViewOrders();
    }

    // Orders----------------------------------------------------------------------------------------
    private void fillListViewOrders() {
        fillListViewOrders(null, null);
    }

    private void fillListViewOrders(String orderStatus, String orderByField) {
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        User user = realmDB.where(User.class)
                .equalTo("tagId", authUser.getTagId())
                .findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
        } else {
            RealmQuery<Orders> query = realmDB.where(Orders.class).equalTo("user.uuid", authUser.getUuid());
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

        ViewGroup.LayoutParams params = listLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        listLayout.setLayoutParams(params);

        int new_orders = MainFunctions.getActiveOrdersCount();
        if (new_orders > 0) {
            bottomBar.getTabAtPosition(1).setBadgeCount(new_orders);
        }

        fab_check.setVisibility(View.INVISIBLE);
    }

    // Task----------------------------------------------------------------------------------------
    private void fillListViewTasks(Orders order) {
        RealmResults<Task> tasks;
        RealmQuery<Task> q = realmDB.where(Task.class);
        boolean first = true;
        boolean all_complete = true;
        toolbar.setSubtitle("Задачи");
        if (order.getTasks().size() > 0) {
            for (Task task : order.getTasks()) {
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

            tasks = q.findAll();
        } else {
            tasks = null;
        }

        // задач нет
        if (first) {
            all_complete = false;
        }

//        if (complete_operation && all_complete && !order.getOrderStatus().getUuid().equals(OrderStatus.Status.COMPLETE)) {
        if (all_complete && !order.getOrderStatus().getUuid().equals(OrderStatus.Status.COMPLETE)) {
            final OrderStatus orderStatusComplete = realmDB.where(OrderStatus.class)
                    .equalTo("uuid", OrderStatus.Status.COMPLETE)
                    .findFirst();
/*
            final OrderVerdict orderVerdictCommplete = realmDB.where(OrderVerdict.class)
                    .equalTo("uuid", OrderVerdict.Status.COMPLETE)
                    .findFirst();*/

            try {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        selectedOrder.setCloseDate(new Date());
                        selectedOrder.setOrderStatus(orderStatusComplete);
                    }
                });
            } catch (Exception e) {
                return;
            }
            addToJournal("Закончен наряд " + order.getTitle() + "(" + order.getUuid() + ")");
            Level = ORDER_LEVEL;
            fillListViewOrders(null, null);
        }

        taskAdapter = new TaskAdapter(getContext(), tasks);
        mainListView.setAdapter(taskAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(order.getTitle());
        }

        fab_camera.setVisibility(View.INVISIBLE);
        fab_check.setVisibility(View.VISIBLE);
    }

    // Stages----------------------------------------------------------------------------------------
    private void fillListViewStage(Task task) {
        RealmResults<Stage> stages;
        RealmQuery<Stage> q = realmDB.where(Stage.class);
        toolbar.setSubtitle("Этапы задач");
        boolean first = true;
        boolean all_complete = true;
        if (task.getStages().size() > 0) {
            for (Stage stage : task.getStages()) {
                long id = stage.get_id();
                if (!stage.getStageStatus().getUuid().equals(StageStatus.Status.COMPLETE)) {
                    all_complete = false;
                }

                if (first) {
                    q = q.equalTo("_id", id);
                    first = false;
                } else {
                    q = q.or().equalTo("_id", id);
                }
            }

            stages = q.findAllSorted("flowOrder");
        } else {
            stages = null;
        }

        // этапов нет
        if (first) {
            all_complete = false;
        }

        stageAdapter = new StageAdapter(getContext(), stages);
        mainListView.setAdapter(stageAdapter);
        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(task.getTaskTemplate().getTitle());
        }

//        if (complete_operation && all_complete && !task.getTaskStatus().getUuid().equals(TaskStatus.Status.COMPLETE)) {
        if (all_complete && !task.getTaskStatus().getUuid().equals(TaskStatus.Status.COMPLETE)) {
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
            Level = TASK_LEVEL;
            fillListViewTasks(selectedOrder);
        }

        ViewGroup.LayoutParams params = listLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        listLayout.setLayoutParams(params);

        fab_camera.setVisibility(View.INVISIBLE);
        fab_check.setVisibility(View.VISIBLE);
    }

    // Operations----------------------------------------------------------------------------------------
    private void fillListViewOperations(Stage stage) {
        RealmResults<Operation> operations;
        RealmQuery<Operation> q = realmDB.where(Operation.class);
        boolean first = true;
        toolbar.setSubtitle("Операции");
        if (stage.getOperations().size() > 0) {
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
        } else {
            operations = null;
        }

        operationAdapter = new OperationAdapter(getContext(), operations, selectedTask.getTaskTemplate().getUuid());
        mainListView.setAdapter(operationAdapter);
        //resultButtonLayout.setVisibility(View.VISIBLE);
        //makePhotoButton.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams params = listLayout.getLayoutParams();
        params.height = 1000;
        listLayout.setLayoutParams(params);

        TextView tl_Header = (TextView) getActivity().findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.VISIBLE);
            tl_Header.setText(stage.getStageTemplate().getTitle());
        }

        fab_camera.setVisibility(View.VISIBLE);
        fab_check.setVisibility(View.VISIBLE);
        //mainListView.setOnItemClickListener(mainListViewClickListener);
    }

    // Start Operations----------------------------------------------------------------------------------------
    void startOperations() {
        boolean isMeasure = false;
        // запрещаем все операции кроме первой не законченной
        if (operationAdapter != null) {
            totalOperationCount = operationAdapter.getCount();
            // нет решительно никакой возможности выполнять выполненные операции по сто раз только если сбросить все
            for (int i = 0; i < totalOperationCount; i++) {
                final Operation operation = operationAdapter.getItem(i);
                if (operation != null) {
                    OperationStatus operationStatus = operation.getOperationStatus();
                    if (operationStatus != null) {
                        final OperationStatus operationStatusInWork = realmDB.where(OperationStatus.class)
                                .equalTo("uuid", OperationStatus.Status.IN_WORK)
                                .findFirst();
                        final OperationType operationType = operation.getOperationTemplate().getOperationType();
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

                        if (operationType.getUuid().equals(OperationType.Type.MEASURE)) {
                            isMeasure = true;
                        }

                        // если эта операция имеет статус любой кроме закончена или отменена, то начинаем с нее
                        if (operationStatus.getUuid().equals(OperationStatus.Status.IN_WORK) ||
                                operationStatus.getUuid().equals(OperationStatus.Status.NEW) ||
                                operationStatus.getUuid().equals(OperationStatus.Status.UN_COMPLETE)) {
                            operationAdapter.setItemEnable(i, true);
                            currentOperationId = i;
                            break;
                        } else {
                            operationAdapter.setItemEnable(i, false);
                        }
                    } else {
                        Log.d(TAG, "Найдена операция с неинициализированным статусом");
                    }
                }
            }
        }

        // время начала работы (приступаем к первой операции и нехрен тормозить)
        startTime = System.currentTimeMillis();
        Log.d(TAG, "Запуск таймера...");
        taskTimer.start();

        // фиксируем начало работы над этапом задачи (если у него статус получен), меняем его статус на в процессе
        final StageStatus stageStatus;
        final StageStatus stageStatusInWork;
        if (selectedStage != null) {
            stageStatus = selectedStage.getStageStatus();
            stageStatusInWork = realmDB.where(StageStatus.class)
                    .equalTo("uuid", StageStatus.Status.IN_WORK)
                    .findFirst();
            if (stageStatus != null && stageStatusInWork != null)
                if (stageStatus.getUuid().equals(StageStatus.Status.NEW) || stageStatus.getUuid().equals(StageStatus.Status.UN_COMPLETE)) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            selectedStage.setStartDate(new Date());
                            selectedStage.setStageStatus(stageStatusInWork);
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
            startActivityForResult(measure, ACTIVITY_MEASURE);
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
        AsyncTask<String[], Integer, List<Orders>> aTask = new AsyncTask<String[], Integer, List<Orders>>() {
            @Override
            protected List<Orders> doInBackground(String[]... params) {
                // обновляем справочники
                ReferenceFragment.updateReferencesForOrders();
                //int current_files_cnt=0;

                List<String> args = java.util.Arrays.asList(params[0]);

                // запрашиваем наряды
                Call<List<Orders>> call = ToirAPIFactory.getOrdersService().getByStatus(args);
                List<Orders> result;
                try {
                    Response<List<Orders>> response = call.execute();
                    if (response.code() != 200) {
                        Toast.makeText(getContext(),
                                "Ошибка получения нарядов! Код ответа сервера:" + response.code(),
                                Toast.LENGTH_LONG).show();
                        return null;
                    }

                    result = response.body();
                    if (result == null) {
                        Toast.makeText(getContext(),
                                "Ошибка получения нарядов! Содержимого ответа нет.",
                                Toast.LENGTH_LONG).show();
                        return null;
                    }
                } catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage());
                    return null;
                }

                String userName = AuthorizedUser.getInstance().getLogin();
                // список оборудования в полученных нарядах (для постройки списка документации)
                Map<String, Equipment> equipmentList = new HashMap<>();
                // список файлов для загрузки
                List<FilePath> files = new ArrayList<>();
                // строим список изображений для загрузки
                for (Orders order : result) {
                    // если это не новый наряд, ставим флаг sent в true
                    String orderStatusUuid = order.getOrderStatus().getUuid();
                    if (!orderStatusUuid.equals(OrderStatus.Status.NEW)) {
                        order.setSent(true);
                    }

                    List<Task> tasks = order.getTasks();
                    for (Task task : tasks) {
                        // UUID шаблона задачи
                        String taskTemplateUuid = task.getTaskTemplate().getUuid();
                        // путь до папки с файлами
                        String equipmentModelUuid = task.getEquipment().getEquipmentModel().getUuid();
                        // общий путь до файлов на сервере
                        String basePath = "/storage/" + userName + "/" + equipmentModelUuid + "/";
                        // общий путь до файлов локальный
                        String basePathLocal = "/tasks/" + taskTemplateUuid + "/";

                        // добавляем для получения документации в дальнейшем
                        equipmentList.put(task.getEquipment().getUuid(), task.getEquipment());

                        boolean isNeedDownload;

                        // урл изображения задачи
                        isNeedDownload = isNeedDownload(task.getTaskTemplate(), basePathLocal);
                        if (isNeedDownload) {
                            files.add(new FilePath(task.getTaskTemplate().getImage(), basePath, basePathLocal));
                        }

                        // урл изображения оборудования
                        isNeedDownload = isNeedDownload(task.getEquipment(), "/equipment/");
                        if (isNeedDownload) {
                            files.add(new FilePath(task.getEquipment().getImage(), basePath, "/equipment/"));
                        }

                        // урл изображения модели оборудования
                        isNeedDownload = isNeedDownload(task.getEquipment().getEquipmentModel(), "/equipment/");
                        if (isNeedDownload) {
                            files.add(new FilePath(task.getEquipment().getEquipmentModel().getImage(), basePath, "/equipment/"));
                        }

                        // урл изображения объекта где расположено оборудование
                        Objects object = task.getEquipment().getLocation();
                        if (object != null) {
                            isNeedDownload = isNeedDownload(object, "/objects/");
                            if (isNeedDownload) {
                                files.add(new FilePath(object.getImage(),
                                        "/storage/" + userName + "/" + object.getUuid() + "/",
                                        "/objects/"));
                            }
                        }

                        List<Stage> stages = task.getStages();
                        for (Stage stage : stages) {
                            // урл изображения этапа задачи
                            isNeedDownload = isNeedDownload(stage.getStageTemplate(), basePathLocal);
                            if (isNeedDownload) {
                                files.add(new FilePath(stage.getStageTemplate().getImage(),
                                        basePath, basePathLocal));
                            }

                            if (stage.getEquipment() != null) {
                                // добавляем для получения документации в дальнейшем
                                equipmentList.put(stage.getEquipment().getUuid(), stage.getEquipment());

                                String equipmentPath;
                                equipmentPath = "/storage/" + userName + "/" + stage.getEquipment().getEquipmentModel().getUuid() + "/";
                                isNeedDownload = isNeedDownload(stage.getEquipment().getEquipmentModel(), "/equipment/");
                                if (isNeedDownload) {
                                    files.add(new FilePath(stage.getEquipment().getEquipmentModel().getImage(), equipmentPath, "/equipment/"));
                                }

                                equipmentPath = "/storage/" + userName + "/" + stage.getEquipment().getEquipmentModel().getUuid() + "/";
                                isNeedDownload = isNeedDownload(stage.getEquipment(), "/equipment/");
                                if (isNeedDownload) {
                                    files.add(new FilePath(stage.getEquipment().getImage(), equipmentPath, "/equipment/"));
                                }
                            }

                            List<Operation> operations = stage.getOperations();
                            for (Operation operation : operations) {
                                // урл изображения операции
                                isNeedDownload = isNeedDownload(operation.getOperationTemplate(), basePathLocal);
                                if (isNeedDownload) {
                                    files.add(new FilePath(operation.getOperationTemplate().getImage(),
                                            basePath, basePathLocal));
                                }
                            }
                        }
                    }
                }

                Set<String> needEquipmentUuids = new HashSet<>();
                Set<String> needEquipmentModelUuids = new HashSet<>();
                for (Map.Entry<String, Equipment> entry : equipmentList.entrySet()) {
                    needEquipmentUuids.add(entry.getValue().getUuid());
                    needEquipmentModelUuids.add(entry.getValue().getEquipmentModel().getUuid());
                }

                Call<List<Documentation>> docCall;
                docCall = ToirAPIFactory.getDocumentationService().getByEquipment(
                        needEquipmentUuids.toArray(new String[]{}));
                try {
                    Response<List<Documentation>> r = docCall.execute();
                    List<Documentation> list = r.body();
                    if (list != null) {
                        for (Documentation doc : list) {
                            String localPath = "/documentation/" + doc.getEquipment().getUuid() + "/";
                            if (isNeedDownload(doc, localPath) && doc.isRequired()) {
                                String url = "/storage/" + userName + "/" + doc.getEquipment().getUuid() + "/";
                                files.add(new FilePath(doc.getPath(), url, localPath));
                            }
                        }

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        realm.close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                docCall = ToirAPIFactory.getDocumentationService().getByEquipmentModel(
                        needEquipmentModelUuids.toArray(new String[]{}));
                try {
                    Response<List<Documentation>> r = docCall.execute();
                    List<Documentation> list = r.body();
                    if (list != null) {
                        for (Documentation doc : list) {
                            if (doc.getEquipmentModel() != null) {
                                String localPath = "/documentation/" + doc.getEquipmentModel().getUuid() + "/";
                                if (isNeedDownload(doc, localPath) && doc.isRequired()) {
                                    String url = "/storage/" + userName + "/" + doc.getEquipmentModel().getUuid() + "/";
                                    files.add(new FilePath(doc.getPath(), url, localPath));
                                }
                            }

                            if (doc.getEquipment() != null) {
                                String localPath = "/documentation/" + doc.getEquipment().getUuid() + "/";
                                if (isNeedDownload(doc, localPath) && doc.isRequired()) {
                                    String url = "/storage/" + userName + "/" + doc.getEquipment().getUuid() + "/";
                                    files.add(new FilePath(doc.getPath(), url, localPath));
                                }
                            }
                        }

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        realm.close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Ошибка при получении документации.");
                    e.printStackTrace();
                }

                Map<String, Set<String>> requestList = new HashMap<>();
                // тестовый вывод для принятия решения о группировке файлов для минимизации количества загружаемых данных
                for (FilePath item : files) {
                    String key = item.urlPath + item.fileName;
                    if (!requestList.containsKey(key)) {
                        Set<String> list = new HashSet<>();
                        list.add(item.localPath);
                        requestList.put(key, list);
                    } else {
                        requestList.get(key).add(item.localPath);
                    }
                }

                // загружаем файлы
                int filesCount = 0;
                for (String key : requestList.keySet()) {
                    Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().get(ToirApplication.serverUrl + key);
                    try {
                        Response<ResponseBody> r = call1.execute();
                        ResponseBody trueImgBody = r.body();
                        if (trueImgBody == null) {
                            continue;
                        }

                        for (String localPath : requestList.get(key)) {
                            filesCount++;
                            publishProgress(filesCount);
                            String fileName = key.substring(key.lastIndexOf("/") + 1);
                            File file = new File(getContext().getExternalFilesDir(localPath), fileName);
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
                        }
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
                        final List<String> uuids = new ArrayList<>();
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        // проставляем дату получения нарядов
                        for (Orders order : orders) {
                            order.setReceivDate(new Date());

                            if (order.getOrderStatus().getUuid().equals(OrderStatus.Status.NEW)) {
                                uuids.add(order.getUuid());
                            }
                        }

                        realm.copyToRealmOrUpdate(orders);
                        realm.commitTransaction();
                        realm.close();
                        addToJournal("Клиент успешно получил " + count + " нарядов");
                        Toast.makeText(getActivity(), "Количество нарядов " + count, Toast.LENGTH_SHORT).show();

                        // если есть новые наряды, отправляем подтверждение о получении
                        if (!uuids.isEmpty()) {
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    // отправляем запрос на установку статуса IN_WORK на сервере
                                    // в случае не успеха, ни каких действий для повторной отправки
                                    // не предпринимается (т.к. нет ни каких средств для фиксации этого события)
                                    Call<ResponseBody> call = ToirAPIFactory.getOrdersService().setInWork(uuids);
                                    try {
                                        Response response = call.execute();
                                        if (response.code() != 200) {
                                            // TODO: нужно реализовать механизм повторной попытки установки статуса
                                            addToJournal("Не удалось отправить запрос на установку статуса нарядов IN_WORK");
                                        } else {
                                            addToJournal("Успешно отправили статус для полученных нарядов IN_WORK");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        addToJournal("Исключение при запросе на установку статуса нарядов IN_WORK");
                                    }
                                }
                            };
                            Thread thread = new Thread(runnable);
                            thread.start();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Нарядов нет.", Toast.LENGTH_SHORT).show();
                    }
                }

                processDialog.dismiss();
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                processDialog.setProgress(values[0]);
            }
        };

        String[] statusArray = status.toArray(new String[]{});
        aTask.execute(statusArray);
    }

    /**
     * Метод для отправки файлов созданных во время выполнения операций или привязанных к операции.
     */
    private void sendFiles(List<OperationFile> files) {

        AsyncTask<OperationFile[], Void, LongSparseArray<String>> task = new AsyncTask<OperationFile[], Void, LongSparseArray<String>>() {
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
                return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
            }

            @Override
            protected LongSparseArray<String> doInBackground(OperationFile[]... lists) {
                LongSparseArray<String> idUuid = new LongSparseArray<>();

                for (OperationFile file : lists[0]) {
                    RequestBody descr = createPartFromString("Photos due execution operation.");
                    Uri uri = null;
                    try {
                        // TODO: нужно добавить полный путь до файла в каталоге Pictures !!!
                        uri = Uri.fromFile(new File(
                                getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                file.getFileName()));
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }

                    List<MultipartBody.Part> list = new ArrayList<>();
                    String fileUuid = file.getUuid();
                    String formId = "file[" + fileUuid + "]";
                    list.add(prepareFilePart(formId, uri));
                    list.add(MultipartBody.Part.createFormData(formId + "[_id]", String.valueOf(file.get_id())));
                    list.add(MultipartBody.Part.createFormData(formId + "[uuid]", file.getUuid()));
                    list.add(MultipartBody.Part.createFormData(formId + "[operationUuid]", file.getOperation().getUuid()));
                    list.add(MultipartBody.Part.createFormData(formId + "[fileName]", file.getFileName()));
                    list.add(MultipartBody.Part.createFormData(formId + "[createdAt]", String.valueOf(file.getCreatedAt())));
                    list.add(MultipartBody.Part.createFormData(formId + "[changedAt]", String.valueOf(file.getChangedAt())));
                    // запросы делаем по одному, т.к. может сложиться ситуация когда будет попытка отправить
                    // объём данных превышающий ограничения на отправку POST запросом на сервере
                    Call<ResponseBody> call = ToirAPIFactory.getOperationFileService().upload(descr, list);
                    try {
                        Response response = call.execute();
                        ResponseBody result = (ResponseBody) response.body();
                        if (response.isSuccessful()) {
                            JSONObject jObj = new JSONObject(result.string());
                            // при сохранении данных на сервере произошли ошибки
                            // данный флаг пока не используем
//                            boolean success = (boolean) jObj.get("success");
                            JSONArray data = (JSONArray) jObj.get("data");
                            for (int idx = 0; idx < data.length(); idx++) {
                                JSONObject item = (JSONObject) data.get(idx);
                                Long _id = Long.parseLong(item.get("_id").toString());
                                String uuid = item.get("uuid").toString();
                                idUuid.put(_id, uuid);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getLocalizedMessage());
                    }
                }

                return idUuid;
            }

            @Override
            protected void onPostExecute(LongSparseArray<String> idUuid) {
                super.onPostExecute(idUuid);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                for (int idx = 0; idx < idUuid.size(); idx++) {
                    long id = idUuid.keyAt(idx);
                    String uuid = idUuid.valueAt(idx);
                    OperationFile file = realm.where(OperationFile.class).equalTo("_id", id)
                            .equalTo("uuid", uuid).findFirst();
                    if (file != null) {
                        file.setSent(true);
                    }
                }

                realm.commitTransaction();
                realm.close();
            }
        };

        OperationFile[] sendFiles = files.toArray(new OperationFile[]{});
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
                processDialog.setIndeterminate(false);
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

                return true;
            }
        };
        sendTaskResultMenu.setOnMenuItemClickListener(listener);
    }

    private void sendOrders(List<Orders> orders) {
        AsyncTask<Orders[], Void, LongSparseArray<String>> task = new AsyncTask<Orders[], Void, LongSparseArray<String>>() {
            @Override
            protected LongSparseArray<String> doInBackground(Orders[]... lists) {
                List<Orders> args = Arrays.asList(lists[0]);
                LongSparseArray<String> idUuid = new LongSparseArray<>();
                Call<ResponseBody> call = ToirAPIFactory.getOrdersService().send(args);
                try {
                    Response response = call.execute();
                    ResponseBody result = (ResponseBody) response.body();
                    if (response.isSuccessful()) {
                        JSONObject jObj = new JSONObject(result.string());
                        // при сохранении данных на сервере произошли ошибки
                        // данный флаг пока не используем
//                            boolean success = (boolean) jObj.get("success");
                        JSONArray data = (JSONArray) jObj.get("data");
                        for (int idx = 0; idx < data.length(); idx++) {
                            JSONObject item = (JSONObject) data.get(idx);
                            Long _id = Long.parseLong(item.get("_id").toString());
                            String uuid = item.get("uuid").toString();
                            idUuid.append(_id, uuid);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

                return idUuid;
            }

            @Override
            protected void onPostExecute(LongSparseArray<String> idUuid) {
                super.onPostExecute(idUuid);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                for (int idx = 0; idx < idUuid.size(); idx++) {
                    long _id = idUuid.keyAt(idx);
                    String uuid = idUuid.valueAt(idx);
                    Orders value = realm.where(Orders.class).equalTo("_id", _id)
                            .equalTo("uuid", uuid)
                            .findFirst();
                    if (value != null) {
                        value.setSent(true);
                    }
                }

                realm.commitTransaction();
                realm.close();
            }
        };

        addToJournal("Отправляем выполненные наряды на сервер");
        Orders[] ordersArray = orders.toArray(new Orders[]{});
        task.execute(ordersArray);
    }

    private void sendMeasuredValues(List<MeasuredValue> values) {
        AsyncTask<MeasuredValue[], Void, LongSparseArray<String>> task = new AsyncTask<MeasuredValue[], Void, LongSparseArray<String>>() {
            @Override
            protected LongSparseArray<String> doInBackground(MeasuredValue[]... lists) {
                List<MeasuredValue> args = Arrays.asList(lists[0]);
                LongSparseArray<String> idUuid = new LongSparseArray<>();
                Call<ResponseBody> call = ToirAPIFactory.getMeasuredValueService().send(args);
                try {
                    Response response = call.execute();
                    ResponseBody result = (ResponseBody) response.body();
                    if (response.isSuccessful()) {
                        JSONObject jObj = new JSONObject(result.string());
                        // при сохранении данных на сервере произошли ошибки
                        // данный флаг пока не используем
//                            boolean success = (boolean) jObj.get("success");
                        JSONArray data = (JSONArray) jObj.get("data");
                        for (int idx = 0; idx < data.length(); idx++) {
                            JSONObject item = (JSONObject) data.get(idx);
                            Long _id = Long.parseLong(item.get("_id").toString());
                            String uuid = item.get("uuid").toString();
                            idUuid.append(_id, uuid);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    e.printStackTrace();
                }

                return idUuid;
            }

            @Override
            protected void onPostExecute(LongSparseArray<String> idUuid) {
                super.onPostExecute(idUuid);
                if (processDialog != null) {
                    processDialog.dismiss();
                }

                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                for (int idx = 0; idx < idUuid.size(); idx++) {
                    long _id = idUuid.keyAt(idx);
                    String uuid = idUuid.valueAt(idx);
                    MeasuredValue value = realm.where(MeasuredValue.class).equalTo("_id", _id)
                            .equalTo("uuid", uuid)
                            .findFirst();
                    if (value != null) {
                        value.setSent(true);
                    }
                }

                realm.commitTransaction();
                realm.close();

                Toast.makeText(getContext(), "Результаты отправлены на сервер.", Toast.LENGTH_SHORT).show();
            }
        };

        MeasuredValue[] valuesArray = values.toArray(new MeasuredValue[]{});
        task.execute(valuesArray);
    }

    /**
     * Отправка всех выполненных нарядов на сервер
     */
    private void sendCompleteTask() {
        AuthorizedUser user = AuthorizedUser.getInstance();
        RealmResults<Orders> ordersList = realmDB.where(Orders.class)
                .equalTo("user.uuid", user.getUuid())
                .equalTo("sent", false)
                .equalTo("orderStatus.uuid", OrderStatus.Status.COMPLETE).or()
                .equalTo("orderStatus.uuid", OrderStatus.Status.UN_COMPLETE).or()
                .equalTo("orderStatus.uuid", OrderStatus.Status.IN_WORK).or()
                .equalTo("orderStatus.uuid", OrderStatus.Status.CANCELED)
                .findAll();
        if (ordersList.size() == 0) {
            Toast.makeText(getActivity(), "Нет результатов для отправки.", Toast.LENGTH_SHORT).show();
            return;
        }

        // отправляем результат
        sendOrders(realmDB.copyFromRealm(ordersList));

        // получаем список всех операций
        List<String> operationUuids = new ArrayList<>();

        for (Orders order : ordersList) {
            List<Task> tasks = order.getTasks();
            for (Task task : tasks) {
                List<Stage> stages = task.getStages();
                for (Stage stage : stages) {
                    List<Operation> operations = stage.getOperations();
                    for (Operation operation : operations) {
                        operationUuids.add(operation.getUuid());
                    }
                }
            }
        }

        // предполагаем что все файлы мы сохраняем в папку приложения DIRECTORY_PICTURES
        // строим список файлов связанных с выполненными операциями
        // раньше список передавался как параметр в сервис отправки данных, сейчас пока не решено
        List<OperationFile> filesToSend = new ArrayList<>();
        RealmResults<OperationFile> operationFiles = realmDB.where(OperationFile.class)
                .in("operation.uuid", operationUuids.toArray(new String[]{}))
                .equalTo("sent", false)
                .findAll();

        for (OperationFile item : operationFiles) {
            File operationFile = new File(
                    getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    item.getFileName());
            if (operationFile.exists()) {
                filesToSend.add(realmDB.copyFromRealm(item));
            }
        }

        sendFiles(filesToSend);

        String[] opUuidsArray = operationUuids.toArray(new String[]{});

        // получаем все измерения связанные с выполненными операциями
        RealmResults<MeasuredValue> measuredValues = realmDB
                .where(MeasuredValue.class)
                .equalTo("sent", false)
                .in("operation.uuid", opUuidsArray)
                .findAll();

        sendMeasuredValues(realmDB.copyFromRealm(measuredValues));
        addToJournal("Наряды отправлены на сервер");

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
    private void closeOrderManual(final Orders order, ViewGroup parent) {
        final Spinner orderVerdictSpinner;
        // диалог для отмены операции
        final OrderStatus orderStatusUnComplete;
        final TaskStatus taskStatusUnComplete;
        final StageStatus stageStatusUnComplete;
        final OperationStatus operationStatusUnComplete;

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, parent, false);
        // список статусов нарядов в выпадающем списке для выбора
        RealmResults<OrderVerdict> orderVerdict = realmDB.where(OrderVerdict.class).findAll();
        orderVerdictSpinner = (Spinner) myView.findViewById(R.id.simple_spinner);
        OrderVerdictAdapter orderVerdictAdapter = new OrderVerdictAdapter(getContext(), orderVerdict);
        orderVerdictAdapter.notifyDataSetChanged();
        orderVerdictSpinner.setAdapter(orderVerdictAdapter);

        orderStatusUnComplete = realmDB.where(OrderStatus.class)
                .equalTo("uuid", OrderStatus.Status.UN_COMPLETE)
                .findFirst();
        stageStatusUnComplete = realmDB.where(StageStatus.class)
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
                for (final Task task : order.getTasks()) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            task.setTaskStatus(taskStatusUnComplete);
                        }
                    });
                    for (final Stage stages : task.getStages()) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                stages.setStageStatus(stageStatusUnComplete);
                            }
                        });
                        for (final Operation operation : stages.getOperations()) {
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

    public String getLastPhotoFilePath() {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        String result;
        if (cursor != null && cursor.moveToFirst()) {
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            result = cursor.getString(column_index_data);
            cursor.close();
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    // получаем штатными средствами последний снятый кадр в системе
                    String fromFilePath = getLastPhotoFilePath();
                    File fromFile = new File(fromFilePath);

                    File mediaStorageDir;
                    File picDir = getActivity().getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    if (picDir == null) {
                        return;
                    }

                    mediaStorageDir = new File(picDir.getAbsolutePath());
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.d(TAG, "Required media storage does not exist");
                            return;
                        }
                    }

                    String toFilePath = "";
                    toFilePath += mediaStorageDir.getPath();
                    toFilePath += File.separator;
                    toFilePath += currentOperationUuid;
                    toFilePath += '-';
                    toFilePath += new Date().getTime() / 1000;
                    toFilePath += fromFilePath.substring(fromFilePath.lastIndexOf('.'));
                    File toFile = new File(toFilePath);
                    try {
                        copyFile(fromFile, toFile);
                        if (!fromFile.delete()) {
                            Log.e(TAG, "Не удалось удалить исходный файл фотографии.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    Uri fileUri = Uri.fromFile(toFile);
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

                    // добавляем запись о полученном файле
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    OperationFile operationFile = new OperationFile();
                    operationFile.set_id(OperationFile.getLastId() + 1);
                    operationFile.setOperation(realm.where(Operation.class).equalTo("uuid", currentOperationUuid).findFirst());
                    operationFile.setFileName(toFilePath.substring(toFilePath.lastIndexOf('/') + 1));
                    realm.copyToRealm(operationFile);
                    realm.commitTransaction();
                    realm.close();
                }

                break;
            case ACTIVITY_MEASURE:
                if (resultCode == Activity.RESULT_OK) {
                    String value = "";
                    if (data != null) {
                        value = data.getStringExtra("value");
                    }
                    CheckBox checkBox = (CheckBox) mainListView.getChildAt(currentOperationId).findViewById(R.id.operation_status);
                    checkBox.setChecked(true);
                    CompleteCurrentOperation(currentOperationId, value);
                }

                break;

            default:
                break;
        }
    }

    /**
     * Диалог если не выполнены некоторые операции
     */
    private void setOperationsVerdict(ViewGroup parent) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        final OperationStatus operationStatusUnComplete;
        operationStatusUnComplete = realmDB.where(OperationStatus.class).findFirst();

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_dialog_cancel, parent, false);

        final ListView listView = (ListView) myView.findViewById(R.id.odc_list_view);
        CheckBox checkBoxAll = (CheckBox) myView.findViewById(R.id.odc_main_status);
        Spinner mainSpinner = (Spinner) myView.findViewById(R.id.simple_spinner);

        RealmResults<OperationVerdict> operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        final OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(getContext(), operationVerdict);
        operationVerdictAdapter.notifyDataSetChanged();
        mainSpinner.setAdapter(operationVerdictAdapter);

        fillListViewUncompleteOperations(listView);

        dialog.setView(myView);
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
//                            currentStageUuid = selectedStage.getUuid();
                            currentTaskUuid = selectedTask.getUuid();
                            fillListViewStage(selectedTask);
                            Level = STAGE_LEVEL;
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
        TextView textTime = null;
        TextView textValue;
        final long currentTime = System.currentTimeMillis();
        final OperationStatus operationStatusCompleted;
        final OperationVerdict operationVerdictCompleted;
        if (position >= operationAdapter.getCount() || currentOperationId >= operationAdapter.getCount()) {
            Log.d(TAG, "Неверный индекс операции");
        }

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
            if (mainListView.getChildAt(currentOperationId) != null) {
                textTime = (TextView) mainListView.getChildAt(currentOperationId).findViewById(R.id.op_time);
                if (textTime != null) {
                    textTime.setText(getString(R.string.sec_with_value, (int) (currentTime - startTime) / 1000));
                } else {
                    Log.d(TAG, "Операции с индексом {currentOperationId} нет в списке");
                }
            }
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

                if (textTime != null) {
                    if (((currentTime - startTime) / 1000) <= operation.getOperationTemplate().getNormative()) {
                        textTime.setBackgroundColor(Color.GREEN);
                    } else {
                        textTime.setBackgroundColor(Color.RED);
                    }
                }

                // перезапоминаем таймер
                startTime = System.currentTimeMillis();
                operationAdapter.setItemEnable(position, false);
            }
        }
    }

    private void runRfidDialog(String expectedTagId, final int level) {
//        Toast.makeText(getContext(), "Нужно поднести метку", Toast.LENGTH_LONG).show();
        final String expectedTagUuid = expectedTagId;

        currentEquipment = selectedTask.getEquipment();
        Log.d(TAG, "Ожидаемая метка: " + expectedTagId);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
//                    Bundle bundle = message.getData();
//                    String[] tagIds = bundle.getStringArray("result");
                    String[] tagIds = (String[]) message.obj;
                    if (tagIds == null) {
                        Toast.makeText(getContext(), "Не верное оборудование!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    String tagId = tagIds[0].substring(4);
                    Log.d(TAG, "Ид метки получили: " + tagId);
                    if (expectedTagUuid.equals(tagId)) {
                        boolean run_ar_content = sp.getBoolean("run_ar_content_key", false);
                        if (run_ar_content) {
                            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("ru.shtrm.toir");
                            if (intent != null) {
                                intent.putExtra("hardwareUUID", currentEquipment.getUuid());
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Приложение ТОиР не установлено", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (level == TASK_LEVEL) {
                            fillListViewStage(selectedTask);
                            Level = STAGE_LEVEL;
                        }

                        if (level == STAGE_LEVEL) {
                            fillListViewOperations(selectedStage);
                            Level = OPERATION_LEVEL;
                            startOperations();
                        }
                    } else {
                        Toast.makeText(getContext(), "Не верное оборудование!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Ошибка чтения метки!");
                    Toast.makeText(getContext(), "Ошибка чтения метки.", Toast.LENGTH_SHORT).show();
                }

                // закрываем диалог
                rfidDialog.dismiss();
                return false;
            }
        });

        rfidDialog = new RfidDialog();
        rfidDialog.setHandler(handler);
        rfidDialog.readMultiTagId(expectedTagId);
        rfidDialog.show(getActivity().getFragmentManager(), TAG);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    public void copyFile(File sourceFile, File destFile) throws IOException {

        if (!destFile.exists()) {
            if (!destFile.createNewFile()) {
                throw new IOException();
            }
        }

        FileChannel source = null;
        FileChannel destination = null;
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(sourceFile);
            os = new FileOutputStream(destFile);
            source = is.getChannel();
            destination = os.getChannel();

            long count = 0;
            long size = source.size();
            while (count < size) {
                count += destination.transferFrom(source, count, size - count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (source != null) {
                source.close();
            }
            if (is != null) {
                is.close();
            }
            if (destination != null) {
                destination.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     * Проверка на необходимость загрузки файла с сервера.
     *
     * @param obj       {@link RealmObject} Объект. Должен реализовывать {@link IToirDbObject}
     * @param localPath {@link String} Локальный путь к файлу. Относительно папки /files
     * @return boolean
     */
    private boolean isNeedDownload(RealmObject obj, String localPath) {
        Realm realm = Realm.getDefaultInstance();
        String uuid = ((IToirDbObject) obj).getUuid();
        RealmObject dbObj = realm.where(obj.getClass()).equalTo("uuid", uuid).findFirst();
        long localChangedAt;

        // есть ли локальная запись
        try {
            localChangedAt = ((IToirDbObject) dbObj).getChangedAt().getTime();
        } catch (Exception e) {
            return true;
        } finally {
            realm.close();
        }

        // есть ли локально файл
        String fileName = ((IToirDbObject) obj).getImageFile();
        if (fileName != null) {
            File file = new File(getContext().getExternalFilesDir(localPath), fileName);
            if (!file.exists()) {
                return true;
            }
        } else {
            return false;
        }

        // есть ли изменения на сервере
        return localChangedAt < ((IToirDbObject) obj).getChangedAt().getTime();
    }

    // обработчик кнопки "завершить все операции"
    private class submitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {

            if (Level == TASK_LEVEL || Level == ORDER_LEVEL) {
                initView();
            }

            if (Level == STAGE_LEVEL) {
                Level = TASK_LEVEL;
                fillListViewTasks(selectedOrder);
            }

            if (Level == OPERATION_LEVEL) {
                int completedOperationCount = 0;
                CheckBox checkBox;
                final StageStatus stageComplete;
                uncompleteOperationList.clear();
                // по умолчанию у нас все выполнено
                stageComplete = realmDB.where(StageStatus.class).equalTo("uuid", StageStatus.Status.COMPLETE).findFirst();

                if (operationAdapter != null) {
                    totalOperationCount = operationAdapter.getCount();
                }

                for (int i = 0; i < totalOperationCount; i++) {
                    checkBox = (CheckBox) getViewByPosition(i, mainListView).findViewById(R.id.operation_status);
                    final Operation operation = operationAdapter.getItem(i);
                    if (operation != null) {
                        if (checkBox != null) {
                            if (checkBox.isChecked()) {
                                completedOperationCount++;
                            } else {
                                uncompleteOperationList.add(operation);
                            }
                        }
                    } else {
                        Log.d(TAG, "Операция под индексом " + i + " не найдена");
                    }
                }

                // все операции выполнены
                if (totalOperationCount == completedOperationCount) {
                    if (selectedStage != null && !selectedStage.getStageStatus().getUuid().equals(StageStatus.Status.COMPLETE)) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                selectedStage.setStageStatus(stageComplete);
                                selectedStage.setEndDate(new Date());
                            }
                        });
                    }

                    Log.d(TAG, "Остановка таймера...");
                    taskTimer.cancel();
                    firstLaunch = true;
                    currentOperationId = 0;

                    if (selectedStage != null && selectedTask != null) {
//                        currentStageUuid = selectedStage.getUuid();
                        currentTaskUuid = selectedTask.getUuid();
                        Level = STAGE_LEVEL;
                        fillListViewStage(selectedTask);
                    }

                } else {
                    Log.d("order", "dialog");
                    setOperationsVerdict((ViewGroup) v.getParent());
                }

            }

            if (Level == TASK_LEVEL || Level == ORDER_LEVEL) {
                initView();
            }
        }
    }

    private class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View selectedItemView, int position, long id) {
            // находимся на "экране" нарядов
            if (Level == ORDER_LEVEL) {
                if (orderAdapter != null) {
                    selectedOrder = orderAdapter.getItem(position);
                    if (selectedOrder != null) {
                        // устанавливаем дату начала работы с нарядом
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        selectedOrder.setStartDate(new Date());
                        realm.commitTransaction();
//                        currentOrderUuid = selectedOrder.getUuid();
                        fillListViewTasks(selectedOrder);
                        Level = TASK_LEVEL;
                        fab_camera.setVisibility(View.INVISIBLE);
                        fab_check.setVisibility(View.INVISIBLE);
                    }
                }

                return;
            }

            // Task
            if (Level == TASK_LEVEL) {
                if (taskAdapter != null) {
                    selectedTask = taskAdapter.getItem(position);
                    if (selectedTask != null) {
                        currentEquipment = selectedTask.getEquipment();
                        final String expectedTagId = currentEquipment.getTagId();
                        boolean ask_tags = sp.getBoolean("without_tags_mode", true);
                        if (!ask_tags && !expectedTagId.equals("")) {
                            runRfidDialog(expectedTagId, TASK_LEVEL);
                        } else {
                            fillListViewStage(selectedTask);
                            Level = STAGE_LEVEL;
                        }

                        fab_camera.setVisibility(View.INVISIBLE);
                        //fab_check.setVisibility(View.INVISIBLE);
                    }
                }

                return;
            }

            // Stage
            if (Level == STAGE_LEVEL) {
                if (stageAdapter != null) {
                    selectedStage = stageAdapter.getItem(position);
                    if (selectedStage != null) {
                        final String expectedTagId;
//                        currentStageUuid = selectedStage.getUuid();
                        if (selectedStage.getEquipment() != null) {
                            expectedTagId = selectedStage.getEquipment().getTagId();
                            boolean ask_tags = sp.getBoolean("without_tags_mode", true);
                            if (!ask_tags && !expectedTagId.equals("")) {
                                runRfidDialog(expectedTagId, STAGE_LEVEL);
                            } else {
                                fillListViewOperations(selectedStage);
                                Level = OPERATION_LEVEL;
                                startOperations();
                            }
                        } else {
                            Log.d(TAG, "этапу задач не указано оборудование");
                        }
                    }
                }

                return;
            }

            // Operation
            if (Level == OPERATION_LEVEL) {
                if (operationAdapter != null) {
                    Operation selectedOperation = operationAdapter.getItem(position);
                    if (selectedOperation != null) {
                        operationAdapter.setItemVisibility(position);
                        operationAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private class onCheckBoxClickListener implements View.OnClickListener {
        int pos;

        onCheckBoxClickListener(int position) {
            this.pos = position;
        }

        @Override
        public void onClick(View arg) {
            /*if (pos != currentOperationId) {
                // показываем меню предупреждения о пропуске операций
                return;
            }*/

            if (!operationAdapter.getItemEnable(currentOperationId)) {
                return;
            }
            CompleteCurrentOperation(currentOperationId, null);
        }
    }

    private class ListViewLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            // находимся на "экране" с операциями
            if (Level == OPERATION_LEVEL) {
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
                    dialog.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            } else if (Level == ORDER_LEVEL) {
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
                            closeOrderManual(order, parent);
                        }
                    } else {
                        closeOrderManual(order, parent);
                    }
                }
            }

            return true;
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
