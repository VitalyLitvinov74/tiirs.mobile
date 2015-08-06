/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import com.google.gson.Gson;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.TaskResult;
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
	private static final String TASK_CONFIRMATION_URL = "/confirm.php";
	private static final String TASK_SEND_RESULT_URL = "/taskresult.php";
	private String mServerUrl;

	
	ArrayList<Task> tasks = null;
	ArrayList<EquipmentOperation> equipmentOperations = null;
	ArrayList<Equipment> equipments = null;
	ArrayList<TaskStatus> taskStatus = null;
	ArrayList<CriticalType> criticalTypes = null;
	ArrayList<OperationType> operationTypes = null;
	ArrayList<EquipmentType> equipmentTypes = null;
	ArrayList<OperationPattern> operationPatterns = null;
	ArrayList<MeasureType> measureTypes = null;
	ArrayList<OperationPatternStep> operationPatternSteps = null;
	ArrayList<OperationPatternStepResult> operationPatternStepResults = null;
	ArrayList<OperationStatus> operationStatus = null;
	ArrayList<DocumentationType> documentationTypes = null;
	ArrayList<EquipmentDocumentation> equipmentDocumentations = null;

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
		
		tasks = new ArrayList<Task>();
		equipments = new ArrayList<Equipment>();
		equipmentOperations = new ArrayList<EquipmentOperation>();
		taskStatus = new ArrayList<TaskStatus>();
		criticalTypes = new ArrayList<CriticalType>();
		operationTypes = new ArrayList<OperationType>();
		equipmentTypes = new ArrayList<EquipmentType>();
		operationPatterns = new ArrayList<OperationPattern>();
		documentationTypes = new ArrayList<DocumentationType>();
		measureTypes = new ArrayList<MeasureType>();
		operationResults = new ArrayList<OperationResult>();
		operationPatternSteps = new ArrayList<OperationPatternStep>();
		operationPatternStepResults = new ArrayList<OperationPatternStepResult>();
		equipmentDocumentations = new ArrayList<EquipmentDocumentation>();
		measureValues = new ArrayList<MeasureValue>();
		equipmentOperationResults = new ArrayList<EquipmentOperationResult>();
		operationStatus = new ArrayList<OperationStatus>();
	}

	/**
	 * Получение нарядов со статусом "Новый"
	 * @param bundle
	 * @return
	 */
	public boolean GetTask(Bundle bundle) {
		URI requestUri = null;
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
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
						tasks.add(getTask(serverTasks[i]));
					}
				}

			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;

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
		item.setUsers_uuid("");
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
		taskStatus.add(getTaskStatus(serverTask.getOrderStatus()));
		
		List<Item> operations = serverTask.getItems();
		if (operations != null) {
			for (int i = 0; i < operations.size(); i++) {
				equipmentOperations.add(getOperation(operations.get(i), item.getUuid()));
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
		equipments.add(getEquipment(operation.getEquipment()));
		
		item.setOperation_type_uuid(operation.getOperationType().getId());
		// создаём объект типа операции
		operationTypes.add(getOperationType(operation.getOperationType()));
		
		item.setOperation_pattern_uuid(operation.getOperationPattern().getId());
		// создаём объект шаблона операции
		operationPatterns.add(getOperationPattern(operation.getOperationPattern()));
		
		item.setOperation_status_uuid(operation.getStatus().getId());
		// создаём объект статуса операции
		operationStatus.add(getOperationStatus(operation.getStatus()));

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
				operationPatternSteps.add(getStep(steps.get(i), item.getUuid()));
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
				operationPatternStepResults.add(getStepResult(results.get(i), item.getUuid()));
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
		measureTypes.add(getMeasureType(result.getMeasureType()));
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
		equipmentTypes.add(getEquipmentType(equipment.getEquipmentType()));
		
		item.setCritical_type_uuid(equipment.getCriticalityType().getId());
		// создаём объект типа критичности оборудования
		criticalTypes.add(getCriticalType(equipment.getCriticalityType()));
		
		// TODO нужна дата ввода оборудования в эксплуатацию
		item.setStart_date(0);
		
		List<Document> documents = equipment.getDocuments();
		for (int i = 0; i < documents.size(); i++) {
			equipmentDocumentations.add(getDocumentation(documents.get(i), item.getUuid()));
		}
		
		item.setLatitude(equipment.getGeoCoordinates().getLatitude());
		item.setLongitude(equipment.getGeoCoordinates().getLongitude());
		
		// TODO нужна метка оборудования
		item.setTag_id("");
		
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
		documentationTypes.add(getDocumentationType(document.getDocumentType()));
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
	 * Отправка списка uuid нарядов со статусом "Новый" для подтверждения о получении.
	 * В ответ с сервера отправляется список нарядов которые подтверждны.
	 * Соответственно в локальной базе у соответствующих нарядов меняется статус на "В работе"
	 * @param bundle
	 * @return
	 */
	public boolean TaskConfirmation(Bundle bundle) {
		URI requestUri = null;
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String jsonString = null;
		// TODO необходимо реализовать хранение данных по текущему аутентифицированному пользователю и данные для запроса брать на "ходу"
		String user_uuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";
		
		try {
			requestUri = new URI(mServerUrl + TASK_CONFIRMATION_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);
			
			TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
			ArrayList<Task> tasks;
			tasks = adapter.getTaskByUserAndStatus(user_uuid, TaskStatusDBAdapter.STATUS_UUID_CREATED);
			adapter.close();
			if (tasks != null) {
				StringBuilder postData = new StringBuilder();
				Iterator<Task> tasksIterator = tasks.iterator();
				while (tasksIterator.hasNext()) {
					postData.append("uuids[]=");
					postData.append(tasksIterator.next().getUuid());
				}

				Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
				Response response = new RestClient().execute(request);
				if (response.mStatus == 200) {
					jsonString = new String(response.mBody, "UTF-8");
					JSONArray jsonArray = new JSONArray(jsonString);
					ParseConfirmTask(jsonArray);
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
	
	/**
	 * Отправка результатов выполнения наряда.
	 * @param bundle
	 * @return
	 */
	public boolean TaskSendResult(Bundle bundle) {
		URI requestUri = null;
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String taskUuid = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TASK_UUID);
		String jsonString = null;
		
		try {
			requestUri = new URI(mServerUrl + TASK_SEND_RESULT_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);
			
			TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
			Task task;
			task = adapter.getTaskByUuidAndUpdated(taskUuid);
			adapter.close();
			if (task != null) {
				StringBuilder postData = new StringBuilder();
				
				TaskResult taskResult = new TaskResult(mContext);
				if (taskResult.getTaskResult(task.getUuid())) {
					// TODO реализовать упаковку данных с результатами выполнения наряда, на сервер должен уехать массив с одним элементом 
					/*
					postData.append("uuids[]=");
					postData.append(tasksIterator.next().getUuid());
					*/
	
					Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
					Response response = new RestClient().execute(request);
					if (response.mStatus == 200) {
						// TODO реализовать разбор ответа с подтверждением об отправке результатов
						// TODO реализовать изменение статусов данных(updated) на "отправлено"
						jsonString = new String(response.mBody, "UTF-8");
						JSONArray jsonArray = new JSONArray(jsonString);
						//ParseConfirmTask(jsonArray);
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Отправка результатов выполнения нарядов.
	 * @param bundle
	 * @return
	 */
	public boolean TasksSendResult(Bundle bundle) {
		URI requestUri = null;
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String jsonString = null;
		// TODO необходимо реализовать хранение данных по текущему аутентифицированному пользователю и данные для запроса брать на "ходу"
		String user_uuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";
		
		try {
			requestUri = new URI(mServerUrl + TASK_SEND_RESULT_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);
			
			TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
			ArrayList<Task> tasks;
			// TODO необходимо решить и реализовать выборку не отправленных нарядов, либо по текущему пользователю либо все какие есть неотправленные.
			tasks = adapter.getTaskByUserAndUpdated(user_uuid);
			adapter.close();
			if (tasks != null) {
				StringBuilder postData = new StringBuilder();
				Iterator<Task> tasksIterator = tasks.iterator();
				while (tasksIterator.hasNext()) {
					postData.append("uuids[]=");
					postData.append(tasksIterator.next().getUuid());
				}

				Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
				Response response = new RestClient().execute(request);
				if (response.mStatus == 200) {
					// TODO реализовать разбор ответа с подтверждением об отправке результатов
					// TODO реализовать изменение статусов данных(updated) на "отправлено"
					jsonString = new String(response.mBody, "UTF-8");
					JSONArray jsonArray = new JSONArray(jsonString);
					//ParseConfirmTask(jsonArray);
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
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseConfirmTask(JSONArray array) {
		String uuid;
		int elementCount = array.length();
		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				uuid = array.getString(i);
				Log.d("test", uuid);
				Task item = adapter.getItem(uuid);
				if (item != null) {
					item.setTask_status_uuid(TaskStatusDBAdapter.STATUS_UUID_SENDED);
					adapter.replace(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
}
