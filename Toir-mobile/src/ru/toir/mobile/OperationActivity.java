/**
 * 
 */
package ru.toir.mobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
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
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.rfid.EquipmentTagStructure;
import ru.toir.mobile.rfid.RFID;
import ru.toir.mobile.rfid.TagRecordStructure;
import ru.toir.mobile.rfid.driver.RFIDDriver;
import ru.toir.mobile.rfid.driver.RFIDDriverC5;
import ru.toir.mobile.utils.DataUtils;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
//import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Dmitriy Logachov
 * 
 */
public class OperationActivity extends Activity {
	public static final String OPERATION_UUID_EXTRA = "operation_uuid";
	public static final String TASK_UUID_EXTRA = "task_uuid";
	public static final String EQUIPMENT_UUID_EXTRA = "equipment_uuid";
	public static final String EQUIPMENT_TAG_EXTRA = "equipment_tag";
	// private EquipmentOperation operation;
	private OperationPattern pattern;
	private ArrayList<OperationPatternStep> patternSteps;
	private ArrayList<OperationPatternStepResult> stepsResults;
	private ArrayList<OperationResult> operationResults;
	private String task_uuid, operation_uuid, equipment_uuid;
	private String taskname = "";
	private String operationname = "";
	private LinearLayout layout;
	private TextView stepTitle;
	private TextView stepDescrition;
	private Button numStepButton;
	private NumberPicker numberPicker;
	private Spinner spinnerSuffix;
	private ArrayAdapter<Suffixes> spinnerSuffixAdapter;
	private ArrayList<Suffixes> suffixList;

	private Camera mCamera;
	private CameraPreview mPreview;
	private View mCameraView;
	private String lastPhotoFile;

	private String driverClassName;
	private Class<?> driverClass;
	private RFIDDriver driver;
	private	RFID rfid;

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
	
	// TODO нужно сделать обработку выхода из activity по нажатию кнопки back
	// и показ оператору диалога с выбором причины отказа от выполнения операции

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
		task_uuid = b.getString(TASK_UUID_EXTRA);
		equipment_uuid = b.getString(EQUIPMENT_UUID_EXTRA);
		setContentView(R.layout.taskwork_fragment);

		layout = (LinearLayout) findViewById(R.id.resultButtonLayout);
		stepTitle = (TextView) findViewById(R.id.stepTitle);
		stepDescrition = (TextView) findViewById(R.id.step_description);
		numStepButton = (Button) findViewById(R.id.numStepButton);

		// получаем статус и время наряда
		TaskDBAdapter dbTask = new TaskDBAdapter(new TOiRDatabaseContext(
				getApplicationContext()));
		Task task = dbTask.getItem(task_uuid);
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		taskname = "Наряд: " + dbTask.getCreateTimeByUUID(task_uuid)
				+ " / Статус: "
				+ taskStatusDBAdapter.getNameByUUID(task.getTask_status_uuid());

		EquipmentDBAdapter eqDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		EquipmentOperation equipmentOperation = operationDBAdapter
				.getItem(operation_uuid);

		// получаем шаблон операции
		OperationPatternDBAdapter patternDBAdapter = new OperationPatternDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		pattern = patternDBAdapter.getItem(equipmentOperation
				.getOperation_pattern_uuid());
		operationname = "Оборудование: "
				+ eqDBAdapter.getEquipsNameByUUID(equipment_uuid)
				+ " / Операция: " + pattern.getTitle();

		// получаем шаги шаблона операции
		OperationPatternStepDBAdapter patternStepDBAdapter = new OperationPatternStepDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		patternSteps = patternStepDBAdapter.getItems(pattern.getUuid());

		// получаем варианты выполнения шагов
		ArrayList<String> uuids = new ArrayList<String>();
		for (OperationPatternStep step : patternSteps) {
			uuids.add(step.getUuid());
		}
		OperationPatternStepResultDBAdapter stepResultDBAdapter = new OperationPatternStepResultDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		stepsResults = stepResultDBAdapter.getItems(uuids);

		// получаем список вариантов завершения операции
		OperationResultDBAdapter resultDBAdapter = new OperationResultDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		operationResults = resultDBAdapter.getItems(equipmentOperation
				.getOperation_type_uuid());

		TextView taskName = (TextView) findViewById(R.id.twf_task_title);
		taskName.setText(taskname);
		TextView operationName = (TextView) findViewById(R.id.twf_equipment_title);
		operationName.setText(operationname);

		/*
		 * cоздаём запись с результатом выполнения операции для фиксации времени
		 * начала выполнения
		 */
		// TODO сделать проверку на наличие результата выполнения!!! чтобы был только один! 
		EquipmentOperationResult operationResult = new EquipmentOperationResult();
		operationResult.setEquipment_operation_uuid(operation_uuid);
		operationResult.setStart_date(new Date().getTime());
		EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		equipmentOperationResultDBAdapter.replace(operationResult);

		// инициализируем драйвер для работы с метками
		// получаем текущий драйвер считывателя
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		driverClassName = sp.getString(getString(R.string.RFIDDriver), "RFIDDriverNull");

		// пытаемся получить класс драйвера
		try {
			driverClass = Class.forName("ru.toir.mobile.rfid.driver." + driverClassName);
		}catch(ClassNotFoundException e){
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			finish();
		}
		
		// пытаемся создать объект драйвера
		try{
			driver = (RFIDDriver)driverClass.newInstance();
		}catch(InstantiationException e){
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			e.printStackTrace();
			finish();
		}catch(IllegalAccessException e){
			setResult(RFID.RESULT_RFID_CLASS_NOT_FOUND);
			e.printStackTrace();
			finish();
		}

		rfid = new RFID(driver);
		rfid.setActivity(this);

		// инициализируем драйвер
		if (!rfid.init((byte)RFIDDriverC5.RW_OPERATION_LABLE)) {
			setResult(RFID.RESULT_RFID_INIT_ERROR);
			finish();
		}	

		// TODO нужно отработать вариант когда activiti будет создаваться вновь после ухода в фон
		// соответственно нужно показывать не первый шаг а текущий на котором приложение ушло в фон
		showStep(getFirstStep().getUuid());
	}
	
	private void showStep(String uuid) {
		
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
		numStepButton.setText(step.get_id() + "");
		layout.removeAllViewsInLayout();
		RelativeLayout photoContainer = (RelativeLayout) findViewById(R.id.photoContainer);
		photoContainer.removeAllViewsInLayout();
		photoContainer.setVisibility(View.INVISIBLE);

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
			if (result.getNext_operation_pattern_step_uuid().equals(
					"00000000-0000-0000-0000-000000000000")) {
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
						ShowFinalStep();
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

			layout.addView(resultButton);

			// выводим элементы интерфейса для ввода значения измеренний
			if (!measureType.equals(MeasureTypeDBAdapter.Type.NONE)) {
				measureUI(measureType);
			}
		}
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
			layout.addView(numberPicker);

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

			layout.addView(spinnerSuffix);
		} else if (measureType.equals(MeasureTypeDBAdapter.Type.VOLTAGE)) {
			layout.addView(numberPicker);

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

			layout.addView(spinnerSuffix);
		} else if (measureType.equals(MeasureTypeDBAdapter.Type.PRESSURE)) {
			layout.addView(numberPicker);

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

			layout.addView(spinnerSuffix);
		} else if (measureType.equals(MeasureTypeDBAdapter.Type.PHOTO)) {
			// инициализировать интерфейс для фотографии
			View cameraView = View.inflate(getApplicationContext(),
					R.layout.fragment_native_camera, null);
			RelativeLayout photoContainer = (RelativeLayout) findViewById(R.id.photoContainer);
			RelativeLayout cameraLayout = new RelativeLayout(
					getApplicationContext());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					320, 240);
			cameraLayout.setLayoutParams(params);
			cameraLayout.addView(cameraView);
			photoContainer.addView(cameraLayout);
			photoContainer.setVisibility(View.VISIBLE);
			
			lastPhotoFile = null;

			// Create our Preview view and set it as the content of our
			// activity.
			boolean opened = safeCameraOpenInView(cameraView);

			if (opened == false) {
				Log.d("CameraGuide", "Error, Camera failed to open");
				return;
			}

			// Trap the capture button.
			Button captureButton = (Button) cameraView
					.findViewById(R.id.button_capture);
			captureButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an image from the camera
					mCamera.takePicture(null, null, mPicture);
				}
			});

		}
	}

	/**
	 * Сохранение результата измерения
	 * @param type
	 * @param resultUuid
	 * @return
	 */
	private boolean saveMeasureValue(String type, String resultUuid) {
		
		MeasureValue value = new MeasureValue();
		MeasureValueDBAdapter adapter = new MeasureValueDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));

		value.setEquipment_operation_uuid(operation_uuid);
		value.setOperation_pattern_step_result_uuid(resultUuid);
		value.setDate(Calendar.getInstance().getTime().getTime());
		
		if (type.equals(MeasureTypeDBAdapter.Type.PHOTO)) {
			if (lastPhotoFile == null || lastPhotoFile.equals("")) {
				Toast.makeText(getApplicationContext(), "Сфотографируйте объект!", Toast.LENGTH_SHORT).show();
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

		adapter.replace(value);
		return true;
	}

	/**
	 * Показываем экран с выбором результата(вердикта) выполнения операции
	 * и возможностью изменить статус операции (вместо "Выполнена" по умолчанию)
	 */
	private void ShowFinalStep() {
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
				Log.d("test", result.getTitle());
				EquipmentOperationResult operationResult = null;
				EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
						new TOiRDatabaseContext(getApplicationContext()));
				operationResult = equipmentOperationResultDBAdapter
						.getItemByOperation(operation_uuid);
				operationResult.setOperation_result_uuid(result.getUuid());
				operationResult.setEnd_date(new Date().getTime());
				equipmentOperationResultDBAdapter.update(operationResult);
				
				// обновление статуса операции по результату выполнения
				Spinner operationStatusSpinner = (Spinner) findViewById(R.id.altOperationStatusSpinner);
				String operationStatusUuid = ((OperationStatus)operationStatusSpinner.getSelectedItem()).getUuid();
				EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
				EquipmentOperation operation = operationDBAdapter.getItem(operation_uuid);
				operation.setOperation_status_uuid(operationStatusUuid);
				operationDBAdapter.update(operation);
				
				// обновляем информацию об операции в метке устройства
				// читаем > обновляем > записываем		
				if (EquipmentTagStructure.getInstance().get_equipment_uuid() == null) 
					 rfid.read((byte)RFIDDriverC5.RW_OPERATION_LABLE);
				

				finish();
			}
		});

		ArrayAdapter<OperationResult> resultsAdapter = new ArrayAdapter<OperationResult>(
				this, android.R.layout.simple_spinner_item, operationResults);
		Spinner spinner = new Spinner(getApplicationContext());
		spinner.setAdapter(resultsAdapter);
		spinner.setTag("result");

		layout.removeAllViewsInLayout();
		layout.addView(spinner);
		layout.addView(resultButton);
		
		// показ элементов отвечающих за статус выполнения операции
		RelativeLayout alterOperationStatusLayout = (RelativeLayout) findViewById(R.id.alterOperationStatus);
		alterOperationStatusLayout.setVisibility(View.VISIBLE);

		Spinner alterSpinner = (Spinner) findViewById(R.id.altOperationStatusSpinner);
		OperationStatusDBAdapter dbAdapter = new OperationStatusDBAdapter(new TOiRDatabaseContext(getApplicationContext()));
		ArrayList<OperationStatus> list = dbAdapter.getAllItems();
		ArrayAdapter<OperationStatus> adapter = new ArrayAdapter<OperationStatus>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
		alterSpinner.setAdapter(adapter);
		// по умолчанию ставим статус Выполнена
		for(OperationStatus item : list) {
			if (item.getUuid().equals(OperationStatusDBAdapter.Status.COMPLETE)) {
				alterSpinner.setSelection(adapter.getPosition(item));
				break;
			}
		}

		CheckBox checkBox = (CheckBox) findViewById(R.id.showAltOperationStatusCheckbox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Spinner alterSpinner = (Spinner) findViewById(R.id.altOperationStatusSpinner);
				if (isChecked) {
					alterSpinner.setVisibility(View.VISIBLE);
				} else {
					alterSpinner.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	/**
	 * Возвращает первый шаг операции
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

	// класс для работы с камеров во фрагменте(возможно можно вынести в отдельный класс)
	/**
	 * Surface on which the camera projects it's capture results. This is
	 * derived both from Google's docs and the excellent StackOverflow answer
	 * provided below.
	 * 
	 * Reference / Credit:
	 * http://stackoverflow.com/questions/7942378/android-camera
	 * -will-not-work-startpreview-fails
	 */
	class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

		// SurfaceHolder
		private SurfaceHolder mHolder;

		// Our Camera.
		private Camera mCamera;

		// Parent Context.
		private Context mContext;

		// Camera Sizing (For rotation, orientation changes)
		private Camera.Size mPreviewSize;

		// List of supported preview sizes
		private List<Camera.Size> mSupportedPreviewSizes;

		// Flash modes supported by this camera
		private List<String> mSupportedFlashModes;

		// View holding this camera.
		private View mCameraView;

		public CameraPreview(Context context, Camera camera, View cameraView) {
			super(context);

			// Capture the context
			mCameraView = cameraView;
			mContext = context;
			setCamera(camera);

			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			mHolder.setKeepScreenOn(true);
		}

		/**
		 * Begin the preview of the camera input.
		 */
		public void startCameraPreview() {
			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Extract supported preview and flash modes from the camera.
		 * 
		 * @param camera
		 */
		private void setCamera(Camera camera) {
			// Source:
			// http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
			mCamera = camera;
			mSupportedPreviewSizes = mCamera.getParameters()
					.getSupportedPreviewSizes();
			mSupportedFlashModes = mCamera.getParameters()
					.getSupportedFlashModes();

			// Set the camera to Auto Flash mode.
			if (mSupportedFlashModes != null
					&& mSupportedFlashModes
							.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				mCamera.setParameters(parameters);
			}

			requestLayout();
		}

		/**
		 * The Surface has been created, now tell the camera where to draw the
		 * preview.
		 * 
		 * @param holder
		 */
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Dispose of the camera preview.
		 * 
		 * @param holder
		 */
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mCamera != null) {
				mCamera.stopPreview();
			}
		}

		/**
		 * React to surface changed events
		 * 
		 * @param holder
		 * @param format
		 * @param w
		 * @param h
		 */
		public void surfaceChanged(SurfaceHolder holder, int format, int w,
				int h) {
			// If your preview can change or rotate, take care of those events
			// here.
			// Make sure to stop the preview before resizing or reformatting it.

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				Camera.Parameters parameters = mCamera.getParameters();

				// Set the auto-focus mode to "continuous"
				parameters
						.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

				// Preview size must exist.
				if (mPreviewSize != null) {
					Camera.Size previewSize = mPreviewSize;
					parameters.setPreviewSize(previewSize.width,
							previewSize.height);
				}

				mCamera.setParameters(parameters);
				mCamera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * Calculate the measurements of the layout
		 * 
		 * @param widthMeasureSpec
		 * @param heightMeasureSpec
		 */
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// Source:
			// http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
			final int width = resolveSize(getSuggestedMinimumWidth(),
					widthMeasureSpec);
			final int height = resolveSize(getSuggestedMinimumHeight(),
					heightMeasureSpec);
			setMeasuredDimension(width, height);

			if (mSupportedPreviewSizes != null) {
				mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes,
						width, height);
			}
		}

		/**
		 * Update the layout based on rotation and orientation changes.
		 * 
		 * @param changed
		 * @param left
		 * @param top
		 * @param right
		 * @param bottom
		 */
		@Override
		protected void onLayout(boolean changed, int left, int top, int right,
				int bottom) {
			// Source:
			// http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
			if (changed) {
				final int width = right - left;
				final int height = bottom - top;

				int previewWidth = width;
				int previewHeight = height;

				if (mPreviewSize != null) {
					Display display = ((WindowManager) mContext
							.getSystemService(Context.WINDOW_SERVICE))
							.getDefaultDisplay();

					switch (display.getRotation()) {
					case Surface.ROTATION_0:
						previewWidth = mPreviewSize.height;
						previewHeight = mPreviewSize.width;
						mCamera.setDisplayOrientation(90);
						break;
					case Surface.ROTATION_90:
						previewWidth = mPreviewSize.width;
						previewHeight = mPreviewSize.height;
						break;
					case Surface.ROTATION_180:
						previewWidth = mPreviewSize.height;
						previewHeight = mPreviewSize.width;
						break;
					case Surface.ROTATION_270:
						previewWidth = mPreviewSize.width;
						previewHeight = mPreviewSize.height;
						mCamera.setDisplayOrientation(180);
						break;
					}
				}

				final int scaledChildHeight = previewHeight * width
						/ previewWidth;
				mCameraView
						.layout(0, height - scaledChildHeight, width, height);
			}
		}

		/**
		 * 
		 * @param sizes
		 * @param width
		 * @param height
		 * @return
		 */
		private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes,
				int width, int height) {
			// Source:
			// http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
			Camera.Size optimalSize = null;

			final double ASPECT_TOLERANCE = 0.1;
			double targetRatio = (double) height / width;

			// Try to find a size match which suits the whole screen minus the
			// menu on the left.
			for (Camera.Size size : sizes) {

				if (size.height != width)
					continue;
				double ratio = (double) size.width / size.height;
				if (ratio <= targetRatio + ASPECT_TOLERANCE
						&& ratio >= targetRatio - ASPECT_TOLERANCE) {
					optimalSize = size;
				}
			}

			// If we cannot find the one that matches the aspect ratio, ignore
			// the requirement.
			if (optimalSize == null) {
				// Backup in case we don't get a size.
			}

			return optimalSize;
		}
	}

	/**
	 * Picture Callback for handling a picture capture and saving it out to a
	 * file.
	 */
	private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

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
				// Restart the camera preview.
				safeCameraOpenInView(mCameraView);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * Used to return the camera File output.
	 * 
	 * @return
	 */
	private File getOutputMediaFile() {

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"toir");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Camera Guide", "Required media storage does not exist");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}

	/**
	 * Recommended "safe" way to open the camera.
	 * 
	 * @param view
	 * @return
	 */
	private boolean safeCameraOpenInView(View view) {
		boolean qOpened = false;
		releaseCameraAndPreview();
		mCamera = getCameraInstance();
		mCameraView = view;
		qOpened = (mCamera != null);

		if (qOpened == true) {
			mPreview = new CameraPreview(getApplicationContext(), mCamera, view);
			FrameLayout preview = (FrameLayout) view
					.findViewById(R.id.camera_preview);
			preview.addView(mPreview);
			mPreview.startCameraPreview();
		}
		return qOpened;
	}

	/**
	 * Safe method for getting a camera instance.
	 * 
	 * @return
	 */
	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c; // returns null if camera is unavailable
	}

	/**
	 * Clear any existing preview / camera.
	 */
	private void releaseCameraAndPreview() {

		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
		if (mPreview != null) {
			mPreview.destroyDrawingCache();
			mPreview.mCamera = null;
		}
	}

	public void Callback(String result) {
		if(result == null){
			setResult(RFID.RESULT_RFID_READ_ERROR);	
		} else {
			if (result.length()<100)
				{
				 Toast.makeText(this, "Ответ слишком короткий",Toast.LENGTH_SHORT).show();					
				 return;
				}
			// парсим ответ
			equipmenttag.set_equipment_uuid(DataUtils.StringToUUID(result.substring(0, 32)));
			equipmenttag.set_status(result.substring(32, 36).toLowerCase(Locale.ENGLISH));
			equipmenttag.set_last(result.substring(36, 40));
			tagrecord.operation_date=Long.parseLong(result.substring(40, 56),16);
			tagrecord.operation_length = Short.parseShort(result.substring(56, 60),16);
			tagrecord.operation_type = result.substring(60, 64).toLowerCase(Locale.ENGLISH);
			tagrecord.operation_result = result.substring(64, 68).toLowerCase(Locale.ENGLISH);
			tagrecord.user = result.substring(68, 72).toLowerCase(Locale.ENGLISH);
			tagrecords.add(0,tagrecord);
			tagrecord2.operation_date=Long.parseLong(result.substring(72, 88),16);
			tagrecord2.operation_length = Short.parseShort(result.substring(88, 92),16);
			tagrecord2.operation_type = result.substring(92, 96).toLowerCase(Locale.ENGLISH);
			tagrecord2.operation_result = result.substring(96, 100).toLowerCase(Locale.ENGLISH);
			tagrecord2.user = result.substring(100, 104).toLowerCase(Locale.ENGLISH);
			tagrecords.add(1,tagrecord2);

			// вариант 2 с хранением данных в глобальной структуре 
			EquipmentTagStructure.getInstance().set_equipment_uuid(DataUtils.StringToUUID(result.substring(0, 32)));
			EquipmentTagStructure.getInstance().set_status(result.substring(32, 36).toLowerCase(Locale.ENGLISH));
			EquipmentTagStructure.getInstance().set_last(result.substring(36, 40));
		}
	}
}
