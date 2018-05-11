package ru.toir.mobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.MeasureActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.OperationAdapter;
import ru.toir.mobile.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.db.adapters.OrderAdapter;
import ru.toir.mobile.db.adapters.OrderVerdictAdapter;
import ru.toir.mobile.db.adapters.StageAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.MeasuredValue;
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
import ru.toir.mobile.rest.GetOrderAsyncTask;
import ru.toir.mobile.rest.SendFiles;
import ru.toir.mobile.rest.SendMeasureValues;
import ru.toir.mobile.rest.SendOrders;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.MainFunctions;

import static ru.toir.mobile.utils.MainFunctions.addToJournal;
import static ru.toir.mobile.utils.RoundedImageView.getResizedBitmap;

public class OrderFragment extends Fragment {
    private static final int ORDER_LEVEL = 0;
    private static final int TASK_LEVEL = 1;
    private static final int STAGE_LEVEL = 2;
    private static final int OPERATION_LEVEL = 3;

    private static final int ACTIVITY_PHOTO = 100;
    private static final int ACTIVITY_MEASURE = 101;
    private static final String TAG = OrderFragment.class.getSimpleName();
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
    private ListView mainListView;
    private LinearLayout listLayout;
    private BottomBar bottomBar;
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
            if (operationAdapter != null && currentOperationId < operationAdapter.getCount()) {
                currentOperation = operationAdapter.getItem(currentOperationId);
                if (currentOperation != null) {
                    // TODO: вместо прямого изменения отображения в ListView счётчик
                    // нужно отображать в отдельном View!!!
//                    if (!currentOperation.isComplete()) {
//                        TextView textTime;
//                        long currentTime = System.currentTimeMillis();
//                        textTime = getViewByPosition(currentOperationId, mainListView).findViewById(R.id.op_time);
//                        textTime.setText(getString(R.string.sec_with_value, (int) (currentTime - startTime) / 1000));
//                    }

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
                        checkBox = mainListView.getChildAt(i).findViewById(R.id.operation_status);
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
    private ListViewLongClickListener infoListViewLongClickListener = new ListViewLongClickListener();

    //private NumberPicker numberPicker;
    //private Spinner spinnerSuffix;
    //private ArrayList<OrderFragment.Suffixes> suffixList;

    private RfidDialog rfidDialog;
    private AtomicInteger taskCounter;
    private ProgressDialog dialog;
    BroadcastReceiver taskDoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive:" + intent.getAction());
            String action = intent.getAction();
            if (action == null) {
                return;
            } else if (!intent.getAction().equals("all_task_have_complete")) {
                return;
            }

            if (dialog != null) {
                dialog.dismiss();
            }

            Toast.makeText(context, "Результаты отправлены на сервер.", Toast.LENGTH_SHORT).show();
            addToJournal("Наряды отправлены на сервер");
        }
    };

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.orders_layout, container, false);
        Activity activity = getActivity();
        if (activity != null) {
            toolbar = activity.findViewById(R.id.toolbar);
            toolbar.setSubtitle("Наряды");
            sp = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            bottomBar = activity.findViewById(R.id.bottomBar);
        }

        uncompleteOperationList = new ArrayList<>();

        realmDB = Realm.getDefaultInstance();
        listLayout = rootView.findViewById(R.id.tl_listview_layout);
        fab_check = rootView.findViewById(R.id.fab_check);
        fab_camera = rootView.findViewById(R.id.fab_photo);
        fab_check.setVisibility(View.INVISIBLE);
        fab_camera.setVisibility(View.INVISIBLE);

        fab_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ACTIVITY_PHOTO);
            }
        });
        fab_check.setOnClickListener(new SubmitOnClickListener());

        mainListView = rootView.findViewById(R.id.list_view);

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
        mainListView.setOnItemLongClickListener(infoListViewLongClickListener);

        mainListView.setLongClickable(true);

        initView();
        return rootView;
    }

    private void initView() {

        Level = ORDER_LEVEL;
        if (toolbar != null) {
            toolbar.setSubtitle("Наряды");
        }

        fillListViewOrders();
    }

    // Orders----------------------------------------------------------------------------------------
    private void fillListViewOrders() {
        fillListViewOrders(null, null);
    }

    @SuppressWarnings("SameParameterValue")
    private void fillListViewOrders(String orderStatus, String orderByField) {
        AuthorizedUser authUser = AuthorizedUser.getInstance();
        Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        User user = realmDB.where(User.class)
                .equalTo("tagId", authUser.getTagId())
                .findFirst();
        if (user == null) {
            Toast.makeText(activity, "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
        } else {
            RealmQuery<Orders> query = realmDB.where(Orders.class).equalTo("user.uuid", authUser.getUuid());
            if (orderStatus != null) {
                query.equalTo("orderStatus.uuid", orderStatus);
            }

            RealmResults<Orders> orders;
            if (orderByField != null) {
                orders = query.findAllSorted(orderByField);
            } else {
                orders = query.findAllSorted("startDate", Sort.DESCENDING);
            }

            orderAdapter = new OrderAdapter(orders);
            mainListView.setAdapter(orderAdapter);
        }

        TextView tl_Header = activity.findViewById(R.id.tl_Header);
        if (tl_Header != null) {
            tl_Header.setVisibility(View.GONE);
        }

        ViewGroup.LayoutParams params = listLayout.getLayoutParams();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        listLayout.setLayoutParams(params);

        int new_orders = MainFunctions.getActiveOrdersCount();
        if (new_orders > 0 && bottomBar != null) {
            bottomBar.getTabAtPosition(1).setBadgeCount(new_orders);
        }

        fab_check.setVisibility(View.INVISIBLE);
    }

    // Task----------------------------------------------------------------------------------------
    private void fillListViewTasks(Orders order) {
        boolean all_complete = true;

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (toolbar != null) {
            toolbar.setSubtitle("Задачи");
        }

        if (order.getTasks() != null && order.getTasks().size() > 0) {
            for (Task task : order.getTasks()) {
                if (!task.isComplete()) {
                    all_complete = false;
                }
            }

            if (all_complete && !order.isComplete()) {
                final OrderStatus orderStatusComplete = realmDB.where(OrderStatus.class)
                        .equalTo("uuid", OrderStatus.Status.COMPLETE)
                        .findFirst();
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

                // отправляем сообщение о изменении статуса на сервер
                final String uuid = order.getUuid();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // отправляем запрос на установку статуса COMPLETE на сервере
                        // в случае не успеха, ни каких действий для повторной отправки
                        // не предпринимается (т.к. нет ни каких средств для фиксации этого события)
                        Call<ResponseBody> call = ToirAPIFactory.getOrdersService().setComplete(uuid);
                        try {
                            retrofit2.Response response = call.execute();
                            if (response.code() != 200) {
                                // TODO: нужно реализовать механизм повторной попытки установки статуса
                                addToJournal("Не удалось отправить запрос на установку статуса нарядов COMPLETE");
                            } else {
                                addToJournal("Успешно отправили статус для полученных нарядов COMPLETE");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            addToJournal("Исключение при запросе на установку статуса нарядов COMPLETE");
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }

            taskAdapter = new TaskAdapter(order.getTasks().sort("_id"));
            mainListView.setAdapter(taskAdapter);
            TextView tl_Header = activity.findViewById(R.id.tl_Header);
            if (tl_Header != null) {
                tl_Header.setVisibility(View.VISIBLE);
                tl_Header.setText(order.getTitle());
            }

            fab_camera.setVisibility(View.INVISIBLE);
            fab_check.setVisibility(View.VISIBLE);
        }
    }

    // Stages----------------------------------------------------------------------------------------
    private void fillListViewStage(Task task) {
        boolean all_complete = true;

        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (toolbar != null) {
            toolbar.setSubtitle("Этапы задач");
        }

        if (task.getStages() != null && task.getStages().size() > 0) {
            for (Stage stage : task.getStages()) {
                if (!stage.isComplete()) {
                    all_complete = false;
                }
            }

            stageAdapter = new StageAdapter(task.getStages().sort("_id"));
            mainListView.setAdapter(stageAdapter);
            TextView tl_Header = activity.findViewById(R.id.tl_Header);
            if (tl_Header != null) {
                tl_Header.setVisibility(View.VISIBLE);
                tl_Header.setText(task.getTaskTemplate().getTitle());
            }

            if (all_complete && !task.isComplete()) {
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
    }

    // Operations----------------------------------------------------------------------------------------
    private void fillListViewOperations(Stage stage) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (toolbar != null) {
            toolbar.setSubtitle("Операции");
        }

        if (stage.getOperations() != null && stage.getOperations().size() > 0) {
            operationAdapter = new OperationAdapter(stage.getOperations().sort("_id"));
            mainListView.setAdapter(operationAdapter);
            //resultButtonLayout.setVisibility(View.VISIBLE);
            //makePhotoButton.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = listLayout.getLayoutParams();
            params.height = 1000;
            listLayout.setLayoutParams(params);

            TextView tl_Header = activity.findViewById(R.id.tl_Header);
            if (tl_Header != null) {
                tl_Header.setVisibility(View.VISIBLE);
                tl_Header.setText(stage.getStageTemplate().getTitle());
            }

            fab_camera.setVisibility(View.VISIBLE);
            fab_check.setVisibility(View.VISIBLE);
            //mainListView.setOnItemClickListener(mainListViewClickListener);
        }

    }

    // Start Operations----------------------------------------------------------------------------------------
    void startOperations() {
        boolean isMeasure = false;
        // запрещаем все операции кроме первой не законченной
        if (operationAdapter != null) {
            totalOperationCount = operationAdapter.getCount();
            // нет решительно никакой возможности выполнять выполненные операции по сто раз,
            // только если сбросить все
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
                        if (operation.isNew() || operation.isCanceled() || operation.isUnComplete()) {
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

                        // если эта операция имеет статус любой кроме закончена или отменена,
                        // то начинаем с нее
                        if (operationStatus.isInWork() || operationStatus.isNew()
                                || operationStatus.isUnComplete()) {
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

        // фиксируем начало работы над этапом задачи (если у него статус получен),
        // меняем его статус на в процессе
        final StageStatus stageStatus;
        final StageStatus stageStatusInWork;
        if (selectedStage != null) {
            stageStatus = selectedStage.getStageStatus();
            stageStatusInWork = realmDB.where(StageStatus.class)
                    .equalTo("uuid", StageStatus.Status.IN_WORK)
                    .findFirst();
            if (stageStatus != null && stageStatusInWork != null)
                if (stageStatus.isNew() || stageStatus.isUnComplete()) {
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
//        final OrderStatus orderStatus;
//        final OrderStatus orderStatusInWork;
//        if (selectedOrder != null) {
//            orderStatus = selectedOrder.getOrderStatus();
//            orderStatusInWork = realmDB.where(OrderStatus.class)
//                    .equalTo("uuid", OrderStatus.Status.IN_WORK)
//                    .findFirst();
//            if (orderStatus != null && orderStatusInWork != null)
//                if (orderStatus.getUuid().equals(OrderStatus.Status.NEW) || orderStatus.getUuid().equals(OrderStatus.Status.UN_COMPLETE)) {
//                    realmDB.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            selectedOrder.setOpenDate(new Date());
//                            selectedOrder.setOrderStatus(orderStatusInWork);
//                        }
//                    });
//                }
//        }

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
    private void getOrdersByStatus(String status, ProgressDialog dialog) {
        List<String> list = new ArrayList<>();
        list.add(status);
        getOrdersByStatus(list, dialog);
    }

    /**
     * Получение нарядов с определённым статусом.
     *
     * @param status - статус наряда
     */
    private void getOrdersByStatus(List<String> status, ProgressDialog dialog) {
        Context context = getContext();
        if (context != null) {
            GetOrderAsyncTask aTask = new GetOrderAsyncTask(dialog, context.getExternalFilesDir(""));
            String[] statusArray = status.toArray(new String[]{});
            aTask.execute(statusArray);
        } else {
            Log.e(TAG, "Не удалось запустить задачу получения нарядов!");
        }
    }

    /**
     * Метод для отправки файлов созданных во время выполнения операций или привязанных к операции.
     */
    private void sendFiles(List<OperationFile> files) {
        Context context = getContext();
        if (context != null) {
            File extDir = context.getExternalFilesDir("");
            SendFiles task = new SendFiles(extDir, context, taskCounter);
            OperationFile[] sendFiles = files.toArray(new OperationFile[]{});
            task.execute(sendFiles);
        } else {
            Log.e(TAG, "Не удалось запустить задачу отправки фотографий!");
        }
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

                // создаём диалог
                ProgressDialog dialog;
                dialog = new ProgressDialog(getActivity());

                // запускаем поток получения новых нарядов с сервера
                getOrdersByStatus(OrderStatus.Status.NEW, dialog);

                // показываем диалог получения нарядов
                dialog.setMessage("Получаем наряды");
                dialog.setIndeterminate(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setButton(
                        DialogInterface.BUTTON_NEGATIVE, "Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Получение нарядов отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.show();
                return true;
            }
        });

        // добавляем элемент меню для получения "архивных" нарядов
        MenuItem getTaskDone = menu.add("Получить сделанные наряды");
        getTaskDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "Получаем сделанные наряды.");

                // запускаем поток получения выполненных, невыполненных, отменнённых нарядов с сервера
                List<String> stUuids = new ArrayList<>();
                stUuids.add(OrderStatus.Status.CANCELED);
                stUuids.add(OrderStatus.Status.COMPLETE);
                stUuids.add(OrderStatus.Status.UN_COMPLETE);

                // создаём диалог
                ProgressDialog dialog;
                dialog = new ProgressDialog(getActivity());

                getOrdersByStatus(stUuids, dialog);

                // показываем диалог получения наряда
                dialog.setMessage("Получаем наряды");
                dialog.setIndeterminate(true);
                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                dialog.setCancelable(false);
                dialog.setButton(
                        DialogInterface.BUTTON_NEGATIVE, "Отмена",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(), "Получение нарядов отменено",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.show();
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
        Context context = getContext();
        if (context != null) {
            SendOrders task = new SendOrders(context, taskCounter);
            addToJournal("Отправляем выполненные наряды на сервер");
            Orders[] ordersArray = orders.toArray(new Orders[]{});
            task.execute(ordersArray);
        } else {
            Log.e(TAG, "Не удалось запустить задачу отправки нарядов!");
        }
    }

    private void sendMeasuredValues(List<MeasuredValue> values) {
        Context context = getContext();
        if (context != null) {
            SendMeasureValues task = new SendMeasureValues(context, taskCounter);
            MeasuredValue[] valuesArray = values.toArray(new MeasuredValue[]{});
            task.execute(valuesArray);
        } else {
            Log.e(TAG, "Не удалось запустить задачу отправки измеренных значений!");
        }
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

        // создаём диалог
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Отправляем результаты");
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Отправка результатов отменена",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        dialog.show();

        taskCounter = new AtomicInteger(3);

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

        // строим список файлов связанных с выполненными операциями
        // раньше список передавался как параметр в сервис отправки данных, сейчас пока не решено
        List<OperationFile> filesToSend = new ArrayList<>();
        String[] opUuidsArray = operationUuids.toArray(new String[]{});
        RealmResults<OperationFile> operationFiles = realmDB.where(OperationFile.class)
                .in("operation.uuid", opUuidsArray)
                .equalTo("sent", false)
                .findAll();

        Context context = getContext();
        if (context != null) {
            for (OperationFile item : operationFiles) {
                File extDir = context.getExternalFilesDir(item.getImageFilePath());
                File operationFile = new File(extDir, item.getFileName());
                if (operationFile.exists()) {
                    filesToSend.add(realmDB.copyFromRealm(item));
                }
            }
        }

        sendFiles(filesToSend);

        // получаем все измерения связанные с выполненными операциями
        RealmResults<MeasuredValue> measuredValues = realmDB
                .where(MeasuredValue.class)
                .equalTo("sent", false)
                .in("operation.uuid", opUuidsArray)
                .findAll();

        sendMeasuredValues(realmDB.copyFromRealm(measuredValues));
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
        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }

        operationStatusUnComplete = realmDB.where(OperationStatus.class)
                .equalTo("uuid", OperationStatus.Status.UN_COMPLETE)
                .findFirst();

        // диалог для отмены операции
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = activity.getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, parent, false);
        final Spinner operationVerdictSpinner;
        // список статусов операций в выпадающем списке для выбора
        RealmResults<OperationVerdict> operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        operationVerdictSpinner = myView.findViewById(R.id.simple_spinner);
        OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(operationVerdict);
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

        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = activity.getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, parent, false);
        // список статусов нарядов в выпадающем списке для выбора
        RealmResults<OrderVerdict> orderVerdict = realmDB.where(OrderVerdict.class).findAll();
        orderVerdictSpinner = myView.findViewById(R.id.simple_spinner);
        OrderVerdictAdapter orderVerdictAdapter = new OrderVerdictAdapter(orderVerdict);
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

                final String uuid = order.getUuid();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        // отправляем запрос на установку статуса COMPLETE на сервере
                        // в случае не успеха, ни каких действий для повторной отправки
                        // не предпринимается (т.к. нет ни каких средств для фиксации этого события)
                        Call<ResponseBody> call = ToirAPIFactory.getOrdersService().setUnComplete(uuid);
                        try {
                            retrofit2.Response response = call.execute();
                            if (response.code() != 200) {
                                // TODO: нужно реализовать механизм повторной попытки установки статуса
                                addToJournal("Не удалось отправить запрос на установку статуса нарядов UN_COMPLETE");
                            } else {
                                addToJournal("Успешно отправили статус для полученных нарядов UN_COMPLETE");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            addToJournal("Исключение при запросе на установку статуса нарядов UN_COMPLETE");
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();

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
        Activity activity = getActivity();

        if (activity == null) {
            return null;
        }

        String[] projection = {
                MediaStore.Images.Media.DATA,
//                MediaStore.Images.Media._ID,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
//                MediaStore.Images.Media.DATE_TAKEN
        };
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = activity.getContentResolver();
        String orderBy = android.provider.MediaStore.Video.Media.DATE_TAKEN + " DESC";
        Cursor cursor = resolver.query(uri, projection, null, null, orderBy);
        // TODO: реализовать удаление записи о фотке котрую мы "забрали"
        //resolver.delete(uri,);
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
                    Activity activity = getActivity();
                    if (activity == null) {
                        // какое-то сообщение пользователю что не смогли "сохранить" результат
                        // фотофиксации?
                        return;
                    }

                    // получаем штатными средствами последний снятый кадр в системе
                    String fromFilePath = getLastPhotoFilePath();
                    File fromFile = new File(fromFilePath);

                    // имя файла для сохранения
                    SimpleDateFormat format = new SimpleDateFormat("HHmmss", Locale.US);
                    StringBuilder fileName = new StringBuilder();
                    fileName.append(currentOperation.getUuid());
                    fileName.append('-');
                    fileName.append(format.format(new Date()));
                    String extension = fromFilePath.substring(fromFilePath.lastIndexOf('.'));
                    fileName.append(extension);

                    // создаём объект файла фотографии для операции
                    OperationFile operationFile = new OperationFile();
                    operationFile.set_id(OperationFile.getLastId() + 1);
                    operationFile.setOperation(currentOperation);
                    operationFile.setFileName(fileName.toString());

                    File picDir = activity.getApplicationContext()
                            .getExternalFilesDir(operationFile.getImageFilePath());
                    if (picDir == null) {
                        // какое-то сообщение пользователю что не смогли "сохранить" результат
                        // фотофиксации?
                        return;
                    }

                    if (!picDir.exists()) {
                        if (!picDir.mkdirs()) {
                            Log.d(TAG, "Required media storage does not exist");
                            return;
                        }
                    }

                    File toFile = new File(picDir, operationFile.getFileName());
                    try {
                        copyFile(fromFile, toFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }

                    try {
                        getResizedBitmap(toFile.getParent(), toFile.getName(), 1024, 0, new Date().getTime());
                    } catch (Exception e) {
                        Toast.makeText(activity, "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }

                    // добавляем запись о полученном файле
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
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

                    CheckBox checkBox = mainListView.getChildAt(currentOperationId)
                            .findViewById(R.id.operation_status);
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

        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }

        operationStatusUnComplete = realmDB.where(OperationStatus.class).findFirst();

        LayoutInflater inflater = activity.getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_dialog_cancel, parent, false);

        final ListView listView = myView.findViewById(R.id.odc_list_view);
        CheckBox checkBoxAll = myView.findViewById(R.id.odc_main_status);
        Spinner mainSpinner = myView.findViewById(R.id.simple_spinner);

        RealmResults<OperationVerdict> operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        final OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(operationVerdict);
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
                            checkBox = getViewByPosition(i, listView).findViewById(R.id.operation_status);
                            spinner = getViewByPosition(i, listView).findViewById(R.id.operation_verdict_spinner);
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
                checkBoxAll = v.findViewById(R.id.odc_main_status);
                for (int i = 0; i < uncompleteOperationList.size(); i++) {
                    checkBox = getViewByPosition(i, listView).findViewById(R.id.operation_status);
                    checkBox.setChecked(checkBoxAll.isChecked());
                }
            }
        });
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Spinner spinner;
                for (int j = 0; j < uncompleteOperationList.size(); j++) {
                    spinner = getViewByPosition(j, listView).findViewById(R.id.operation_verdict_spinner);
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
        String ids[] = new String[uncompleteOperationList.size()];
        int index = 0;
        for (Operation operation : uncompleteOperationList) {
            ids[index++] = String.valueOf(operation.get_id());
        }

        operations = q.in("_id", ids, Case.SENSITIVE).findAll();
        OperationAdapter uncompleteOperationAdapter = new OperationAdapter(operations);
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
                textTime = mainListView.getChildAt(currentOperationId).findViewById(R.id.op_time);
                if (textTime != null) {
                    textTime.setText(getString(R.string.sec_with_value, (int) (currentTime - startTime) / 1000));
                } else {
                    Log.d(TAG, "Операции с индексом {currentOperationId} нет в списке");
                }
            }

            if (measureValue != null) {
                textValue = mainListView.getChildAt(currentOperationId).findViewById(R.id.op_measure_value);
                textValue.setText(measureValue);
            }

            int nextOperationId = currentOperationId + 1;
            if (nextOperationId < operationAdapter.getCount()) {
                operationAdapter.setItemEnable(nextOperationId, true);
                operationAdapter.setItemVisibility(currentOperationId,
                        !operationAdapter.getItemVisibility(currentOperationId));
                operationAdapter.setItemVisibility(nextOperationId,
                        !operationAdapter.getItemVisibility(nextOperationId));
                startTime = System.currentTimeMillis();
                currentOperationId = nextOperationId;

                // фиксируем начало работы над следующей операцией (если у нее нет статуса закончена), меняем ее статус на в процессе
                final Operation operation = operationAdapter.getItem(currentOperationId);
                final OperationStatus operationStatusInWork;
                operationStatusInWork = realmDB.where(OperationStatus.class)
                        .equalTo("uuid", OperationStatus.Status.IN_WORK)
                        .findFirst();
                if (operation != null) {
                    if (!operation.isComplete()) {
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
            if (operation != null && !operation.isComplete()) {
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
        final Activity activity = getActivity();

        if (activity == null) {
            return;
        }

        Log.d(TAG, "Ожидаемая метка: " + expectedTagId);
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == RfidDriverBase.RESULT_RFID_SUCCESS) {
                    String[] tagIds = (String[]) message.obj;
                    if (tagIds == null) {
                        Toast.makeText(getContext(), "Не верное оборудование!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    String tagId = tagIds[0].substring(4);
                    Log.d(TAG, "Ид метки получили: " + tagId);
                    if (expectedTagUuid.equals(tagId)) {
                        boolean run_ar_content = false;
                        if (sp != null) {
                            run_ar_content = sp.getBoolean("run_ar_content_key", false);
                        }

                        if (run_ar_content) {
                            Intent intent = activity.getPackageManager()
                                    .getLaunchIntentForPackage("ru.shtrm.toir");
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
        rfidDialog.show(activity.getFragmentManager(), TAG);
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

    @Override
    public void onResume() {
        super.onResume();
        // регистрация получения сообщения
        Context context = getContext();
        if (context != null) {
            LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
            mgr.registerReceiver(taskDoneReceiver, new IntentFilter("all_task_have_complete"));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // удаление приёмника
        Context context = getContext();
        if (context != null) {
            LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(context);
            mgr.unregisterReceiver(taskDoneReceiver);
        }
    }

    /**
     * Диалог с общей информацией по наряду/задаче/этапу/операции
     */
    private void showInformation(int type, long id, AdapterView parent) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = activity.getLayoutInflater();
        TextView level, status, title, reason, author, worker, recieve, start, open, close, comment, verdict;
        View myView = inflater.inflate(R.layout.order_full_information, parent, false);
        DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
            @Override
            public String[] getMonths() {
                return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
                        "июля", "августа", "сентября", "октября", "ноября", "декабря"};
            }
        };
        String sDate = "неизвестно";
        if (type == ORDER_LEVEL) {
            myView = inflater.inflate(R.layout.order_full_information, parent, false);
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

            Orders order = realmDB.where(Orders.class).equalTo("_id", id).findFirst();
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
                title.setText(getString(R.string.order_title, order.get_id(), order.getTitle()));
                reason.setText(getString(R.string.order_reason, order.getReason()));
                author.setText(getString(R.string.order_author, order.getAuthor().getName()));
                worker.setText(getString(R.string.order_worker, order.getUser().getName()));
                if (order.getStartDate().getTime() > 10000) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(order.getStartDate());
                } else {
                    sDate = "не назначен";
                }

                start.setText(getString(R.string.order_start, sDate));
                if (order.getReceivDate().getTime() > 10000) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(order.getReceivDate());
                } else {
                    sDate = "не получен";
                }

                recieve.setText(getString(R.string.order_recieved, sDate));
                if (order.getOpenDate().getTime() > 10000) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(order.getOpenDate());
                } else {
                    sDate = "не начат";
                }

                open.setText(getString(R.string.order_open, sDate));
                if (order.getCloseDate().getTime() > 10000) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(order.getCloseDate());
                } else {
                    sDate = "не закрыт";
                }

                close.setText(getString(R.string.order_close, sDate));
                comment.setText(getString(R.string.order_comment, order.getComment()));
                verdict.setText(getString(R.string.order_verdict, order.getOrderVerdict().getTitle()));
            }
        }

        dialog.setView(myView);
        dialog.show();
    }

    // обработчик кнопки "завершить все операции"
    private class SubmitOnClickListener implements View.OnClickListener {
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
                    checkBox = getViewByPosition(i, mainListView).findViewById(R.id.operation_status);
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
                    if (selectedStage != null && !selectedStage.isComplete()) {
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
                        fillListViewStage(selectedTask);
                        Level = STAGE_LEVEL;
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
                        currentEquipment = selectedStage.getEquipment();
                        expectedTagId = currentEquipment.getTagId();
                        boolean ask_tags = sp.getBoolean("without_tags_mode", true);
                        if (!ask_tags && !expectedTagId.equals("")) {
                            runRfidDialog(expectedTagId, STAGE_LEVEL);
                        } else {
                            fillListViewOperations(selectedStage);
                            Level = OPERATION_LEVEL;
                            startOperations();
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
                        operationAdapter.setItemVisibility(position,
                                !operationAdapter.getItemVisibility(position));
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

            if (!operationAdapter.isItemEnabled(currentOperationId)) {
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

                showInformation(ORDER_LEVEL, order.get_id(), parent);
                // проверяем статус наряда
                if (false && order != null) {
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
}
