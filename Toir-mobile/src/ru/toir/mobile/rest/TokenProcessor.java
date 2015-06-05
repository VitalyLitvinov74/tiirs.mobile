/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
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
	public TokenProcessor(Context context) throws Exception {
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
	 * @param data
	 * @return
	 */
	private Token GetTokenData(byte data[]) {
		Token token = null;
		String jsonString = null;
		JSONObject jsonArray = null;

		try {
			jsonString = new String(data, "UTF-8");
			Log.d("test", jsonString);

			jsonArray = new JSONObject(jsonString);

			token = new Token();
			token.setToken_type(jsonArray
					.getString(TokenDBAdapter.FIELD_TOKEN_TYPE_NAME));
			token.setAccess_token(jsonArray
					.getString(TokenDBAdapter.FIELD_ACCESS_TOKEN_NAME));
			token.setExpires_in(jsonArray
					.getInt(TokenDBAdapter.FIELD_EXPIRES_IN_NAME));
			token.setUserName(jsonArray
					.getString(TokenDBAdapter.FIELD_USER_NAME_NAME));
			token.setIssued(jsonArray
					.getString(TokenDBAdapter.FIELD_ISSUED_NAME));
			token.setExpires(jsonArray
					.getString(TokenDBAdapter.FIELD_EXPIRES_NAME));
			return token;
		} catch (Exception e) {
			e.printStackTrace();
			return token;
		}
	}

	/**
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean GetTokenByTag(Bundle bundle) {
		URI requestUri = null;
		String tag = bundle
				.getString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_TAG);
		try {
			requestUri = new URI(mServerUrl + USERS_GET_TOKEN_URL);
			Log.d("test", "requestUri = " + requestUri.toString());

			StringBuilder postData = new StringBuilder("label=").append(tag)
					.append("&grant_type=label");
			Request request = new Request(Method.POST, requestUri, null,
					postData.toString().getBytes());
			Response response = new RestClient().execute(request);

			if (response.mStatus == 200) {
				Token token = GetTokenData(response.mBody);
				if (token != null) {
					TokenDBAdapter adapter = new TokenDBAdapter(
							new TOiRDatabaseContext(mContext)).open();
					adapter.replace(token);
					adapter.close();
					return true;
				} else {
					return false;
				}
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
	 * @param bundle
	 * @return
	 */
	public boolean GetTokenByUsernameAndPassword(Bundle bundle) {
		URI requestUri = null;
		String username = bundle
				.getString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_USERNAME);
		String password = bundle
				.getString(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_PASSWORD);

		try {
			requestUri = new URI(mServerUrl + USERS_GET_TOKEN_URL);
			Log.d("test", "requestUri = " + requestUri.toString());

			StringBuilder postData = new StringBuilder(
					TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_USERNAME)
					.append("=")
					.append(username)
					.append("&")
					.append(TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_PASSWORD)
					.append("=").append(password).append("&grant_type=password");
			Request request = new Request(Method.POST, requestUri, null,
					postData.toString().getBytes());
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {
				Token token = GetTokenData(response.mBody);
				if (token != null) {
					TokenDBAdapter adapter = new TokenDBAdapter(
							new TOiRDatabaseContext(mContext)).open();
					adapter.replace(token);
					adapter.close();
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
