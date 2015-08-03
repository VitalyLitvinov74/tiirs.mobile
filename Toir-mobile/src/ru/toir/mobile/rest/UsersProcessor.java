package ru.toir.mobile.rest;

import java.net.URI;
import org.json.JSONObject;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
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

public class UsersProcessor {
	private Context mContext;
	private static final String USERS_GET_USER_URL = "/user";
	private String mServerUrl;
	

	public UsersProcessor(Context context) throws Exception {
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
	public boolean getUser(Bundle bundle) {
		// TODO реализовать получение пользователя с сервера для внесения в локальную базу
		URI requestUri = null;
		String tag = bundle.getString(UsersServiceProvider.Methods.GET_USER_PARAMETER_TAG);
		String jsonString = null;
		JSONObject jsonArray = null;
		try {
			requestUri = new URI(mServerUrl + USERS_GET_USER_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			StringBuilder postData = new StringBuilder("label=").append(tag);
			Request request = new Request(Method.POST, requestUri, null, postData.toString().getBytes());
			
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {

				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);

				jsonArray = new JSONObject(jsonString);
				
				JSONObject value = jsonArray.getJSONObject("data");
				Users user = new Users();
				user.setUuid(value.getString(UsersDBAdapter.FIELD_UUID_NAME));
				user.setName(value.getString(UsersDBAdapter.FIELD_NAME_NAME));
				user.setLogin(value.getString(UsersDBAdapter.FIELD_LOGIN_NAME));
				user.setPass(value.getString(UsersDBAdapter.FIELD_PASS_NAME));
				user.setType(value.getInt(UsersDBAdapter.FIELD_TYPE_NAME));
				user.setTag_id(value.getString(UsersDBAdapter.FIELD_TAGID_NAME));
				user.setActive(value.getInt(UsersDBAdapter.FIELD_ACTIVE_NAME) == 0 ? false : true);
				UsersDBAdapter adapter = new UsersDBAdapter(new TOiRDatabaseContext(mContext)).open();
				adapter.replaceItem(user);
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
