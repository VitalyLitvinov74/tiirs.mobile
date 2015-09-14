package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.User;
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
				
				User serverUser = new Gson().fromJson(jsonString, User.class);
				if (serverUser != null) {
					UsersDBAdapter adapter = new UsersDBAdapter(new TOiRDatabaseContext(mContext));
					Users user = adapter.getItem(AuthorizedUser.getInstance().getTagId());
					if (user == null) {
						user = new Users();
					}
					user.setUuid(serverUser.getId());
					user.setName(serverUser.getUserName());
					user.setLogin(serverUser.getEmail());
					// TODO с сервера не приходит хэш
					user.setPass("password");
					// TODO с сервера не приходит тип
					user.setType(3);
					// TODO реализовать сохранение хэша вместо ид метки
					user.setTag_id(AuthorizedUser.getInstance().getTagId());
					user.setActive(serverUser.isIsActive());
					// TODO с сервера не приходит должность
					user.setWhois("Бугор");
					adapter.replaceItem(user);
				} else {
					return false;
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

}
