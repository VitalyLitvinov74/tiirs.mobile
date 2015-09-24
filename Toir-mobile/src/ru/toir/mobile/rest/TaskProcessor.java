/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
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
import ru.toir.mobile.db.tables.EquipmentStatus;
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
import ru.toir.mobile.serializer.EquipmentOperationResultSerializer;
import ru.toir.mobile.serializer.EquipmentOperationSerializer;
import ru.toir.mobile.serializer.MeasureValueSerializer;
import ru.toir.mobile.serializer.TaskResultSerializer;
import ru.toir.mobile.serializer.TaskSerializer;
import ru.toir.mobile.serverapi.CriticalTypeSrv;
import ru.toir.mobile.serverapi.EquipmentDocumentationSrv;
import ru.toir.mobile.serverapi.DocumentationTypeSrv;
import ru.toir.mobile.serverapi.EquipmentOperationSrv;
import ru.toir.mobile.serverapi.EquipmentSrv;
import ru.toir.mobile.serverapi.EquipmentStatusSrv;
import ru.toir.mobile.serverapi.EquipmentTypeSrv;
import ru.toir.mobile.serverapi.MeasureTypeSrv;
import ru.toir.mobile.serverapi.OperationPatternSrv;
import ru.toir.mobile.serverapi.OperationTypeSrv;
import ru.toir.mobile.serverapi.TaskSrv;
import ru.toir.mobile.serverapi.TaskStatusSrv;
import ru.toir.mobile.serverapi.OperationPatternStepResultSrv;
import ru.toir.mobile.serverapi.OperationStatusSrv;
import ru.toir.mobile.serverapi.OperationPatternStepSrv;
import ru.toir.mobile.serverapi.result.TaskResult;
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
	private static final String TASK_GET_URL = "/api/orders/";
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
	Map<String, EquipmentStatus> equipmentStatus = null;

	ArrayList<OperationResult> operationResults = null;
	ArrayList<MeasureValue> measureValues = null;
	ArrayList<EquipmentOperationResult> equipmentOperationResults = null;
	
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
		equipmentStatus = new ArrayMap<String, EquipmentStatus>();
		
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
				
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
				
				TaskSrv[] serverTasks = gson.fromJson(jsonString, TaskSrv[].class);
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

		// получаем данные по шаблонам
		boolean result = false;
		Set<String> set = equipmentOperations.keySet();
		ArrayList<String> operationPatternUuids = new ArrayList<String>();
		for (String uuid: set) {
			operationPatternUuids.add(equipmentOperations.get(uuid).getOperation_pattern_uuid());
		}
		try {
			ReferenceProcessor referenceProcessor = new ReferenceProcessor(mContext);
			Bundle extra = new Bundle();
			extra.putStringArrayList(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID, operationPatternUuids);
			result = referenceProcessor.getOperationPattern(extra);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		return result;

	}
	
	public void saveAllData() {
		Set<String> set;
		
		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext));
		set = tasks.keySet();
		for (String uuid: set) {
			taskDBAdapter.replace(tasks.get(uuid));
		}
		
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(new TOiRDatabaseContext(mContext));
		set = equipments.keySet();
		for (String uuid: set) {
			equipmentDBAdapter.replace(equipments.get(uuid));
		}
		
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(new TOiRDatabaseContext(mContext));
		set = taskStatus.keySet();
		for (String uuid: set) {
			taskStatusDBAdapter.replace(taskStatus.get(uuid));
		}
		
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(mContext));
		set = criticalTypes.keySet();
		for (String uuid: set) {
			criticalTypeDBAdapter.replace(criticalTypes.get(uuid));
		}
		
		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		set = operationTypes.keySet();
		for (String uuid: set) {
			operationTypeDBAdapter.replace(operationTypes.get(uuid));
		}
		
		EquipmentTypeDBAdapter equipmentTypeDBAdapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(mContext));
		set = equipmentTypes.keySet();
		for (String uuid: set) {
			equipmentTypeDBAdapter.replace(equipmentTypes.get(uuid));
		}
		
		DocumentationTypeDBAdapter documentationTypeDBAdapter = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		set = documentationTypes.keySet();
		for (String uuid: set) {
			documentationTypeDBAdapter.replace(documentationTypes.get(uuid));
		}
		
		MeasureTypeDBAdapter measureTypeDBAdapter = new MeasureTypeDBAdapter(new TOiRDatabaseContext(mContext));
		set = measureTypes.keySet();
		for (String uuid: set) {
			measureTypeDBAdapter.replace(measureTypes.get(uuid));
		}
		
		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(new TOiRDatabaseContext(mContext));
		set = equipmentDocumentations.keySet();
		for (String uuid: set) {
			documentationDBAdapter.replace(equipmentDocumentations.get(uuid));
		}
		
		OperationStatusDBAdapter operationStatusDBAdapter = new OperationStatusDBAdapter(new TOiRDatabaseContext(mContext));
		set = operationStatus.keySet();
		for (String uuid: set) {
			operationStatusDBAdapter.replace(operationStatus.get(uuid));
		}
		
		EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(new TOiRDatabaseContext(mContext));
		set = equipmentStatus.keySet();
		for (String uuid: set) {
			equipmentStatusDBAdapter.replace(equipmentStatus.get(uuid));
		}

		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(mContext));
		set = equipmentOperations.keySet();
		for (String uuid: set) {
			operationDBAdapter.replace(equipmentOperations.get(uuid));
		}

		// TODO перед записью в базу операций, нужно получить отдельно шаблоны операций и связанную с ними информацию
		// шаблоны получаем, удалить/переписать лишний код!
		/*
		OperationPatternDBAdapter operationPatternDBAdapter = new OperationPatternDBAdapter(new TOiRDatabaseContext(mContext));
		set = operationPatterns.keySet();
		for (String uuid: set) {
			operationPatternDBAdapter.replace(operationPatterns.get(uuid));
		}
		
		OperationPatternStepDBAdapter patternStepDBAdapter = new OperationPatternStepDBAdapter(new TOiRDatabaseContext(mContext));
		set = operationPatternSteps.keySet();
		for (String uuid: set) {
			patternStepDBAdapter.replace(operationPatternSteps.get(uuid));
		}
		
		OperationPatternStepResultDBAdapter patternStepResultDBAdapter = new OperationPatternStepResultDBAdapter(new TOiRDatabaseContext(mContext));
		set = operationPatternStepResults.keySet();
		for (String uuid: set) {
			patternStepResultDBAdapter.replace(operationPatternStepResults.get(uuid));
		}
		*/
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param serverTask
	 * @return
	 */
	public Task getTask(TaskSrv serverTask) {

		Task item = new Task();
		item.setUuid(serverTask.getId());
		item.setUsers_uuid(serverTask.getEmployeeId());
		item.setCreatedAt(serverTask.getCreatedAtTime());
		item.setChangedAt(serverTask.getChangedAtTime());
		item.setClose_date(serverTask.getCloseDate() == null ? 0 : serverTask.getCloseDateTime());
		
		item.setTask_status_uuid(serverTask.getOrderStatus().getId());
		// добавляем объект статуса наряда
		taskStatus.put(serverTask.getOrderStatus().getId(), getTaskStatus(serverTask.getOrderStatus()));
		
		item.setTask_name("номер " + serverTask.getNumber());
		
		List<EquipmentOperationSrv> operations = serverTask.getItems();
		if (operations != null) {
			for (int i = 0; i < operations.size(); i++) {
				equipmentOperations.put(operations.get(i).getId(), getOperation(operations.get(i), item.getUuid()));
			}
		}
		
		item.setCreatedAt(serverTask.getCreatedAtTime());
		item.setChangedAt(serverTask.getChangedAtTime());

		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param status
	 * @return
	 */
	public TaskStatus getTaskStatus(TaskStatusSrv status) {
		TaskStatus item = new TaskStatus();
		item.setUuid(status.getId());
		item.setTitle(status.getTitle());
		item.setCreatedAt(status.getCreatedAtTime());
		item.setChangedAt(status.getChangedAtTime());
		return item;
	}

	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param operation
	 * @return
	 */
	public EquipmentOperation getOperation(EquipmentOperationSrv operation, String parentUuid) {
		EquipmentOperation item = new EquipmentOperation();
		
		item.setUuid(operation.getId());
		
		item.setTask_uuid(parentUuid);
		
		item.setEquipment_uuid(operation.getEquipment().getId());
		// создаём объект оборудования
		equipments.put(operation.getEquipment().getId(), getEquipment(operation.getEquipment()));
		
		item.setOperation_type_uuid(operation.getOperationType().getId());
		// создаём объект типа операции
		operationTypes.put(operation.getOperationType().getId(), getOperationType(operation.getOperationType()));
		
		item.setOperation_pattern_uuid(operation.getOperationPatternId());
		// TODO шаблоны теперь приходят в другом месте, реализовать получение!!!
		// создаём объект шаблона операции
		//operationPatterns.put(operation.getOperationPatternId(), getOperationPattern(operation.getOperationPattern()));

		
		item.setOperation_status_uuid(operation.getStatus().getId());
		// создаём объект статуса операции
		operationStatus.put(operation.getStatus().getId(), getOperationStatus(operation.getStatus()));
		
		item.setCreatedAt(operation.getCreatedAtTime());
		item.setChangedAt(operation.getChangedAtTime());

		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param status
	 * @return
	 */
	public OperationStatus getOperationStatus(OperationStatusSrv status) {
		OperationStatus item = new OperationStatus();
		item.setUuid(status.getId());
		item.setTitle(status.getTitle());
		item.setCreatedAt(status.getCreatedAtTime());
		item.setChangedAt(status.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param pattern
	 * @return
	 */
	public OperationPattern getOperationPattern(OperationPatternSrv pattern) {
		
		OperationPattern item = new OperationPattern();
		
		item.setUuid(pattern.getId());
		item.setTitle(pattern.getTitle());
		// TODO данные не приходят с сервера
		item.setOperation_type_uuid("");
		item.setCreatedAt(pattern.getCreatedAtTime());
		item.setChangedAt(pattern.getChangedAtTime());

		// создаём объекты шагов шаблона выполнения операции
		List<OperationPatternStepSrv> steps = pattern.getSteps();
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
	public OperationPatternStep getStep(OperationPatternStepSrv step, String parentUuid) {
		OperationPatternStep item = new OperationPatternStep();
		item.setUuid(step.getId());
		item.setOperation_pattern_uuid(parentUuid);
		item.setDescription(step.getDescription());
		item.setImage(step.getImagePath());
		item.setFirst_step(step.getIsFirstStep() == 0 ? false : true);
		item.setLast_step(step.getIsLastStep() == 0 ? false : true);
		item.setTitle(step.getTitle());
		item.setCreatedAt(step.getCreatedAtTime());
		item.setChangedAt(step.getChangedAtTime());
		
		// создаём объекты варантов результатов шагов
		List<OperationPatternStepResultSrv> results = step.getResults();
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
	public OperationPatternStepResult getStepResult(OperationPatternStepResultSrv result, String parrentUuid) {
		OperationPatternStepResult item = new OperationPatternStepResult();
		item.setUuid(result.getId());
		item.setOperation_pattern_step_uuid(parrentUuid);
		String nextStepUuid = result.getNextPatternStepId() == null ? "00000000-0000-0000-0000-000000000000" : result.getNextPatternStepId();
		item.setNext_operation_pattern_step_uuid(nextStepUuid);
		item.setTitle(result.getTitle());
		item.setMeasure_type_uuid(result.getMeasureType().getId());
		// создаём объект варианта измерения
		measureTypes.put(result.getMeasureType().getId(), getMeasureType(result.getMeasureType()));
		item.setCreatedAt(result.getCreatedAtTime());
		item.setChangedAt(result.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public MeasureType getMeasureType(MeasureTypeSrv type) {
		MeasureType item = new MeasureType();
		item.setUuid(type.getId());
		item.setTitle(type.getTitle());
		item.setCreatedAt(type.getCreatedAtTime());
		item.setChangedAt(type.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param operationType
	 * @return
	 */
	public OperationType getOperationType(OperationTypeSrv operationType) {
		OperationType item = new OperationType();
		item.setUuid(operationType.getId());
		item.setTitle(operationType.getTitle());
		item.setCreatedAt(operationType.getCreatedAtTime());
		item.setChangedAt(operationType.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param equipment
	 * @return
	 */
	public Equipment getEquipment(EquipmentSrv equipment) {
		
		Equipment item = new Equipment();
		item.setUuid(equipment.getId());
		item.setTitle(equipment.getName());
		
		item.setEquipment_type_uuid(equipment.getEquipmentType().getId());
		// создаём объект типа оборудования
		equipmentTypes.put(equipment.getEquipmentType().getId(), getEquipmentType(equipment.getEquipmentType()));
		
		item.setCritical_type_uuid(equipment.getCriticalityType().getId());
		// создаём объект типа критичности оборудования
		criticalTypes.put(equipment.getCriticalityType().getId(), getCriticalType(equipment.getCriticalityType()));
		item.setStart_date(equipment.getStartupDateTime());

		List<EquipmentDocumentationSrv> documents = equipment.getDocuments();
		for (int i = 0; i < documents.size(); i++) {
			equipmentDocumentations.put(documents.get(i).getId(), getDocumentation(documents.get(i), item.getUuid()));
		}
		
		item.setLatitude(equipment.getGeoCoordinates().getLatitude());
		item.setLongitude(equipment.getGeoCoordinates().getLongitude());
		item.setTag_id(equipment.getTag());
		
		item.setEquipmentStatus_uuid(equipment.getEquipmentStatus().getId());
		// создаём объект статуса оборудования
		equipmentStatus.put(equipment.getEquipmentStatus().getId(), getEquipmentStatus(equipment.getEquipmentStatus()));

		// TODO данные не приходят с сервера
		item.setInventory_number("");
		// TODO данные не приходят с сервера
		item.setLocation("");
		item.setCreatedAt(equipment.getCreatedAtTime());
		item.setChangedAt(equipment.getChangedAtTime());
		
		return item;
	}
	
	public EquipmentStatus getEquipmentStatus(EquipmentStatusSrv status) {
		EquipmentStatus item = new EquipmentStatus();
		item.setUuid(status.getId());
		item.setTitle(status.getTitle());
		item.setType(status.getType());
		item.setCreatedAt(status.getCreatedAtTime());
		item.setChangedAt(status.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param document
	 * @return
	 */
	public EquipmentDocumentation getDocumentation(EquipmentDocumentationSrv document, String parrentUuid) {
		EquipmentDocumentation item = new EquipmentDocumentation();
		item.setUuid(document.getId());
		item.setDocumentation_type_uuid(document.getDocumentType().getId());
		// создаём объект типа документации
		documentationTypes.put(document.getDocumentType().getId(), getDocumentationType(document.getDocumentType()));
		item.setEquipment_uuid(parrentUuid);
		item.setTitle(document.getTitle());
		item.setPath(document.getPath());
		item.setCreatedAt(document.getCreatedAtTime());
		item.setChangedAt(document.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public DocumentationType getDocumentationType(DocumentationTypeSrv type) {
		DocumentationType item = new DocumentationType();
		item.setUuid(type.getId());
		item.setTitle(type.getTitle());
		item.setCreatedAt(type.getCreatedAtTime());
		item.setChangedAt(type.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public EquipmentType getEquipmentType(EquipmentTypeSrv type) {
		EquipmentType item = new EquipmentType();
		item.setUuid(type.getId());
		item.setTitle(type.getTitle());
		item.setCreatedAt(type.getCreatedAtTime());
		item.setChangedAt(type.getChangedAtTime());
		return item;
	}
	
	/**
	 * преобразуем объект с сервера в локальный объект
	 * @param type
	 * @return
	 */
	public CriticalType getCriticalType(CriticalTypeSrv type) {
		CriticalType item = new CriticalType();
		item.setUuid(type.getId());
		item.setType(type.getValue());
		item.setCreatedAt(type.getCreatedAtTime());
		item.setChangedAt(type.getChangedAtTime());
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

		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext));
		Task task;
		task = adapter.getTaskByUuidAndUpdated(taskUuid);

		if (task != null) {
			TaskResult taskResult = new TaskResult();
			if (taskResult.load(mContext, task.getUuid())) {
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
		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext));
		// TODO необходимо решить и реализовать выборку не отправленных нарядов, либо по текущему пользователю либо все какие есть неотправленные.
		tasks = adapter.getTaskByUserAndUpdated(user_uuid);

		// получаем из базы результаты связанные с нарядами
		ArrayList<TaskResult> taskResults = new ArrayList<TaskResult>();
		for (Task task : tasks) {
			TaskResult taskResult = new TaskResult();
			taskResult.load(mContext, task.getUuid());
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
				// TODO реализовать упаковку результатов в json объект
				//TaskResult taskResult = new TaskResult();
				//taskResult.loadTaskResult(mContext, "a1f3a9af-d05b-4123-858f-a753a46f97d5");
				//TaskResult[] resultArray = new TaskResult[] { taskResult };

				Gson gson = new GsonBuilder()
						.setPrettyPrinting()
						.registerTypeAdapter(Task.class, new TaskSerializer())
						.registerTypeAdapter(TaskResult.class, new TaskResultSerializer())
						.registerTypeAdapter(EquipmentOperation.class, new EquipmentOperationSerializer())
						.registerTypeAdapter(EquipmentOperationResult.class, new EquipmentOperationResultSerializer())
						.registerTypeAdapter(MeasureValue.class, new MeasureValueSerializer()).create();
				
				String json = gson.toJson(tasks);
				Log.d("test", json);

				postData.append("tasks=");
				postData.append(json);

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
