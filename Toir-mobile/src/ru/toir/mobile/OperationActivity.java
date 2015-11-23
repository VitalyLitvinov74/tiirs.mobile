/**
 * 
 */
package ru.toir.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
import ru.toir.mobile.rfid.EquipmentTagStructure;
import ru.toir.mobile.rfid.RfidDriverBase;
import ru.toir.mobile.rfid.TagRecordStructure;
import ru.toir.mobile.utils.DataUtils;
import android.app.Activity;
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
import android.text.format.Time;
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

/**
 * @author Dmitriy Logachov
 * 
 */
public class OperationActivity extends Activity {

	private static final String TAG = "OperationActivity";

	public static final String OPERATION_UUID_EXTRA = "operation_uuid";
	public static final String TASK_UUID_EXTRA = "task_uuid";
	public static final String EQUIPMENT_UUID_EXTRA = "equipment_uuid";
	public static final String EQUIPMENT_TAG_EXTRA = "equipment_tag";

	private OperationPattern pattern;
	private ArrayList<OperationPatternStep> patternSteps;
	private ArrayList<OperationPatternStepResult> stepsResults;
	private ArrayList<OperationResult> operationResults;

	private String taskUuid;
	private String operation_uuid;
	private String equipment_uuid;
	private String operation_result_uuid;
	private String operation_type_uuid;

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

	private RfidDriverBase driver;

	TagRecordStructure tagrecord = new TagRecordStructure();
	TagRecordStructure tagrecord2 = new TagRecordStructure();
	private ArrayList<TagRecordStructure> tagrecords = new ArrayList<TagRecordStructure>();
	EquipmentTagStructure equipmenttag = new EquipmentTagStructure();

	/**
	 * Класс для представления множителей (частоты, напряжения, тока...)
	 * 
	 * @author Dmitriy Logachov
	 * 
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
		setContentView(R.layout.taskwork_fragment);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		resultButtonLayout = (LinearLayout) findViewById(R.id.twf_resultButtonLayout);
		photoContainer = (RelativeLayout) findViewById(R.id.twf_photoContainer);
		rootView = (ScrollView) findViewById(R.id.twf_rootView);

		stepTitle = (TextView) findViewById(R.id.twf_stepTitle);
		stepDescrition = (TextView) findViewById(R.id.twf_step_description);
		step_image = (ImageView) findViewById(R.id.twf_step_image);

		task_title = (TextView) findViewById(R.id.twf_task_title);
		task_status = (TextView) findViewById(R.id.twf_task_status);
		equipment_title = (TextView) findViewById(R.id.twf_equipment_title);
		equipment_status = (TextView) findViewById(R.id.twf_equipment_status);
		equipment_operation = (TextView) findViewById(R.id.twf_equipment_operation);

		// получаем статус и время наряда
		TaskDBAdapter dbTask = new TaskDBAdapter(new ToirDatabaseContext(
				getApplicationContext()));
		Task task = dbTask.getItem(taskUuid);
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		EquipmentOperation equipmentOperation = operationDBAdapter
				.getItem(operation_uuid);
		EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		// получаем шаблон операции
		OperationPatternDBAdapter patternDBAdapter = new OperationPatternDBAdapter(
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
		OperationPatternStepDBAdapter patternStepDBAdapter = new OperationPatternStepDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		patternSteps = patternStepDBAdapter.getItems(pattern.getUuid());

		// получаем варианты выполнения шагов
		ArrayList<String> uuids = new ArrayList<String>();
		for (OperationPatternStep step : patternSteps) {
			uuids.add(step.getUuid());
		}
		OperationPatternStepResultDBAdapter stepResultDBAdapter = new OperationPatternStepResultDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		stepsResults = stepResultDBAdapter.getItems(uuids);

		// получаем список вариантов завершения операции
		OperationResultDBAdapter resultDBAdapter = new OperationResultDBAdapter(
				new ToirDatabaseContext(getApplicationContext()));
		operationResults = resultDBAdapter.getItems(equipmentOperation
				.getOperation_type_uuid());

		/*
		 * cоздаём запись с результатом выполнения операции для фиксации времени
		 * начала выполнения (в текущем варианте просто предотвращаем создание
		 * кучи записей для одной операции)
		 */
		EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
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
		OperationPatternStep step = null;

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
					|| result.getNext_operation_pattern_step_uuid().equals("")) {
				resultButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// если были измерения, сохраняем полученные значения
						if (!measureType.equals(MeasureTypeDBAdapter.Type.NONE)) {
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
						if (!measureType.equals(MeasureTypeDBAdapter.Type.NONE)) {
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
	 * @param measureType
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
					R.layout.fragment_native_camera, photoContainer);

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
	 * @param type
	 * @param resultUuid
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
				EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
						new ToirDatabaseContext(getApplicationContext()));
				// к этому моменту запись с установленной датой начала
				// выполнения уже должна существовать
				operationResult = equipmentOperationResultDBAdapter
						.getItemByOperation(operation_uuid);
				operationResult.setOperation_result_uuid(result.getUuid());
				operation_result_uuid = result.getUuid();
				operationResult.setEnd_date(new Date().getTime());
				equipmentOperationResultDBAdapter.update(operationResult);

				// обновление статуса операции по результату выполнения
				Spinner operationStatusSpinner = (Spinner) findViewById(R.id.twf_altOperationStatusSpinner);
				String operationStatusUuid = ((OperationStatus) operationStatusSpinner
						.getSelectedItem()).getUuid();
				EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
						new ToirDatabaseContext(getApplicationContext()));
				EquipmentOperation operation = operationDBAdapter
						.getItem(operation_uuid);
				operation_type_uuid = operation.getOperation_type_uuid();

				operation.setOperation_status_uuid(operationStatusUuid);
				operationDBAdapter.update(operation);

				// обновляем информацию об операции в метке устройства
				// читаем > обновляем > записываем
				// if (EquipmentTagStructure.getInstance().get_equipment_uuid()
				// == null) {
				// setContentView(R.layout.rfid_read);
				// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				// rfid.read((byte)
				// RFIDDriverC5.READ_EQUIPMENT_OPERATION_LABLE_ID);
				// }

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
					task.setTask_status_uuid(TaskStatusDBAdapter.Status.COMPLETE);
					task.setUpdated(true);
					task.setClose_date(Calendar.getInstance().getTimeInMillis());
					taskDBAdapter.update(task);
				}

				finish();
			}
		});

		ArrayAdapter<OperationResult> resultsAdapter = new ArrayAdapter<OperationResult>(
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
		RelativeLayout alterOperationStatusLayout = (RelativeLayout) findViewById(R.id.twf_alterOperationStatus);
		alterOperationStatusLayout.setVisibility(View.VISIBLE);

		Spinner alterSpinner = (Spinner) findViewById(R.id.twf_altOperationStatusSpinner);
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
		ArrayAdapter<OperationStatus> adapter = new ArrayAdapter<OperationStatus>(
				getApplicationContext(),
				android.R.layout.simple_spinner_dropdown_item, list);
		alterSpinner.setAdapter(adapter);
		// по умолчанию ставим статус Выполнена
		for (OperationStatus item : list) {
			if (item.getUuid().equals(OperationStatusDBAdapter.Status.COMPLETE)) {
				alterSpinner.setSelection(adapter.getPosition(item));
				break;
			}
		}

		CheckBox checkBox = (CheckBox) findViewById(R.id.twf_showAltOperationStatusCheckbox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Spinner alterSpinner = (Spinner) findViewById(R.id.twf_altOperationStatusSpinner);
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
	private ArrayList<OperationPatternStepResult> getStepResult(String step_uuid) {
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
	 * 
	 */
	public class Preview extends SurfaceView implements SurfaceHolder.Callback {

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

	// TODO разобраться зачем это
	public void CallbackOnReadLable(String result) {
		// driver.SetOperationType((byte)
		// RfidDriverC5.READ_EQUIPMENT_OPERATION_LABLE_ID);
		// driver.readTagId((byte)
		// RfidDriverC5.READ_EQUIPMENT_OPERATION_MEMORY);
		driver.readTagId();
	}

	// TODO разобраться зачем это
	public void Callback(String result) {
		if (result == null) {
			setResult(RfidDriverBase.RESULT_RFID_READ_ERROR);
		} else {
			if (result.length() < 100) {
				Toast.makeText(this, "Ответ слишком короткий",
						Toast.LENGTH_SHORT).show();
				return;
			}
			byte out_buffer[];

			// парсим ответ
			equipmenttag.set_equipment_uuid(DataUtils.StringToUUID(result
					.substring(0, 32)));
			equipmenttag.set_last(result.substring(36, 40));
			equipmenttag.set_status(result.substring(32, 36).toLowerCase(
					Locale.ENGLISH));
			tagrecord.operation_date = Long.parseLong(result.substring(40, 56),
					16);
			tagrecord.operation_length = Short.parseShort(
					result.substring(56, 60), 16);
			tagrecord.operation_type = result.substring(60, 64).toLowerCase(
					Locale.ENGLISH);
			tagrecord.operation_result = result.substring(64, 68).toLowerCase(
					Locale.ENGLISH);
			tagrecord.user = result.substring(68, 72).toLowerCase(
					Locale.ENGLISH);
			tagrecords.add(0, tagrecord);
			tagrecord2.operation_date = Long.parseLong(
					result.substring(72, 88), 16);
			tagrecord2.operation_length = Short.parseShort(
					result.substring(88, 92), 16);
			tagrecord2.operation_type = result.substring(92, 96).toLowerCase(
					Locale.ENGLISH);
			tagrecord2.operation_result = result.substring(96, 100)
					.toLowerCase(Locale.ENGLISH);
			tagrecord2.user = result.substring(100, 104).toLowerCase(
					Locale.ENGLISH);
			tagrecords.add(1, tagrecord2);

			// вариант 2 с хранением данных в глобальной структуре
			EquipmentTagStructure.getInstance().set_equipment_uuid(
					DataUtils.StringToUUID(result.substring(0, 32)));
			EquipmentTagStructure.getInstance().set_status(
					result.substring(32, 36).toLowerCase(Locale.ENGLISH));
			EquipmentTagStructure.getInstance().set_last(
					result.substring(36, 40));

			// заполоняем структуру результата обследования
			if (equipmenttag.get_last() == "1")
				equipmenttag.set_last("2");
			else
				equipmenttag.set_last("1");
			// TODO добавить статус оборудования после обслуживания
			// equipmenttag.set_status("?");
			Time now = new Time();
			now.setToNow();
			tagrecord.operation_date = now.toMillis(false);
			// TODO решить как считать время выполнения операции
			tagrecord.operation_length = 0;
			tagrecord.operation_result = operation_result_uuid;

			EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
					new ToirDatabaseContext(getApplicationContext()));
			EquipmentOperation operation = operationDBAdapter
					.getItem(operation_uuid);
			operation.getOperation_type_uuid();
			tagrecord.operation_result = operation_result_uuid;
			tagrecord.operation_type = operation_type_uuid;
			tagrecord.user = AuthorizedUser.getInstance().getUuid();

			if (equipmenttag.get_last() == "1")
				tagrecords.set(0, tagrecord);
			else
				tagrecords.set(1, tagrecord);

			// for (int pointer=0;
			// pointer<equipmenttag.toString().length()+2*tagrecord.toString().length())
			out_buffer = (equipmenttag.toString() + tagrecord.toString() + tagrecord2
					.toString()).getBytes();
			// driver.SetOperationType((byte)
			// RfidDriverC5.WRITE_EQUIPMENT_OPERATION_MEMORY);
			// TODO исправить на новый вариант записи в метку
			// driver.write(out_buffer);
		}
	}

	public void CallbackOnWrite(String result) {
		if (result == null) {
			setResult(RfidDriverBase.RESULT_RFID_WRITE_ERROR);
		} else {
			finish();
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

}
