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
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
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
import ru.toir.mobile.rfid.RFID;
import ru.toir.mobile.rfid.TagRecordStructure;
import ru.toir.mobile.rfid.driver.RFIDDriver;
import ru.toir.mobile.rfid.driver.RFIDDriverC5;
import ru.toir.mobile.utils.DataUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
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

	private OperationPattern pattern;
	private ArrayList<OperationPatternStep> patternSteps;
	private ArrayList<OperationPatternStepResult> stepsResults;
	private ArrayList<OperationResult> operationResults;
	private String task_uuid, operation_uuid, equipment_uuid,operation_result_uuid,operation_type_uuid;
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
	private ImageView step_image;

	private String lastPhotoFile;
	Preview mPreview;

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
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		layout = (LinearLayout) findViewById(R.id.resultButtonLayout);
		stepTitle = (TextView) findViewById(R.id.stepTitle);
		stepDescrition = (TextView) findViewById(R.id.step_description);
		numStepButton = (Button) findViewById(R.id.numStepButton);
		step_image = (ImageView) findViewById(R.id.step_image);
		
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
		 * начала выполнения (в текущем варианте просто предотвращаем создание
		 * кучи записей для одной операции)
		 */
		EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		EquipmentOperationResult operationResult;
		operationResult = equipmentOperationResultDBAdapter
				.getItemByOperation(operation_uuid);
		if (operationResult == null) {
			operationResult = new EquipmentOperationResult();
			operationResult.setEquipment_operation_uuid(operation_uuid);
			operationResult.setStart_date(new Date().getTime());
			equipmentOperationResultDBAdapter.replace(operationResult);
		}

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
		if (!rfid.init((byte)RFIDDriverC5.READ_EQUIPMENT_OPERATION_LABLE_ID)) {
			setResult(RFID.RESULT_RFID_INIT_ERROR);
			finish();
		}	

		showStep(getFirstStep().getUuid());
	}

	public void cancelOnClick(View view){
		setResult(RESULT_CANCELED);
		finish();
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

		//stepTitle.setText(step.getTitle());
		stepTitle.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getPackageName() + File.separator + "img" + File.separator+ step.getImage());
		stepDescrition.setText(step.getDescription());
		numStepButton.setText(step.get_id() + "");
		layout.removeAllViewsInLayout();
		RelativeLayout photoContainer = (RelativeLayout) findViewById(R.id.photoContainer);
		photoContainer.removeAllViewsInLayout();
		photoContainer.setVisibility(View.INVISIBLE);

		File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getPackageName() + File.separator + "img" + File.separator+ step.getImage());						
		if(imgFile.exists() && imgFile.isFile()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		    step_image.setImageBitmap(myBitmap);			    			    
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
					"00000000-0000-0000-0000-000000000000") || result.getNext_operation_pattern_step_uuid().equals("")) {
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
				new TOiRDatabaseContext(getApplicationContext()));

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
				// к этому моменту запись с установленной датой начала
				// выполнения уже должна существовать
				operationResult = equipmentOperationResultDBAdapter
						.getItemByOperation(operation_uuid);
				operationResult.setOperation_result_uuid(result.getUuid());
				operation_result_uuid = result.getUuid();
				operationResult.setEnd_date(new Date().getTime());
				equipmentOperationResultDBAdapter.update(operationResult);

				// обновление статуса операции по результату выполнения
				Spinner operationStatusSpinner = (Spinner) findViewById(R.id.altOperationStatusSpinner);
				String operationStatusUuid = ((OperationStatus) operationStatusSpinner
						.getSelectedItem()).getUuid();
				EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
						new TOiRDatabaseContext(getApplicationContext()));
				EquipmentOperation operation = operationDBAdapter
						.getItem(operation_uuid);
				operation_type_uuid = operation.getOperation_type_uuid();

				operation.setOperation_status_uuid(operationStatusUuid);
				operationDBAdapter.update(operation);

				// обновляем информацию об операции в метке устройства
				// читаем > обновляем > записываем		
				if (EquipmentTagStructure.getInstance().get_equipment_uuid() == null)
					{
					 setContentView(R.layout.rfid_read);
					 setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					 rfid.read((byte)RFIDDriverC5.READ_EQUIPMENT_OPERATION_LABLE_ID);
					}				
				//finish();
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
		OperationStatusDBAdapter dbAdapter = new OperationStatusDBAdapter(
				new TOiRDatabaseContext(getApplicationContext()));
		ArrayList<OperationStatus> list = dbAdapter.getAllItems();
		Iterator<OperationStatus> iterator = list.iterator();
		// удаляем из списка статус "Новая", "Отменена"
		while (iterator.hasNext()) {
			OperationStatus item = iterator.next();
			if (item.getUuid().equals(OperationStatus.Extras.STATUS_UUID_NEW)) {
				iterator.remove();
			} else if (item.getUuid().equals(
					OperationStatus.Extras.STATUS_UUID_CANCELED)) {
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

		CheckBox checkBox = (CheckBox) findViewById(R.id.showAltOperationStatusCheckbox);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
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

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.ENGLISH).format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
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

	public void CallbackOnReadLable(String result) {
		rfid.SetOperationType((byte)RFIDDriverC5.READ_EQUIPMENT_OPERATION_LABLE_ID);
		rfid.read((byte)RFIDDriverC5.READ_EQUIPMENT_OPERATION_MEMORY);
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
			byte out_buffer[];
			
			// парсим ответ
			equipmenttag.set_equipment_uuid(DataUtils.StringToUUID(result.substring(0, 32)));
			equipmenttag.set_last(result.substring(36, 40));
			equipmenttag.set_status(result.substring(32, 36).toLowerCase(Locale.ENGLISH));
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
			
			// заполоняем структуру результата обследования 
			if (equipmenttag.get_last()=="1")
				equipmenttag.set_last("2");
			else equipmenttag.set_last("1");
			// TODO добавить статус оборудования после обслуживания
			// equipmenttag.set_status("?");
			Time now = new Time();
			now.setToNow();
			tagrecord.operation_date = now.toMillis(false);
			// TODO решить как считать время выполнения операции
			tagrecord.operation_length = 0;
			tagrecord.operation_result = operation_result_uuid;
			
			EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
					new TOiRDatabaseContext(getApplicationContext()));
			EquipmentOperation operation = operationDBAdapter
					.getItem(operation_uuid);
			operation.getOperation_type_uuid();
			tagrecord.operation_result = operation_result_uuid;
			tagrecord.operation_type = operation_type_uuid;
			tagrecord.user = AuthorizedUser.getInstance().getUuid();

			if (equipmenttag.get_last()=="1")
				tagrecords.set(0,tagrecord);
			else tagrecords.set(1, tagrecord);
			
			//for (int pointer=0; pointer<equipmenttag.toString().length()+2*tagrecord.toString().length())
			out_buffer = (equipmenttag.toString() + tagrecord.toString() + tagrecord2.toString()).getBytes();
			rfid.SetOperationType((byte)RFIDDriverC5.WRITE_EQUIPMENT_OPERATION_MEMORY);
			rfid.write(out_buffer);
		}
	}

	public void CallbackOnWrite(String result) {
		if(result == null){
			setResult(RFID.RESULT_RFID_WRITE_ERROR);	
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
			dialog.setTitle("Отмена выполнения операции");
			dialog.setPositiveButton("Продолжить",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			dialog.setNegativeButton("Выйти",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			dialog.show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
