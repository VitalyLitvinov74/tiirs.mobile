/**
 * 
 */
package ru.toir.mobile.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureValueDBAdapter;
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
import ru.toir.mobile.db.tables.MeasureValue;
import ru.toir.mobile.db.tables.OperationPatternStep;
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
import android.os.Environment;
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

		// получаем изображения к шагам шаблона операции
		OperationPatternStepDBAdapter stepDBAdapter = new OperationPatternStepDBAdapter(
				new ToirDatabaseContext(mContext));
		for (String patternUuid : patternUuids) {
			ArrayList<OperationPatternStep> steps = stepDBAdapter
					.getItems(patternUuid);

			for (OperationPatternStep step : steps) {
				if (step.getImage() == null) {
					continue;
				}
				url.setLength(0);
				url.append(mServerUrl).append("/api/operationpatterns/")
						.append(patternUuid).append("/steps/")
						.append(step.getUuid()).append("/images/")
						.append(step.getUuid()).append(".jpg");

				try {
					URI requestUri = new URI(url.toString());
					Log.d("test", "requestUri = " + requestUri.toString());

					Map<String, List<String>> headers = new ArrayMap<String, List<String>>();
					List<String> tList = new ArrayList<String>();
					tList.add("bearer "
							+ AuthorizedUser.getInstance().getToken());
					headers.put("Authorization", tList);

					Request request = new Request(Method.GET, requestUri,
							headers, null);
					Response response = new RestClient().execute(request);

					if (response.mStatus == 200) {
						File file = new File(
								mContext.getExternalFilesDir("patterns") + "/"
										+ patternUuid, step.getUuid() + ".jpg");
						if (!file.getParentFile().exists()) {
							file.getParentFile().mkdirs();
						}
						FileOutputStream fos = new FileOutputStream(file);
						fos.write(response.mBody);
						fos.close();
						step.setImage(file.getPath());
						stepDBAdapter.replace(step);
					} else {
						throw new Exception("Не удалось получить файл. URL: "
								+ url);
					}
				} catch (Exception e) {
					e.printStackTrace();
					result = new Bundle();
					result.putBoolean(IServiceProvider.RESULT, false);
					result.putString(IServiceProvider.MESSAGE, e.getMessage());
					return result;
				}
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
	public Bundle getDocumentType(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.DocumentType);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(
				new ToirDatabaseContext(mContext));
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
			result = saveDocumentType(types);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем файл документации
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getDocumentationFile(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String fileUuids[] = bundle
				.getStringArray(ReferenceServiceProvider.Methods.GET_DOCUMENTATION_FILE_PARAMETER_UUID);
		EquipmentDocumentationDBAdapter documentationDBAdapter = new EquipmentDocumentationDBAdapter(
				new ToirDatabaseContext(mContext));
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(mContext));

		for (String fileUuid : fileUuids) {
			EquipmentDocumentation document = documentationDBAdapter
					.getItem(fileUuid);
			Equipment equipment = equipmentDBAdapter.getItem(document
					.getEquipment_uuid());

			url.setLength(0);
			url.append(mServerUrl).append("/api/Equipment/")
					.append(equipment.getUuid()).append("/Documents/")
					.append(document.getUuid()).append("/file");

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
									+ equipment.getUuid(), document.getPath());
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(response.mBody);
					fos.close();
					document.setPath(file.getPath());
					documentationDBAdapter.replace(document);
				} else {
					throw new Exception("Не удалось получить файл. URL: " + url);
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				result.putString(IServiceProvider.MESSAGE, e.getMessage());
				return result;
			}
		}

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		result.putStringArray(
				ReferenceServiceProvider.Methods.RESULT_GET_DOCUMENTATION_FILE_UUID,
				fileUuids);
		return result;
	}

	/**
	 * Получаем файл изображения оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getEquipmentFile(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String equipmentsUuids[] = bundle
				.getStringArray(ReferenceServiceProvider.Methods.GET_IMAGE_FILE_PARAMETER_UUID);

		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(mContext));

		for (String equipmentsUuid : equipmentsUuids) {
			Equipment equipment = equipmentDBAdapter.getItem(equipmentsUuid);

			File imgFile = new File(equipment.getImage());
			String fileName = imgFile.getName();
			String fileNameEncoded;
			String filePath = imgFile.getParent();

			String charset = "UTF-8";
			if (Charset.isSupported(charset)) {
				try {
					fileNameEncoded = URLEncoder.encode(fileName, charset)
							.replace("+", "%20");
				} catch (Exception e) {
					e.printStackTrace();
					fileNameEncoded = fileName;
				}
			} else {
				fileNameEncoded = fileName;
			}

			url.setLength(0);
			url.append(mServerUrl).append("/").append(filePath).append("/")
					.append(fileNameEncoded);

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
									+ equipment.getUuid(), fileName);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(response.mBody);
					fos.close();
					equipment.setImage(file.getPath());
					equipmentDBAdapter.replace(equipment);
				} else {
					throw new Exception("Не удалось получить файл. URL: " + url);
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				result.putString(IServiceProvider.MESSAGE, e.getMessage());
				return result;
			}
		}

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Получаем файл изображения результата измерения
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getMeasureValueFile(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String measureValueUuids[] = bundle
				.getStringArray(ReferenceServiceProvider.Methods.GET_IMAGE_FILE_PARAMETER_UUID);

		MeasureValueDBAdapter measureValueDBAdapter = new MeasureValueDBAdapter(
				new ToirDatabaseContext(mContext));

		for (String measureValueUuid : measureValueUuids) {
			MeasureValue measureValue = measureValueDBAdapter
					.getItem(measureValueUuid);

			File imgFile = new File(measureValue.getValue());
			String fileName = imgFile.getName();
			String fileNameEncoded;
			String filePath = imgFile.getParent();

			String charset = "UTF-8";
			if (Charset.isSupported(charset)) {
				try {
					fileNameEncoded = URLEncoder.encode(fileName, charset)
							.replace("+", "%20");
				} catch (Exception e) {
					e.printStackTrace();
					fileNameEncoded = fileName;
				}
			} else {
				fileNameEncoded = fileName;
			}

			url.setLength(0);
			url.append(mServerUrl).append("/").append(filePath).append("/")
					.append(fileNameEncoded);

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
					File file = new File(mContext.getExternalFilesDir(
							Environment.DIRECTORY_PICTURES).getAbsolutePath(),
							fileName);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(response.mBody);
					fos.close();
					measureValue.setValue(file.getPath());
					measureValueDBAdapter.replace(measureValue);
				} else {
					throw new Exception("Не удалось получить файл. URL: " + url);
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				result.putString(IServiceProvider.MESSAGE, e.getMessage());
				return result;
			}
		}

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Получаем статусы оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getEquipmentStatus(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.EquipmentStatus);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(
				new ToirDatabaseContext(mContext));
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
			result = saveEquipmentStatus(statuses);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем типы оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getEquipmentType(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.EquipmentType);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(mContext));
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

			result = saveEquipmentType(types);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем типы измерений
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getMeasureType(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.MeasureType);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(
				new ToirDatabaseContext(mContext));
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

			result = saveMeasureType(list);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;

		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем статусы операций
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getOperationStatus(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.OperationStatus);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(
				new ToirDatabaseContext(mContext));
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

			result = saveOperationStatus(list);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем типы операций
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getOperationType(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.OperationType);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(
				new ToirDatabaseContext(mContext));
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

			result = saveOperationType(operations);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем статусы нарядов
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getTaskStatus(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.TaskStatus);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(
				new ToirDatabaseContext(mContext));
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

			result = saveTaskStatus(list);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем оборудование
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getEquipment(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
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

				result = saveEquipment(equipment);
				boolean success = result.getBoolean(IServiceProvider.RESULT);
				if (!success) {
					db.endTransaction();
					return result;
				}
			} else {
				db.endTransaction();
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				result.putString(IServiceProvider.MESSAGE,
						"Ошибка получения данных справочника.");
				return result;
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Получаем типы критичности оборудования
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getCriticalType(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		StringBuilder url = new StringBuilder();
		String jsonString;
		Long lastChangedAt;

		// получаем урл справочника
		String referenceUrl = getReferenceURL(ReferenceName.CriticalType);
		if (referenceUrl == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Не известный справочник.");
			return result;
		}

		url.append(mServerUrl).append('/').append(referenceUrl);

		// получаем дату последней модификации содержимого таблицы
		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(mContext));
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

			result = saveCriticalType(types);
			boolean success = result.getBoolean(IServiceProvider.RESULT);
			if (success) {
				db.setTransactionSuccessful();
			}
			db.endTransaction();
			return result;
		} else {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка получения данных справочника.");
			return result;
		}
	}

	/**
	 * Получаем документацию
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getDocumentation(Bundle bundle) {

		Bundle result;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
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
				result = saveDocumentations(list, equipmentUuid);
				boolean success = result.getBoolean(IServiceProvider.RESULT);
				if (!success) {
					db.endTransaction();
					return result;
				}
			} else {
				db.endTransaction();
				result = new Bundle();
				result.putBoolean(IServiceProvider.RESULT, false);
				result.putString(IServiceProvider.MESSAGE,
						"Ошибка получения данных справочника.");
				return result;
			}
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
	}

	/**
	 * Получаем все справочники
	 * 
	 * @param bundle
	 * @return
	 */
	public Bundle getAll(Bundle bundle) {

		Bundle result;
		boolean success;

		if (!checkToken()) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE, "Нет связи с сервером.");
			return result;
		}

		// TODO определиться как всё-таки будут обновляться справочники
		// на каждом устройстве будет копия всех данных с сервера?
		// совершенно не нужно тащить все объекты оборудования на каждое
		// устройство.
		// обновлять будем только те данные которые есть на устройстве?
		// можно пропустить новые данные.
		result = getCriticalType(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		result = getDocumentType(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		result = getEquipmentStatus(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		result = getEquipmentType(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		result = getMeasureType(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		EquipmentOperationDBAdapter operationAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(mContext));
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
			result = getOperationResult(bundle);
			success = result.getBoolean(IServiceProvider.RESULT);
			if (!success) {
				return result;
			}
		}

		result = getOperationStatus(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		result = getOperationType(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		result = getTaskStatus(bundle);
		success = result.getBoolean(IServiceProvider.RESULT);
		if (!success) {
			return result;
		}

		EquipmentDBAdapter equipmentAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(mContext));
		ArrayList<Equipment> equipments = equipmentAdapter.getAllItems("", "");
		if (equipments != null) {
			Set<String> uuids = new HashSet<String>();
			for (Equipment equipment : equipments) {
				uuids.add(equipment.getUuid());
			}

			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_DOCUMENTATION_PARAMETER_UUID,
					uuids.toArray(new String[] {}));
			result = getDocumentation(bundle);
			success = result.getBoolean(IServiceProvider.RESULT);
			if (!success) {
				return result;
			}

			bundle.clear();
			bundle.putStringArray(
					ReferenceServiceProvider.Methods.GET_EQUIPMENT_PARAMETER_UUID,
					uuids.toArray(new String[] {}));
			result = getEquipment(bundle);
			success = result.getBoolean(IServiceProvider.RESULT);
			if (!success) {
				return result;
			}
		}

		result = new Bundle();
		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
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
				new ToirDatabaseContext(mContext));
		if (adapter0.replace(pattern.getLocal()) == -1) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		OperationPatternStepDBAdapter adapter1 = new OperationPatternStepDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!adapter1.saveItems(OperationPatternSrv
				.getOperationPatternSteps(pattern))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		OperationPatternStepResultDBAdapter adapter2 = new OperationPatternStepResultDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!adapter2.saveItems(OperationPatternSrv
				.getOperationPatternStepResults(pattern))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		MeasureTypeDBAdapter adapter3 = new MeasureTypeDBAdapter(
				new ToirDatabaseContext(mContext));
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
				new ToirDatabaseContext(mContext));
		if (!adapter0
				.saveItems(OperationResultSrv.getOperationResults(results))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			return result;
		}

		OperationTypeDBAdapter adapter1 = new OperationTypeDBAdapter(
				new ToirDatabaseContext(mContext));

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
	private Bundle saveDocumentType(ArrayList<DocumentationTypeSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют типы документации для сохранения.");
			return result;
		}

		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter
				.saveItems(DocumentationTypeSrv.getDocumentationTypes(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов документации.");
			return result;
		}
	}

	/**
	 * Сохраняем в базу статусы оборудования
	 * 
	 * @param array
	 * @return
	 */
	private Bundle saveEquipmentStatus(ArrayList<EquipmentStatusSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют статусы оборудования для сохранения.");
			return result;
		}

		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter.saveItems(EquipmentStatusSrv.getEquipmentStatuses(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении статусов оборудования.");
			return result;
		}
	}

	/**
	 * Сохраняем в базу типы оборудования
	 * 
	 * @param array
	 * @return
	 */
	private Bundle saveEquipmentType(ArrayList<EquipmentTypeSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют типы оборудования для сохранения.");
			return result;
		}

		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter.saveItems(EquipmentTypeSrv.getEquipmentTypes(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов оборудования.");
			return result;
		}
	}

	/**
	 * Сохраняем в базу типы измерений
	 * 
	 * @param array
	 * @return
	 */
	private Bundle saveMeasureType(ArrayList<MeasureTypeSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют типы измерений для сохранения.");
			return result;
		}

		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter.saveItems(MeasureTypeSrv.getMeasureTypes(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов измерений.");
			return result;
		}
	}

	/**
	 * Сохраняем в базу статусы нарядов
	 * 
	 * @param array
	 * @return
	 */
	private Bundle saveTaskStatus(ArrayList<TaskStatusSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют типы документации для сохранения.");
			return result;
		}

		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter.saveItems(TaskStatusSrv.getTaskStatuses(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов документации.");
			return result;
		}
	}

	/**
	 * Сохраняем в базу статусы операций
	 * 
	 * @param array
	 * @return
	 */
	private Bundle saveOperationStatus(ArrayList<OperationStatusSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют статусы операций для сохранения.");
			return result;
		}

		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter.saveItems(OperationStatusSrv.getOperationStatuses(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении статусов операций.");
			return result;
		}
	}

	/**
	 * Сохраняем в базу типы операций операций
	 * 
	 * @param array
	 * @return
	 */
	private Bundle saveOperationType(ArrayList<OperationTypeSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют типы операций для сохранения.");
			return result;
		}

		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter.saveItems(OperationTypeSrv.getOperationTypes(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов операций.");
			return result;
		}
	}

	/**
	 * Сохраняем в базу оборудование
	 * 
	 * @param element
	 * @return
	 */
	private Bundle saveEquipment(EquipmentSrv element) {

		Bundle result = new Bundle();

		if (element == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствует оборудование для сохранения.");
			return result;
		}

		EquipmentDBAdapter equipmentAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(mContext));

		if (equipmentAdapter.replace(element.getLocal()) == -1) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка сохранения оборудования.");
			return result;
		}

		EquipmentTypeDBAdapter equipmentTypeAdapter = new EquipmentTypeDBAdapter(
				new ToirDatabaseContext(mContext));
		if (equipmentTypeAdapter.replace(element.getEquipmentType().getLocal()) == -1) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка сохранения типа оборудования.");
			return result;
		}

		CriticalTypeDBAdapter criticalTypeAdapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(mContext));
		if (criticalTypeAdapter
				.replace(element.getCriticalityType().getLocal()) == -1) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка сохранения критичности оборудования.");
			return result;
		}

		EquipmentStatusDBAdapter equipmentStatusAdapter = new EquipmentStatusDBAdapter(
				new ToirDatabaseContext(mContext));
		if (equipmentStatusAdapter.replace(element.getEquipmentStatus()
				.getLocal()) == -1) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка сохранения статуса оборудования.");
			return result;
		}

		EquipmentDocumentationDBAdapter documentationAdapter = new EquipmentDocumentationDBAdapter(
				new ToirDatabaseContext(mContext));
		ArrayList<EquipmentSrv> elements = new ArrayList<EquipmentSrv>();
		elements.add(element);
		if (!documentationAdapter.saveItems(EquipmentSrv
				.getEquipmentDocumentations(elements))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка сохранения документации оборудования.");
			return result;
		}

		DocumentationTypeDBAdapter documentationTypeAdapter = new DocumentationTypeDBAdapter(
				new ToirDatabaseContext(mContext));
		if (!documentationTypeAdapter.saveItems(EquipmentSrv
				.getDocumentationTypes(elements))) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка сохранения типов документации оборудования.");
			return result;
		}

		result.putBoolean(IServiceProvider.RESULT, true);
		return result;
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
	private Bundle saveDocumentations(
			ArrayList<EquipmentDocumentationSrv> array, String equipmentUuid) {

		Bundle result;

		if (array == null) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствует документация для сохранения.");
			return result;
		}

		EquipmentDocumentationDBAdapter adapter = new EquipmentDocumentationDBAdapter(
				new ToirDatabaseContext(mContext));

		if (!adapter.saveItems(EquipmentDocumentationSrv
				.getEquipmentDocumentations(array, equipmentUuid))) {
			result = new Bundle();
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка сохранения документации.");
			return result;
		}

		result = saveDocumentType(EquipmentDocumentationSrv
				.getDocumentationTypesSrv(array));

		return result;
	}

	/**
	 * Сохраняем в базу типы критичности оборудования
	 * 
	 * @param array
	 * @return
	 */
	private Bundle saveCriticalType(ArrayList<CriticalTypeSrv> array) {

		Bundle result = new Bundle();

		if (array == null) {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Остсутствуют типы критичности для сохранения.");
			return result;
		}

		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new ToirDatabaseContext(mContext));

		if (adapter.saveItems(CriticalTypeSrv.getCriticalTypes(array))) {
			result.putBoolean(IServiceProvider.RESULT, true);
			return result;
		} else {
			result.putBoolean(IServiceProvider.RESULT, false);
			result.putString(IServiceProvider.MESSAGE,
					"Ошибка при сохранении типов критичности.");
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
