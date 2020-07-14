package ru.toir.mobile.multi.fragments;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.support.v4.content.FileProvider;
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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.MeasureActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.ToirApplication;
import ru.toir.mobile.multi.db.adapters.InstructionAdapter;
import ru.toir.mobile.multi.db.adapters.OperationAdapter;
import ru.toir.mobile.multi.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.multi.db.adapters.OrderAdapter;
import ru.toir.mobile.multi.db.adapters.OrderVerdictAdapter;
import ru.toir.mobile.multi.db.adapters.StageAdapter;
import ru.toir.mobile.multi.db.adapters.StageVerdictAdapter;
import ru.toir.mobile.multi.db.adapters.TaskAdapter;
import ru.toir.mobile.multi.db.realm.Equipment;
import ru.toir.mobile.multi.db.realm.Instruction;
import ru.toir.mobile.multi.db.realm.InstructionStageTemplate;
import ru.toir.mobile.multi.db.realm.MeasuredValue;
import ru.toir.mobile.multi.db.realm.MediaFile;
import ru.toir.mobile.multi.db.realm.Operation;
import ru.toir.mobile.multi.db.realm.OperationStatus;
import ru.toir.mobile.multi.db.realm.OperationType;
import ru.toir.mobile.multi.db.realm.OperationVerdict;
import ru.toir.mobile.multi.db.realm.OrderStatus;
import ru.toir.mobile.multi.db.realm.OrderVerdict;
import ru.toir.mobile.multi.db.realm.Orders;
import ru.toir.mobile.multi.db.realm.Stage;
import ru.toir.mobile.multi.db.realm.StageStatus;
import ru.toir.mobile.multi.db.realm.StageVerdict;
import ru.toir.mobile.multi.db.realm.Task;
import ru.toir.mobile.multi.db.realm.TaskStatus;
import ru.toir.mobile.multi.db.realm.TaskTemplate;
import ru.toir.mobile.multi.db.realm.TaskVerdict;
import ru.toir.mobile.multi.db.realm.User;
import ru.toir.mobile.multi.rest.GetOrderAsyncTask;
import ru.toir.mobile.multi.rest.SendFiles;
import ru.toir.mobile.multi.rest.SendMeasureValues;
import ru.toir.mobile.multi.rest.SendMessages;
import ru.toir.mobile.multi.rest.SendOrders;
import ru.toir.mobile.multi.rest.ToirAPIFactory;
import ru.toir.mobile.multi.rfid.RfidDialog;
import ru.toir.mobile.multi.rfid.RfidDriverBase;
import ru.toir.mobile.multi.utils.MainFunctions;

import static ru.toir.mobile.multi.EquipmentInfoActivity.showDialogDefect2;
import static ru.toir.mobile.multi.utils.MainFunctions.addToJournal;
import static ru.toir.mobile.multi.utils.RoundedImageView.getResizedBitmap;

public class OrderFragment extends Fragment implements OrderAdapter.EventListener, StageAdapter.EventListener, OperationAdapter.EventListener {
    private static final int ORDER_LEVEL = 0;
    private static final int TASK_LEVEL = 1;
    private static final int STAGE_LEVEL = 2;
    private static final int OPERATION_LEVEL = 3;

    private static final int ACTIVITY_PHOTO = 100;
    private static final int ACTIVITY_MEASURE = 101;
    private static final int EXTERNAL_STORAGE_ACCESS = 102;
    private static final String TAG = OrderFragment.class.getSimpleName();
    FloatingActionButton fab_check;
    FloatingActionButton fab_question;
    FloatingActionButton fab_camera;
    FloatingActionButton fab_defect;
    FloatingActionButton fab_equipmentInfo;
    OnCheckBoxClickListener gCBlistener = new OnCheckBoxClickListener();
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
    private int currentOperationPosition = 0;
    private long startTime = 0;
    private boolean firstLaunch = true;

    CountDownTimer taskTimer = new CountDownTimer(1000000000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "Тик таймера...");
            if (operationAdapter != null && currentOperationPosition < operationAdapter.getCount()) {
                currentOperation = operationAdapter.getItem(currentOperationPosition);
                if (currentOperation != null && !currentOperation.isComplete()) {
                    TextView textTime;
                    long currentTime = System.currentTimeMillis();
                    textTime = getViewByPosition(currentOperationPosition, mainListView).findViewById(R.id.op_time);
                    if (textTime != null) {
                        textTime.setText(getString(R.string.sec_with_value,
                                (int) (currentTime - startTime) / 1000));
                    }

                    if (firstLaunch) {
                        firstLaunch();
                    }
                }
            }
        }

        void firstLaunch() {
            Log.d(TAG, "Инициализация вьюх для отображения секунд...");
            firstLaunch = false;
        }

        @Override
        public void onFinish() {
        }
    };
    // свойство для хранения uuid сущности к которой мы привяжем медиа файл
    private String currentEntityUuid;
    private String photoFilePath;
    private SharedPreferences sp;
    private ListViewClickListener mainListViewClickListener = new ListViewClickListener();
    //private ListViewLongClickListener infoListViewLongClickListener = new ListViewLongClickListener();

    private RfidDialog rfidDialog;
    private AtomicInteger taskCounter;
    private AtomicInteger messageCounter;
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

    public static void copyFile(File sourceFile, File destFile) throws IOException {

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
    public void onDestroy() {
        if (taskTimer != null) {
            taskTimer.cancel();
            taskTimer = null;
        }
        super.onDestroy();
    }

    private void initView() {

        Level = ORDER_LEVEL;
        if (toolbar != null) {
            toolbar.setSubtitle(getString(R.string.menu_orders));
        }

        fillListViewOrders();
    }

    /**
     * Метод для заполнения списка всеми нарядами из базы.
     */
    private void fillListViewOrders() {
        fillListViewOrders(null, null);
    }

    /**
     * Метод для заполнения списка нарядами с указанным статусом и указанным полем для сортировки.
     */
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
                orders = query.sort(orderByField).findAll();
            } else {
                orders = query.sort("startDate", Sort.DESCENDING).findAll();
            }

            orderAdapter = new OrderAdapter(orders, getContext(), this);
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

        fab_defect.setVisibility(View.INVISIBLE);
        fab_camera.setVisibility(View.INVISIBLE);
        fab_equipmentInfo.setVisibility(View.INVISIBLE);
        fab_check.setVisibility(View.INVISIBLE);
        fab_question.setVisibility(View.INVISIBLE);
    }

    /**
     * Метод для заполнения списка задачами из указанного наряда.
     *
     * @param order Наряд, задачами из которого нужно заполнить список.
     */
    private void fillListViewTasks(Orders order) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        if (toolbar != null) {
            toolbar.setSubtitle(getString(R.string.menu_tasks));
        }

        if (order.getTasks() != null && order.getTasks().size() > 0) {
            taskAdapter = new TaskAdapter(order.getTasks().sort("_id"));
            mainListView.setAdapter(taskAdapter);
            TextView tl_Header = activity.findViewById(R.id.tl_Header);
            if (tl_Header != null) {
                tl_Header.setVisibility(View.VISIBLE);
                tl_Header.setText(order.getTitle());
            }

            fab_defect.setVisibility(View.INVISIBLE);
            fab_camera.setVisibility(View.INVISIBLE);
            fab_equipmentInfo.setVisibility(View.INVISIBLE);
            fab_check.setVisibility(View.VISIBLE);
            fab_question.setVisibility(View.VISIBLE);
        }
    }

    // Stages----------------------------------------------------------------------------------------
    private int fillListViewStage(Task task) {
        Activity activity = getActivity();
        if (activity == null) {
            return 0;
        }

        if (task.getStages() != null && task.getStages().size() > 0) {
            if (toolbar != null) {
                toolbar.setSubtitle(getString(R.string.menu_tasks));
            }
            stageAdapter = new StageAdapter(task.getStages().sort("_id"), getContext(), this);
            mainListView.setAdapter(stageAdapter);
            TextView tl_Header = activity.findViewById(R.id.tl_Header);
            if (tl_Header != null) {
                tl_Header.setVisibility(View.VISIBLE);
                tl_Header.setText(selectedOrder.getTitle());
            }

            ViewGroup.LayoutParams params = listLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            listLayout.setLayoutParams(params);

            fab_defect.setVisibility(View.INVISIBLE);
            fab_camera.setVisibility(View.INVISIBLE);
            fab_equipmentInfo.setVisibility(View.INVISIBLE);
            fab_check.setVisibility(View.VISIBLE);
            fab_question.setVisibility(View.INVISIBLE);
            return task.getStages().size();
        } else {
            Level = ORDER_LEVEL;
            return 0;
            //initView();
        }
    }

    // Operations----------------------------------------------------------------------------------------
    private void fillListViewOperations(Stage stage) {
        Activity activity = getActivity();

        firstLaunch = true;

        if (activity == null) {
            return;
        }

        if (toolbar != null) {
            toolbar.setSubtitle(getString(R.string.menu_operations));
        }

        if (stage.getOperations() != null && stage.getOperations().size() > 0) {
            operationAdapter = new OperationAdapter(stage.getOperations().sort("_id"), getContext(), this);
            operationAdapter.setGBListener(gCBlistener);
            currentOperation = operationAdapter.getItem(0);
            mainListView.setAdapter(operationAdapter);
            //resultButtonLayout.setVisibility(View.VISIBLE);
            //makePhotoButton.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams params = listLayout.getLayoutParams();
            params.height = 1000;
            listLayout.setLayoutParams(params);

            TextView tl_Header = activity.findViewById(R.id.tl_Header);
            if (tl_Header != null) {
                tl_Header.setVisibility(View.VISIBLE);
                tl_Header.setText(selectedOrder.getTitle()
                        .concat(" : ")
                        .concat(stage.getStageTemplate().getTitle()));
            }

            fab_defect.setVisibility(View.VISIBLE);
            fab_camera.setVisibility(View.VISIBLE);
            fab_equipmentInfo.setVisibility(View.VISIBLE);
            fab_check.setVisibility(View.VISIBLE);
            fab_question.setVisibility(View.INVISIBLE);
            //mainListView.setOnItemClickListener(mainListViewClickListener);
        } else {
            Level = STAGE_LEVEL;
        }
    }

    // Start Operations----------------------------------------------------------------------------------------
    void startOperations() {
        if (operationAdapter == null) {
            // здесь видимо нужно какое-то уведомление пользователю, либо попытка создать новый адаптер
            return;
        }

        int totalOperationCount = operationAdapter.getCount();
        // нет решительно никакой возможности выполнять выполненные операции по сто раз,
        // только если сбросить все
        // запрещаем все операции кроме первой не законченной
        for (int i = 0; i < totalOperationCount; i++) {
            final Operation operation = operationAdapter.getItem(i);
            if (operation != null) {
                if (operation.getOperationStatus() != null) {
                    // фиксируем начало работы над первой операцией (статус "новая"),
                    // меняем ее статус на в процессе
//                    if (operation.isNew() || operation.isCanceled() || operation.isUnComplete()) {
                    if (operation.isNew()) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                operation.setStartDate(new Date());
                                operation.setOperationStatus(OperationStatus.getObjectInWork(realm));
                            }
                        });
                    }

                    // если эта операция имеет статус "в работе", то начинаем с нее
                    if (operation.isInWork()) {
                        operationAdapter.setItemEnable(i, true);
                        currentOperationPosition = i;
                        currentOperation = operationAdapter.getItem(currentOperationPosition);

                        // время начала работы (приступаем к первой операции и нехрен тормозить)
                        startTime = System.currentTimeMillis();
                        if (currentOperation != null && currentOperation.getStartDate() != null) {
                            startTime = currentOperation.getStartDate().getTime();
                        }
                        Log.d(TAG, "Запуск таймера...");
                        taskTimer.start();

                        OperationType operationType = operation.getOperationTemplate().getOperationType();
                        if (operationType.isMeasure()) {
                            startMeasure();
                        }

                        break;
                    } else {
                        operationAdapter.setItemEnable(i, false);
                    }
                } else {
                    Log.d(TAG, "Найдена операция с неинициализированным статусом");
                }
            }
        }

        // фиксируем начало работы над этапом задачи, меняем его статус на "в работе"
        if (selectedStage != null) {
            StageStatus stageStatus = selectedStage.getStageStatus();
            if (stageStatus != null && stageStatus.isNew()) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        selectedStage.setStartDate(new Date());
                        selectedStage.setStageStatus(StageStatus.getObjectInWork(realm));
                    }
                });
            }
        }

        // фиксируем начало работы над задачей, меняем ее статус на "в работе"
        if (selectedTask != null) {
            TaskStatus taskStatus = selectedTask.getTaskStatus();
            if (taskStatus != null && taskStatus.isNew()) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        selectedTask.setStartDate(new Date());
                        selectedTask.setTaskStatus(TaskStatus.getObjectInWork(realm));
                    }
                });
            }
        }

        // фиксируем начало работы над нарядом (если у него статус получен), меняем его статус на в процессе
        if (selectedOrder != null) {
            if (selectedOrder.isInWork() || selectedOrder.isUnComplete()) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (selectedOrder.getOpenDate() == null) {
                            selectedOrder.setOpenDate(new Date());
                        }
//                        selectedOrder.setOrderStatus(OrderStatus.getObjectInWork(realm));
                    }
                });
            }
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
     * Метод для отправки медиа файлов
     */
    private void sendFiles(List<MediaFile> files) {
        Context context = getContext();
        if (context != null) {
            File extDir = context.getExternalFilesDir("");
            SendFiles task = new SendFiles(extDir, context, taskCounter);
            MediaFile[] sendFiles = files.toArray(new MediaFile[]{});
            task.execute(sendFiles);
        } else {
            Log.e(TAG, "Не удалось запустить задачу отправки медиа!");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // добавляем элемент меню для получения новых нарядов
        MenuItem getTaskNew = menu.add(getString(R.string.receive_new_orders));
        getTaskNew.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "Получаем новые наряды.");

                if (!ToirApplication.isInternetOn(getContext())) {
                    Toast.makeText(getContext(), "Нет соединения с сетью", Toast.LENGTH_LONG).show();
                    return true;
                }

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
        MenuItem getTaskDone = menu.add(getString(R.string.receive_completed_orders));
        getTaskDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "Получаем сделанные наряды.");

                if (!ToirApplication.isInternetOn(getContext())) {
                    Toast.makeText(getContext(), "Нет соединения с сетью", Toast.LENGTH_LONG).show();
                    return true;
                }

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
        MenuItem sendTaskResultMenu = menu.add(getString(R.string.send_results));
        MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (!ToirApplication.isInternetOn(getContext())) {
                    Toast.makeText(getContext(), "Нет соединения с сетью", Toast.LENGTH_LONG).show();
                    return true;
                }

                // проверяем наличие не законченных нарядов
                RealmResults<Orders> ordersInWork = realmDB.where(Orders.class)
                        .equalTo("orderStatus.uuid", OrderStatus.Status.IN_WORK)
                        .findAll();
                int inWorkCount = ordersInWork.size();
                if (inWorkCount > 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

                    dialog.setTitle(getString(R.string.warning));
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

    private void sendMessages(List<ru.toir.mobile.multi.db.realm.Message> values) {
        Context context = getContext();
        if (context != null) {
            SendMessages task = new SendMessages(context, messageCounter);
            ru.toir.mobile.multi.db.realm.Message[] valuesArray = values.toArray(new ru.toir.mobile.multi.db.realm.Message[]{});
            task.execute(valuesArray);
        } else {
            Log.e(TAG, "Не удалось запустить задачу отправки сообщений!");
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
        messageCounter = new AtomicInteger(3);

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

        // строим список (всех) неотправленых медиа файлов
        // раньше список передавался как параметр в сервис отправки данных, сейчас пока не решено
        List<MediaFile> filesToSend = new ArrayList<>();
        RealmResults<MediaFile> mediaFiles = realmDB.where(MediaFile.class)
                .equalTo("sent", false)
                .findAll();

        Context context = getContext();
        if (context != null) {
            for (MediaFile item : mediaFiles) {
                File extDir = context.getExternalFilesDir(item.getPath());
                File mediaFile = new File(extDir, item.getName());
                if (mediaFile.exists()) {
                    filesToSend.add(realmDB.copyFromRealm(item));
                }
            }
        }

        sendFiles(filesToSend);

        // получаем все измерения связанные с выполненными операциями
        String[] opUuidsArray = operationUuids.toArray(new String[]{});
        RealmResults<MeasuredValue> measuredValues = realmDB
                .where(MeasuredValue.class)
                .equalTo("sent", false)
                .in("operation.uuid", opUuidsArray)
                .findAll();

        sendMeasuredValues(realmDB.copyFromRealm(measuredValues));

        // отправляем сообшения
        RealmResults<ru.toir.mobile.multi.db.realm.Message> messages = realmDB
                .where(ru.toir.mobile.multi.db.realm.Message.class)
                .equalTo("sent", false)
                .findAll();
        sendMessages(realmDB.copyFromRealm(messages));
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
    public void closeOperationManual(final Operation operation, AdapterView<?> parent, int position) {

        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }

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
        dialog.setTitle(getString(R.string.operation_cancel));

        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final OperationVerdict verdict = (OperationVerdict) operationVerdictSpinner.getSelectedItem();
                // выставляем выбранный вердикт
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        operation.setOperationStatus(OperationStatus.getObjectUnComplete(realm));
                        operation.setOperationVerdict(verdict);
                        if (operation.getStartDate() == null) {
                            operation.setStartDate(new Date());
                        }
                        operation.setEndDate(new Date());
                        operationAdapter.setItemEnable(position, false);
                        operationAdapter.notifyDataSetChanged();
                    }
                });

                // переходим на следующую операцию
                if (goToNextOperation()) {
                    if (isAllOperationComplete(selectedStage)) {
                        closeLevel(OPERATION_LEVEL, false);
                        fillListView(ORDER_LEVEL);
                    }
                }

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
     * Диалог вопроса начала работы
     */
    private void askStartOperations() {
        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }
        if (!selectedStage.isNew()) {
            startOperations();
            return;
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.start_operations, null);
        final CheckBox mCheckBox = mView.findViewById(R.id.checkBox);
        ListView list = mView.findViewById(R.id.instruction_listView);
        dialog.setTitle(R.string.dialog_start_title);
        dialog.setPositiveButton(R.string.dialog_start,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startOperations();
                    }
                });
        dialog.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //stopOperations();
                    }
                });
        dialog.setView(mView);

        RealmResults<InstructionStageTemplate> ist = realmDB.where(InstructionStageTemplate.class)
                .equalTo("stageTemplate.uuid", selectedStage.getStageTemplate().getUuid())
                .findAll();
        List<String> stageTemplatesUuids = new ArrayList<>();
        for (InstructionStageTemplate instructionStageTemplate : ist) {
            stageTemplatesUuids.add(instructionStageTemplate.getInstruction().getUuid());
        }
        RealmResults<Instruction> instructions = realmDB.where(Instruction.class)
                .in("uuid", stageTemplatesUuids.toArray(new String[]{}))
                .findAll();

        InstructionAdapter instructionAdapter = new InstructionAdapter(instructions);
        list.setAdapter(instructionAdapter);
        AlertDialog dlg = dialog.create();
        dlg.show();
        final Button button = dlg.getButton(AlertDialog.BUTTON_POSITIVE);
        if (ist.size() == 0) {
            mCheckBox.setVisibility(View.GONE);
        } else {
            button.setEnabled(false);
        }
        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBox.isChecked()) {
                    button.setEnabled(true);
                } else {
                    button.setEnabled(false);
                }
            }
        });
    }

    /**
     * Диалог изменения статуса этапа
     *
     * @param stage - этап для отмены
     */
    public void closeStageManual(final Stage stage, AdapterView<?> parent) {
        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }

        // диалог для отмены этапа
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = activity.getLayoutInflater();
        View myView = inflater.inflate(R.layout.operation_cancel_dialog, parent, false);
        final Spinner verdictSpinner;
        final Realm realmDB = Realm.getDefaultInstance();
        // список вердиктов в выпадающем списке для выбора
        RealmResults<StageVerdict> stageVerdicts = realmDB.where(StageVerdict.class)
                // TODO: завести типы? сделать отдельный метод для выбрки вердиктов по типу этапа + "общие"?
                //.equalTo("stageType.uuid", "7F7458E4-D3FE-4862-AEFF-1F6FD7D68B43").or()
                //.equalTo("stageType.uuid", "4CC86857-9D7D-4EE2-A838-65CCC62FDD6E").or()
                //.equalTo("stageType.uuid", stage.getStageTemplate().getStageType().getUuid())
                .findAll();
        verdictSpinner = myView.findViewById(R.id.simple_spinner);
        StageVerdictAdapter stageVerdictAdapter = new StageVerdictAdapter(stageVerdicts);
        stageVerdictAdapter.notifyDataSetChanged();
        verdictSpinner.setAdapter(stageVerdictAdapter);

        dialog.setView(myView);
        dialog.setTitle("Отмена задачи");
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                final StageVerdict verdict = (StageVerdict) verdictSpinner.getSelectedItem();
                // выставляем выбранный статус
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        stage.setStageStatus(StageStatus.getObjectUnComplete(realm));
                        stage.setStageVerdict(verdict);
                        stage.setStartDate(new Date());
                        stage.setEndDate(new Date());
                        if (selectedOrder.getOpenDate() == null) {
                            selectedOrder.setOpenDate(new Date());
                        }
                    }
                });

                List<Operation> operations = getUncompleteOperations(stage);
                OperationStatus operationStatus = OperationStatus.getObjectUnComplete(realmDB);
                OperationVerdict operationVerdict = OperationVerdict.getObjectCanceled(realmDB);
                realmDB.beginTransaction();
                for (Operation operation : operations) {
                    operation.setOperationStatus(operationStatus);
                    operation.setOperationVerdict(operationVerdict);
                    if (operation.getStartDate() == null) {
                        operation.setStartDate(new Date());
                    }
                    operation.setEndDate(new Date());
                }

                realmDB.commitTransaction();
                // TODO здесь должен быть алгоритм выставления вердикта наряда в зависимости от вердикта этапа
                closeLevel(STAGE_LEVEL, false);
                if (isAllStageComplete(selectedTask)) {
                    OrderStatus orderStatusComplete = OrderStatus.getObjectComplete(realmDB);
                    realmDB.beginTransaction();
                    selectedOrder.setOrderStatus(orderStatusComplete);
                    realmDB.commitTransaction();
                    // если хотя бы одна задача не выполнена
                    final TaskVerdict taskVerdict = TaskVerdict.getObjectUnComplete(realmDB);
                    if (taskVerdict != null) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                selectedTask.setTaskVerdict(taskVerdict);
                            }
                        });
                    }
                    if (isAllTaskComplete()) {

                    }
                }

                // закрываем диалог
                //dialog.dismiss();
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
        realmDB.close();
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

        orderStatusUnComplete = OrderStatus.getObjectUnComplete(realmDB);
        stageStatusUnComplete = StageStatus.getObjectUnComplete(realmDB);
        operationStatusUnComplete = OperationStatus.getObjectUnComplete(realmDB);
        taskStatusUnComplete = TaskStatus.getObjectUnComplete(realmDB);

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
                                    if (operation.getStartDate() == null) {
                                        operation.setStartDate(new Date());
                                    }

                                    operation.setEndDate(new Date());
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

    private File createImageFile() throws IOException {
        Context context = getContext();
        if (context == null) {
            return null;
        }

        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    public String getLastPhotoFilePath() {
        String result;
        Activity activity = getActivity();

        if (activity == null) {
            return null;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
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

            if (cursor != null && cursor.moveToFirst()) {
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                result = cursor.getString(column_index_data);
                cursor.close();
            } else {
                result = null;
            }
        } else {
            result = photoFilePath;
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
                    String fromFilePath = photoFilePath;
                    File fromFile = new File(fromFilePath);

                    // имя файла для сохранения
                    SimpleDateFormat format = new SimpleDateFormat("HHmmss", Locale.US);
                    StringBuilder fileName = new StringBuilder();
                    fileName.append(currentEntityUuid);
                    fileName.append('-');
                    fileName.append(format.format(new Date()));
                    String extension = fromFilePath.substring(fromFilePath.lastIndexOf('.'));
                    fileName.append(extension);

                    // создаём объект файла фотографии для операции
                    MediaFile mediaFile = new MediaFile();
                    mediaFile.set_id(MediaFile.getLastId() + 1);
                    mediaFile.setEntityUuid(currentEntityUuid);
                    format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    mediaFile.setPath(MediaFile.getImageRoot() + "/" + format.format(mediaFile.getCreatedAt()));
                    mediaFile.setName(fileName.toString());
                    File picDir = activity.getApplicationContext()
                            .getExternalFilesDir(mediaFile.getPath());
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

                    File toFile = new File(picDir, mediaFile.getName());
                    try {
                        copyFile(fromFile, toFile);
                        if (!fromFile.delete()) {
                            Log.d(TAG, "Не удалось удалить исходный файл");
                        }
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
                    realm.copyToRealm(mediaFile);
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

                    CheckBox checkBox = mainListView.getChildAt(currentOperationPosition)
                            .findViewById(R.id.operation_status);
                    checkBox.setChecked(true);
                    CompleteCurrentOperation(value);
                }

                break;
            case EXTERNAL_STORAGE_ACCESS:
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(getContext(), "Без доступа к носителю, невозможно сохранить файлы.", Toast.LENGTH_LONG).show();
                }

                break;

            default:
                break;
        }
    }

    /**
     * Построение диалога если не выполнены некоторые операции
     *
     * @param uncompleteOperationList Список не выполненных операций
     */
    private void setOperationsVerdict(final List<Operation> uncompleteOperationList) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }

        LayoutInflater inflater = activity.getLayoutInflater();
        final View myView = inflater.inflate(R.layout.operation_dialog_cancel, null, false);
        final Spinner mainSpinner = myView.findViewById(R.id.simple_spinner);

        RealmResults<OperationVerdict> operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        final OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(operationVerdict);
        operationVerdictAdapter.notifyDataSetChanged();
        mainSpinner.setAdapter(operationVerdictAdapter);

        dialog.setTitle(R.string.select_verdict);
        dialog.setView(myView);
        dialog.setPositiveButton(R.string.dialog_accept,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < uncompleteOperationList.size(); i++) {
                            final OperationVerdict operationVerdict = operationVerdictAdapter.getItem(mainSpinner.getSelectedItemPosition());
                            final Operation operation = uncompleteOperationList.get(i);
                            if (operation != null) {
                                realmDB.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // TODO Если будем переделывать логику, то здесь меням статус обратно на "не выполнена"
                                        operation.setOperationStatus(
                                                OperationStatus.getObjectUnComplete(realm));
                                        operation.setOperationVerdict(operationVerdict);
                                        if (operation.getStartDate() == null) {
                                            operation.setStartDate(new Date());
                                        }
                                        operation.setEndDate(new Date());
                                    }
                                });
                            }
                        }

                        // устанавливаем статус для текущего этапа - "выполнен"
                        if (selectedStage != null && !selectedStage.isComplete()) {
                            realmDB.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    selectedStage.setStageStatus(StageStatus.getObjectUnComplete(realm));
                                    selectedStage.setStageVerdict(StageVerdict.getObjectUnComplete(realm));
                                    if (selectedStage.getStartDate() == null) {
                                        selectedStage.setStartDate(new Date());
                                    }
                                    selectedStage.setEndDate(new Date());
                                }
                            });
                        }

                        Log.d(TAG, "Остановка таймера...");
                        taskTimer.cancel();
                        firstLaunch = true;
                        currentOperationPosition = 0;
                        Level = closeLevel(OPERATION_LEVEL, false);
                        fillListView(Level);
                    }
                });

        dialog.setNegativeButton(R.string.dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    /**
     * Построение диалога если не выполнены некоторые этапы
     *
     * @param uncompleteStageList Список не выполненных этапов
     */
    private void setStagesVerdict(final List<Stage> uncompleteStageList) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        Activity activity = getActivity();
        if (activity == null) {
            // какое-то сообщение пользователю что не смогли показать диалог?
            return;
        }
        LayoutInflater inflater = activity.getLayoutInflater();
        final View myView = inflater.inflate(R.layout.operation_dialog_cancel, null, false);
        TextView description = myView.findViewWithTag("description");
        description.setText(getString(R.string.stage_cancel_text));
        final Spinner mainSpinner = myView.findViewById(R.id.simple_spinner);
        // пока не будет универсальных вердиктов - показываем все
        RealmResults<StageVerdict> stageVerdicts = realmDB.where(StageVerdict.class)
                //.equalTo("stageType.uuid", "7F7458E4-D3FE-4862-AEFF-1F6FD7D68B43").or()
                //.equalTo("stageType.uuid", "4CC86857-9D7D-4EE2-A838-65CCC62FDD6E")
                .findAll();
        final StageVerdictAdapter stageVerdictAdapter = new StageVerdictAdapter(stageVerdicts);
        stageVerdictAdapter.notifyDataSetChanged();
        mainSpinner.setAdapter(stageVerdictAdapter);

        dialog.setTitle(R.string.select_verdict);
        dialog.setView(myView);
        dialog.setPositiveButton(R.string.dialog_accept,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < uncompleteStageList.size(); i++) {
                            final StageVerdict stageVerdict = stageVerdictAdapter.getItem(mainSpinner.getSelectedItemPosition());
                            final Stage stage = uncompleteStageList.get(i);
                            if (stage != null) {
                                realmDB.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        // был статус "не выполнен"
                                        stage.setStageStatus(
                                                StageStatus.getObjectUnComplete(realm));
                                        stage.setStageVerdict(stageVerdict);
                                        if (stage.getStartDate() == null) {
                                            stage.setStartDate(new Date());
                                        }
                                        stage.setEndDate(new Date());
                                        if (selectedOrder.getOpenDate() == null) {
                                            selectedOrder.setOpenDate(new Date());
                                        }

                                        OperationStatus operationStatus = OperationStatus.getObjectUnComplete(realm);
                                        OperationVerdict operationVerdict = OperationVerdict.getObjectCanceled(realm);
                                        List<Operation> operations = getUncompleteOperations(stage);
                                        for (Operation operation : operations) {
                                            // по тому, что установлена дата окончания мы понимаем, что ее уже завершили
                                            if (operation.getEndDate() == null) {
                                                operation.setOperationStatus(operationStatus);
                                                operation.setOperationVerdict(operationVerdict);
                                                operation.setEndDate(new Date());
                                            }
                                            if (operation.getStartDate() == null) {
                                                operation.setStartDate(new Date());
                                            }
                                        }
                                    }
                                });
                            }
                        }

                        // устанавливаем статус для текущей задачи - выполнена
                        if (selectedTask != null && !selectedTask.isComplete()) {
                            realmDB.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    selectedTask.setTaskStatus(TaskStatus.getObjectComplete(realm));
                                    // но вердикт нужен иной
                                    selectedTask.setTaskVerdict(TaskVerdict.getObjectUnComplete(realm));
                                    if (selectedTask.getStartDate() == null) {
                                        selectedTask.setStartDate(new Date());
                                    }

                                    selectedTask.setEndDate(new Date());
                                }
                            });
                        }
                        Level = closeLevel(STAGE_LEVEL, false);
                        fillListView(Level);
                    }
                });

        dialog.setNegativeButton(R.string.dialog_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }

    /**
     * Метод завершения операции по событию "Операция выполнена"
     *
     * @param measureValue Измеренное значение.
     */
    void CompleteCurrentOperation(String measureValue) {
        TextView textTime;
        TextView textValue;
        final long currentTime = System.currentTimeMillis();

        if (operationAdapter == null) {
            return;
        }

        if (currentOperationPosition >= operationAdapter.getCount()) {
            Log.d(TAG, "Неверный индекс операции");
            return;
        }

//        View operationView = mainListView.getChildAt(currentOperationPosition);
        View operationView = operationAdapter.getView(currentOperationPosition, null, mainListView);
        if (operationView == null) {
            Log.d(TAG, "Операции с индексом {currentOperationPosition} нет в списке");
            return;
        }

        if (measureValue != null) {
            textValue = operationView.findViewById(R.id.op_measure_value);
            textValue.setText(measureValue);
        }

        // "сворачиваем" текущую операцию
        operationAdapter.setItemVisibility(currentOperationPosition, false);
        // "отключаем" текущую операцию
        operationAdapter.setItemEnable(currentOperationPosition, false);

        final Operation operationFinished = operationAdapter.getItem(currentOperationPosition);
        // если операция уже завершена - то ни статус, ни дату не меняем
        if (operationFinished != null && !operationFinished.isComplete()) {
//            final OperationStatus operationStatus = OperationStatus.getObjectComplete(realmDB);
//            final OperationVerdict operationVerdict = OperationVerdict.getObjectComplete(realmDB);
            realmDB.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    operationFinished.setEndDate(new Date(currentTime));
                    //operation.setStartDate(new Date(startTime));
                    operationFinished.setOperationStatus(OperationStatus.getObjectComplete(realm));
                    operationFinished.setOperationVerdict(OperationVerdict.getObjectComplete(realm));
                }
            });

            final String operationUuid = operationFinished.getUuid();
            final String statusUuid = OperationStatus.getObjectComplete(realmDB).getUuid();
            // уведомляем сервер о изменении статуса операции
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Call<Boolean> call = ToirAPIFactory.getOperationService()
                            .setStatus(operationUuid, statusUuid);
                    call.enqueue(new Callback<Boolean>() {
                        @Override
                        public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                            Log.d(TAG, "set status result = " + response.body());
                        }

                        @Override
                        public void onFailure(Call<Boolean> call, Throwable t) {
                            Log.d(TAG, "set status result failure!!!");
                        }
                    });
                }
            });
            t.start();

            textTime = operationView.findViewById(R.id.op_time);
            if (textTime != null) {
                int workTime = (int) (currentTime - startTime) / 1000;
                textTime.setText(getString(R.string.sec_with_value, workTime));
                if (workTime <= operationFinished.getOperationTemplate().getNormative() ||
                        operationFinished.getOperationTemplate().getNormative() == 0) {
                    textTime.setBackgroundColor(Color.GREEN);
                } else {
                    textTime.setBackgroundColor(Color.RED);
                }
            } else {
                Log.d(TAG, "Во view операции нет элемента op_time!");
            }

            // перезапоминаем таймер
            startTime = currentTime;
        }
        goToNextOperation();
        if (isAllOperationComplete(selectedStage)) {
            closeLevel(Level, true);
        }
    }

    public boolean goToNextOperation() {
        boolean isMeasure = false;

        final Operation operationStarted;
        int nextOperationPosition = currentOperationPosition + 1;
        if (nextOperationPosition < operationAdapter.getCount()) {
            // "включаем" следующую операцию
            operationAdapter.setItemEnable(nextOperationPosition, true);

            // фиксируем начало работы над следующей операцией (если у нее нет статуса закончена),
            // меняем ее статус на "в процессе"
            operationStarted = operationAdapter.getItem(nextOperationPosition);
            if (operationStarted != null && !operationStarted.isComplete()) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        operationStarted.setStartDate(new Date());
                        operationStarted.setOperationStatus(OperationStatus.getObjectInWork(realm));
                    }
                });

                isMeasure = operationStarted.getOperationTemplate().getOperationType().isMeasure();
            }

            currentOperationPosition = nextOperationPosition;
            currentOperation = operationAdapter.getItem(currentOperationPosition);

            if (isMeasure) {
                startMeasure();
            }
            return false;
        }
        return true;
    }

    private void startMeasure() {
        Intent measure = new Intent(getActivity(), MeasureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("operationUuid", currentOperation.getUuid());
        bundle.putString("equipmentUuid", currentEquipment.getUuid());
        measure.putExtras(bundle);
        //getActivity().startActivity (measure);
        startActivityForResult(measure, ACTIVITY_MEASURE);
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
                    // закрываем диалог
                    rfidDialog.dismiss();

                    String[] tagIds = (String[]) message.obj;
                    if (tagIds == null) {
                        try {
                            Toast.makeText(getActivity(), "Не верное оборудование!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }

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
                                Toast.makeText(getActivity(), "Приложение ТОиР не установлено", Toast.LENGTH_SHORT).show();
                            }
                        }

                        if (level == TASK_LEVEL) {
                            fillListViewStage(selectedTask);
                            Level = STAGE_LEVEL;
                        }

                        if (level == STAGE_LEVEL) {
                            fillListViewOperations(selectedStage);
                            Level = OPERATION_LEVEL;
                            askStartOperations();
                        }
                    } else {
                        try {
                            Toast.makeText(getActivity(), "Не верное оборудование!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                        }
                    }
                } else if (message.what == RfidDriverBase.RESULT_RFID_CANCEL) {
                    Log.d(TAG, "Отмена чтения метки.");
                    try {
                        Toast.makeText(getActivity(), "Отмена чтения метки.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                } else {
                    Log.d(TAG, "Ошибка чтения метки!");
                    try {
                        Toast.makeText(getActivity(), "Ошибка чтения метки.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                    }
                }

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
            toolbar.setSubtitle(getString(R.string.menu_orders));
            sp = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
            bottomBar = activity.findViewById(R.id.bottomBar);
        }

        realmDB = Realm.getDefaultInstance();
        listLayout = rootView.findViewById(R.id.tl_listview_layout);
        fab_check = rootView.findViewById(R.id.fab_check);
        fab_question = rootView.findViewById(R.id.fab_question);
        fab_camera = rootView.findViewById(R.id.fab_photo);
        fab_check.setVisibility(View.INVISIBLE);
        fab_question.setVisibility(View.INVISIBLE);
        fab_camera.setVisibility(View.INVISIBLE);

        fab_defect = rootView.findViewById(R.id.fab_defect);
        fab_defect.setVisibility(View.INVISIBLE);
        fab_defect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDefect2((ViewGroup) v.getParent(), getLayoutInflater(), v.getContext(), currentEquipment.getUuid(), null);
            }
        });

        fab_camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Context context = getContext();
                if (context == null) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_ACCESS);
                        return;
                    }
                }

                File file = null;
                try {
                    file = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (file != null) {
                    photoFilePath = file.getAbsolutePath();
                    switch (Level) {
                        case OPERATION_LEVEL:
                            currentEntityUuid = currentOperation.getUuid();
                            break;
                        default:
                            currentEntityUuid = null;
                            break;
                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        file = new File(Environment.getExternalStorageDirectory(), "image.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(file));
                        photoFilePath = file.getAbsolutePath();
                        startActivityForResult(intent, ACTIVITY_PHOTO);
                    } else {
                        Uri photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            startActivityForResult(intent, ACTIVITY_PHOTO);
                        } else {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, ACTIVITY_PHOTO);
                        }
                    }
                }
            }
        });
        fab_check.setOnClickListener(new SubmitOnClickListener());
        fab_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View addCommentLayout;
                final TextView author;
                LayoutInflater inflater = getLayoutInflater();

                addCommentLayout = inflater.inflate(R.layout.order_question, null, false);
                author = addCommentLayout.findViewById(R.id.order_author);
                author.setText(selectedOrder.getAuthor().getName());

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
                builder.setTitle("Cообщение автору");
                builder.setView(addCommentLayout);
                builder.setIcon(R.drawable.ic_icon_user);
                builder.setCancelable(false);
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                final android.support.v7.app.AlertDialog dialog = builder.create();
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText orderComment = addCommentLayout.findViewById(R.id.order_comment);

                        Realm realm = Realm.getDefaultInstance();
                        AuthorizedUser authUser = AuthorizedUser.getInstance();
                        User user = realm.where(User.class).equalTo("tagId", authUser.getTagId()).findFirst();
                        UUID uuid = UUID.randomUUID();
                        Date date = new Date();
                        realm.beginTransaction();

                        long nextId = ru.toir.mobile.multi.db.realm.Message.getLastId() + 1;
                        ru.toir.mobile.multi.db.realm.Message message = new ru.toir.mobile.multi.db.realm.Message();
                        message.set_id(nextId);
                        message.setUuid(uuid.toString().toUpperCase());
                        message.setFromUser(user);
                        message.setToUser(selectedOrder.getAuthor());
                        message.setDate(date);
                        message.setCreatedAt(date);
                        message.setChangedAt(date);
                        message.setStatus(0);
                        message.setText(orderComment.getText().toString());
                        //message.setTitle("Вопрос по наряду #".concat(String.valueOf(selectedOrder.get_id())));
                        realm.copyToRealmOrUpdate(message);
                        realm.commitTransaction();
                        realm.close();
                        // отправляем сообшения
                        messageCounter = new AtomicInteger(3);
                        RealmResults<ru.toir.mobile.multi.db.realm.Message> messages = realmDB
                                .where(ru.toir.mobile.multi.db.realm.Message.class)
                                .equalTo("sent", false)
                                .findAll();
                        sendMessages(realmDB.copyFromRealm(messages));
                        dialog.dismiss();
                    }
                };
                dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
                dialog.show();
                dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(listener);
            }
        });

        fab_equipmentInfo = rootView.findViewById(R.id.fab_equipmentInfo);
        fab_equipmentInfo.setVisibility(View.INVISIBLE);
        fab_equipmentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentEquipment != null) {
                    Activity activity = getActivity();
                    if (activity == null) {
                        return;
                    }

                    String equipment_uuid = currentEquipment.getUuid();
                    Intent equipmentInfo = new Intent(activity, EquipmentInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("equipment_uuid", equipment_uuid);
                    equipmentInfo.putExtras(bundle);
                    activity.startActivity(equipmentInfo);
                }
            }
        });

        mainListView = rootView.findViewById(R.id.list_view);

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    Log.d(TAG, "OrderFragment !!! back pressed!!!");
                    if (Level == TASK_LEVEL || Level == 0 || Level == STAGE_LEVEL) {
                        Level = ORDER_LEVEL;
                        initView();
                    }

                    if (Level == STAGE_LEVEL && false) {
                        Level = TASK_LEVEL;
                        fillListViewTasks(selectedOrder);
                    }

                    if (Level == OPERATION_LEVEL) {
                        taskTimer.cancel();
                        firstLaunch = true;
                        currentOperationPosition = 0;
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
        //mainListView.setOnItemLongClickListener(infoListViewLongClickListener);

        //mainListView.setLongClickable(true);
        //mainListView.setClickable(false);

        initView();
        return rootView;
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
//        DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols() {
//            @Override
//            public String[] getMonths() {
//                return new String[]{"января", "февраля", "марта", "апреля", "мая", "июня",
//                        "июля", "августа", "сентября", "октября", "ноября", "декабря"};
//            }
//        };
        String sDate;
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
                Date startDate = order.getStartDate();
                if (startDate != null) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(startDate);
                } else {
                    sDate = "не назначен";
                }

                start.setText(getString(R.string.order_start, sDate));
                Date receivDate = order.getReceivDate();
                if (receivDate != null) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(receivDate);
                } else {
                    sDate = "не получен";
                }

                recieve.setText(getString(R.string.order_recieved, sDate));

                Date openDate = order.getOpenDate();
                if (openDate != null) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(openDate);
                } else {
                    sDate = "не начат";
                }

                open.setText(getString(R.string.order_open, sDate));

                Date closeDate = order.getCloseDate();
                if (closeDate != null) {
                    sDate = new SimpleDateFormat("dd MM yyyy HH:mm", Locale.ENGLISH)
                            .format(closeDate);
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

    /**
     * Возвращает список невыполненных операций в текущем выполняемом этапе.
     *
     * @return List<Operation>
     */
    List<Operation> getUncompleteOperations(Stage stage) {
        List<Operation> uncompleteOperationList = new ArrayList<>();
        List<Operation> operations = stage.getOperations();

        // проверяем/строим список невыполненных операций
        for (Operation operation : operations) {
            if (operation.isNew() || operation.isInWork()) {
                uncompleteOperationList.add(operation);
            }
        }
        return uncompleteOperationList;
    }

    /**
     * Проверяет все ли операции в текущем этапе выполнены
     */
    boolean isAllOperationComplete(Stage stage) {
        return getUncompleteOperations(stage).size() == 0;
    }

    /**
     * Возвращает список невыполненных этапов в текущей выполняемой задаче.
     *
     * @return List<Stage>
     */
    List<Stage> getUncompleteStages(Task task) {
        List<Stage> uncompleteList = new ArrayList<>();
        List<Stage> stages = task.getStages();

        // проверяем/строим список невыполненных этапов
        for (Stage stage : stages) {
            if (stage.isNew() || stage.isInWork()) {
                uncompleteList.add(stage);
            }
        }

        return uncompleteList;
    }

    /**
     * Проверяет все ли этапы в текущей задаче выполнены
     */
    boolean isAllStageComplete(Task task) {
        return getUncompleteStages(task).size() == 0;
    }

    /**
     * Проверяет все ли этапы в текущей задаче были выполнены без проблем
     */
    boolean isAllStageFullComplete(Task task) {
        boolean good = true;
        List<Stage> stages = task.getStages();
        // проверяем/строим список невыполненных этапов
        for (Stage stage : stages) {
            if (stage.isUnComplete()) {
                good = false;
            }
        }
        return good;
    }

    /**
     * Возвращает список невыполненных задач в текущем выполняемом наряде.
     *
     * @return List<Task>
     */
    List<Task> getUncompleteTasks() {
        List<Task> uncompleteList = new ArrayList<>();
        List<Task> tasks = selectedOrder.getTasks();

        // проверяем/строим список невыполненных этапов
        for (Task task : tasks) {
            if (task.isNew() || task.isInWork()) {
                uncompleteList.add(task);
            }
        }

        return uncompleteList;
    }

    /**
     * Проверяет все ли этапы в текущей задаче выполнены
     */
    boolean isAllTaskComplete() {
        return getUncompleteTasks().size() == 0;
    }

    /**
     * Метод для проведения необходимых действий при завершении текущего этапа, задачи, наряда.
     *
     * @param currentLevel Текущий уровень
     * @param setVerdict   Показывать или нет диалог с установкой вердиктов при закрытии элемента наряда и при наличии невыполненных дочерних элементов
     * @return Новый уровень на который необходимо переключится
     */
    private int closeLevel(int currentLevel, boolean setVerdict) {
        switch (currentLevel) {
            case OPERATION_LEVEL:
                // если недоступен адаптер с операцииями, сразу переходим на уровень этапов
                if (operationAdapter == null) {
                    return closeLevel(STAGE_LEVEL, false);
                }

                // проверяем все ли операции завершены,
                // если нет показываем диалог для установки причины их невыполнения
                if (isAllOperationComplete(selectedStage)) {
                    // устанавливаем статус для текущего этапа - выполнен
                    if (selectedStage != null && !selectedStage.isComplete()) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                StageStatus status;
                                long totalOperationsOfStage = selectedStage.getOperations().size();
                                long completeStatusOperationOfStage = realm.where(Operation.class)
                                        .equalTo("stageUuid", selectedStage.getUuid())
                                        .equalTo("operationStatus.uuid", OperationStatus.Status.COMPLETE)
                                        .findAll()
                                        .size();
                                if (totalOperationsOfStage == completeStatusOperationOfStage) {
                                    status = StageStatus.getObjectComplete(realm);
                                } else {
                                    status = StageStatus.getObjectUnComplete(realm);
                                }

                                selectedStage.setStageStatus(status);
                                selectedStage.setEndDate(new Date());
                            }
                        });

                        // пытаемся просигнализировать серверу что этап выполнен
                        final String stageUuid = selectedStage.getUuid();
                        final String statusUuid = StageStatus.getObjectComplete(realmDB).getUuid();
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Call<Boolean> call = ToirAPIFactory.getStageService()
                                        .setStatus(stageUuid, statusUuid);
                                call.enqueue(new Callback<Boolean>() {
                                    @Override
                                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                        Log.d(TAG, "set status result = " + response.body());
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                        Log.d(TAG, "set status result failure!!!");
                                    }
                                });
                            }
                        });
                        t.start();

                    }

                    Log.d(TAG, "Остановка таймера...");
                    taskTimer.cancel();
                    firstLaunch = true;
                    currentOperationPosition = 0;

                    return closeLevel(STAGE_LEVEL, false);
                } else {
                    // показываем диалог для установки вердиктов для невыполненных операций
                    if (setVerdict) {
                        setOperationsVerdict(getUncompleteOperations(selectedStage));
                    }

                    return OPERATION_LEVEL;
                }

            case STAGE_LEVEL:
                // если недоступен адаптер с этапами, сразу переходим на уровень задач
                if (stageAdapter == null) {
                    return closeLevel(TASK_LEVEL, false);
                }

                // проверяем все ли этапы выполнены
                if (isAllStageComplete(selectedTask)) {
                    // устанавливаем статус для текущей задачи - выполнена
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            selectedTask.setEndDate(new Date());
                            selectedTask.setTaskStatus(TaskStatus.getObjectComplete(realm));
                        }
                    });
                    addToJournal("Закончено выполнение задачи "
                            + selectedTask.getTaskTemplate().getTitle() + "("
                            + selectedTask.getUuid() + ")");

                    // пытаемся просигнализировать серверу что этап выполнен
                    final String taskUuid = selectedTask.getUuid();
                    final String statusUuid = TaskStatus.getObjectComplete(realmDB).getUuid();
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Call<Boolean> call = ToirAPIFactory.getTasksService()
                                    .setStatus(taskUuid, statusUuid);
                            call.enqueue(new Callback<Boolean>() {
                                @Override
                                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                    Log.d(TAG, "set status result = " + response.body());
                                }

                                @Override
                                public void onFailure(Call<Boolean> call, Throwable t) {
                                    Log.d(TAG, "set status result failure!!!");
                                }
                            });
                        }
                    });
                    t.start();
                    if (!isAllStageFullComplete(selectedTask)) {
                        final TaskVerdict taskVerdict = TaskVerdict.getObjectUnComplete(realmDB);
                        if (taskVerdict != null) {
                            realmDB.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    selectedTask.setTaskVerdict(taskVerdict);
                                }
                            });
                        }
                    }
                    // если все этапы в задаче завершены, переключаемся на список задач
                    return closeLevel(TASK_LEVEL, false);
                } else {
                    Log.d(TAG, "Leave current level");

                    if (setVerdict) {
                        // показываем диалог для установки вердиктов для невыполненных этапов
                        setStagesVerdict(getUncompleteStages(selectedTask));
                    }

                    // указываем на то что переключаемся на список этапов
                    return STAGE_LEVEL;
                }

            case TASK_LEVEL:
                // если недоступен адаптер с задачами, сразу переходим на уровень нарядов
                // нет уровня задач теперь!
                //if (taskAdapter == null) {
                //return closeLevel(ORDER_LEVEL, false);
                //}

                // проверяем все ли задачи выполнены
                if (isAllTaskComplete()) {
                    // устанавливаем статус для текущего наряда - выполнен
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            //  TODO в будущем решаем ставить ли здесь статус отличный от "выполнен", если что-то внутри наряда "не выполнено"
                            selectedOrder.setCloseDate(new Date());
                            selectedOrder.setOrderStatus(OrderStatus.getObjectComplete(realm));
                        }
                    });
                    addToJournal("Закончен наряд " + selectedOrder.getTitle() + "(" + selectedOrder.getUuid() + ")");

                    // отправляем сообщение о изменении статуса на сервер
                    final String uuid = selectedOrder.getUuid();
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

                    // если все задачи в наряде завершены, переключаемся на список нарядов
                    return closeLevel(ORDER_LEVEL, false);
                } else {
                    // указываем на то что переключаемся на список задач
                    Log.d(TAG, "Leave current level");
                    return TASK_LEVEL;
                }

            case ORDER_LEVEL:
            default:
                return ORDER_LEVEL;
        }
    }

    /**
     * Заполняем список элементами в зависимости от уровня на котором находимся.
     *
     * @param level Уровень на который переключаемся
     */
    private void fillListView(int level) {
        switch (level) {
            case OPERATION_LEVEL:
                fillListViewOperations(selectedStage);
                askStartOperations();
                //startOperations();
                break;
            case STAGE_LEVEL:
                fillListViewStage(selectedTask);
                break;
            case TASK_LEVEL:
                if (selectedOrder.getTasks() != null && selectedOrder.getTasks().size() > 0) {
                    selectedTask = selectedOrder.getTasks().get(0);
                    if (selectedTask != null && selectedTask.getUuid().equals(TaskTemplate.DEFAULT_TASK)) {
                        fillListViewStage(selectedTask);
                        Level = STAGE_LEVEL;
                        fab_camera.setVisibility(View.INVISIBLE);
                    }
                }
                fillListViewTasks(selectedOrder);
                break;
            case ORDER_LEVEL:
                initView();
                break;
        }
    }

    public void checkClickItem(final int position) {
        // находимся на "экране" нарядов
        switch (Level) {
            case ORDER_LEVEL:
                if (orderAdapter != null) {
                    selectedOrder = orderAdapter.getItem(position);
                    if (selectedOrder != null) {
                        // устанавливаем дату начала работы с нарядом
                        if (selectedOrder.getStartDate() == null) {
                            Realm realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            selectedOrder.setStartDate(new Date());
                            realm.commitTransaction();
                        }
                        if (selectedOrder.getTasks() != null && selectedOrder.getTasks().size() > 0) {
                            selectedTask = selectedOrder.getTasks().get(0);
                            if (selectedTask != null && selectedTask.getTaskTemplate().getUuid().equals(TaskTemplate.DEFAULT_TASK)) {
                                // если есть этапы
                                if (fillListViewStage(selectedTask) > 0) {
                                    Level = STAGE_LEVEL;
                                    fab_camera.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                fillListViewTasks(selectedOrder);
                                Level = TASK_LEVEL;
                                fab_camera.setVisibility(View.INVISIBLE);
                                fab_check.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }

                break;

            case TASK_LEVEL:
                if (taskAdapter != null) {
                    selectedTask = taskAdapter.getItem(position);
                    if (selectedTask != null) {
                        fillListViewStage(selectedTask);
                        Level = STAGE_LEVEL;
                        fab_camera.setVisibility(View.INVISIBLE);
                        //fab_check.setVisibility(View.INVISIBLE);
                    }
                }

                break;
            case STAGE_LEVEL:
                if (stageAdapter != null) {
                    selectedStage = stageAdapter.getItem(position);
                    if (selectedStage != null) {
                        final String expectedTagId;
                        currentEquipment = selectedStage.getEquipment();
                        expectedTagId = currentEquipment.getTagId();
                        boolean ask_tags = sp.getBoolean("without_tags_mode", true);
                        // только если новая
                        if (!ask_tags && !expectedTagId.equals("") && selectedStage.getStageStatus().isNew()) {
                            runRfidDialog(expectedTagId, STAGE_LEVEL);
                        } else {
                            fillListViewOperations(selectedStage);
                            Level = OPERATION_LEVEL;
                            //startOperations();
                            askStartOperations();
                        }
                    }
                }

                break;

            case OPERATION_LEVEL:
                if (operationAdapter != null) {
                    Operation selectedOperation = operationAdapter.getItem(position);
                    if (selectedOperation != null) {
                        operationAdapter.setItemVisibility(position,
                                !operationAdapter.getItemVisibility(position));
                        operationAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }

    // временная тестовая функция, которая возвращает операциям первоначальный вид для отладки
    void stopOperations() {
        int totalOperationCount = operationAdapter.getCount();
        for (int i = 0; i < totalOperationCount; i++) {
            final Operation operation = operationAdapter.getItem(i);
            if (operation != null) {
                if (operation.getOperationStatus() != null) {
                    if (!operation.isNew()) {
                        realmDB.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                operation.setStartDate(null);
                                operation.setEndDate(null);
                                operation.setOperationStatus(OperationStatus.getObjectNew(realm));
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Найдена операция с неинициализированным статусом");
                }
            }
        }
    }

    /**
     * Обработчик кнопки "завершить все операции"
     */
    private class SubmitOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Level = closeLevel(Level, true);
            fillListView(Level);
        }
    }

    private class ListViewClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, View selectedItemView, final int position, long id) {
            // находимся на "экране" нарядов
            if (Level != ORDER_LEVEL)
                checkClickItem(position);
        }
    }

    public class OnCheckBoxClickListener implements View.OnClickListener {

        @Override
        public void onClick(View arg) {
            if (!operationAdapter.isItemEnabled(currentOperationPosition)) {
                return;
            }
            CompleteCurrentOperation(null);
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
                if (operation.isNew() || operation.isInWork()) {
                    // показываем диалог изменения статуса
                    closeOperationManual(operation, parent, 0);
                } else {
                    // операция уже выполнена, изменить статус нельзя
                    // сообщаем об этом
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle(getString(R.string.warning));
                    dialog.setMessage(getString(R.string.stage_cancel_error));
                    dialog.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            } else if (Level == STAGE_LEVEL) {
                // находимся на "экране" с этапами
                //String operationStatus = operation.getOperation_status_uuid();
                // менять произвольно статус этапа позволяем только если
                // статус этапа "Новый" или "В работе"
                Stage stage = (Stage) parent.getItemAtPosition(position);

                if (stage.isNew() || stage.isInWork()) {
                    // показываем диалог изменения статуса
                    closeStageManual(stage, parent);
                } else {
                    // операция уже выполнена, изменить статус нельзя
                    // сообщаем об этом
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle(getString(R.string.warning));
                    dialog.setMessage(getString(R.string.stage_cancel_error));
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
                if (order != null) {
                    showInformation(ORDER_LEVEL, order.get_id(), parent);
                }

                // проверяем статус наряда
                // TODO: Разобраться зачем так сделано!
                if (false && order != null) {
//                    orderStatus = order.getOrderStatus();
                    if (orderStatus != null) {
                        if (orderStatus.getUuid().equals(OrderStatus.Status.COMPLETE)
                                || order.getOrderStatus().getUuid().equals(OrderStatus.Status.UN_COMPLETE)) {
                            // наряд уже закрыт, изменить статус нельзя
                            // сообщаем об этом
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            dialog.setTitle(getString(R.string.warning));
                            dialog.setMessage(getString(R.string.stage_cancel_error));
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
