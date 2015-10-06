/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationResultDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serializer.EquipmentOperationResultSerializer;
import ru.toir.mobile.serializer.EquipmentOperationSerializer;
import ru.toir.mobile.serializer.MeasureValueSerializer;
import ru.toir.mobile.serializer.TaskSerializer;
import ru.toir.mobile.serverapi.EquipmentOperationSrv;
import ru.toir.mobile.serverapi.EquipmentSrv;
import ru.toir.mobile.serverapi.TaskSrv;
import ru.toir.mobile.serverapi.result.EquipmentOperationRes;
import ru.toir.mobile.serverapi.result.EquipmentOperationResultRes;
import ru.toir.mobile.serverapi.result.MeasureValueRes;
import ru.toir.mobile.serverapi.result.TaskRes;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * @author Dmitriy Logachov
 * 
 */
public class TaskProcessor {

	private Context mContext;
	private static final String TASK_GET_URL = "/api/orders/";
	private static final String TASK_SEND_RESULT_URL = "/api/orders/";
	private String mServerUrl;

	private Set<String> patternUuids;
	private Set<String> operationTypeUuids;

	public TaskProcessor(Context context) throws Exception {
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
	 * Получение нарядов со статусом "Новый"
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean GetTask(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		boolean result;

		result = getTasks();
		if (!result) {
			return false;
		}

		result = getPatterns();
		if (!result) {
			return false;
		}

		result = getOperationResults();
		if (!result) {
			return false;
		}

		return true;
	}

	/**
	 * Получаем данные по нарядам
	 * 
	 * @return
	 */
	private boolean getTasks() {

		URI requestUri = null;
		String token = AuthorizedUser.getInstance().getToken();
		String jsonString = null;

		try {
			requestUri = new URI(mServerUrl + TASK_GET_URL);
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

				Gson gson = new GsonBuilder().setDateFormat(
						"yyyy-MM-dd'T'hh:mm:ss").create();

				ArrayList<TaskSrv> serverTasks = gson.fromJson(jsonString,
						new TypeToken<ArrayList<TaskSrv>>() {
							private static final long serialVersionUID = 1l;
						}.getType());
				if (serverTasks != null) {

					// разбираем и сохраняем полученные данные
					SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
							.getWritableDatabase();
					db.beginTransaction();
					boolean result = saveTasks(serverTasks);
					if (result) {
						db.setTransactionSuccessful();
					}
					db.endTransaction();

					return result;
				} else {
					// TODO нужен механизм который при наличии полученных
					// нарядов выведет диалог с их колличеством, либо с надписью
					// "Новых нарядов нет"
					// нарядов нет - считаем что процедура получения прошла
					// успешно
					return true;
				}
			} else {
				throw new Exception("Не удалось получить наряды.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Получаем данные по шаблонам
	 * 
	 * @return
	 */
	private boolean getPatterns() {

		try {
			ArrayList<String> operationPatternUuids = new ArrayList<String>(
					patternUuids);
			ReferenceProcessor referenceProcessor = new ReferenceProcessor(
					mContext);
			Bundle extra = new Bundle();
			extra.putStringArrayList(
					ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID,
					operationPatternUuids);
			boolean result = referenceProcessor.getOperationPattern(extra);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Получаем данные по результатам операций
	 * 
	 * @return
	 */
	public boolean getOperationResults() {

		try {
			ReferenceProcessor processor = new ReferenceProcessor(mContext);
			Bundle bundle = new Bundle();
			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_OPERATION_RESULT_PARAMETER_UUID,
					operationTypeUuids.toArray(new String[] {}));
			boolean result = processor.getOperationResult(bundle);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean saveTasks(ArrayList<TaskSrv> tasks) {

		// новый вариант разбора и сохранения данных с сервера
		TaskDBAdapter adapter0 = new TaskDBAdapter(new TOiRDatabaseContext(
				mContext));
		if (!adapter0.saveItems(TaskSrv.getTasks(tasks))) {
			return false;
		}

		TaskStatusDBAdapter adapter1 = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter1.saveItems(TaskSrv.getTaskStatuses(tasks))) {
			return false;
		}

		EquipmentOperationDBAdapter adapter2 = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter2.saveItems(TaskSrv.getEquipmentOperations(tasks))) {
			return false;
		}

		ArrayList<EquipmentOperationSrv> operations = TaskSrv
				.getEquipmentOperationSrvs(tasks);
		EquipmentDBAdapter adapter3 = new EquipmentDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter3
				.saveItems(EquipmentOperationSrv.getEquipments(operations))) {
			return false;
		}

		OperationTypeDBAdapter adapter4 = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter4.saveItems(EquipmentOperationSrv
				.getOperationTypes(operations))) {
			return false;
		}

		OperationStatusDBAdapter adapter5 = new OperationStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter5.saveItems(EquipmentOperationSrv
				.getOperationStatuses(operations))) {
			return false;
		}

		patternUuids = EquipmentOperationSrv
				.getOperationPatternUuids(operations);

		operationTypeUuids = EquipmentOperationSrv
				.getOperationTypeUuids(operations);

		ArrayList<EquipmentSrv> equipments = EquipmentOperationSrv
				.getEquipmentSrvs(operations);
		EquipmentTypeDBAdapter adapter6 = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter6.saveItems(EquipmentSrv.getEquipmentTypes(equipments))) {
			return false;
		}

		CriticalTypeDBAdapter adapter7 = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter7.saveItems(EquipmentSrv.getCriticalTypes(equipments))) {
			return false;
		}

		EquipmentStatusDBAdapter adapter8 = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter8.saveItems(EquipmentSrv.getEquipmentStatuses(equipments))) {
			return false;
		}

		EquipmentDocumentationDBAdapter adapter9 = new EquipmentDocumentationDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter9.saveItems(EquipmentSrv
				.getEquipmentDocumentations(equipments))) {
			return false;
		}

		DocumentationTypeDBAdapter adapter10 = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter10
				.saveItems(EquipmentSrv.getDocumentationTypes(equipments))) {
			return false;
		}

		return true;
	}

	/**
	 * Отправка результата выполнения наряда.
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean TaskSendResult(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		String taskUuid = bundle
				.getString(TaskServiceProvider.Methods.PARAMETER_TASK_UUID);

		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(
				mContext));
		Task task;
		task = adapter.getTaskByUuidAndUpdated(taskUuid);

		if (task != null) {
			TaskRes taskResult = TaskRes.load(mContext, task.getUuid());
			if (taskResult != null) {
				ArrayList<TaskRes> taskResults = new ArrayList<TaskRes>();
				taskResults.add(taskResult);
				return TasksSendResults(taskResults);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Отправка результатов выполнения нарядов.
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean TasksSendResult(Bundle bundle) {

		String user_uuid = AuthorizedUser.getInstance().getUuid();
		ArrayList<Task> tasks;
		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(
				mContext));
		// TODO необходимо решить и реализовать выборку не отправленных нарядов,
		// либо по текущему пользователю либо все какие есть неотправленные.
		tasks = adapter.getTaskByUserAndUpdated(user_uuid);

		// получаем из базы результаты связанные с нарядами
		ArrayList<TaskRes> taskResults = new ArrayList<TaskRes>();
		for (Task task : tasks) {
			TaskRes taskResult = TaskRes.load(mContext, task.getUuid());
			taskResults.add(taskResult);
		}

		return TasksSendResults(taskResults);
	}

	/**
	 * Отправка результатов выполнения нарядов на сервер
	 * 
	 * @return
	 */
	private boolean TasksSendResults(ArrayList<TaskRes> results) {

		URI requestUri = null;

		if (!checkToken()) {
			return false;
		}

		try {
			requestUri = new URI(mServerUrl + TASK_SEND_RESULT_URL);
			Log.d("test", "requestUri = " + requestUri.toString());

			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> aList = new ArrayList<String>();
			aList.add("Bearer " + AuthorizedUser.getInstance().getToken());
			headers.put("Authorization", aList);
			List<String> cList = new ArrayList<String>();
			cList.add("application/json");
			headers.put("Content-Type", cList);

			if (results != null) {
				StringBuilder postData = new StringBuilder();

				Gson gson = new GsonBuilder()
						.setPrettyPrinting()
						.registerTypeAdapter(TaskRes.class,
								new TaskSerializer())
						.registerTypeAdapter(EquipmentOperationRes.class,
								new EquipmentOperationSerializer())
						.registerTypeAdapter(EquipmentOperationResultRes.class,
								new EquipmentOperationResultSerializer())
						.registerTypeAdapter(MeasureValueRes.class,
								new MeasureValueSerializer()).create();

				String json = gson.toJson(results);
				Log.d("test", json);

				postData.append(json);

				Request request = new Request(Method.POST, requestUri, headers,
						postData.toString().getBytes());
				Response response = new RestClient().execute(request);
				// если ответ 204 значит всё сохранилось на сервере
				if (response.mStatus == 204) {
					clearUpdated(results);
				} else {
					throw new Exception("Не удалось отправить результаты");
				}
			}

		} catch (Exception e) {
			riseUpdated(results);
			e.printStackTrace();
			return false;
		}

		return true;
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
				return tp.getTokenByTag(bundle);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Увеличиваем счётчик попыток отправки результатов
	 * 
	 * @param resultsList
	 */
	private void riseUpdated(ArrayList<TaskRes> resultsList) {
		TaskDBAdapter taskAdapter = new TaskDBAdapter(new TOiRDatabaseContext(
				mContext));
		EquipmentOperationDBAdapter operationAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(mContext));
		EquipmentOperationResultDBAdapter operationResultAdapter = new EquipmentOperationResultDBAdapter(
				new TOiRDatabaseContext(mContext));
		MeasureValueDBAdapter valueAdapter = new MeasureValueDBAdapter(
				new TOiRDatabaseContext(mContext));

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		db.beginTransaction();
		for (TaskRes task : resultsList) {
			task.setAttempt_count(task.getAttempt_count() + 1);
			taskAdapter.replace(task);

			ArrayList<EquipmentOperationRes> operations = task
					.getEquipmentOperations();
			for (EquipmentOperationRes operation : operations) {
				operation.setAttempt_count(operation.getAttempt_count() + 1);
				operationAdapter.replace(operation);

				ArrayList<MeasureValueRes> values = operation
						.getMeasureValues();
				for (MeasureValueRes value : values) {
					value.setAttempt_count(value.getAttempt_count() + 1);
					valueAdapter.replace(value);
				}

				EquipmentOperationResultRes result = operation
						.getEquipmentOperationResult();
				result.setAttempt_count(result.getAttempt_count() + 1);
				operationResultAdapter.replace(result);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * Сбрасываем флаг изменнённого состояния(данные отправленны на сервер)
	 * 
	 * @param resultsList
	 */
	private void clearUpdated(ArrayList<TaskRes> resultsList) {

		TaskDBAdapter taskAdapter = new TaskDBAdapter(new TOiRDatabaseContext(
				mContext));
		EquipmentOperationDBAdapter operationAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(mContext));
		EquipmentOperationResultDBAdapter operationResultAdapter = new EquipmentOperationResultDBAdapter(
				new TOiRDatabaseContext(mContext));
		MeasureValueDBAdapter valueAdapter = new MeasureValueDBAdapter(
				new TOiRDatabaseContext(mContext));

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		db.beginTransaction();
		for (TaskRes task : resultsList) {
			task.setUpdated(false);
			taskAdapter.replace(task);

			ArrayList<EquipmentOperationRes> operations = task
					.getEquipmentOperations();
			for (EquipmentOperationRes operation : operations) {
				operation.setUpdated(false);
				operationAdapter.replace(operation);

				ArrayList<MeasureValueRes> values = operation
						.getMeasureValues();
				for (MeasureValueRes value : values) {
					value.setUpdated(false);
					valueAdapter.replace(value);
				}

				EquipmentOperationResultRes result = operation
						.getEquipmentOperationResult();
				result.setUpdated(false);
				operationResultAdapter.replace(result);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

}
