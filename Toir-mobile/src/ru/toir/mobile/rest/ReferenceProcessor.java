/**
 * 
 */
package ru.toir.mobile.rest;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.reference.ReferenceList;
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
public class ReferenceProcessor {

	private static final String GET_REFERENCE_URL = "/api/references";
	private String mServerUrl;
	public static class ReferenceNames {
		public static String EquipmentName = "Справочник оборудования";
		public static String OperationResultName = "Справочник результатов операций";
		public static String OperationName = "Справочник операций";
	}

	/**
	 * 
	 */
	public ReferenceProcessor(Context context) throws Exception {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		// урл к которому будем обращаться с запросами
		mServerUrl = sp.getString(context.getString(R.string.serverUrl), "");

		if (mServerUrl.equals("")) {
			throw new Exception("URL сервера не указан!");
		}
	}
	
	// TODO реализовать получение справочника в два шага
	// 1 шаг получение списка справочников
	// 2 шаг поиск uuid в списке по имени, получение данных по uuid

	/**
	 * Получаем полностью данные из справочника
	 * @param bundle
	 * @return
	 */
	public boolean getReference(Bundle bundle) {
		URI requestUri = null;
		String name = bundle
				.getString(ReferenceServiceProvider.Methods.GET_REFERENCE_PARAMETER_NAME);
		try {
			requestUri = new URI(mServerUrl + GET_REFERENCE_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			// шлём запрос на список справочников
			/*
			StringBuilder postData = new StringBuilder("label=").append(name)
					.append("&grant_type=label");
			Request request = new Request(Method.POST, requestUri, null,
					postData.toString().getBytes());
			*/
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("bearer " + AuthorizedUser.getInstance().getToken());
			headers.put("Authorization", tList);
			
			Request request = new Request(Method.GET, requestUri, headers, null);
			Response response = new RestClient().execute(request);

			if (response.mStatus == 200) {
				String jsonString = new String(response.mBody);
				Type listType = new TypeToken<List<ru.toir.mobile.serverapi.reference.ReferenceList>>(){}.getType();
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();
				List<ru.toir.mobile.serverapi.reference.ReferenceList> list = gson.fromJson(jsonString, listType);
				if (list != null) {
					for (ReferenceList element: list) {
						if (element.getName().equals(name)) {
							// запрашиваем данные по справочнику
							requestUri = new URI(mServerUrl + GET_REFERENCE_URL + "/" + element.getId());
							request = new Request(Method.GET, requestUri, headers, null);
							response = new RestClient().execute(request);
							if (response.mStatus == 200) {
								// разбираем данные по справочнику
								jsonString = new String(response.mBody);
								jsonString = null;
							}
						}
					}
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
