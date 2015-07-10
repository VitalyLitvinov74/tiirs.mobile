/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
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
public class EquipmentOperationResultProcessor {
	private Context mContext;
	private static final String SEND_RESULT_URL = "/opres.php";
	private String mServerUrl;

	/**
	 * 
	 */
	public EquipmentOperationResultProcessor(Context context) throws Exception {
		mContext = context;

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		// урл к которому будем обращаться с запросами
		mServerUrl = sp.getString(context.getString(R.string.serverUrl), "");

		if (mServerUrl.equals("")) {
			throw new Exception("URL сервера не указан!");
		}
	}
	
	public boolean SendResult(Bundle bundle) {
		URI requestUri = null;
		String token = bundle.getString(EquipmentOperationResultServiceProvider.Methods.PARAMETER_TOKEN);
		String jsonString = null;
		JSONObject jsonRootObject = null;
		// TODO необходимо реализовать хранение данных по текущему аутентифицированному пользователю и данные для запроса брать на "ходу"
		String uuid = "4462ed77-9bf0-4542-b127-f4ecefce49da";

		try {
			requestUri = new URI(mServerUrl + SEND_RESULT_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			StringBuilder postData;
			// TODO сделать упаковку данных из базы с результатами операций для отправки
			EquipmentOperationResultDBAdapter adapter = new EquipmentOperationResultDBAdapter(new TOiRDatabaseContext(mContext)).open();
			ArrayList<EquipmentOperationResult> operationResults = null;
			adapter.close();
			if (operationResults != null) {
				postData = new StringBuilder();
				Iterator<EquipmentOperationResult> equipmentOperationResultIterator = operationResults.iterator();
				while (equipmentOperationResultIterator.hasNext()) {
					postData.append("uuids[]=");
					postData.append(equipmentOperationResultIterator.next().getUuid());
				}
			} else {
				return false;
			}
			
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);

			Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
			Response response = new RestClient().execute(request);
			if (response.mStatus == 200) {

				jsonString = new String(response.mBody, "UTF-8");
				Log.d("test", jsonString);

				jsonRootObject = new JSONObject(jsonString);
				Iterator<?> iterator = jsonRootObject.keys();
				while (iterator.hasNext()) {
					String next = (String) iterator.next();
					JSONArray elementArray = jsonRootObject.getJSONArray(next);
					elementArray.toString();
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
