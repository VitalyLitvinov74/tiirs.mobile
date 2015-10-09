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
	public Bundle GetTask(Bundle bundle) {

		Bundle result = new Bundle();

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		db.beginTransaction();

		boolean success;

		// получаем наряды
		Bundle taskResult = getTasks();
		success = taskResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return taskResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(taskResult);
		}

		// получаем шаблоны
		ArrayList<String> operationPatternUuids = new ArrayList<String>(
				patternUuids);
		Bundle patternResult = getPatterns(operationPatternUuids);
		success = patternResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return patternResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(patternResult);
		}

		// получаем возможные результаты выполнения операций
		Bundle operationResultsResult = getOperationResults(operationTypeUuids
				.toArray(new String[] {}));
		success = operationResultsResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return operationResultsResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(operationResultsResult);
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Получаем данные по нарядам
	 * 
	 * @return
	 */
	private Bundle getTasks() {

		URI requestUri = null;
		String token = AuthorizedUser.getInstance().getToken();
		String jsonString = null;
		Bundle result = new Bundle();

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
						"yyyy-MM-dd'T'HH:mm:ss").create();

				ArrayList<TaskSrv> serverTasks = gson.fromJson(jsonString,
						new TypeToken<ArrayList<TaskSrv>>() {
							private static final long serialVersionUID = 1l;
						}.getType());
				if (serverTasks != null) {
					// разбираем и сохраняем полученные данные
					return saveTasks(serverTasks);
				} else {
					result.putBoolean(IServiceProvider.RESULT, false);
					result.putString(IServiceProvider.MESSAGE,
							"Ошибка разбора ответа сервера на запрос нарядов.");
					return result;
				}
			} else {
				throw new Exception(
						"Не удалось получить наряды. RESPONSE STATUS = "
								+ response.mStatus);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Получаем данные по шаблонам. Напрямую вызываем метод из процессора
	 * справочников.
	 * 
	 * @param uuids
	 *            список uuid шаблонов которые нужно получить
	 * @return
	 */
	private Bundle getPatterns(ArrayList<String> uuids) {

		try {
			ReferenceProcessor referenceProcessor = new ReferenceProcessor(
					mContext);
			Bundle extra = new Bundle();
			extra.putStringArrayList(
					ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID,
					uuids);

			return referenceProcessor.getOperationPattern(extra);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Получаем данные по возможным результатам выполнения операций. Напрямую
	 * вызываем метод из процессора справочников.
	 * 
	 * @param uuids
	 *            список uuid типов операций для которых нужно получить данные
	 * @return
	 */
	private Bundle getOperationResults(String[] uuids) {

		try {
			ReferenceProcessor processor = new ReferenceProcessor(mContext);
			Bundle bundle = new Bundle();
			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_OPERATION_RESULT_PARAMETER_UUID,
					uuids);

			return processor.getOperationResult(bundle);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Сохраняем в базу составные части наряда
	 * 
	 * @param tasks
	 *            Список нарядов в серверном представлении
	 * @return Bundle
	 */
	private Bundle saveTasks(ArrayList<TaskSrv> tasks) {

		Bundle result = new Bundle();

		ArrayList<Task> localTasks = TaskSrv.getTasks(tasks);

		// добавляем в результат колличество полученных нарядов
		result.putInt(TaskServiceProvider.Methods.RESULT_GET_TASK_COUNT,
				localTasks.size());

		// для новых нарядов выставляем статус "В работе"
		for (Task item : localTasks) {
			if (item.getTask_status_uuid().equals(Task.Extras.STATUS_UUID_NEW)) {
				item.setTask_status_uuid(Task.Extras.STATUS_UUID_IN_PROCESS);
			}
		}

		// сохраняем
		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!taskDBAdapter.saveItems(localTasks)) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении нарядов.");
			return result;
		}

		// сохраняем
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!taskStatusDBAdapter.saveItems(TaskSrv.getTaskStatuses(tasks))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении статусов нарядов.");
			return result;
		}

		// сохраняем
		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!operationDBAdapter
				.saveItems(TaskSrv.getEquipmentOperations(tasks))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении операций.");
			return result;
		}

		ArrayList<EquipmentOperationSrv> operations = TaskSrv
				.getEquipmentOperationSrvs(tasks);

		// сохраняем
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!equipmentDBAdapter.saveItems(EquipmentOperationSrv
				.getEquipments(operations))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении оборудования.");
			return result;
		}

		// сохраняем
		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!operationTypeDBAdapter.saveItems(EquipmentOperationSrv
				.getOperationTypes(operations))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов операций.");
			return result;
		}

		// сохраняем
		OperationStatusDBAdapter operationStatusDBAdapter = new OperationStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!operationStatusDBAdapter.saveItems(EquipmentOperationSrv
				.getOperationStatuses(operations))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении статусов операций.");
			return result;
		}

		// получаем список шаблонов для того чтобы позже загрузить их
		patternUuids = EquipmentOperationSrv
				.getOperationPatternUuids(operations);

		/*
		 * получаем список типов операций чтобы позже загрузить варианты
		 * результатов выполнения операций
		 */
		operationTypeUuids = EquipmentOperationSrv
				.getOperationTypeUuids(operations);

		ArrayList<EquipmentSrv> equipments = EquipmentOperationSrv
				.getEquipmentSrvs(operations);

		// сохраняем
		EquipmentTypeDBAdapter equipmentTypeDBAdapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!equipmentTypeDBAdapter.saveItems(EquipmentSrv
				.getEquipmentTypes(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов оборудования.");
			return result;
		}

		// сохраняем
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!criticalTypeDBAdapter.saveItems(EquipmentSrv
				.getCriticalTypes(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов критичности оборудования.");
			return result;
		}

		// сохраняем
		EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!equipmentStatusDBAdapter.saveItems(EquipmentSrv
				.getEquipmentStatuses(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении статусов оборудования.");
			return result;
		}

		// сохраняем
		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!documentationDBAdapter.saveItems(EquipmentSrv
				.getEquipmentDocumentations(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении документации.");
			return result;
		}

		// сохраняем
		DocumentationTypeDBAdapter documentationTypeDBAdapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!documentationTypeDBAdapter.saveItems(EquipmentSrv
				.getDocumentationTypes(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов документации.");
			return result;
		}

		// если добрались сюда, значит всё в порядке
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Отправка результата выполнения наряда.
	 * 
	 * @param bundle
	 * @return {@link Bundle}
	 */
	public Bundle TaskSendResult(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		// список uuid нарядов(результатов) для отправки на сервер
		String[] taskUuids = bundle
				.getStringArray(TaskServiceProvider.Methods.PARAMETER_TASK_UUID);

		ArrayList<TaskRes> taskResults = new ArrayList<TaskRes>();

		// загружаем данные в формат понятный серверу
		for (String taskUuid : taskUuids) {
			TaskRes taskResult = TaskRes.load(mContext, taskUuid);
			if (taskResult != null) {
				taskResults.add(taskResult);
			} else {
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				result.putString(IServiceProvider.MESSAGE,
						"Ошибка при чтении результатов выполнения наряда.");
				return result;
			}
		}

		return TasksSendResults(taskResults);
	}

	/**
	 * Вспомогательный метод для отправки результатов выполнения нарядов на
	 * сервер
	 * 
	 * @return {@link Bundle}
	 */
	private Bundle TasksSendResults(ArrayList<TaskRes> results) {

		URI requestUri = null;
		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
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
					throw new Exception(
							"Не удалось отправить результаты. RESPONSE STATUS = "
									+ response.mStatus);
				}
			}

		} catch (Exception e) {
			riseUpdated(results);
			e.printStackTrace();
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
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
			task.setAttempt_count(0);
			taskAdapter.replace(task);

			ArrayList<EquipmentOperationRes> operations = task
					.getEquipmentOperations();
			for (EquipmentOperationRes operation : operations) {
				operation.setUpdated(false);
				operation.setAttempt_count(0);
				operationAdapter.replace(operation);

				ArrayList<MeasureValueRes> values = operation
						.getMeasureValues();
				for (MeasureValueRes value : values) {
					value.setUpdated(false);
					value.setAttempt_count(0);
					valueAdapter.replace(value);
				}

				EquipmentOperationResultRes result = operation
						.getEquipmentOperationResult();
				result.setUpdated(false);
				result.setAttempt_count(0);
				operationResultAdapter.replace(result);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

}
