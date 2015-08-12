/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import com.google.gson.Gson;

import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
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
public class TokenProcessor {

	private static final String USERS_GET_TOKEN_URL = "/token";
	private String mServerUrl;

	/**
	 * 
	 */
	public TokenProcessor(Context context) throws Exception {

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
	public boolean getTokenByTag(Bundle bundle) {
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
				String jsonString = new String(response.mBody);
				ru.toir.mobile.serverapi.Token serverToken = new Gson().fromJson(jsonString, ru.toir.mobile.serverapi.Token.class);
				if (serverToken != null) {
					AuthorizedUser.getInstance().setToken(serverToken.getAccessToken());
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
	public boolean getTokenByUsernameAndPassword(Bundle bundle) {
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
				String jsonString = new String(response.mBody);
				ru.toir.mobile.serverapi.Token serverToken = new Gson().fromJson(jsonString, ru.toir.mobile.serverapi.Token.class);
				if (serverToken != null) {
					AuthorizedUser.getInstance().setToken(serverToken.getAccessToken());
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
