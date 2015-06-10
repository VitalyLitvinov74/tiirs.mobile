/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;

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
	private static final String TASK_GET_URL = "/task.php";
	private String mServerUrl;
	

	public TaskProcessor(Context context) throws Exception {
		mContext = context;
		
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		
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
		String tag = bundle.getString(TaskServiceProvider.Methods.GET_TASK_PARAMETER_USER_TAG);
		String jsonString = null;
		JSONObject jsonArray = null;
		try {
			requestUri = new URI(mServerUrl + TASK_GET_URL + tag);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			Request request = new Request(Method.GET, requestUri, null, null);
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {

				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);

				jsonArray = new JSONObject(jsonString);
				// TODO реализовать разбор полученных данных и разложить по таблицам
				JSONObject value = jsonArray.getJSONObject("data");
				Task task = new Task();
				/*
				user.setUuid(value.getString(UsersDBAdapter.FIELD_UUID_NAME));
				user.setName(value.getString(UsersDBAdapter.FIELD_NAME_NAME));
				user.setLogin(value.getString(UsersDBAdapter.FIELD_LOGIN_NAME));
				user.setPass(value.getString(UsersDBAdapter.FIELD_PASS_NAME));
				user.setType(value.getInt(UsersDBAdapter.FIELD_TYPE_NAME));
				user.setTag_id(value.getString(UsersDBAdapter.FIELD_TAGID_NAME));
				user.setActive(value.getInt(UsersDBAdapter.FIELD_ACTIVE_NAME) == 0 ? false : true);
								*/
				TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext)).open();
				adapter.replace(task);
				adapter.close();
				
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
