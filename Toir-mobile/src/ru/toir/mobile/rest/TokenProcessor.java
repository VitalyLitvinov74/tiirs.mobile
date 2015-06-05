/**
 * 
 */
package ru.toir.mobile.rest;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.TokenDBAdapter;
import ru.toir.mobile.db.tables.Token;
import ru.toir.mobile.rest.RestClient.Method;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author koputo
 *
 */
public class TokenProcessor {

	private Context mContext;
	private static final String USERS_GET_TOKEN_URL = "/token.php";
	private String mServerUrl;
	
	/**
	 * 
	 */
	public TokenProcessor(Context context) {
		mContext = context;

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		
		// урл к которому будем обращаться с запросами
		mServerUrl = sp.getString(context.getString(R.string.serverUrl), "");
		
		if (mServerUrl.equals("")) {
			// TODO бросить exception !!!
		}
	}
	
	/**
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean GetTokenByTag(Bundle bundle) {
		// TODO сделать правильную отправку POST запроса
		// TODO обернуть всё в один try/catch
		URI requestUri = null;
		String tag = bundle.getString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_TAG);
		String jsonString = null;
		JSONObject jsonArray = null;
		try {
			requestUri = new URI(mServerUrl + USERS_GET_TOKEN_URL + tag);
			Log.d("test", "requestUri = " + requestUri.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
			
		Request request = new Request(Method.POST, requestUri, null, null);
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
				// TODO сделать разбор данных токена !!!
				JSONObject value = jsonArray.getJSONObject("data");
				Token token = new Token();
				token.setToken_type(value.getString(TokenDBAdapter.FIELD_TOKEN_TYPE_NAME));
				token.setAccess_token(value.getString(TokenDBAdapter.FIELD_ACCESS_TOKEN_NAME));
				token.setExpires_in(value.getInt(TokenDBAdapter.FIELD_EXPIRES_IN_NAME));
				token.setUserName(value.getString(TokenDBAdapter.FIELD_USER_NAME_NAME));
				token.setIssued(value.getString(TokenDBAdapter.FIELD_ISSUED_NAME));
				token.setExpires(value.getString(TokenDBAdapter.FIELD_EXPIRES_NAME));
				TokenDBAdapter adapter = new TokenDBAdapter(new TOiRDatabaseContext(mContext)).open();
				adapter.replace(token);
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
	
	/**
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean GetTokenByUsernameAndPassword(Bundle bundle) {
		// TODO сделать правильную отправку POST запроса
		// TODO обернуть всё в один try/catch
		URI requestUri = null;
		String username = bundle.getString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_USERNAME);
		String password = bundle.getString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_PASSWORD);
		String jsonString = null;
		JSONObject jsonArray = null;
		try {
			requestUri = new URI(mServerUrl + USERS_GET_TOKEN_URL + username + password);
			Log.d("test", "requestUri = " + requestUri.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return false;
		}
			
		Request request = new Request(Method.POST, requestUri, null, null);
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
				// TODO сделать разбор данных токена !!!
				JSONObject value = jsonArray.getJSONObject("data");
				Token token = new Token();
				token.setToken_type(value.getString(TokenDBAdapter.FIELD_TOKEN_TYPE_NAME));
				token.setAccess_token(value.getString(TokenDBAdapter.FIELD_ACCESS_TOKEN_NAME));
				token.setExpires_in(value.getInt(TokenDBAdapter.FIELD_EXPIRES_IN_NAME));
				token.setUserName(value.getString(TokenDBAdapter.FIELD_USER_NAME_NAME));
				token.setIssued(value.getString(TokenDBAdapter.FIELD_ISSUED_NAME));
				token.setExpires(value.getString(TokenDBAdapter.FIELD_EXPIRES_NAME));
				TokenDBAdapter adapter = new TokenDBAdapter(new TOiRDatabaseContext(mContext)).open();
				adapter.replace(token);
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
