/**
 * 
 */
package ru.toir.mobile.rest;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit.Call;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.BaseDBAdapter;
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
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentOperationResult;
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serializer.EquipmentOperationResultSerializer;
import ru.toir.mobile.serializer.EquipmentOperationSerializer;
import ru.toir.mobile.serializer.MeasureValueSerializer;
import ru.toir.mobile.serializer.TaskSerializer;
import ru.toir.mobile.serverapi.EquipmentOperationSrv;
import ru.toir.mobile.serverapi.EquipmentSrv;
import ru.toir.mobile.serverapi.TaskSrv;
import ru.toir.mobile.serverapi.TokenSrv;
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

	private static final String TASK_GET_URL = "/api/orders/";
	private static final String TASK_SEND_RESULT_URL = "/api/orders/";
	private Context mContext;
	private String mServerUrl;

	private Set<String> patternUuids;
	private Set<String> operationTypeUuids;
	private Set<String> requiredDocuments;
	private Set<String> equipmentImages;
	private Set<String> measureValuesImages;

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
	 * Получение нарядов
	 *
     * @param bundle Параметры
     * @return Bundle
     */
	public Bundle GetTask(Bundle bundle) {

		Bundle result = new Bundle();

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		String status = bundle
				.getString(TaskServiceProvider.Methods.PARAMETER_GET_TASK_STATUS);

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		db.beginTransaction();

		boolean success;

		// получаем наряды
		String url = mServerUrl + TASK_GET_URL + status;
		Bundle taskResult = getTasks(url);
		success = taskResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return taskResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(taskResult);
		}

		// получаем статусы нарядов
		Bundle taskStatusResult = getTaskStatuses();
		success = taskStatusResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return taskStatusResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(taskStatusResult);
		}

		// получаем шаблоны
        ArrayList<String> operationPatternUuids = new ArrayList<>(
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
                .toArray(new String[]{""}));
        success = operationResultsResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return operationResultsResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(operationResultsResult);
		}

		// получаем статусы операций
		Bundle operationStatusesResult = getOperationStatuses();
		success = operationStatusesResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return operationStatusesResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(operationStatusesResult);
		}

		// получаем обязательную документацию
		Bundle documentFileResult = getDocumentFiles();
		success = documentFileResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return documentFileResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(documentFileResult);
		}

		// получаем изображение оборудования
		Bundle imageFileResult = getEquipmentFiles();
		success = imageFileResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return imageFileResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(imageFileResult);
		}

		// получаем изображение результата измерения
		Bundle measureImageFileResult = getMeasureValueImages(measureValuesImages
                .toArray(new String[]{""}));
        success = measureImageFileResult.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			db.endTransaction();
			return measureImageFileResult;
		} else {
			// добавляем в результат все элементы ответа
			result.putAll(measureImageFileResult);
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	private Bundle getDocumentFiles() {

		try {
			ReferenceProcessor referenceProcessor = new ReferenceProcessor(
					mContext);
			Bundle extra = new Bundle();
			extra.putStringArray(
					ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE_PARAMETER_UUID,
                    requiredDocuments.toArray(new String[]{""}));

			return referenceProcessor.getDocumentationFile(extra);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	private Bundle getEquipmentFiles() {

		try {
			ReferenceProcessor referenceProcessor = new ReferenceProcessor(
					mContext);
			Bundle extra = new Bundle();
			extra.putStringArray(
					ReferenceServiceProvider.Methods.GET_IMAGE_FILE_PARAMETER_UUID,
                    equipmentImages.toArray(new String[]{""}));

			return referenceProcessor.getEquipmentFile(extra);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Получаем данные по нарядам
	 *
     * @return Bundle
     */
	private Bundle getTasks(String url) {

        URI requestUri;
        String token = AuthorizedUser.getInstance().getToken();
        String jsonString;
        Bundle result = new Bundle();

		try {
			requestUri = new URI(url);
			Log.d("test", "requestUri = " + requestUri.toString());

            Map<String, List<String>> headers = new ArrayMap<>();
            List<String> tList = new ArrayList<>();
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
     * @return Bundle
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
     * @return Bundle
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
	 * Получаем все возможные статусы операций
	 *
     * @return Bundle
     */
	private Bundle getOperationStatuses() {

		try {
			ReferenceProcessor processor = new ReferenceProcessor(mContext);
			Bundle bundle = new Bundle();
			return processor.getOperationStatus(bundle);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Получаем изображения к результатам измерений
	 *
     * @return Bundle
     */
	private Bundle getMeasureValueImages(String[] uuids) {

		try {
			ReferenceProcessor processor = new ReferenceProcessor(mContext);
			Bundle bundle = new Bundle();
			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_IMAGE_FILE_PARAMETER_UUID,
					uuids);
			return processor.getMeasureValueFile(bundle);
		} catch (Exception e) {
			e.printStackTrace();
			Bundle result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, e.getMessage());
			return result;
		}
	}

	/**
	 * Получаем статусы нарядов
	 *
     * @return Bundle
     */
	private Bundle getTaskStatuses() {

		try {
			ReferenceProcessor processor = new ReferenceProcessor(mContext);
			Bundle bundle = new Bundle();
			return processor.getTaskStatus(bundle);
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

		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(
				new ToirDatabaseContext(mContext));

		// проверяем наряды со статусом Новый на наличие в локальной базе, если
		// такой наряд есть, удаляем его из списка полученных нарядов.
		// то есть, если наряд на сервере всё еще новый, а у нас он уже есть,
		// значит он в работе.
		// наряд со статусом В работе с сервера приехать не может.
		// соответственно наряды со статусом Закончен и Не закончен,
		// загружаются и обновляют содержимое локального наряда
		Iterator<TaskSrv> taskIterator = tasks.iterator();
		while (taskIterator.hasNext()) {
			TaskSrv item = taskIterator.next();
			if (item.getOrderStatus()
					.getId()
					.toUpperCase(Locale.ENGLISH)
					.equals(TaskStatusDBAdapter.Status.NEW
							.toUpperCase(Locale.ENGLISH))) {
				if (taskDBAdapter.getItem(item.getId()) != null) {
					taskIterator.remove();
				}
			}
		}

		ArrayList<Task> localTasks = TaskSrv.getTasks(tasks);

		// добавляем в результат колличество полученных нарядов
		result.putInt(TaskServiceProvider.Methods.RESULT_GET_TASK_COUNT,
				localTasks.size());

		// для новых нарядов выставляем статус "В работе"
		for (Task item : localTasks) {
			if (item.getTask_status_uuid().equals(
					TaskStatusDBAdapter.Status.NEW)) {
				item.setTask_status_uuid(TaskStatusDBAdapter.Status.IN_WORK);
			}
		}

		// сохраняем наряды
		if (!taskDBAdapter.saveItems(localTasks)) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении нарядов.");
			return result;
		}

		// сохраняем статусы нарядов
		TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!taskStatusDBAdapter.saveItems(TaskSrv.getTaskStatuses(tasks))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении статусов нарядов.");
			return result;
		}

		// сохраняем операции
		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!operationDBAdapter
				.saveItems(TaskSrv.getEquipmentOperations(tasks))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении операций.");
			return result;
		}

		ArrayList<EquipmentOperationSrv> operations = TaskSrv
				.getEquipmentOperationSrvs(tasks);

		// сохраняем оборудование
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!equipmentDBAdapter.saveItems(EquipmentOperationSrv
				.getEquipments(operations))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении оборудования.");
			return result;
		}

		// сохраняем типы операций
		OperationTypeDBAdapter operationTypeDBAdapter = new OperationTypeDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!operationTypeDBAdapter.saveItems(EquipmentOperationSrv
				.getOperationTypes(operations))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов операций.");
			return result;
		}

		// сохраняем статусы операций
		OperationStatusDBAdapter operationStatusDBAdapter = new OperationStatusDBAdapter(
				new ToirDatabaseContext(mContext));
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

		// список полученного оборудования для загрузки позже изображений
        Map<String, String> tmpEquipmentImages = new HashMap<>();
        for (EquipmentSrv equipment : equipments) {
			tmpEquipmentImages.put(equipment.getId(), null);
		}

		equipmentImages = tmpEquipmentImages.keySet();

		// сохраняем типы оборудования
		EquipmentTypeDBAdapter equipmentTypeDBAdapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!equipmentTypeDBAdapter.saveItems(EquipmentSrv
				.getEquipmentTypes(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов оборудования.");
			return result;
		}

		// сохраняем типы критичности оборудования
		CriticalTypeDBAdapter criticalTypeDBAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!criticalTypeDBAdapter.saveItems(EquipmentSrv
				.getCriticalTypes(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов критичности оборудования.");
			return result;
		}

		// сохраняем статусы оборудования
		EquipmentStatusDBAdapter equipmentStatusDBAdapter = new EquipmentStatusDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!equipmentStatusDBAdapter.saveItems(EquipmentSrv
				.getEquipmentStatuses(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении статусов оборудования.");
			return result;
		}

		// получаем список документов для обязательной загрузки, позже загрузить
		ArrayList<EquipmentDocumentation> doclist = EquipmentSrv
				.getEquipmentDocumentations(equipments);
        Map<String, String> tmpRequiredDocuments = new HashMap<>();

		for (EquipmentDocumentation doc : doclist) {
			if (doc.isRequired()) {

				tmpRequiredDocuments.put(doc.getUuid(), null);
			}
		}

		requiredDocuments = tmpRequiredDocuments.keySet();

		// сохраняем записи о документации на оборудование
		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!documentationDBAdapter.saveItems(doclist)) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении документации.");
			return result;
		}

		// сохраняем типы документации
		DocumentationTypeDBAdapter documentationTypeDBAdapter = new DocumentationTypeDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!documentationTypeDBAdapter.saveItems(EquipmentSrv
				.getDocumentationTypes(equipments))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов документации.");
			return result;
		}

		// сохраняем записи о результатах выполненых работ
		EquipmentOperationResultDBAdapter equipmentOperationResultDBAdapter = new EquipmentOperationResultDBAdapter(
				new ToirDatabaseContext(mContext));
		for (TaskSrv taskSrv : tasks) {
			ArrayList<EquipmentOperationSrv> operationsSrv = taskSrv.getItems();
			for (EquipmentOperationSrv operationSrv : operationsSrv) {
				// если uuid "нулевой" то результатов нет, ни чего не пишем в
				// базу
				if (!operationSrv.getEquipmentOperationResultId().equals(
						BaseDBAdapter.uuidNull)) {
					EquipmentOperationResult operationResult = new EquipmentOperationResult();
					operationResult.setUuid(operationSrv
							.getEquipmentOperationResultId());
					operationResult.setEquipment_operation_uuid(operationSrv
							.getId());
					operationResult.setStart_date(operationSrv
							.getInspectionStartTime().getTime());
					operationResult.setEnd_date(operationSrv
							.getInspectionEndTime().getTime());
					operationResult.setOperation_result_uuid(operationSrv
							.getOperationResultId());
					// TODO не передаётся с сервера
					operationResult.setType(-1);
					operationResult.setCreatedAt(operationSrv
							.getCreatedAtTime());
					operationResult.setChangedAt(operationSrv
							.getChangedAtTime());
					equipmentOperationResultDBAdapter.replace(operationResult);
				}
			}
		}

		// сохраняем результаты измерений
		MeasureValueDBAdapter measureValueDBAdapter = new MeasureValueDBAdapter(
				new ToirDatabaseContext(mContext));
		ArrayList<MeasureValue> measureValues = EquipmentOperationSrv
				.getMeasureValues(tasks);
		if (!measureValueDBAdapter.saveItems(measureValues)) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении результатов измерений.");
			return result;
		}

		// строим список изображений результатов измерений
        Map<String, String> tmpMeasureValuesImages = new HashMap<>();
        for (MeasureValue measureValue : measureValues) {
			if (measureValue.getValue().startsWith("api/")) {
				tmpMeasureValuesImages.put(measureValue.getUuid(), null);
			}
		}

		measureValuesImages = tmpMeasureValuesImages.keySet();

		// если добрались сюда, значит всё в порядке
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Отправка результата выполнения наряда.
	 *
     * @param bundle Параметры
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

        ArrayList<TaskRes> taskResults = new ArrayList<>();

        if (taskUuids != null) {
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
        }

		return TasksSendResults(taskResults);
	}

	/**
	 * Вспомогательный метод для отправки результатов выполнения нарядов на
	 * сервер
     *
     * @param results Результаты выполнения наряда
     * @return {@link Bundle}
	 */
	private Bundle TasksSendResults(ArrayList<TaskRes> results) {

        URI requestUri;
        Bundle result;

		try {
			requestUri = new URI(mServerUrl + TASK_SEND_RESULT_URL);
			Log.d("test", "requestUri = " + requestUri.toString());

            Map<String, List<String>> headers = new ArrayMap<>();
            List<String> aList = new ArrayList<>();
            aList.add("Bearer " + AuthorizedUser.getInstance().getToken());
			headers.put("Authorization", aList);
            List<String> cList = new ArrayList<>();
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
				if (response.mStatus == 204 || response.mStatus == 200) {
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
			Call<TokenSrv> call = ToirAPIFactory.getTokenService().user(au.getTagId());
			try {
				retrofit.Response<TokenSrv> response = call.execute();
				TokenSrv token = response.body();
				au.setToken(token.getAccessToken());
				return true;
			} catch (IOException e) {
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
     * @param resultsList Список результатов выполнения нарядов
     */
	private void riseUpdated(ArrayList<TaskRes> resultsList) {
		TaskDBAdapter taskAdapter = new TaskDBAdapter(new ToirDatabaseContext(
				mContext));
		EquipmentOperationDBAdapter operationAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(mContext));
		EquipmentOperationResultDBAdapter operationResultAdapter = new EquipmentOperationResultDBAdapter(
				new ToirDatabaseContext(mContext));
		MeasureValueDBAdapter valueAdapter = new MeasureValueDBAdapter(
				new ToirDatabaseContext(mContext));

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
     * @param resultsList Список результатов выполнения нарядов
     */
	private void clearUpdated(ArrayList<TaskRes> resultsList) {

		TaskDBAdapter taskAdapter = new TaskDBAdapter(new ToirDatabaseContext(
				mContext));
		EquipmentOperationDBAdapter operationAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(mContext));
		EquipmentOperationResultDBAdapter operationResultAdapter = new EquipmentOperationResultDBAdapter(
				new ToirDatabaseContext(mContext));
		MeasureValueDBAdapter valueAdapter = new MeasureValueDBAdapter(
				new ToirDatabaseContext(mContext));

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
