package ru.toir.mobile.rest;

import java.net.URI;
import org.json.JSONObject;

import com.google.gson.Gson;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.Token;
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
	private static final String USERS_GET_USER_URL = "/token";
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
	public boolean GetUser(Bundle bundle) {
		URI requestUri = null;
		String tag = bundle.getString(UsersServiceProvider.Methods.GET_USER_PARAMETER_TAG);
		String username = bundle.getString(UsersServiceProvider.Methods.GET_USER_PARAMETER_USER_NAME);
		String jsonString = null;
		try {
			requestUri = new URI(mServerUrl + USERS_GET_USER_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			StringBuilder postData = new StringBuilder();
			postData.append(UsersServiceProvider.Methods.GET_USER_PARAMETER_TAG).append("=").append(tag);
			postData.append("&");
			postData.append(UsersServiceProvider.Methods.GET_USER_PARAMETER_USER_NAME).append("=").append(username);
			postData.append("&");
			postData.append("grant_type=label");
			
			Request request = new Request(Method.POST, requestUri, null, postData.toString().getBytes());
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {

				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);

				Token token = new Gson().fromJson(jsonString, Token.class);
				// TODO нужно кудато занести полученные данные !!!!
				
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
