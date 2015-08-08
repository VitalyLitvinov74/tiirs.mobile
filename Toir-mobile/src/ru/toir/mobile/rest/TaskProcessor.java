/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import com.google.gson.Gson;

import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.TaskResult;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.MeasureType;
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.OperationPattern;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.db.tables.OperationPatternStepResult;
import ru.toir.mobile.db.tables.OperationResult;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.CriticalityType;
import ru.toir.mobile.serverapi.Document;
import ru.toir.mobile.serverapi.DocumentType;
import ru.toir.mobile.serverapi.Item;
import ru.toir.mobile.serverapi.OrderStatus;
import ru.toir.mobile.serverapi.Result;
import ru.toir.mobile.serverapi.Status;
import ru.toir.mobile.serverapi.Step;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 */
public class TaskProcessor {

	private Context mContext;
	private static final String TASK_GET_URL = "/api/ordershierarchy/";
	private static final String TASK_SEND_RESULT_URL = "/taskresult.php";
	private String mServerUrl;

	
	Map<String, Task> tasks = null;
	Map<String, EquipmentOperation> equipmentOperations = null;
	Map<String, Equipment> equipments = null;
	Map<String, TaskStatus> taskStatus = null;
	Map<String, CriticalType> criticalTypes = null;
	Map<String, OperationType> operationTypes = null;
	Map<String, EquipmentType> equipmentTypes = null;
	Map<String, OperationPattern> operationPatterns = null;
	Map<String, MeasureType> measureTypes = null;
	Map<String, OperationPatternStep> operationPatternSteps = null;
	Map<String, OperationPatternStepResult> operationPatternStepResults = null;
	Map<String, OperationStatus> operationStatus = null;
	Map<String, DocumentationType> documentationTypes = null;
	Map<String, EquipmentDocumentation> equipmentDocumentations = null;

	ArrayList<OperationResult> operationResults = null;
	ArrayList<MeasureValue> measureValues = null;
	ArrayList<EquipmentOperationResult> equipmentOperationResults = null;
	
	// TODO удалить когда с сервера будут приезжать метки для оборудования
	int tagId = 1;
	
	
	public TaskProcessor(Context context) throws Exception {
		mContext = context;

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		// урл к которому будем обращаться с запросами
		mServerUrl = sp.getString(context.getString(R.string.serverUrl), "");

		if (mServerUrl.equals("")) {
			throw new Exception("URL сервера не указан!");
		}
		
		tasks = new ArrayMap<String, Task>();
		equipments = new ArrayMap<String, Equipment>();
		equipmentOperations = new ArrayMap<String, EquipmentOperation>();
		taskStatus = new ArrayMap<String, TaskStatus>();
		criticalTypes = new ArrayMap<String, CriticalType>();
		operationTypes = new ArrayMap<String, OperationType>();
		equipmentTypes = new ArrayMap<String, EquipmentType>();
		operationPatterns = new ArrayMap<String, OperationPattern>();
		documentationTypes = new ArrayMap<String, DocumentationType>();
		measureTypes = new ArrayMap<String, MeasureType>();
		operationPatternSteps = new ArrayMap<String, OperationPatternStep>();
		operationPatternStepResults = new ArrayMap<String, OperationPatternStepResult>();
		equipmentDocumentations = new ArrayMap<String, EquipmentDocumentation>();
		operationStatus = new ArrayMap<String, OperationStatus>();
		
		operationResults = new ArrayList<OperationResult>();
		measureValues = new ArrayList<MeasureValue>();
		equipmentOperationResults = new ArrayList<EquipmentOperationResult>();
		
	}

	/**
	 * Получение нарядов со статусом "Новый"
	 * @param bundle
	 * @return
	 */
	public boolean GetTask(Bundle bundle) {
		URI requestUri = null;
		//String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String token = AuthorizedUser.getInstance().getToken();

		String jsonString = null;

		try {
			requestUri = new URI(mServerUrl + TASK_GET_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("bearer " + token);
			headers.put("Authorization", tList);

			Request request = new Request(Method.GET, requestUri, headers, null);
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {
				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);
				
				ru.toir.mobile.serverapi.Task[] serverTasks = new Gson().fromJson(jsonString, ru.toir.mobile.serverapi.Task[].class);
				if (serverTasks != null) {
					for (int i = 0; i < serverTasks.length; i++) {
						tasks.put(serverTasks[i].getId(), getTask(serverTasks[i]));
					}
				}

			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// всё полученные данные заносим в базу
		saveAllData();
		
		return true;

	}
	
	public void saveAllData() {
		Set<String> set;
		
		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = tasks.keySet();
		for (String uuid: set) {
			taskDBAdapter.replace(tasks.get(uuid));
		}
		taskDBAdapter.close();
		
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = equipments.keySet();
		for (String uuid: set) {
			equipmentDBAdapter.replace(equipments.get(uuid));
		}
		equipmentDBAdapter.close();
		
		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = equipmentOperations.keySet();
		for (String uuid: set) {
			operationDBAdapter.replace(equipmentOperations.get(uuid));
		}
		operationDBAdapter.close();
		
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = taskStatus.keySet();
		for (String uuid: set) {
			taskStatusDBAdapter.replace(taskStatus.get(uuid));
		}
		taskStatusDBAdapter.close();
		
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = criticalTypes.keySet();
		for (String uuid: set) {
			criticalTypeDBAdapter.replace(criticalTypes.get(uuid));
		}
		criticalTypeDBAdapter.close();
		
		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = operationTypes.keySet();
		for (String uuid: set) {
			operationTypeDBAdapter.replace(operationTypes.get(uuid));
		}
		operationDBAdapter.close();
		
		EquipmentTypeDBAdapter equipmentTypeDBAdapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = equipmentTypes.keySet();
		for (String uuid: set) {
			equipmentTypeDBAdapter.replace(equipmentTypes.get(uuid));
		}
		equipmentDBAdapter.close();
		
		OperationPatternDBAdapter operationPatternDBAdapter = new OperationPatternDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = operationPatterns.keySet();
		for (String uuid: set) {
			operationPatternDBAdapter.replace(operationPatterns.get(uuid));
		}
		operationPatternDBAdapter.close();
		
		DocumentationTypeDBAdapter documentationTypeDBAdapter = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = documentationTypes.keySet();
		for (String uuid: set) {
			documentationTypeDBAdapter.replace(documentationTypes.get(uuid));
		}
		documentationTypeDBAdapter.close();
		
		MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = measureTypes.keySet();
		for (String uuid: set) {
			measureTypeDBAdapter.replace(measureTypes.get(uuid));
		}
		measureTypeDBAdapter.close();
		
		OperationPatternStepDBAdapter patternStepDBAdapter = new OperationPatternStepDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = operationPatternSteps.keySet();
		for (String uuid: set) {
			patternStepDBAdapter.replace(operationPatternSteps.get(uuid));
		}
		patternStepDBAdapter.close();
		
		OperationPatternStepResultDBAdapter patternStepResultDBAdapter = new OperationPatternStepResultDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = operationPatternStepResults.keySet();
		for (String uuid: set) {
			patternStepResultDBAdapter.replace(operationPatternStepResults.get(uuid));
		}
		patternStepResultDBAdapter.close();
		
		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = equipmentDocumentations.keySet();
		for (String uuid: set) {
			documentationDBAdapter.replace(equipmentDocumentations.get(uuid));
		}
		documentationDBAdapter.close();
		
		OperationStatusDBAdapter operationStatusDBAdapter = new OperationStatusDBAdapter(new TOiRDatabaseContext(mContext)).open();
		set = operationStatus.keySet();
		for (String uuid: set) {
			operationStatusDBAdapter.replace(operationStatus.get(uuid));
		}
		operationStatusDBAdapter.close();
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param serverTask
	 * @return
	 */
	public Task getTask(ru.toir.mobile.serverapi.Task serverTask) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

		Task item = new Task();
		item.setUuid(serverTask.getId());
		// TODO здесь нужен uuid пользователя, с сервера пока не приходит
		item.setUsers_uuid("4462ed77-9bf0-4542-b127-f4ecefce49da");
		try {
			item.setCreate_date(dateFormat.parse(serverTask.getCreatedAt()).getTime() / 1000);
		} catch(ParseException e) {
			e.printStackTrace();
		}
		try {
			item.setModify_date(dateFormat.parse(serverTask.getChangedAt()).getTime() / 1000);
		} catch(ParseException e) {
			e.printStackTrace();
		}
		item.setClose_date(serverTask.getCloseDate());
		
		item.setTask_status_uuid(serverTask.getOrderStatus().getId());
		// добавляем объект статуса наряда
		taskStatus.put(serverTask.getOrderStatus().getId(), getTaskStatus(serverTask.getOrderStatus()));
		
		List<Item> operations = serverTask.getItems();
		if (operations != null) {
			for (int i = 0; i < operations.size(); i++) {
				equipmentOperations.put(operations.get(i).getId(), getOperation(operations.get(i), item.getUuid()));
			}
		}

		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param status
	 * @return
	 */
	public TaskStatus getTaskStatus(OrderStatus status) {
		TaskStatus item = new TaskStatus();
		item.setUuid(status.getId());
		item.setTitle(status.getTitle());
		return item;
	}

	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param operation
	 * @return
	 */
	public EquipmentOperation getOperation(Item operation, String parentUuid) {
		EquipmentOperation item = new EquipmentOperation();
		
		item.setUuid(operation.getId());
		
		item.setTask_uuid(parentUuid);
		
		item.setEquipment_uuid(operation.getEquipment().getId());
		// создаём объект оборудования
		equipments.put(operation.getEquipment().getId(), getEquipment(operation.getEquipment()));
		
		item.setOperation_type_uuid(operation.getOperationType().getId());
		// создаём объект типа операции
		operationTypes.put(operation.getOperationType().getId(), getOperationType(operation.getOperationType()));
		
		item.setOperation_pattern_uuid(operation.getOperationPattern().getId());
		// создаём объект шаблона операции
		operationPatterns.put(operation.getOperationPattern().getId(), getOperationPattern(operation.getOperationPattern()));
		
		item.setOperation_status_uuid(operation.getStatus().getId());
		// создаём объект статуса операции
		operationStatus.put(operation.getStatus().getId(), getOperationStatus(operation.getStatus()));

		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param status
	 * @return
	 */
	public OperationStatus getOperationStatus(Status status) {
		OperationStatus item = new OperationStatus();
		item.setUuid(status.getId());
		item.setTitle(status.getTitle());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param pattern
	 * @return
	 */
	public OperationPattern getOperationPattern(ru.toir.mobile.serverapi.OperationPattern pattern) {
		
		OperationPattern item = new OperationPattern();
		
		item.setUuid(pattern.getId());
		item.setTitle(pattern.getTitle());

		// создаём объекты шагов шаблона выполнения операции
		List<Step> steps = pattern.getSteps();
		if (steps != null) {
			for (int i = 0; i < steps.size(); i++) {
				operationPatternSteps.put(steps.get(i).getId(), getStep(steps.get(i), item.getUuid()));
			}
		}
		
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param step
	 * @return
	 */
	public OperationPatternStep getStep(Step step, String parentUuid) {
		OperationPatternStep item = new OperationPatternStep();
		item.setUuid(step.getId());
		item.setOperation_pattern_uuid(parentUuid);
		item.setDescription(step.getDescription());
		item.setImage(step.getImagePath());
		item.setFirst_step(step.getIsFirstStep() == 0 ? false : true);
		item.setLast_step(step.getIsLastStep() == 0 ? false : true);
		// TODO исправить как с сервера будут приходить необходимые данные
		item.setName("");
		
		// создаём объекты варантов результатов шагов
		List<Result> results = step.getResults();
		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				operationPatternStepResults.put(results.get(i).getId(), getStepResult(results.get(i), item.getUuid()));
			} 
		}
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param result
	 * @return
	 */
	public OperationPatternStepResult getStepResult(Result result, String parrentUuid) {
		OperationPatternStepResult item = new OperationPatternStepResult();
		item.setUuid(result.getId());
		item.setOperation_pattern_step_uuid(parrentUuid);
		item.setNext_operation_pattern_step_uuid(result.getNextPatternStep().getId());
		item.setTitle(result.getTitle());
		item.setMeasure_type_uuid(result.getMeasureType().getId());
		// создаём объект варианта измерения
		measureTypes.put(result.getMeasureType().getId(), getMeasureType(result.getMeasureType()));
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public MeasureType getMeasureType(ru.toir.mobile.serverapi.MeasureType type) {
		MeasureType item = new MeasureType();
		item.setUuid(type.getId());
		item.setTitle(type.getTitle());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param operationType
	 * @return
	 */
	public OperationType getOperationType(ru.toir.mobile.serverapi.OperationType operationType) {
		OperationType item = new OperationType();
		item.setUuid(operationType.getId());
		item.setTitle(operationType.getTitle());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param equipment
	 * @return
	 */
	public Equipment getEquipment(ru.toir.mobile.serverapi.Equipment equipment) {

		Equipment item = new Equipment();
		item.setUuid(equipment.getId());
		item.setTitle(equipment.getName());
		
		item.setEquipment_type_uuid(equipment.getEquipmentType().getId());
		// создаём объект типа оборудования
		equipmentTypes.put(equipment.getEquipmentType().getId(), getEquipmentType(equipment.getEquipmentType()));
		
		item.setCritical_type_uuid(equipment.getCriticalityType().getId());
		// создаём объект типа критичности оборудования
		criticalTypes.put(equipment.getCriticalityType().getId(), getCriticalType(equipment.getCriticalityType()));
		
		// TODO нужна дата ввода оборудования в эксплуатацию, пока не приходит с сервера
		item.setStart_date(0);
		
		List<Document> documents = equipment.getDocuments();
		for (int i = 0; i < documents.size(); i++) {
			equipmentDocumentations.put(documents.get(i).getId(), getDocumentation(documents.get(i), item.getUuid()));
		}
		
		item.setLatitude(equipment.getGeoCoordinates().getLatitude());
		item.setLongitude(equipment.getGeoCoordinates().getLongitude());
		
		// TODO нужна метка оборудования, пока не приходит с сервера
		item.setTag_id("000000" + tagId);
		tagId++;
		
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param document
	 * @return
	 */
	public EquipmentDocumentation getDocumentation(Document document, String parrentUuid) {
		EquipmentDocumentation item = new EquipmentDocumentation();
		item.setUuid(document.getId());
		item.setDocumentation_type_uuid(document.getDocumentType().getId());
		// создаём объект типа документации
		documentationTypes.put(document.getDocumentType().getId(), getDocumentationType(document.getDocumentType()));
		item.setEquipment_uuid(parrentUuid);
		item.setTitle(document.getTitle());
		item.setPath(document.getPath());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public DocumentationType getDocumentationType(DocumentType type) {
		DocumentationType item = new DocumentationType();
		item.setUuid(type.getId());
		item.setTitle(type.getTitle());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public EquipmentType getEquipmentType(ru.toir.mobile.serverapi.EquipmentType type) {
		EquipmentType item = new EquipmentType();
		item.setUuid(type.getId());
		item.setTitle(type.getTitle());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public CriticalType getCriticalType(CriticalityType type) {
		CriticalType item = new CriticalType();
		item.setUuid(type.getId());
		item.setType(type.getValue());
		return item;
	}

	/**
	 * Отправка результатов выполнения наряда.
	 * @param bundle
	 * @return
	 */
	public boolean TaskSendResult(Bundle bundle) {
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String taskUuid = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TASK_UUID);

		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
		Task task;
		task = adapter.getTaskByUuidAndUpdated(taskUuid);
		adapter.close();

		if (task != null) {
			TaskResult taskResult = new TaskResult(mContext);
			if (taskResult.getTaskResult(task.getUuid())) {
				ArrayList<TaskResult> taskResults = new ArrayList<TaskResult>();
				taskResults.add(taskResult);
				return TasksSendResults(taskResults, token);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Отправка результатов выполнения нарядов.
	 * @param bundle
	 * @return
	 */
	public boolean TasksSendResult(Bundle bundle) {
		
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		
		String user_uuid = AuthorizedUser.getInstance().getUuid();
		ArrayList<Task> tasks;
		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
		// TODO необходимо решить и реализовать выборку не отправленных нарядов, либо по текущему пользователю либо все какие есть неотправленные.
		tasks = adapter.getTaskByUserAndUpdated(user_uuid);
		adapter.close();

		// получаем из базы результаты связанные с нарядами
		ArrayList<TaskResult> taskResults = new ArrayList<TaskResult>();
		for (Task task : tasks) {
			TaskResult taskResult = new TaskResult(mContext);
			taskResult.getTaskResult(task.getUuid());
			taskResults.add(taskResult);
		}

		return TasksSendResults(taskResults, token);
	}
	
	/**
	 * Отправка результатов выполнения нарядов на сервер
	 * @return
	 */
	private boolean TasksSendResults(ArrayList<TaskResult> tasks, String token) {

		URI requestUri = null;
		String jsonString = null;

		try {
			requestUri = new URI(mServerUrl + TASK_SEND_RESULT_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);
			
			if (tasks != null) {
				StringBuilder postData = new StringBuilder();
				for (TaskResult taskResult : tasks) {
					// TODO реализовать упаковку результатов в json объект 
					postData.append("tasks[]=");
					postData.append(taskResult.mTask.getUuid());
				}

				Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
				Response response = new RestClient().execute(request);
				if (response.mStatus == 200) {
					// TODO реализовать разбор ответа с подтверждением об отправке результатов
					// TODO реализовать изменение статусов данных(updated) на "отправлено"
					jsonString = new String(response.mBody, "UTF-8");
					JSONArray jsonArray = new JSONArray(jsonString);
				} else {
					return false;
				}				
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
}
