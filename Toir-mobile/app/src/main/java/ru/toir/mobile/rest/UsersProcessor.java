package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import io.realm.Realm;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.db.realm.User;
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

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		// урл к которому будем обращаться с запросами
		mServerUrl = sp.getString(context.getString(R.string.serverUrl), "");

		if (mServerUrl.equals("")) {
			throw new Exception("URL сервера не указан!");
		}
	}

	/**
	 * Получаем и сохраняем информацию по пользователю
	 * 
	 * @param bundle Параметры для выполнения запроса
	 * @return Bundle с результатом выполнения запроса
	 */
	public Bundle getUser(Bundle bundle) {

		Bundle result = new Bundle();

		if (!checkToken()) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		URI requestUri;
		String token = AuthorizedUser.getInstance().getToken();
		String jsonString;

		try {
			requestUri = new URI(mServerUrl + USERS_GET_USER_URL);
			Log.d("test", "requestUri = " + requestUri.toString());

			Map<String, List<String>> headers = new ArrayMap<>();
			List<String> tList = new ArrayList<>();
			tList.add("bearer " + token);
			headers.put("Authorization", tList);

			Request request = new Request(Method.GET, requestUri, headers, null);

			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {

				jsonString = new String(response.mBody, "UTF-8");

                User user =  new Gson().fromJson(jsonString, User.class);
				if (user != null) {
                    Realm realm = Realm.getDefaultInstance();
                    User realmUser = realm.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
                    realm.beginTransaction();
                    if (realmUser == null) {
                        realmUser = realm.copyToRealm(user);
                    } else {
                        realmUser = realm.copyToRealmOrUpdate(user);
                    }
                    realm.commitTransaction();
                    Log.d("test", "realm user = " + realmUser);

					result.putBoolean(IServiceProvider.RESULT, true);
					return result;
				} else {
					result.putBoolean(IServiceProvider.RESULT, true);
					result.putString(IServiceProvider.MESSAGE,
							"Ошибка разбора ответа сервера на запрос информации о пользователе.");
					return result;
				}
			} else {
				result.putBoolean(IServiceProvider.RESULT, false);
				result.putString(IServiceProvider.MESSAGE,
						"Ошибка получения информации о пользователе. RESPONSE STATUS = "
								+ response.mStatus);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			e.printStackTrace();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Получаем токен. Метод использульзуется для проверки наличия токена, так
	 * как может сложится ситуация когда пользователь вошел в систему но токен
	 * не получил из за отсутствия связи.
	 */
	private boolean checkToken() {
		AuthorizedUser au = AuthorizedUser.getInstance();
		if (au.getToken() == null) {
			try {
				TokenProcessor tp = new TokenProcessor(mContext);
				Bundle bundle = new Bundle();
				bundle.putString(
						TokenServiceProvider.Methods.GET_TOKEN_PARAMETER_TAG,
						au.getTagId());
				Bundle result = tp.getTokenByTag(bundle);
				return result.getBoolean(IServiceProvider.RESULT);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}

}
