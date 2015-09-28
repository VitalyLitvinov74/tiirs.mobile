/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serializer.EquipmentOperationResultSerializer;
import ru.toir.mobile.serializer.EquipmentOperationSerializer;
import ru.toir.mobile.serializer.MeasureValueSerializer;
import ru.toir.mobile.serializer.TaskResultSerializer;
import ru.toir.mobile.serializer.TaskSerializer;
import ru.toir.mobile.serverapi.EquipmentOperationSrv;
import ru.toir.mobile.serverapi.EquipmentSrv;
import ru.toir.mobile.serverapi.ParseHelper;
import ru.toir.mobile.serverapi.TaskSrv;
import ru.toir.mobile.serverapi.result.TaskResultRes;
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
public class TaskProcessor {

	private Context mContext;
	private static final String TASK_GET_URL = "/api/orders/";
	private static final String TASK_SEND_RESULT_URL = "/taskresult.php";
	private String mServerUrl;

	Set<String> patternUuids;
	
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
	 * @param bundle
	 * @return
	 */
	public boolean GetTask(Bundle bundle) {

		// TODO сделать вызов двух медотодов - получить наряды, получить шаблоны
		// TODO реализовать внятное поведение с возвратом кода и еще нереализованой транзакцией
		boolean result = false;
		
		result = getTasks();
		if (!result) {
			return false;
		}
		
		result = getPatterns();
		if (!result) {
			return false;
		}

		return result;
	}
	
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
				
				Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
				
				TaskSrv[] serverTasks = gson.fromJson(jsonString, TaskSrv[].class);
				if (serverTasks != null) {

					// TODO нужен механизм для запуска транзакции т.е. операций вставки в базу много

					// разбираем и сохраняем полученные данные
					saveTasks(serverTasks);
					
					
					// TODO нужна проверка на то что успешно разобрали и сохранили в базу,
					return false;

				} else {
					// TODO нужно решить что делать если нарядов нет!!!
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

	private boolean getPatterns() {
		
		boolean result = false;

		// получаем данные по шаблонам
		ArrayList<String> operationPatternUuids = new ArrayList<String>(patternUuids);
		try {
			ReferenceProcessor referenceProcessor = new ReferenceProcessor(mContext);
			Bundle extra = new Bundle();
			extra.putStringArrayList(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID, operationPatternUuids);
			result = referenceProcessor.getOperationPattern(extra);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}

		return result;
	}
	
	public void saveTasks(TaskSrv[] tasks) {

		// новый вариант разбора и сохранения данных с сервера
		TaskDBAdapter adapter0 = new TaskDBAdapter(new TOiRDatabaseContext(mContext));
		adapter0.saveItems(ParseHelper.getTasks(tasks));						
		
		TaskStatusDBAdapter adapter1 = new TaskStatusDBAdapter(new TOiRDatabaseContext(mContext));
		adapter1.saveItems(ParseHelper.getTaskStatuses(tasks));
		
		EquipmentOperationDBAdapter adapter2 = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(mContext));
		adapter2.saveItems(ParseHelper.getEquipmentOperations(tasks));
		
		ArrayList<EquipmentOperationSrv> operations = ParseHelper.getEquipmentOperationSrvs(tasks);
		EquipmentDBAdapter adapter3 = new EquipmentDBAdapter(new TOiRDatabaseContext(mContext));
		adapter3.saveItems(ParseHelper.getEquipments(operations));
		
		OperationTypeDBAdapter adapter4 = new OperationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		adapter4.saveItems(ParseHelper.getOperationTypes(operations));
		
		OperationStatusDBAdapter adapter5 = new OperationStatusDBAdapter(new TOiRDatabaseContext(mContext));
		adapter5.saveItems(ParseHelper.getOperationStatuses(operations));
		
		patternUuids = ParseHelper.getOperationPatternUuids(operations);
		
		ArrayList<EquipmentSrv> equipments = ParseHelper.getEquipmentSrvs(operations);
		EquipmentTypeDBAdapter adapter6 = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(mContext));			
		adapter6.saveItems(ParseHelper.getEquipmentTypes(equipments));
		
		CriticalTypeDBAdapter adapter7 = new CriticalTypeDBAdapter(new TOiRDatabaseContext(mContext));
		adapter7.saveItems(ParseHelper.getCriticalTypes(equipments));
		
		EquipmentStatusDBAdapter adapter8 = new EquipmentStatusDBAdapter(new TOiRDatabaseContext(mContext));
		adapter8.saveItems(ParseHelper.getEquipmentStatuses(equipments));
		
		EquipmentDocumentationDBAdapter adapter9 = new EquipmentDocumentationDBAdapter(new TOiRDatabaseContext(mContext));
		adapter9.saveItems(ParseHelper.getEquipmentDocumentations(equipments));

		DocumentationTypeDBAdapter adapter10 = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		adapter10.saveItems(ParseHelper.getEquipmentDocumentationTypes(equipments));
	}

	/**
	 * Отправка результатов выполнения наряда.
	 * @param bundle
	 * @return
	 */
	public boolean TaskSendResult(Bundle bundle) {
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		String taskUuid = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TASK_UUID);

		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext));
		Task task;
		task = adapter.getTaskByUuidAndUpdated(taskUuid);

		if (task != null) {
			TaskResultRes taskResult = new TaskResultRes();
			if (taskResult.load(mContext, task.getUuid())) {
				ArrayList<TaskResultRes> taskResults = new ArrayList<TaskResultRes>();
				taskResults.add(taskResult);
				return TasksSendResults(taskResults, token);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Отправка результатов выполнения нарядов.
	 * @param bundle
	 * @return
	 */
	public boolean TasksSendResult(Bundle bundle) {
		
		String token = bundle.getString(TaskServiceProvider.Methods.PARAMETER_TOKEN);
		
		String user_uuid = AuthorizedUser.getInstance().getUuid();
		ArrayList<Task> tasks;
		TaskDBAdapter adapter = new TaskDBAdapter(new TOiRDatabaseContext(mContext));
		// TODO необходимо решить и реализовать выборку не отправленных нарядов, либо по текущему пользователю либо все какие есть неотправленные.
		tasks = adapter.getTaskByUserAndUpdated(user_uuid);

		// получаем из базы результаты связанные с нарядами
		ArrayList<TaskResultRes> taskResults = new ArrayList<TaskResultRes>();
		for (Task task : tasks) {
			TaskResultRes taskResult = new TaskResultRes();
			taskResult.load(mContext, task.getUuid());
			taskResults.add(taskResult);
		}

		return TasksSendResults(taskResults, token);
	}
	
	/**
	 * Отправка результатов выполнения нарядов на сервер
	 * @return
	 */
	private boolean TasksSendResults(ArrayList<TaskResultRes> tasks, String token) {

		URI requestUri = null;
		String jsonString = null;

		try {
			requestUri = new URI(mServerUrl + TASK_SEND_RESULT_URL);
			Log.d("test", "requestUri = " + requestUri.toString());
			
			
			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("Bearer " + token);
			headers.put("Authorization", tList);
			
			if (tasks != null) {
				StringBuilder postData = new StringBuilder();
				// TODO реализовать упаковку результатов в json объект
				//TaskResult taskResult = new TaskResult();
				//taskResult.loadTaskResult(mContext, "a1f3a9af-d05b-4123-858f-a753a46f97d5");
				//TaskResult[] resultArray = new TaskResult[] { taskResult };

				Gson gson = new GsonBuilder()
						.setPrettyPrinting()
						.registerTypeAdapter(Task.class, new TaskSerializer())
						.registerTypeAdapter(TaskResultRes.class, new TaskResultSerializer())
						.registerTypeAdapter(EquipmentOperation.class, new EquipmentOperationSerializer())
						.registerTypeAdapter(EquipmentOperationResult.class, new EquipmentOperationResultSerializer())
						.registerTypeAdapter(MeasureValue.class, new MeasureValueSerializer()).create();
				
				String json = gson.toJson(tasks);
				Log.d("test", json);

				postData.append("tasks=");
				postData.append(json);

				Request request = new Request(Method.POST, requestUri, headers, postData.toString().getBytes());
				Response response = new RestClient().execute(request);
				if (response.mStatus == 200) {
					// TODO реализовать разбор ответа с подтверждением об отправке результатов
					// TODO реализовать изменение статусов данных(updated) на "отправлено"
					jsonString = new String(response.mBody, "UTF-8");
					JSONArray jsonArray = new JSONArray(jsonString);
				} else {
					return false;
				}				
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
}
