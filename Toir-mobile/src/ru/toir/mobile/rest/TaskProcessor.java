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
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.tables.Task;
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
		URI requestUri = null;
		String tag = bundle
				.getString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG);
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
