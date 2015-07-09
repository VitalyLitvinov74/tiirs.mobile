/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
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
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.RestClient.Method;
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
	private static final String TASK_GET_URL = "/orders.php";
	private static final String TASK_CONFIRMATION_URL = "/orders_confirm.php";
	private String mServerUrl;

	public TaskProcessor(Context context) throws Exception {
		mContext = context;

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		// урл к которому будем обращаться с запросами
		mServerUrl = sp.getString(context.getString(R.string.serverUrl), "");

		if (mServerUrl.equals("")) {
			throw new Exception("URL сервера не указан!");
		}
	}

	/**
	 * Получение нарядов со статусом "Новый"
	 * @param bundle
	 * @return
	 */
	public boolean GetTask(Bundle bundle) {
		URI requestUri = null;
		String tag = bundle.getString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG);
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String jsonString = null;
		JSONObject jsonRootObject = null;
		try {
			requestUri = new URI(mServerUrl + TASK_GET_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			StringBuilder postData = new StringBuilder("tag_id=").append(tag);
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);

			Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {

				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);

				jsonRootObject = new JSONObject(jsonString);
				Iterator<?> iterator = jsonRootObject.keys();
				while (iterator.hasNext()) {
					String next = (String) iterator.next();
					JSONArray elementArray = jsonRootObject.getJSONArray(next);
					if (next.equals(TaskDBAdapter.TABLE_NAME)) {
						ParseTask(elementArray);
					} else if (next.equals(EquipmentDBAdapter.TABLE_NAME)) {
						ParseEquipment(elementArray);
					} else if (next.equals(EquipmentOperationDBAdapter.TABLE_NAME)) {
						ParseEquipmentOperation(elementArray);
					} else if (next.equals(TaskStatusDBAdapter.TABLE_NAME)) {
						ParseTaskStatus(elementArray);
					} else if (next.equals(CriticalTypeDBAdapter.TABLE_NAME)) {
						ParseCriticalType(elementArray);
					} else if (next.equals(OperationTypeDBAdapter.TABLE_NAME)) {
						ParseOperationType(elementArray);
					} else if (next.equals(EquipmentTypeDBAdapter.TABLE_NAME)) {
						ParseEquipmentType(elementArray);
					} else if (next.equals(OperationPatternDBAdapter.TABLE_NAME)) {
						ParseOperationPattern(elementArray);
					} else if (next.equals(DocumentationTypeDBAdapter.TABLE_NAME)) {
						ParseDocumentationType(elementArray);
					} else if (next.equals(MeasureTypeDBAdapter.TABLE_NAME)) {
						ParseMeasureType(elementArray);
					} else if (next.equals(OperationResultDBAdapter.TABLE_NAME)) {
						ParseOperationResult(elementArray);
					} else if (next.equals(OperationPatternStepDBAdapter.TABLE_NAME)) {
						ParseOperationPatternStep(elementArray);
					} else if (next.equals(OperationPatternStepResultDBAdapter.TABLE_NAME)) {
						ParseOperationPatternStepResult(elementArray);
					} else if (next.equals(EquipmentDocumentationDBAdapter.TABLE_NAME)) {
						ParseEquipmentDocumentation(elementArray);
					} else if (next.equals(MeasureValueDBAdapter.TABLE_NAME)) {
						ParseMeasureValue(elementArray);
					} else if (next.equals(EquipmentOperationResultDBAdapter.TABLE_NAME)) {
						ParseEquipmentOperationResult(elementArray);
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
	 * Отправка списка uuid нарядов со статусом "Новый" для подтверждения о получении.
	 * В ответ с сервера отправляется список нарядов которые подтверждны.
	 * Соответственно в локальной базе у соответствующих нарядов меняется статус на "В работе"
	 * @param bundle
	 * @return
	 */
	public boolean TaskConfirmation(Bundle bundle) {
		URI requestUri = null;
		String tag = bundle.getString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG);
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String jsonString = null;
		JSONObject jsonRootObject = null;
		
		// TODO реализовать отправку подтверждения получения нарядов
		try {
			requestUri = new URI(mServerUrl + TASK_CONFIRMATION_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);
			
			TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
			ArrayList<Task> tasks;
			tasks = adapter.getTaskByUserAndStatus(tag, TaskStatusDBAdapter.STATUS_UUID_CREATED);
			adapter.close();
			if (tasks != null) {
				// TODO реализовать упаковку данных по полученным нарядам для подтверждения о получении
				StringBuilder postData = new StringBuilder("tag_id=").append(tag);
				Iterator<Task> tasksIterator = tasks.iterator();
				while (tasksIterator.hasNext()) {
					postData.append("&uuids[]=");
					postData.append(tasksIterator.next().getUuid());
				}
				Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
				Response response = new RestClient().execute(request);
				if (response.mStatus == 200) {

					jsonString = new String(response.mBody, "UTF-8");
					Log.d("test", jsonString);

					jsonRootObject = new JSONObject(jsonString);
					Iterator<?> iterator = jsonRootObject.keys();
					while (iterator.hasNext()) {
						// TODO реализовать разбор данных, обновление записей в базе
						/*
						String next = (String) iterator.next();
						JSONArray elementArray = jsonRootObject.getJSONArray(next);
						// TODO разобрать ответ от сервера, по полученным uuid изменить статусы на "В работе"
						elementArray.toString();
						*/
					}
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
	private boolean ParseOperationPatternStepResult(JSONArray array) {
		int elementCount = array.length();
		OperationPatternStepResultDBAdapter adapter = new OperationPatternStepResultDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				OperationPatternStepResult item = new OperationPatternStepResult();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(OperationPatternStepResultDBAdapter.FIELD_UUID_NAME));
				item.setOperation_pattern_step_uuid(value.getString(OperationPatternStepResultDBAdapter.FIELD_OPERATION_PATTERN_STEP_UUID_NAME));
				item.setNext_operation_pattern_step_uuid(value.getString(OperationPatternStepResultDBAdapter.FIELD_NEXT_OPERATION_PATTERN_STEP_UUID_NAME));
				item.setTitle(value.getString(OperationPatternStepResultDBAdapter.FIELD_TITLE_NAME));
				item.setMeasure_type_uuid(value.getString(OperationPatternStepResultDBAdapter.FIELD_MEASURE_TYPE_UUID_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseOperationPatternStep(JSONArray array) {
		int elementCount = array.length();
		OperationPatternStepDBAdapter adapter = new OperationPatternStepDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				OperationPatternStep item = new OperationPatternStep();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(OperationPatternStepDBAdapter.FIELD_UUID_NAME));
				item.setOperation_pattern_uuid(value.getString(OperationPatternStepDBAdapter.FIELD_OPERATION_PATTERN_UUID_NAME));
				item.setDescription(value.getString(OperationPatternStepDBAdapter.FIELD_DESCRIPTION_NAME));
				item.setImage(value.getString(OperationPatternStepDBAdapter.FIELD_IMAGE_NAME));
				item.setFirst_step(value.getInt(OperationPatternStepDBAdapter.FIELD_FIRST_STEP_NAME) == 0 ? false : true);
				item.setLast_step(value.getInt(OperationPatternStepDBAdapter.FIELD_LAST_STEP_NAME) == 0 ? false : true);
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseOperationResult(JSONArray array) {
		int elementCount = array.length();
		OperationResultDBAdapter adapter = new OperationResultDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				OperationResult item = new OperationResult();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(OperationResultDBAdapter.FIELD_UUID_NAME));
				item.setOperation_type_uuid(value.getString(OperationResultDBAdapter.FIELD_OPERATION_TYPE_UUID_NAME));
				item.setTitle(value.getString(OperationResultDBAdapter.FIELD_TITLE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseMeasureType(JSONArray array) {
		int elementCount = array.length();
		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				MeasureType item = new MeasureType();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(DocumentationTypeDBAdapter.FIELD_UUID_NAME));
				item.setTitle(value.getString(DocumentationTypeDBAdapter.FIELD_TITLE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseDocumentationType(JSONArray array) {
		int elementCount = array.length();
		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				DocumentationType item = new DocumentationType();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(DocumentationTypeDBAdapter.FIELD_UUID_NAME));
				item.setTitle(value.getString(DocumentationTypeDBAdapter.FIELD_TITLE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseOperationPattern(JSONArray array) {
		int elementCount = array.length();
		OperationPatternDBAdapter adapter = new OperationPatternDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				OperationPattern item = new OperationPattern();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(EquipmentTypeDBAdapter.FIELD_UUID_NAME));
				item.setTitle(value.getString(EquipmentTypeDBAdapter.FIELD_TITLE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseEquipmentType(JSONArray array) {
		int elementCount = array.length();
		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				EquipmentType item = new EquipmentType();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(EquipmentTypeDBAdapter.FIELD_UUID_NAME));
				item.setTitle(value.getString(EquipmentTypeDBAdapter.FIELD_TITLE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseOperationType(JSONArray array) {
		int elementCount = array.length();
		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				OperationType item = new OperationType();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(OperationTypeDBAdapter.FIELD_UUID_NAME));
				item.setTitle(value.getString(OperationTypeDBAdapter.FIELD_TITLE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseCriticalType(JSONArray array) {
		int elementCount = array.length();
		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				CriticalType item = new CriticalType();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(CriticalTypeDBAdapter.FIELD_UUID_NAME));
				item.setType(value.getInt(CriticalTypeDBAdapter.FIELD_TYPE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseTaskStatus(JSONArray array) {
		int elementCount = array.length();
		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				TaskStatus item = new TaskStatus();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(TaskStatusDBAdapter.FIELD_UUID_NAME));
				item.setTitle(value.getString(TaskStatusDBAdapter.FIELD_TITLE_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseEquipmentOperation(JSONArray array) {
		int elementCount = array.length();
		EquipmentOperationDBAdapter adapter = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				EquipmentOperation item = new EquipmentOperation();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(EquipmentOperationDBAdapter.FIELD_UUID_NAME));
				item.setTask_uuid(value.getString(EquipmentOperationDBAdapter.FIELD_TASK_UUID_NAME));
				item.setEquipment_uuid(value.getString(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID_NAME));
				item.setOperation_type_uuid(value.getString(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID_NAME));
				item.setOperation_pattern_uuid(value.getString(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID_NAME));
				item.setOperation_status_uuid(value.getString(EquipmentOperationDBAdapter.FIELD_OPERATION_STATUS_UUID_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseEquipment(JSONArray array) {
		int elementCount = array.length();
		EquipmentDBAdapter adapter = new EquipmentDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				Equipment item = new Equipment();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(EquipmentDBAdapter.FIELD_UUID_NAME));
				item.setTitle(value.getString(EquipmentDBAdapter.FIELD_TITLE_NAME));
				item.setEquipment_type_uuid(value.getString(EquipmentDBAdapter.FIELD_EQUIPMENT_TYPE_UUID_NAME));
				item.setCritical_type_uuid(value.getString(EquipmentDBAdapter.FIELD_CRITICAL_TYPE_UUID_NAME));
				item.setStart_date(value.getLong(EquipmentDBAdapter.FIELD_START_DATE_NAME));
				item.setLocation(value.getString(EquipmentDBAdapter.FIELD_LOCATION_NAME));
				item.setTag_id(value.getString(EquipmentDBAdapter.FIELD_TAG_ID_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param array
	 * @return
	 */
	private boolean ParseTask(JSONArray array) {

		int elementCount = array.length();
		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {

			for (int i = 0; i < elementCount; i++) {
				Task item = new Task();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(TaskDBAdapter.FIELD_UUID_NAME));
				item.setUsers_uuid(value.getString(TaskDBAdapter.FIELD_USER_UUID_NAME));
				item.setCreate_date(value.getLong(TaskDBAdapter.FIELD_CREATE_DATE_NAME));
				item.setModify_date(value.getLong(TaskDBAdapter.FIELD_MODIFY_DATE_NAME));
				item.setClose_date(value.getLong(TaskDBAdapter.FIELD_CLOSE_DATE_NAME));
				item.setTask_status_uuid(value.getString(TaskDBAdapter.FIELD_TASK_STATUS_UUID_NAME));
				// TODO необходимо для полей отвечающих за отправку данных на сервер, либо устанавливать значения по умолчанию, либо предварительно вытаскивать их из базы. РЕШИТЬ!!!
				item.setAttempt_send_date(value.getLong(TaskDBAdapter.FIELD_ATTEMPT_SEND_DATE_NAME));
				item.setAttempt_count(value.getInt(TaskDBAdapter.FIELD_ATTEMPT_COUNT_NAME));
				item.setUpdated(value.getInt(TaskDBAdapter.FIELD_UPDATED_NAME) == 0 ? false	: true);
				adapter.replace(item);

			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	private boolean ParseEquipmentOperationResult(JSONArray array) {

		int elementCount = array.length();
		EquipmentOperationResultDBAdapter adapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {

			for (int i = 0; i < elementCount; i++) {
				EquipmentOperationResult item = new EquipmentOperationResult();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(EquipmentOperationResultDBAdapter.FIELD_UUID_NAME));
				item.setEquipment_operation_uuid(value.getString(EquipmentOperationResultDBAdapter.FIELD_EQUIPMENT_OPERATION_UUID_NAME));
				item.setStart_date(value.getLong(EquipmentOperationResultDBAdapter.FIELD_START_DATE_NAME));
				item.setEnd_date(value.getLong(EquipmentOperationResultDBAdapter.FIELD_END_DATE_NAME));
				item.setOperation_result_uuid(value.getString(EquipmentOperationResultDBAdapter.FIELD_OPERATION_RESULT_UUID_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}
	
	private boolean ParseEquipmentDocumentation(JSONArray array) {

		int elementCount = array.length();
		EquipmentDocumentationDBAdapter adapter = new EquipmentDocumentationDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				EquipmentDocumentation item = new EquipmentDocumentation();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(EquipmentDocumentationDBAdapter.FIELD_UUID_NAME));
				item.setEquipment_uuid(value.getString(EquipmentDocumentationDBAdapter.FIELD_EQUIPMENT_UUID_NAME));
				item.setDocumentation_type_uuid(value.getString(EquipmentDocumentationDBAdapter.FIELD_DOCUMENTATION_TYPE_UUID_NAME));
				item.setTitle(value.getString(EquipmentDocumentationDBAdapter.FIELD_TITLE_NAME));
				item.setPath(value.getString(EquipmentDocumentationDBAdapter.FIELD_PATH_NAME));
				adapter.replace(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			adapter.close();
		}
		
		return true;
	}

	private boolean ParseMeasureValue(JSONArray array) {

		int elementCount = array.length();
		MeasureValueDBAdapter adapter = new MeasureValueDBAdapter(new TOiRDatabaseContext(
				mContext)).open();

		try {
			for (int i = 0; i < elementCount; i++) {
				MeasureValue item = new MeasureValue();
				JSONObject value = array.getJSONObject(i);
				item.setUuid(value.getString(MeasureValueDBAdapter.FIELD_UUID_NAME));
				item.setEquipment_operation_uuid(value.getString(MeasureValueDBAdapter.FIELD_EQUIPMENT_OPERATION_UUID_NAME));
				item.setOperation_pattern_step_result(value.getString(MeasureValueDBAdapter.FIELD_OPERATION_PATTERN_STEP_RESULT_NAME));
				item.setDate(value.getInt(MeasureValueDBAdapter.FIELD_DATE_NAME));
				item.setValue(value.getString(MeasureValueDBAdapter.FIELD_VALUE_NAME));
				// TODO необходимо для полей отвечающих за отправку данных на сервер, либо устанавливать значения по умолчанию, либо предварительно вытаскивать их из базы. РЕШИТЬ!!! 
				adapter.replace(item);
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
