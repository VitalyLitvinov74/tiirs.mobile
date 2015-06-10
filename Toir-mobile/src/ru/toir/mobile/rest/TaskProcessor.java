/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.RestClient.Method;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 */
public class TaskProcessor {

	private Context mContext;
	private static final String TASK_GET_URL = "/orders.php";
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
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean GetTask(Bundle bundle) {
		// TODO добавить в запрос переменную tag_id
		URI requestUri = null;
		//String tag = bundle.getString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG);
		String jsonString = null;
		JSONObject jsonRootObject = null;
		try {
			requestUri = new URI(mServerUrl + TASK_GET_URL);
			Log.d("test", "requestUri = " + requestUri.toString());

			Request request = new Request(Method.GET, requestUri, null, null);
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {

				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);

				jsonRootObject = new JSONObject(jsonString);
				// TODO реализовать разбор полученных данных и разложить по
				// таблицам
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
					}
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
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
				Task task = new Task();
				JSONObject value = array.getJSONObject(i);
				task.setUuid(value.getString(TaskDBAdapter.FIELD_UUID_NAME));
				task.setUsers_uuid(value.getString(TaskDBAdapter.FIELD_USER_UUID_NAME));
				task.setCreate_date(value.getLong(TaskDBAdapter.FIELD_CREATE_DATE_NAME));
				task.setModify_date(value.getLong(TaskDBAdapter.FIELD_MODIFY_DATE_NAME));
				task.setClose_date(value.getLong(TaskDBAdapter.FIELD_CLOSE_DATE_NAME));
				task.setTask_status_uuid(value.getString(TaskDBAdapter.FIELD_TASK_STATUS_UUID_NAME));
				task.setAttempt_send_date(value.getLong(TaskDBAdapter.FIELD_ATTEMPT_SEND_DATE_NAME));
				task.setAttempt_count(value.getInt(TaskDBAdapter.FIELD_ATTEMPT_COUNT_NAME));
				task.setSuccessefull_send(value
						.getInt(TaskDBAdapter.FIELD_SUCCESSEFULL_SEND_NAME) == 0 ? false
						: true);
				adapter.replace(task);

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
