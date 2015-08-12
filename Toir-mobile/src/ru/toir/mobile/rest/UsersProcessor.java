package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
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

public class UsersProcessor {
	private Context mContext;
	private static final String USERS_GET_USER_URL = "/api/account/me";
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
		URI requestUri = null;
		String token = AuthorizedUser.getInstance().getToken();
		String jsonString = null;
		JSONObject jsonObject = null;
		try {
			requestUri = new URI(mServerUrl + USERS_GET_USER_URL);
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

				jsonObject = new JSONObject(jsonString);
				
				Users user = new Users();
				// TODO реализовать разбор данных о пользователе с сервера
				// пока всех нет рыба для проверки
				user.setUuid("4462ed77-9bf0-4542-b127-f4ecefce49da");
				user.setName(jsonObject.getString("UserName"));
				user.setLogin(jsonObject.getString("Email"));
				user.setPass("password");
				user.setType(3);
				user.setTag_id("01234567");
				int active = 1;
				user.setActive(active == 0 ? false : true);
				user.setWhois("Бугор");

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
