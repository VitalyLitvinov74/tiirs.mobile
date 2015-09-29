/**
 * 
 */
package ru.toir.mobile.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepDBAdapter;
import ru.toir.mobile.db.adapters.OperationPatternStepResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentStatus;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.MeasureType;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.OperationType;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.CriticalTypeSrv;
import ru.toir.mobile.serverapi.EquipmentDocumentationSrv;
import ru.toir.mobile.serverapi.DocumentationTypeSrv;
import ru.toir.mobile.serverapi.EquipmentSrv;
import ru.toir.mobile.serverapi.EquipmentStatusSrv;
import ru.toir.mobile.serverapi.EquipmentTypeSrv;
import ru.toir.mobile.serverapi.MeasureTypeSrv;
import ru.toir.mobile.serverapi.OperationPatternSrv;
import ru.toir.mobile.serverapi.OperationResultSrv;
import ru.toir.mobile.serverapi.OperationTypeSrv;
import ru.toir.mobile.serverapi.ParseHelper;
import ru.toir.mobile.serverapi.TaskStatusSrv;
import ru.toir.mobile.serverapi.OperationStatusSrv;
import ru.toir.mobile.utils.DataUtils;
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
public class ReferenceProcessor {

	private static final String GET_REFERENCE_URL = "/api/references/";
	private String mServerUrl;
	private Context mContext;

	public static class ReferenceNames {
		public static String CriticalTypeName = "CriticalityType";
		public static String DocumentTypeName = "DocumentType";
		public static String EquipmentName = "Equipment";
		public static String EquipmentStatusName = "EquipmentStatus";
		public static String EquipmentTypeName = "EquipmentType";
		public static String MeasureTypeName = "MeasureType";
		public static String OperationResultName = "OperationResult";
		public static String OperationStatusName = "OperationStatus";
		public static String OperationTypeName = "OperationType";
		public static String TaskStatusName = "TaskStatus";
		public static String DocumentationName = "";
		// TODO реализовать получение документов по конкретному оборудованию
		// api/equipment/{equipment_id}/documents
	}

	// TODO нужно изменить условие на сервере с >= на > для lastChangedAt
	// внятного ответа не получено, оставляем костыль в виде +1 секунды
	// это черевато тем, что если на сервере при создании записи дата изменения
	// не будет равна дате создания, новые данные не получим, до тех пор пока
	// запись не будет изменена

	/**
	 * 
	 */
	public ReferenceProcessor(Context context) throws Exception {

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);

		// урл к которому будем обращаться с запросами
		mServerUrl = sp.getString(context.getString(R.string.serverUrl), "");

		mContext = context;

		if (mServerUrl.equals("")) {
			throw new Exception("URL сервера не указан!");
		}
	}

	/**
	 * Делает запрос по переданному url и возвращает строку данных
	 * 
	 * @param url
	 * @return
	 */
	private String getReferenceData(String url) {

		try {
			URI requestUri = new URI(url);
			Log.d("test", "requestUri = " + requestUri.toString());

			Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
			List<String> tList = new ArrayList<String>();
			tList.add("bearer " + AuthorizedUser.getInstance().getToken());
			headers.put("Authorization", tList);

			Request request = new Request(Method.GET, requestUri, headers, null);
			Response response = new RestClient().execute(request);

			if (response.mStatus == 200) {
				String jsonString = new String(response.mBody);
				Log.d("test", jsonString);
				return jsonString;
			} else {
				throw new Exception("Не удалось получить справочник. URL: "
						+ url);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Получаем шаблон выполнения операции с шагами и вариантами выполнения
	 * шагов
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getOperationPattern(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		ArrayList<String> patternUuids = bundle
				.getStringArrayList(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID);

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		db.beginTransaction();

		for (String uuid : patternUuids) {
			url.setLength(0);
			url.append(mServerUrl).append("/api/operationpatterns/")
					.append(uuid);
			jsonString = getReferenceData(url.toString());

			if (jsonString != null) {
				Gson gson = new GsonBuilder().setDateFormat(
						"yyyy-MM-dd'T'hh:mm:ss").create();
				// разбираем и сохраняем полученные данные
				if (!savePattern(gson.fromJson(jsonString,
						OperationPatternSrv.class))) {
					db.endTransaction();
					return false;
				}
			} else {
				return false;
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		return true;
	}

	/**
	 * Получаем возможные результаты выполнения операции
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getOperationResult(Bundle bundle) {

		// TODO для OperationResult нужен параметр - OperationTypeId - для
		// выборки результатов только по определённому типу операции
		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.OperationResultName);

		// получаем дату последней модификации содержимого таблицы
		OperationResultDBAdapter adapter = new OperationResultDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveOperationResult(gson.fromJson(jsonString,
					OperationResultSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}

	}

	/**
	 * Получаем типы документов
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getDocumentType(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.DocumentTypeName);

		// получаем дату последней модификации содержимого таблицы
		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveDocumentType(gson.fromJson(jsonString,
					DocumentationTypeSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем статусы оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getEquipmentStatus(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.EquipmentStatusName);

		// получаем дату последней модификации содержимого таблицы
		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveEquipmentStatus(gson.fromJson(jsonString,
					EquipmentStatusSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем типы оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getEquipmentType(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.EquipmentTypeName);

		// получаем дату последней модификации содержимого таблицы
		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveEquipmentType(gson.fromJson(jsonString,
					EquipmentTypeSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем типы измерений
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getMeasureType(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.MeasureTypeName);

		// получаем дату последней модификации содержимого таблицы
		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveMeasureType(gson.fromJson(jsonString,
					MeasureTypeSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем статусы операций
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getOperationStatus(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.OperationStatusName);

		// получаем дату последней модификации содержимого таблицы
		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveOperationStatus(gson.fromJson(jsonString,
					OperationStatusSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем статусы операций
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getOperationType(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.OperationTypeName);

		// получаем дату последней модификации содержимого таблицы
		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveOperationType(gson.fromJson(jsonString,
					OperationTypeSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем статусы операций
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getTaskStatus(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.TaskStatusName);

		// получаем дату последней модификации содержимого таблицы
		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveTaskStatus(gson.fromJson(jsonString,
					TaskStatusSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем оборудование
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getEquipment(Bundle bundle) {

		// TODO реализовать передачу uuid оборудования, для обновления
		// информации по конкретному экземпляру
		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append("/api/equipment/");

		// получаем дату последней модификации содержимого таблицы
		EquipmentDBAdapter adapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveEquipment(gson.fromJson(jsonString,
					EquipmentSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Получаем типы критичности оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getCriticalType(Bundle bundle) {

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		url.append(mServerUrl).append(GET_REFERENCE_URL)
				.append(ReferenceNames.CriticalTypeName);

		// получаем дату последней модификации содержимого таблицы
		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000,
							"yyyy-MM-dd'T'HH:mm:ss"));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder()
					.setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			boolean result = saveCriticalType(gson.fromJson(jsonString,
					CriticalTypeSrv[].class));
			if (result) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу информацию по шаблону операции и связанные с ним данные.
	 * 
	 * @param pattern
	 */
	private boolean savePattern(OperationPatternSrv pattern) {

		OperationPatternDBAdapter adapter0 = new OperationPatternDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (adapter0.replace(pattern.getLocal()) == -1) {
			return false;
		}

		OperationPatternStepDBAdapter adapter1 = new OperationPatternStepDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter1.saveItems(ParseHelper.getOperationPatternSteps(pattern))) {
			return false;
		}

		OperationPatternStepResultDBAdapter adapter2 = new OperationPatternStepResultDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter2.saveItems(ParseHelper
				.getOperationPatternStepResults(pattern))) {
			return false;
		}

		MeasureTypeDBAdapter adapter3 = new MeasureTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter3
				.saveItems(ParseHelper.getMeasureTypes(pattern.getSteps()))) {
			return false;
		}

		return true;
	}

	/**
	 * Сохраняем в базу возможные результаты выполнения и типы операций
	 * 
	 * @param results
	 * @return
	 */
	private boolean saveOperationResult(OperationResultSrv[] results) {

		OperationResultDBAdapter adapter0 = new OperationResultDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter0.saveItems(ParseHelper.getOperationResults(results))) {
			return false;
		}

		OperationTypeDBAdapter adapter1 = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter1.saveItems(ParseHelper.getOperationTypes(results))) {
			return false;
		}

		return true;
	}

	/**
	 * Сохраняем в базу типы документов
	 * 
	 * @param results
	 * @return
	 */
	private boolean saveDocumentType(DocumentationTypeSrv[] array) {

		if (array == null) {
			return false;
		}

		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<DocumentationType> list = new ArrayList<DocumentationType>();

		for (DocumentationTypeSrv element : array) {
			DocumentationType item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу статусы оборудования
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveEquipmentStatus(EquipmentStatusSrv[] array) {

		if (array == null) {
			return false;
		}

		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentStatus> list = new ArrayList<EquipmentStatus>();

		for (EquipmentStatusSrv element : array) {
			EquipmentStatus item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу типы оборудования
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveEquipmentType(EquipmentTypeSrv[] array) {

		if (array == null) {
			return false;
		}

		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentType> list = new ArrayList<EquipmentType>();

		for (EquipmentTypeSrv element : array) {
			EquipmentType item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу типы измерений
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveMeasureType(MeasureTypeSrv[] array) {

		if (array == null) {
			return false;
		}

		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<MeasureType> list = new ArrayList<MeasureType>();

		for (MeasureTypeSrv element : array) {
			MeasureType item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу статусы нарядов
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveTaskStatus(TaskStatusSrv[] array) {

		if (array == null) {
			return false;
		}

		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<TaskStatus> list = new ArrayList<TaskStatus>();

		for (TaskStatusSrv element : array) {
			TaskStatus item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу статусы операций
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveOperationStatus(OperationStatusSrv[] array) {

		if (array == null) {
			return false;
		}

		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<OperationStatus> list = new ArrayList<OperationStatus>();

		for (OperationStatusSrv element : array) {
			OperationStatus item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу типы операций операций
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveOperationType(OperationTypeSrv[] array) {

		if (array == null) {
			return false;
		}

		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<OperationType> list = new ArrayList<OperationType>();

		for (OperationTypeSrv element : array) {
			OperationType item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу оборудование
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveEquipment(EquipmentSrv[] array) {

		if (array == null) {
			return false;
		}

		EquipmentDBAdapter adapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(mContext));
		adapter.saveItems(ParseHelper.getEquipments(array));

		for (EquipmentSrv element : array) {
			saveEquipmentType(new EquipmentTypeSrv[] { element
					.getEquipmentType() });
			saveCriticalType(element.getCriticalityType());
			saveEquipmentStatus(new EquipmentStatusSrv[] { element
					.getEquipmentStatus() });
			saveDocuments(element.getDocuments(), element.getId());
		}
		// TODO перипасать верный алгоритм получения оборудования!!!
		return false;
	}

	/**
	 * Сохраняем в базу документацию
	 * 
	 * @param array
	 * @return
	 */
	private void saveDocuments(List<EquipmentDocumentationSrv> array,
			String equipmentUuid) {

		if (array == null) {
			return;
		}

		EquipmentDocumentationDBAdapter adapter = new EquipmentDocumentationDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentDocumentation> list = new ArrayList<EquipmentDocumentation>();

		for (EquipmentDocumentationSrv element : array) {
			EquipmentDocumentation item = element.getLocal(equipmentUuid);
			saveDocumentType(new DocumentationTypeSrv[] { element
					.getDocumentType() });
			list.add(item);
		}
		adapter.saveItems(list);
	}

	/**
	 * Сохраняем в базу типы критичности оборудования
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveCriticalType(CriticalTypeSrv[] array) {

		if (array == null) {
			return false;
		}

		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<CriticalType> list = new ArrayList<CriticalType>();

		for (CriticalTypeSrv element : array) {
			CriticalType item = element.getLocal();
			list.add(item);
		}

		if (adapter.saveItems(list)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу типы критичности оборудования
	 * 
	 * @param array
	 * @return
	 */
	private void saveCriticalType(CriticalTypeSrv type) {

		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		adapter.replace(type.getLocal());
	}

}
