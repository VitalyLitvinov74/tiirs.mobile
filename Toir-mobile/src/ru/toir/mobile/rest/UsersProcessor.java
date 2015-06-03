package ru.toir.mobile.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import org.json.JSONException;
import org.json.JSONObject;

import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.rest.RestClient.Method;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 *
 */

public class UsersProcessor {
	private Context mContext;
	private static final String USERS_GET_USER_URL = "http://apkupdate.lan/user.php?tag=";

	public UsersProcessor(Context context) {
		mContext = context;
	}
	
	public boolean GetUser(Bundle bundle) {
		// TODO обернуть всё в один try/catch
		URI requestUri = null;
		String tag = bundle.getString(UsersServiceProvider.Methods.GET_USER_PARAMETER_TAG);
		String jsonString = null;
		JSONObject jsonArray = null;
		try {
			requestUri = new URI(USERS_GET_USER_URL + tag);
			Log.d("test", "requestUri = " + requestUri.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
			
		Request request = new Request(Method.GET, requestUri, null, null);
		Response response = new RestClient().execute(request);
		if (response.mStatus == 200) {
			try {
				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return false;
			}

			try {
				jsonArray = new JSONObject(jsonString);
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
			
			try {
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

			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
			
			return true;
		} else {
			return false;
		}
	}

}
