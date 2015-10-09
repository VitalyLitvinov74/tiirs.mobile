/**
 * 
 */
package ru.toir.mobile.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
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
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.CriticalTypeSrv;
import ru.toir.mobile.serverapi.EquipmentDocumentationSrv;
import ru.toir.mobile.serverapi.DocumentationTypeSrv;
import ru.toir.mobile.serverapi.EquipmentSrv;
import ru.toir.mobile.serverapi.EquipmentStatusSrv;
import ru.toir.mobile.serverapi.EquipmentTypeSrv;
import ru.toir.mobile.serverapi.MeasureTypeSrv;
import ru.toir.mobile.serverapi.OperationPatternSrv;
import ru.toir.mobile.serverapi.OperationPatternStepSrv;
import ru.toir.mobile.serverapi.OperationResultSrv;
import ru.toir.mobile.serverapi.OperationTypeSrv;
import ru.toir.mobile.serverapi.ReferenceListSrv;
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

	private String mServerUrl;
	private Context mContext;

	private static String dateFormat = "yyyy-MM-dd'T'HH:mm:ss";

	private static class ReferenceName {
		public static String CriticalType = "CriticalityType";
		public static String DocumentType = "DocumentType";
		public static String EquipmentStatus = "EquipmentStatus";
		public static String EquipmentType = "EquipmentType";
		public static String MeasureType = "MeasureType";
		public static String OperationResult = "OperationResult";
		public static String OperationStatus = "OperationStatus";
		public static String OperationType = "OperationType";
		public static String TaskStatus = "TaskStatus";
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
				throw new Exception(
						"Не удалось получить данные справочника. URL: " + url);
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
	public Bundle getOperationPattern(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		ArrayList<String> patternUuids = bundle
				.getStringArrayList(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID);
		boolean inParrentTransaction;

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		inParrentTransaction = db.inTransaction();

		// если транзакция не открыта раньше, открываем её
		if (!inParrentTransaction) {
			db.beginTransaction();
		}

		for (String uuid : patternUuids) {
			url.setLength(0);
			url.append(mServerUrl).append("/api/operationpatterns/")
					.append(uuid);
			jsonString = getReferenceData(url.toString());

			if (jsonString != null) {
				Gson gson = new GsonBuilder().setDateFormat(dateFormat)
						.create();

				// разбираем и сохраняем полученные данные
				result = savePattern(gson.fromJson(jsonString,
						OperationPatternSrv.class));
				boolean success = result.getBoolean(IServiceProvider.RESULT);
				if (!success) {
					if (!inParrentTransaction) {
						db.endTransaction();
					}
					return result;
				}
			} else {
				if (!inParrentTransaction) {
					db.endTransaction();
				}
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				return result;
			}
		}

		if (!inParrentTransaction) {
			db.setTransactionSuccessful();
			db.endTransaction();
		}

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Получаем возможные результаты выполнения операции
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getOperationResult(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		String[] operationTypeUuids = bundle
				.getStringArray(ReferenceServiceProvider.Methods.GET_OPERATION_RESULT_PARAMETER_UUID);
		StringBuilder url = new StringBuilder();
		String jsonString;
		boolean inParrentTransaction;

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		inParrentTransaction = db.inTransaction();

		String referenceUrl = getReferenceURL(ReferenceName.OperationResult);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();

		if (!inParrentTransaction) {
			db.beginTransaction();
		}

		for (String typeUuid : operationTypeUuids) {
			url.setLength(0);
			url.append(mServerUrl).append('/').append(referenceUrl).append('?')
					.append("OperationTypeId=").append(typeUuid);
			jsonString = getReferenceData(url.toString());
			if (jsonString != null) {
				// разбираем и сохраняем полученные данные
				ArrayList<OperationResultSrv> results = gson.fromJson(
						jsonString,
						new TypeToken<ArrayList<OperationResultSrv>>() {
							private static final long serialVersionUID = 1l;
						}.getType());
				result = saveOperationResult(results);
				boolean success = result.getBoolean(IServiceProvider.RESULT);
				if (!success) {
					if (!inParrentTransaction) {
						db.endTransaction();
					}
					return result;
				}
			} else {
				if (!inParrentTransaction) {
					db.endTransaction();
				}
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				return result;
			}
		}

		if (!inParrentTransaction) {
			db.setTransactionSuccessful();
			db.endTransaction();
		}

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;

	}

	/**
	 * Получаем типы документов
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getDocumentType(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.DocumentType);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<DocumentationTypeSrv> types = gson.fromJson(jsonString,
					new TypeToken<ArrayList<DocumentationTypeSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveDocumentType(types);
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
	 * Получаем файл документации
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getDocumentaionFile(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String fileUuids[] = bundle
				.getStringArray(ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE_PARAMETER_UUID);
		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new TOiRDatabaseContext(mContext));
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(mContext));

		for (String fileUuid : fileUuids) {
			EquipmentDocumentation document = documentationDBAdapter
					.getItem(fileUuid);
			Equipment equipment = equipmentDBAdapter.getItem(document
					.getEquipment_uuid());

			url.setLength(0);
			url.append(mServerUrl).append(document.getPath());

			try {
				URI requestUri = new URI(url.toString());
				Log.d("test", "requestUri = " + requestUri.toString());

				Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
				List<String> tList = new ArrayList<String>();
				tList.add("bearer " + AuthorizedUser.getInstance().getToken());
				headers.put("Authorization", tList);

				Request request = new Request(Method.GET, requestUri, headers,
						null);
				Response response = new RestClient().execute(request);

				if (response.mStatus == 200) {
					File file = new File(
							mContext.getExternalFilesDir("documentation") + "/"
									+ equipment.getEquipment_type_uuid() + "/"
									+ equipment.getUuid(), document.getPath());
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(response.mBody);
					fos.close();
				} else {
					throw new Exception("Не удалось получить файл. URL: " + url);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	/**
	 * Получаем статусы оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getEquipmentStatus(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.EquipmentStatus);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<EquipmentStatusSrv> statuses = gson.fromJson(jsonString,
					new TypeToken<ArrayList<EquipmentStatusSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveEquipmentStatus(statuses);
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

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.EquipmentType);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<EquipmentTypeSrv> types = gson.fromJson(jsonString,
					new TypeToken<ArrayList<EquipmentTypeSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveEquipmentType(types);
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

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.MeasureType);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<MeasureTypeSrv> list = gson.fromJson(jsonString,
					new TypeToken<ArrayList<MeasureTypeSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveMeasureType(list);
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

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.OperationStatus);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<OperationStatusSrv> list = gson.fromJson(jsonString,
					new TypeToken<ArrayList<OperationStatusSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveOperationStatus(list);
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
	 * Получаем типы операций
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getOperationType(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.OperationType);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<OperationTypeSrv> operations = gson.fromJson(jsonString,
					new TypeToken<ArrayList<OperationTypeSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveOperationType(operations);
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
	 * Получаем статусы нарядов
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getTaskStatus(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.TaskStatus);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<TaskStatusSrv> list = gson.fromJson(jsonString,
					new TypeToken<ArrayList<TaskStatusSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveTaskStatus(list);
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

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;

		Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();

		String[] equipmentUuids = bundle
				.getStringArray(ReferenceServiceProvider.Methods.GET_EQUIPMENT_PARAMETER_UUID);

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		db.beginTransaction();

		for (String equipmentUuid : equipmentUuids) {
			url.setLength(0);
			url.append(mServerUrl).append("/api/equipment/")
					.append(equipmentUuid);

			jsonString = getReferenceData(url.toString());
			if (jsonString != null) {
				// разбираем и сохраняем полученные данные
				EquipmentSrv equipment = gson.fromJson(jsonString,
						new TypeToken<EquipmentSrv>() {
							private static final long serialVersionUID = 1l;
						}.getType());

				boolean result = saveEquipment(equipment);
				if (!result) {
					db.endTransaction();
					return false;
				}
			} else {
				db.endTransaction();
				return false;
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		return true;
	}

	/**
	 * Получаем типы критичности оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getCriticalType(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.CriticalType);
		if (referenceUrl == null) {
			return false;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		lastChangedAt = adapter.getLastChangedAt();
		if (lastChangedAt != null) {
			url.append('?')
					.append("ChangedAfter=")
					.append(DataUtils.getDate(lastChangedAt + 1000, dateFormat));
		}

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
			// разбираем и сохраняем полученные данные
			SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
					.getWritableDatabase();
			db.beginTransaction();
			ArrayList<CriticalTypeSrv> types = gson.fromJson(jsonString,
					new TypeToken<ArrayList<CriticalTypeSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			boolean result = saveCriticalType(types);
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
	 * Получаем документацию
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getDocumentation(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		String[] equipmentUuids = bundle
				.getStringArray(ReferenceServiceProvider.Methods.GET_DOCUMENTATION_PARAMETER_UUID);
		StringBuilder url = new StringBuilder();
		String jsonString;

		SQLiteDatabase db = DatabaseHelper.getInstance(mContext)
				.getWritableDatabase();
		db.beginTransaction();

		for (String equipmentUuid : equipmentUuids) {
			url.setLength(0);
			url.append(mServerUrl)
					.append(String.format("/api/equipment/%s/documents",
							equipmentUuid));
			jsonString = getReferenceData(url.toString());
			if (jsonString != null) {
				Gson gson = new GsonBuilder().setDateFormat(dateFormat)
						.create();
				// разбираем и сохраняем полученные данные
				ArrayList<EquipmentDocumentationSrv> list = gson.fromJson(
						jsonString,
						new TypeToken<ArrayList<EquipmentDocumentationSrv>>() {
							private static final long serialVersionUID = 1l;
						}.getType());
				boolean result = saveDocumentations(list, equipmentUuid);
				if (!result) {
					db.endTransaction();
					return false;
				}
			} else {
				db.endTransaction();
				return false;
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		return true;
	}

	/**
	 * Получаем все справочники
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getAll(Bundle bundle) {

		if (!checkToken()) {
			return false;
		}

		// TODO определиться как всё-таки будут обновляться справочники
		// на каждом устройстве будет копия всех данных с сервера?
		// совершенно не нужно тащить все объекты оборудования на каждое
		// устройство.
		// обновлять будем только те данные которые есть на устройстве?
		// можно пропустить новые данные.
		if (!getCriticalType(bundle)) {
			return false;
		}

		if (!getDocumentType(bundle)) {
			return false;
		}

		if (!getEquipmentStatus(bundle)) {
			return false;
		}

		if (!getEquipmentType(bundle)) {
			return false;
		}

		if (!getMeasureType(bundle)) {
			return false;
		}

		EquipmentOperationDBAdapter operationAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentOperation> operations = operationAdapter
				.getItems(null);
		if (operations != null) {
			Set<String> typeUuids = new HashSet<String>();
			for (EquipmentOperation operation : operations) {
				typeUuids.add(operation.getOperation_type_uuid());
			}
			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_OPERATION_RESULT_PARAMETER_UUID,
					typeUuids.toArray(new String[] {}));
			Bundle result = getOperationResult(bundle);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (!success) {
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				// TODO сделать правильный возврат!!!
				// return result;
				return false;
			}
		}

		if (!getOperationStatus(bundle)) {
			return false;
		}

		if (!getOperationType(bundle)) {
			return false;
		}

		if (!getTaskStatus(bundle)) {
			return false;
		}

		EquipmentDBAdapter equipmentAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<Equipment> equipments = equipmentAdapter.getAllItems("", "");
		if (equipments != null) {
			Set<String> uuids = new HashSet<String>();
			for (Equipment equipment : equipments) {
				uuids.add(equipment.getUuid());
			}

			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_DOCUMENTATION_PARAMETER_UUID,
					uuids.toArray(new String[] {}));
			if (!getDocumentation(bundle)) {
				return false;
			}

			bundle.clear();
			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_EQUIPMENT_PARAMETER_UUID,
					uuids.toArray(new String[] {}));
			if (!getEquipment(bundle)) {
				return false;
			}

		}

		return true;
	}

	private String getReferenceURL(String referenceName) {

		String referenceUrl = null;
		StringBuilder url = new StringBuilder();
		String jsonString;

		url.append(mServerUrl).append("/api/references");

		jsonString = getReferenceData(url.toString());
		if (jsonString != null) {
			Gson gson = new GsonBuilder().create();
			// разбираем полученные данные
			ArrayList<ReferenceListSrv> list = gson.fromJson(jsonString,
					new TypeToken<ArrayList<ReferenceListSrv>>() {
						private static final long serialVersionUID = 1l;
					}.getType());
			for (ReferenceListSrv item : list) {
				if (item.getReferenceName().equals(referenceName)) {
					referenceUrl = item.getLinks().get(0).getLink();
					break;
				}
			}
		}
		return referenceUrl;
	}

	/**
	 * Сохраняем в базу информацию по шаблону операции и связанные с ним данные.
	 * 
	 * @param pattern
	 */
	private Bundle savePattern(OperationPatternSrv pattern) {

		Bundle result = new Bundle();

		OperationPatternDBAdapter adapter0 = new OperationPatternDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (adapter0.replace(pattern.getLocal()) == -1) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		OperationPatternStepDBAdapter adapter1 = new OperationPatternStepDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter1.saveItems(OperationPatternSrv
				.getOperationPatternSteps(pattern))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		OperationPatternStepResultDBAdapter adapter2 = new OperationPatternStepResultDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter2.saveItems(OperationPatternSrv
				.getOperationPatternStepResults(pattern))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		MeasureTypeDBAdapter adapter3 = new MeasureTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter3.saveItems(OperationPatternStepSrv.getMeasureTypes(pattern
				.getSteps()))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Сохраняем в базу возможные результаты выполнения и типы операций
	 * 
	 * @param results
	 * @return
	 */
	private Bundle saveOperationResult(ArrayList<OperationResultSrv> results) {

		Bundle result = new Bundle();

		OperationResultDBAdapter adapter0 = new OperationResultDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!adapter0
				.saveItems(OperationResultSrv.getOperationResults(results))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		OperationTypeDBAdapter adapter1 = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (!adapter1.saveItems(OperationResultSrv.getOperationTypes(results))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Сохраняем в базу типы документов
	 * 
	 * @param results
	 * @return
	 */
	private boolean saveDocumentType(ArrayList<DocumentationTypeSrv> array) {

		if (array == null) {
			return false;
		}

		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter
				.saveItems(DocumentationTypeSrv.getDocumentationTypes(array))) {
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
	private boolean saveEquipmentStatus(ArrayList<EquipmentStatusSrv> array) {

		if (array == null) {
			return false;
		}

		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter.saveItems(EquipmentStatusSrv.getEquipmentStatuses(array))) {
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
	private boolean saveEquipmentType(ArrayList<EquipmentTypeSrv> array) {

		if (array == null) {
			return false;
		}

		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter.saveItems(EquipmentTypeSrv.getEquipmentTypes(array))) {
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
	private boolean saveMeasureType(ArrayList<MeasureTypeSrv> array) {

		if (array == null) {
			return false;
		}

		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter.saveItems(MeasureTypeSrv.getMeasureTypes(array))) {
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
	private boolean saveTaskStatus(ArrayList<TaskStatusSrv> array) {

		if (array == null) {
			return false;
		}

		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter.saveItems(TaskStatusSrv.getTaskStatuses(array))) {
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
	private boolean saveOperationStatus(ArrayList<OperationStatusSrv> array) {

		if (array == null) {
			return false;
		}

		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter.saveItems(OperationStatusSrv.getOperationStatuses(array))) {
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
	private boolean saveOperationType(ArrayList<OperationTypeSrv> array) {

		if (array == null) {
			return false;
		}

		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter.saveItems(OperationTypeSrv.getOperationTypes(array))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Сохраняем в базу оборудование
	 * 
	 * @param element
	 * @return
	 */
	private boolean saveEquipment(EquipmentSrv element) {

		if (element == null) {
			return false;
		}

		EquipmentDBAdapter equipmentAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (equipmentAdapter.replace(element.getLocal()) == -1) {
			return false;
		}

		EquipmentTypeDBAdapter equipmentTypeAdapter = new EquipmentTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (equipmentTypeAdapter.replace(element.getEquipmentType().getLocal()) == -1) {
			return false;
		}

		CriticalTypeDBAdapter criticalTypeAdapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (criticalTypeAdapter
				.replace(element.getCriticalityType().getLocal()) == -1) {
			return false;
		}

		EquipmentStatusDBAdapter equipmentStatusAdapter = new EquipmentStatusDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (equipmentStatusAdapter.replace(element.getEquipmentStatus()
				.getLocal()) == -1) {
			return false;
		}

		EquipmentDocumentationDBAdapter documentationAdapter = new EquipmentDocumentationDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentSrv> elements = new ArrayList<EquipmentSrv>();
		elements.add(element);
		if (!documentationAdapter.saveItems(EquipmentSrv
				.getEquipmentDocumentations(elements))) {
			return false;
		}

		DocumentationTypeDBAdapter documentationTypeAdapter = new DocumentationTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		if (!documentationTypeAdapter.saveItems(EquipmentSrv
				.getDocumentationTypes(elements))) {
			return false;
		}

		return true;
	}

	/**
	 * Сохраняем документацию
	 * 
	 * @param array
	 *            Список документации в серверном представлении
	 * @param equipmentUuid
	 *            UUID оборудования к которому привязана документация
	 * @return
	 */
	private boolean saveDocumentations(
			ArrayList<EquipmentDocumentationSrv> array, String equipmentUuid) {

		if (array == null) {
			return false;
		}

		EquipmentDocumentationDBAdapter adapter = new EquipmentDocumentationDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (!adapter.saveItems(EquipmentDocumentationSrv
				.getEquipmentDocumentations(array, equipmentUuid))) {
			return false;
		}

		if (!saveDocumentType(EquipmentDocumentationSrv
				.getDocumentationTypesSrv(array))) {
			return false;
		}

		return true;
	}

	/**
	 * Сохраняем в базу типы критичности оборудования
	 * 
	 * @param array
	 * @return
	 */
	private boolean saveCriticalType(ArrayList<CriticalTypeSrv> array) {

		if (array == null) {
			return false;
		}

		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));

		if (adapter.saveItems(CriticalTypeSrv.getCriticalTypes(array))) {
			return true;
		} else {
			return false;
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
