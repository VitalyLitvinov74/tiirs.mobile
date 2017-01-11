/**
 *
 */
package ru.toir.mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.RecyclerViewCacheUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.OperationPattern;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.db.tables.OperationPatternStepResult;
import ru.toir.mobile.db.tables.OperationResult;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.Task;

/**
 * @author Dmitriy Logachov
 */
public class OperationActivity extends AppCompatActivity {

    private static final String TAG = "OperationActivity";

    public static final String OPERATION_UUID_EXTRA = "operation_uuid";
    public static final String TASK_UUID_EXTRA = "task_uuid";
    public static final String EQUIPMENT_UUID_EXTRA = "equipment_uuid";
    //public static final String EQUIPMENT_TAG_EXTRA = "equipment_tag";

    private AccountHeader headerResult = null;
    private static final int DRAWER_INFO = 13;
    private static final int DRAWER_EXIT = 14;

    private OperationPattern pattern;
    private ArrayList<OperationPatternStep> patternSteps;
    private ArrayList<OperationPatternStepResult> stepsResults;
    private ArrayList<OperationResult> operationResults;

    private String taskUuid;
    private String operation_uuid;
    private String equipment_uuid;

    private String taskname = "";
    private String taskstatus = "";
    private String operationname = "";
    private String operation = "";

    private LinearLayout resultButtonLayout;
    private RelativeLayout photoContainer;
    private ScrollView rootView;

    private TextView stepTitle;
    private TextView stepDescrition;
    private TextView task_title;
    private TextView task_status;
    private TextView equipment_title;
    private TextView equipment_status;
    private TextView equipment_operation;

    private NumberPicker numberPicker;
    private Spinner spinnerSuffix;
    private ArrayAdapter<Suffixes> spinnerSuffixAdapter;
    private ArrayList<Suffixes> suffixList;
    private ImageView step_image;

    private String lastPhotoFile;
    // костыль для того чтобы передать uuid в класс работы с камерой для
    // сохранения файла изображения с именем шага операции
    private String currentStepUuid;
    Preview mPreview;

    /**
     * Класс для представления множителей (частоты, напряжения, тока...)
     *
     * @author Dmitriy Logachov
     */
    protected class Suffixes {
        String title;
        long multiplier;

        public Suffixes(String t, int m) {
            title = t;
            multiplier = m;
        }

        public String toString() {
            return title;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        operation_uuid = b.getString(OPERATION_UUID_EXTRA);
        taskUuid = b.getString(TASK_UUID_EXTRA);
        equipment_uuid = b.getString(EQUIPMENT_UUID_EXTRA);
        //setContentView(R.layout.taskwork_fragment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setMainLayout(savedInstanceState);

        resultButtonLayout = (LinearLayout) findViewById(R.id
                .twf_resultButtonLayout);
        photoContainer = (RelativeLayout) findViewById(R.id.twf_photoContainer);
        rootView = (ScrollView) findViewById(R.id.twf_rootView);

        stepTitle = (TextView) findViewById(R.id.twf_stepTitle);
        stepDescrition = (TextView) findViewById(R.id.twf_step_description);
        step_image = (ImageView) findViewById(R.id.twf_step_image);

        task_title = (TextView) findViewById(R.id.twf_task_title);
        task_status = (TextView) findViewById(R.id.twf_task_status);
        equipment_title = (TextView) findViewById(R.id.twf_equipment_title);
        equipment_status = (TextView) findViewById(R.id.twf_equipment_status);
        equipment_operation = (TextView) findViewById(R.id
				.twf_equipment_operation);

        // получаем статус и время наряда
        TaskDBAdapter dbTask = new TaskDBAdapter(new ToirDatabaseContext(
                getApplicationContext()));
        Task task = dbTask.getItem(taskUuid);
        TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        EquipmentOperationDBAdapter operationDBAdapter = new
				EquipmentOperationDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        EquipmentOperation equipmentOperation = operationDBAdapter
                .getItem(operation_uuid);
        EquipmentStatusDBAdapter equipmentStatusDBAdapter = new
				EquipmentStatusDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        // получаем шаблон операции
        OperationPatternDBAdapter patternDBAdapter = new
				OperationPatternDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        pattern = patternDBAdapter.getItem(equipmentOperation
                .getOperation_pattern_uuid());

        // android:id="@+id/twf_task_title"
        taskname = dbTask.getItem(taskUuid).getTask_name();
        task_title.setText(taskname);
        // android:id="@+id/twf_task_status"
        taskstatus = taskStatusDBAdapter.getNameByUUID(task
                .getTask_status_uuid());
        task_status.setText(taskstatus);
        // android:id="@+id/twf_equipment_title"
        operationname = eqDBAdapter.getEquipsNameByUUID(equipment_uuid);
        equipment_title.setText(operationname);
        // android:id="@+id/twf_equipment_status"
        operation = equipmentStatusDBAdapter.getNameByUUID(eqDBAdapter.getItem(
                equipment_uuid).getEquipment_status_uuid());
        equipment_status.setText(operation);
        // android:id="@+id/twf_equipment_operation"
        operation = pattern.getTitle();
        equipment_operation.setText(operation);

        // получаем шаги шаблона операции
        OperationPatternStepDBAdapter patternStepDBAdapter = new
				OperationPatternStepDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        patternSteps = patternStepDBAdapter.getItems(pattern.getUuid());

        // получаем варианты выполнения шагов
        ArrayList<String> uuids = new ArrayList<>();
        for (OperationPatternStep step : patternSteps) {
            uuids.add(step.getUuid());
        }
        OperationPatternStepResultDBAdapter stepResultDBAdapter = new
				OperationPatternStepResultDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        stepsResults = stepResultDBAdapter.getItems(uuids);

        // получаем список вариантов завершения операции
        OperationResultDBAdapter resultDBAdapter = new OperationResultDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        operationResults = resultDBAdapter.getItems(equipmentOperation
                .getOperation_type_uuid());

		/*
		 * cоздаём запись с результатом выполнения операции для фиксации
		 * времени
		 * начала выполнения (в текущем варианте просто предотвращаем создание
		 * кучи записей для одной операции)
		 */
        EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter =
				new EquipmentOperationResultDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        EquipmentOperationResult operationResult;
        operationResult = equipmentOperationResultDBAdapter
                .getItemByOperation(operation_uuid);
        if (operationResult == null) {
            operationResult = new EquipmentOperationResult();
            operationResult.setEquipment_operation_uuid(operation_uuid);
            operationResult.setStart_date(new Date().getTime());
            equipmentOperationResultDBAdapter.replace(operationResult);
        }

        showStep(getFirstStep().getUuid());
    }

    // public void cancelOnClick(View view) {
    // setResult(RESULT_CANCELED);
    // finish();
    // }

    private void showStep(String uuid) {

        currentStepUuid = uuid;
        OperationPatternStep step;

        step = getStep(uuid);

        if (step != null) {
            showStepContent(step);
        } else {
            Toast.makeText(this, "Шаг не найден", Toast.LENGTH_SHORT).show();
        }
    }

    private void showStepContent(OperationPatternStep step) {

        stepTitle.setText(step.getTitle());
        stepDescrition.setText(step.getDescription());
        resultButtonLayout.removeAllViewsInLayout();

        clearPhotoContainer();

        if (step.getImage() != null) {
            File imgFile = new File(step.getImage());
            if (imgFile.exists() && imgFile.isFile()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
                        .getAbsolutePath());
                step_image.setImageBitmap(myBitmap);
            } else {
                step_image.setVisibility(View.GONE);
            }
        } else {
            step_image.setVisibility(View.GONE);
        }

        // получаем список результатов шагов
        ArrayList<OperationPatternStepResult> resultsList = getStepResult(step
                .getUuid());
        for (OperationPatternStepResult result : resultsList) {
            final String measureType = result.getMeasure_type_uuid();

            // создаём кнопку для результата выполнения шага операции
            Button resultButton = new Button(getApplicationContext());
            final String current_result_uuid = result.getUuid();
            final String next_step_uuid = result
                    .getNext_operation_pattern_step_uuid();
            resultButton.setText(result.getTitle());
            resultButton.setWidth(300);
            resultButton.setTextSize(14);
            if (result.getNext_operation_pattern_step_uuid().equals(
                    "00000000-0000-0000-0000-000000000000")
                    || result.getNext_operation_pattern_step_uuid().equals
					("")) {
                resultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // если были измерения, сохраняем полученные значения
                        if (!measureType.equals(MeasureTypeDBAdapter.Type
								.NONE)) {
                            if (!saveMeasureValue(measureType,
                                    current_result_uuid)) {
                                return;
                            }
                        }

                        // показываем финальный шаг
                        showFinalStep();
                    }
                });
            } else {
                resultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // если были измерения, сохраняем полученные значения
                        if (!measureType.equals(MeasureTypeDBAdapter.Type
								.NONE)) {
                            if (!saveMeasureValue(measureType,
                                    current_result_uuid)) {
                                return;
                            }
                        }

                        // переходим к следующему шагу
                        showStep(next_step_uuid);
                    }
                });
            }

            resultButtonLayout.addView(resultButton);

            // выводим элементы интерфейса для ввода значения измеренний
            if (!measureType.equals(MeasureTypeDBAdapter.Type.NONE)) {
                measureUI(measureType);
            }
        }

        rootView.post(new Runnable() {

            @Override
            public void run() {
                rootView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    /**
     * Создание элементов интерфейса для шагов операции с измерениями значений
     *
     * @param measureType - тип осуществляемого измерения
     */
    private void measureUI(String measureType) {

        // выбор значения
        if (numberPicker == null) {
            numberPicker = new NumberPicker(getApplicationContext());
        }
        numberPicker.setOrientation(NumberPicker.VERTICAL);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(999);

        // перечень множителей
        if (suffixList == null) {
            suffixList = new ArrayList<OperationActivity.Suffixes>();
        } else {
            suffixList.clear();
        }

        if (measureType.equals(MeasureTypeDBAdapter.Type.FREQUENCY)) {
            resultButtonLayout.addView(numberPicker);

            suffixList.add(new Suffixes("Гц", 1));
            suffixList.add(new Suffixes("кГц", 1000));
            suffixList.add(new Suffixes("МГц", 1000000));
            suffixList.add(new Suffixes("ГГц", 1000000000));

            // адаптер для множителей
            spinnerSuffixAdapter = new ArrayAdapter<Suffixes>(
                    getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, suffixList);

            // выпадающий список с множителями
            if (spinnerSuffix == null) {
                spinnerSuffix = new Spinner(getApplicationContext());
            }
            spinnerSuffix.setAdapter(spinnerSuffixAdapter);

            resultButtonLayout.addView(spinnerSuffix);
        } else if (measureType.equals(MeasureTypeDBAdapter.Type.VOLTAGE)) {
            resultButtonLayout.addView(numberPicker);

            suffixList.add(new Suffixes("В", 1));
            suffixList.add(new Suffixes("кВ", 1000));
            suffixList.add(new Suffixes("МВ", 1000000));
            suffixList.add(new Suffixes("ГВ", 1000000000));

            // адаптер для множителей
            spinnerSuffixAdapter = new ArrayAdapter<Suffixes>(
                    getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, suffixList);

            // выпадающий список с множителями
            if (spinnerSuffix == null) {
                spinnerSuffix = new Spinner(getApplicationContext());
            }
            spinnerSuffix.setAdapter(spinnerSuffixAdapter);

            resultButtonLayout.addView(spinnerSuffix);
        } else if (measureType.equals(MeasureTypeDBAdapter.Type.PRESSURE)) {
            resultButtonLayout.addView(numberPicker);

            suffixList.add(new Suffixes("Па", 1));
            suffixList.add(new Suffixes("кПа", 1000));
            suffixList.add(new Suffixes("МПа", 1000000));
            suffixList.add(new Suffixes("ГПа", 1000000000));

            // адаптер для множителей
            spinnerSuffixAdapter = new ArrayAdapter<Suffixes>(
                    getApplicationContext(),
                    android.R.layout.simple_spinner_dropdown_item, suffixList);

            // выпадающий список с множителями
            if (spinnerSuffix == null) {
                spinnerSuffix = new Spinner(getApplicationContext());
            }
            spinnerSuffix.setAdapter(spinnerSuffixAdapter);

            resultButtonLayout.addView(spinnerSuffix);
        } else if (measureType.equals(MeasureTypeDBAdapter.Type.PHOTO)) {

            // инициализировать интерфейс для фотографии
            View cameraView = View.inflate(getApplicationContext(),
                    R.layout.measure, photoContainer);

            ViewGroup.LayoutParams photoParams = cameraView.getLayoutParams();
            // пропорции предпросмотра с камеры 4:3
            photoParams.height = (int) (cameraView.getWidth() * 0.75);

            cameraView.setLayoutParams(photoParams);

            lastPhotoFile = null;

            mPreview = new Preview(getApplicationContext());
            FrameLayout frame = ((FrameLayout) cameraView
                    .findViewById(R.id.camera_preview));
            frame.addView(mPreview);
            Button captureButton = (Button) cameraView
                    .findViewById(R.id.button_capture);
            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreview.mCamera.takePicture(null, null, pictureCallback);
                }
            });

        }
    }

    /**
     * Сохранение результата измерения
     *
     * @param type - тип измерения
     * @param resultUuid - идентификатор результата
     * @return
     */
    private boolean saveMeasureValue(String type, String resultUuid) {

        boolean valueExists = true;
        MeasureValue value;
        MeasureValueDBAdapter adapter = new MeasureValueDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));

        // проверка на существование записи измерения
        value = adapter.getItem(operation_uuid, resultUuid);
        if (value == null) {
            value = new MeasureValue();
            valueExists = false;
        }

        value.setEquipment_operation_uuid(operation_uuid);
        value.setOperation_pattern_step_result_uuid(resultUuid);

        if (type.equals(MeasureTypeDBAdapter.Type.PHOTO)) {
            if (lastPhotoFile == null || lastPhotoFile.equals("")) {
                Toast.makeText(getApplicationContext(),
                        "Сфотографируйте объект!", Toast.LENGTH_SHORT).show();
                return false;
            }
            value.setValue(lastPhotoFile);
        } else if (type.equals(MeasureTypeDBAdapter.Type.FREQUENCY)
                || type.equals(MeasureTypeDBAdapter.Type.PRESSURE)
                || type.equals(MeasureTypeDBAdapter.Type.VOLTAGE)) {
            long resultValue = numberPicker.getValue()
                    * ((Suffixes) spinnerSuffix.getSelectedItem()).multiplier;
            value.setValue(String.valueOf(resultValue));
        }

        if (valueExists) {
            adapter.update(value);
        } else {
            adapter.replace(value);
        }
        return true;
    }

    /**
     * Показываем экран с выбором результата(вердикта) выполнения операции и
     * возможностью изменить статус операции (вместо "Выполнена" по умолчанию)
     */
    private void showFinalStep() {

        step_image.setVisibility(View.GONE);
        stepTitle.setText("Завершение операции");
        stepDescrition
                .setText("Вынесите вердикт по итогу выполнения операции.");

        clearPhotoContainer();

        Button resultButton = new Button(getApplicationContext());
        resultButton.setText("Завершить операцию");
        resultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				/*
				 * сохраняем результат выполнения операции, фиксируем время
				 * окончания, статус выполнения
				 */
                Spinner spinner = (Spinner) ((LinearLayout) v.getParent())
                        .findViewWithTag("result");
                OperationResult result = (OperationResult) spinner
                        .getSelectedItem();
                Log.d(TAG, result.getTitle());
                EquipmentOperationResult operationResult = null;
                EquipmentOperationResultDBAdapter
						equipmentOperationResultDBAdapter = new
						EquipmentOperationResultDBAdapter(
                        new ToirDatabaseContext(getApplicationContext()));
                // к этому моменту запись с установленной датой начала
                // выполнения уже должна существовать
                operationResult = equipmentOperationResultDBAdapter
                        .getItemByOperation(operation_uuid);
                operationResult.setOperation_result_uuid(result.getUuid());
                operationResult.setEnd_date(new Date().getTime());
                equipmentOperationResultDBAdapter.update(operationResult);

                // обновление статуса операции по результату выполнения
                CheckBox altOperationStatusCheckbox = (CheckBox) findViewById
						(R.id.twf_showAltOperationStatusCheckbox);
                Spinner operationStatusSpinner = (Spinner) findViewById(R.id
						.twf_altOperationStatusSpinner);
                String operationStatusUuid = altOperationStatusCheckbox
						.isChecked() ? ((OperationStatus)
						operationStatusSpinner.getSelectedItem()).getUuid() :
						OperationStatusDBAdapter.Status.COMPLETE;
                EquipmentOperationDBAdapter operationDBAdapter = new
						EquipmentOperationDBAdapter(
                        new ToirDatabaseContext(getApplicationContext()));
                EquipmentOperation operation = operationDBAdapter
                        .getItem(operation_uuid);

                operation.setOperation_status_uuid(operationStatusUuid);
                operationDBAdapter.update(operation);

                // всё ли операции выполненны?
                List<EquipmentOperation> operations = operationDBAdapter
                        .getItems(taskUuid);
                String operationStatus;
                boolean operationsIsDone = true;
                for (EquipmentOperation item : operations) {
                    operationStatus = item.getOperation_status_uuid();
                    if (operationStatus
                            .equals(OperationStatusDBAdapter.Status.NEW)
                            || operationStatus
                            .equals(OperationStatusDBAdapter.Status.IN_WORK)) {
                        operationsIsDone = false;
                    }
                }

                // все операции выполненны, меняем статус наряда на "Закончен"
                if (operationsIsDone) {
                    TaskDBAdapter taskDBAdapter = new TaskDBAdapter(
                            new ToirDatabaseContext(getApplicationContext()));
                    Task task = taskDBAdapter.getItem(taskUuid);
                    task.setTask_status_uuid(TaskStatusDBAdapter.Status
							.COMPLETE);
                    task.setUpdated(true);
                    task.setClose_date(Calendar.getInstance().getTimeInMillis
							());
                    taskDBAdapter.update(task);
                }

                finish();
            }
        });

        ArrayAdapter<OperationResult> resultsAdapter = new
				ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                operationResults);
        Spinner spinner = new Spinner(getApplicationContext());
        spinner.setAdapter(resultsAdapter);
        spinner.setTag("result");

        resultButtonLayout.removeAllViewsInLayout();
        resultButtonLayout.addView(spinner);
        resultButtonLayout.addView(resultButton);

        // после добавления элементов в LinearLayout устанавливаем
        // вес элементов для корректного отображения
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) spinner
                .getLayoutParams();
        param.weight = 1;
        spinner.setLayoutParams(param);
        param = (LinearLayout.LayoutParams) resultButton.getLayoutParams();
        param.weight = 1;
        resultButton.setLayoutParams(param);

        // показ элементов отвечающих за статус выполнения операции
        RelativeLayout alterOperationStatusLayout = (RelativeLayout)
				findViewById(R.id.twf_alterOperationStatus);
        alterOperationStatusLayout.setVisibility(View.VISIBLE);

        Spinner alterSpinner = (Spinner) findViewById(R.id
				.twf_altOperationStatusSpinner);
        OperationStatusDBAdapter dbAdapter = new OperationStatusDBAdapter(
                new ToirDatabaseContext(getApplicationContext()));
        ArrayList<OperationStatus> list = dbAdapter.getAllItems();
        Iterator<OperationStatus> iterator = list.iterator();
        // удаляем из списка статус "Новая", "Отменена"
        while (iterator.hasNext()) {
            OperationStatus item = iterator.next();
            if (item.getUuid().equals(OperationStatusDBAdapter.Status.NEW)) {
                iterator.remove();
            } else if (item.getUuid().equals(
                    OperationStatusDBAdapter.Status.UNCOMPLETE)) {
                iterator.remove();
            }
        }
        ArrayAdapter<OperationStatus> adapter = new
				ArrayAdapter<OperationStatus>(
                getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, list);
        alterSpinner.setAdapter(adapter);
        // по умолчанию ставим статус Выполнена
        for (OperationStatus item : list) {
            if (item.getUuid().equals(OperationStatusDBAdapter.Status
					.COMPLETE)) {
                alterSpinner.setSelection(adapter.getPosition(item));
                break;
            }
        }

        CheckBox checkBox = (CheckBox) findViewById(R.id
				.twf_showAltOperationStatusCheckbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton
				.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Spinner alterSpinner = (Spinner) findViewById(R.id
						.twf_altOperationStatusSpinner);
                if (isChecked) {
                    alterSpinner.setVisibility(View.VISIBLE);
                } else {
                    alterSpinner.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Очищаем от содержимого layout с предпросмотром
     */
    private void clearPhotoContainer() {

        photoContainer.removeAllViewsInLayout();
        ViewGroup.LayoutParams photoParams = photoContainer.getLayoutParams();
        photoParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        photoContainer.setLayoutParams(photoParams);

    }

    /**
     * Возвращает первый шаг операции
     *
     * @return
     */
    private OperationPatternStep getFirstStep() {
        for (OperationPatternStep step : patternSteps) {
            if (step.isFirst_step()) {
                return step;
            }
        }
        return null;
    }

    /**
     * Возвращает шаг операции
     *
     * @param uuid
     * @return
     */
    private OperationPatternStep getStep(String uuid) {
        for (OperationPatternStep step : patternSteps) {
            if (uuid.equals(step.getUuid())) {
                return step;
            }
        }
        return null;
    }

    /**
     * Возвращает список вариантов выполнения шага операции
     *
     * @param step_uuid
     * @return
     */
    private ArrayList<OperationPatternStepResult> getStepResult(String
                                                                        step_uuid) {
        ArrayList<OperationPatternStepResult> resultsList = new ArrayList<OperationPatternStepResult>();
        for (OperationPatternStepResult result : stepsResults) {
            if (step_uuid.equals(result.getOperation_pattern_step_uuid())) {
                resultsList.add(result);
            }
        }
        return resultsList;
    }

    /**
     * Обработчик нажатия кнопки "Сфотографировать"
     */
    private PictureCallback pictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("test", "onPictureTaken - jpeg");
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Toast.makeText(getApplicationContext(),
                        "Image retrieval failed.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                lastPhotoFile = pictureFile.getAbsolutePath();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPreview.mCamera.startPreview();
        }
    };

    private File getOutputMediaFile() {

        File mediaStorageDir = new File(getApplicationContext()
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath());

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Camera Guide", "Required media storage does not exist");
                return null;
            }
        }

        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
        // Locale.ENGLISH).format(new Date());
        String fileName;
        // оригинальное имя файла
        // fileName = "IMG_" + timeStamp + ".jpg";
        fileName = currentStepUuid + ".jpg";
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + fileName);
        return mediaFile;
    }

    /**
     * Класс для вывода предпросмотра с камеры
     *
     * @author Dmitriy Logachov
     */
    public class Preview extends SurfaceView implements SurfaceHolder
            .Callback {

        public SurfaceHolder mHolder;
        public Camera mCamera;

        public Preview(Context context) {
            super(context);
            Log.e("test", "Preview constructor");
            mHolder = getHolder();
            mHolder.addCallback(this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.e("test", "surfaceCreated");
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.e("test", "surfaceChanged");
            mCamera.startPreview();

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.e("test", "surfaceDestroyed");
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // показываем диалог с вопросом почему прекращаем выполнение
            // операции
            final AlertDialog.Builder dialog = new AlertDialog.Builder(
                    OperationActivity.this);
            dialog.setTitle("Отменить выполнение операции?");
            dialog.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
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
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * Устанавливам основной экран приложения
     */
    //@SuppressWarnings("deprecation")
    void setMainLayout(Bundle savedInstanceState) {
        setContentView(R.layout.taskwork_fragment);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundResource(R.drawable.header);
        toolbar.setSubtitle("Обслуживание и ремонт");

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name);
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .build();

        //iprofilelist = new ArrayList<>();
        //users_id = new long[MAX_USER_PROFILE];
        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("О программе").withDescription("Информация о версии").withIcon(FontAwesome.Icon.faw_info).withIdentifier(DRAWER_INFO).withSelectable(false),
                        new PrimaryDrawerItem().withName("Выход").withIcon(FontAwesome.Icon.faw_undo).withIdentifier(DRAWER_EXIT).withSelectable(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            if (drawerItem.getIdentifier() == DRAWER_INFO) {
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("Информация о программе")
                                        .setMessage("TOiR Mobile v1.0.1\n ООО Технологии Энергосбережения (technosber.ru) (c) 2016")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .show();
                            } else if (drawerItem.getIdentifier() == DRAWER_EXIT) {
                                System.exit(0);
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        //if you have many different types of DrawerItems you can magically pre-cache those items to get a better scroll performance
        //make sure to init the cache after the DrawerBuilder was created as this will first clear the cache to make sure no old elements are in
        RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(21, false);
        }
        //getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, UserInfoFragment.newInstance()).commit();
    }
}
