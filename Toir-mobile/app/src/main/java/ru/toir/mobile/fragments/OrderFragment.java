package ru.toir.mobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit.Call;
import retrofit.Response;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.MeasureActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirApplication;
import ru.toir.mobile.db.adapters.OperationAdapter;
import ru.toir.mobile.db.adapters.OperationVerdictAdapter;
import ru.toir.mobile.db.adapters.OrderAdapter;
import ru.toir.mobile.db.adapters.OrderVerdictAdapter;
import ru.toir.mobile.db.adapters.TaskAdapter;
import ru.toir.mobile.db.adapters.TaskStageAdapter;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.GpsTrack;
import ru.toir.mobile.db.realm.ISend;
import ru.toir.mobile.db.realm.Journal;
import ru.toir.mobile.db.realm.MeasureType;
import ru.toir.mobile.db.realm.Operation;
import ru.toir.mobile.db.realm.OperationStatus;
import ru.toir.mobile.db.realm.OperationVerdict;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.OrderVerdict;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.TaskStageStatus;
import ru.toir.mobile.db.realm.TaskStages;
import ru.toir.mobile.db.realm.TaskStatus;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
//import ru.toir.mobile.rest.IServiceProvider;
//import ru.toir.mobile.rest.ProcessorService;
//import ru.toir.mobile.rest.TaskServiceProvider;
import ru.toir.mobile.rest.ToirAPIFactory;
import ru.toir.mobile.rest.ToirAPIResponse;
import ru.toir.mobile.rfid.RfidDialog;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.utils.MainFunctions;

public class OrderFragment extends Fragment implements View.OnClickListener {
    private Tasks selectedTask;
    private Orders selectedOrder;
    private TaskStages selectedStage;
    private Operation selectedOperation;

    private OrderAdapter orderAdapter;
    private TaskAdapter taskAdapter;
    private TaskStageAdapter taskStageAdapter;
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
                textTime = (TextView) getViewByPosition(currentOperationId, mainListView).findViewById(R.id.op_time);
                textTime.setText(getString(R.string.sec_with_value, (int) (currentTime - startTime) / 1000));
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
        Toolbar toolbar = (Toolbar) (getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Наряды");
        submit = (Button) rootView.findViewById(R.id.tl_finishButton);
        submit.setOnClickListener(this);
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
                getActivity().startActivity(measure);

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
            //RealmQuery<Orders> query = realmDB.where(Orders.class).equalTo("user.uuid", authUser.getUuid());
            RealmQuery<Orders> query = realmDB.where(Orders.class);
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

        submit.setVisibility(View.GONE);
        measure.setVisibility(View.GONE);
    }

    // TaskStages----------------------------------------------------------------------------------------
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

        operationAdapter = new OperationAdapter(getContext(), operations, currentTaskUuid);
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
        // запрещаем все операции кроме первой
        if (operationAdapter != null) {
            totalOperationCount = operationAdapter.getCount();
            operationAdapter.setItemEnable(0, true);
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
        final TaskStageStatus taskStageStatus;
        final TaskStageStatus taskStageStatusInWork;
        if (selectedStage != null) {
            taskStageStatus = selectedStage.getTaskStageStatus();
            taskStageStatusInWork = realmDB.where(TaskStageStatus.class)
                    .equalTo("uuid", TaskStageStatus.Status.IN_WORK)
                    .findFirst();
            if (taskStageStatus != null && taskStageStatusInWork != null)
                if (taskStageStatus.getUuid().equals(TaskStageStatus.Status.NEW) || taskStageStatus.getUuid().equals(TaskStageStatus.Status.UN_COMPLETE)) {
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
                            selectedOrder.setStartDate(new Date());
                            selectedOrder.setOrderStatus(orderStatusInWork);
                        }
                    });
                }
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
//                TaskServiceHelper tsh = new TaskServiceHelper(getActivity().getApplicationContext(),
//                        TaskServiceProvider.Actions.ACTION_GET_TASK);
//                getActivity().registerReceiver(mReceiverGetTask, mFilterGetTask);
//                tsh.GetTaskNew();

                // запускаем поток получения новых нарядов с сервера
                AsyncTask<Void, Void, List<Orders>> aTask = new AsyncTask<Void, Void, List<Orders>>() {
                    @Override
                    protected List<Orders> doInBackground(Void... voids) {
                        Call<List<Orders>> call = ToirAPIFactory.getOrdersService()
                                .ordersByStatus(OrderStatus.Status.NEW);
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
                                files.add(new FilePath(task.getTaskTemplate().getImage(),
                                        basePath, basePathLocal));
                                // урл изображения оборудования
                                files.add(new FilePath(task.getEquipment().getImage(),
                                        basePath, "/equipment/"));

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

                        // загружаем файлы
                        for (FilePath path : files) {
                            Call<ResponseBody> call1 = ToirAPIFactory.getFileDownload().getFile(ToirApplication.serverUrl + path.urlPath + path.fileName);
                            try {
                                Response<ResponseBody> r = call1.execute();
                                ResponseBody trueImgBody = r.body();
                                if (trueImgBody == null) {
                                    continue;
                                }

                                File file = new File(
                                        getContext().getExternalFilesDir(path.localPath), path.fileName);
                                if (!file.getParentFile().exists()) {
                                    if (!file.getParentFile().mkdirs()) {
                                        continue;
                                    }
                                }

                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(trueImgBody.bytes());
                                fos.close();
//                                equipment.setImage(file.getPath());
//                                equipmentDBAdapter.replace(equipment);
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
                aTask.execute();

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
                AsyncTask<Void, Void, List<Orders>> aTask = new AsyncTask<Void, Void, List<Orders>>() {
                    @Override
                    protected List<Orders> doInBackground(Void... voids) {
                        List<String> stUuids = new ArrayList<>();
                        stUuids.add(OrderStatus.Status.CANCELED);
                        stUuids.add(OrderStatus.Status.COMPLETE);
                        stUuids.add(OrderStatus.Status.UN_COMPLETE);
                        Call<List<Orders>> call = ToirAPIFactory.getOrdersService()
                                .ordersByStatus(stUuids);
                        List<Orders> result;
                        try {
                            Response<List<Orders>> response = call.execute();
                            result = response.body();
                            return result;
                        } catch (Exception e) {
                            Log.d(TAG, e.getLocalizedMessage());
                            return null;
                        }
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
                aTask.execute();

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
        sendTaskResultMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

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
        });
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

    /**
     * Отправка всех выполненных нарядов на сервер
     */
    private void sendCompleteTask() {
        // TODO: реализовать отправку
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

        // строим список uuid нарядов для отправки на сервер
        // раньше список передавался как параметр в сервис отправки данных, сейчас пока не решено
        String[] sendTaskUuids = new String[ordersList.size()];
        int i = 0;
        for (Orders order : ordersList) {
            sendTaskUuids[i] = order.getUuid();
            i++;
        }

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

    @Override
    public void onClick(View v) {
        int completedOperationCount = 0;
        final long currentTime = System.currentTimeMillis();
        //long totalTimeElapsed = currentTime - startTime;
        CheckBox checkBox;
        final TaskStageStatus taskStageComplete;
        uncompleteOperationList.clear();
        // по умолчанию у нас все выполнено
        taskStageComplete = realmDB.where(TaskStageStatus.class).equalTo("title", "Выполнен").findFirst();

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
            final TaskStages taskStage = realmDB.where(TaskStages.class).equalTo("uuid", currentTaskStageUuid).findFirst();
            if (taskStage != null) {
                realmDB.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        taskStage.setTaskStageStatus(taskStageComplete);
                        //taskStage.setEndDate();
                        taskStage.setEndDate(new Date());
                    }
                });
                taskStage.getEquipment();
            }
            Log.d(TAG, "Остановка таймера...");
            taskTimer.cancel();
            firstLaunch = true;
            currentOperationId = 0;

            if (selectedTask != null) {
                currentTaskStageUuid = selectedStage.getUuid();
                currentTaskUuid = selectedTask.getUuid();
                fillListViewTaskStage(selectedTask);
                submit.setVisibility(View.GONE);
                measure.setVisibility(View.GONE);
                Level = 2;
            }

        } else {
            // TODO показать диалог с коментарием и выбором вердикта
            Log.d("order", "dialog");
            setOperationsVerdict();
        }
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
        operationStatusUnComplete = realmDB.where(OperationStatus.class).findFirst();

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
        final TaskStageStatus taskStageStatusUnComplete;
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
        taskStageStatusUnComplete = realmDB.where(TaskStageStatus.class)
                .equalTo("uuid", TaskStageStatus.Status.UN_COMPLETE)
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
                    ContentResolver cr = getActivity().getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, fileUri);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                        Log.e("Camera", e.toString());
                    }
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
                            spinner = (Spinner) getViewByPosition(i, listView).findViewById(R.id.simple_spinner);
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
                            fillListViewTaskStage(selectedTask);
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

    public class ListViewClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View selectedItemView, int position, long id) {
            // находимся на "экране" нарядов
            if (Level == 0) {
                if (orderAdapter != null) {
                    selectedOrder = orderAdapter.getItem(position);
                    if (selectedOrder != null) {
                        currentOrderUuid = selectedOrder.getUuid();
                        fillListViewTasks(selectedOrder);
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
                                        boolean run_ar_content = sp.getBoolean("@string/pref_debug_mode", false);
                                        if (run_ar_content) {
                                            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage("ru.shtrm.toir");
                                            intent.putExtra("hardwareUUID", currentEquipment.getUuid());
                                            startActivity(intent);
                                        }
                                        fillListViewTaskStage(selectedTask);
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

            TextView textTime;
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

                if (currentOperationId < operationAdapter.getCount()) {
                    operationAdapter.setItemEnable(currentOperationId + 1, true);
                    operationAdapter.setItemVisibility(currentOperationId);
                    operationAdapter.setItemVisibility(currentOperationId + 1);
                    startTime = System.currentTimeMillis();
                    currentOperationId++;
                }

                final Operation operation = operationAdapter.getItem(position);
                if (operation != null) {
                    realmDB.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            operation.setEndDate(new Date(currentTime));
                            operation.setStartDate(new Date(startTime));
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
