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
import ru.toir.mobile.db.adapters.BaseDBAdapter;
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
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.Equipment;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.EquipmentStatus;
import ru.toir.mobile.db.tables.EquipmentType;
import ru.toir.mobile.db.tables.MeasureType;
import ru.toir.mobile.db.tables.OperationPattern;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.db.tables.OperationPatternStepResult;
import ru.toir.mobile.db.tables.OperationResult;
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
import ru.toir.mobile.serverapi.OperationPatternStepResultSrv;
import ru.toir.mobile.serverapi.OperationStatusSrv;
import ru.toir.mobile.serverapi.OperationPatternStepSrv;
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

	private static final String GET_REFERENCE_URL = "/api/references";
	private String mServerUrl;
	private Context mContext;
	public static class ReferenceNames {
		public static String CriticalTypeName = "CriticalityType";
		public static String DocumentTypeName = "DocumentType";
		// TODO нужен отдельный метод для получения оборудования/документации по оборудованию(api/equipment)
		//public static String EquipmentName = "Equipment";
		public static String EquipmentStatusName = "EquipmentStatus";
		public static String EquipmentTypeName = "EquipmentType";
		public static String MeasureTypeName = "MeasureType";
		public static String OperationResultName = "OperationResult";
		public static String OperationStatusName = "OperationStatus";
		public static String OperationTypeName = "OperationType";
		public static String TaskStatusName = "TaskStatus";
	}

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
	 * Получаем шаблон выполнения операции с шагами и вариантами выполнения
	 * шагов
	 * 
	 * @param bundle
	 * @return
	 */
	public boolean getOperationPattern(Bundle bundle) {
		final String OPERATION_PATTERN_URL = "/api/operationpatterns/";
		URI requestUri = null;
		ArrayList<String> patternUuids = bundle
				.getStringArrayList(ReferenceServiceProvider.Methods.GET_OPERATION_PATTERN_PARAMETER_UUID);

		for (String uuid: patternUuids) {
			try {
				requestUri = new URI(mServerUrl + OPERATION_PATTERN_URL + uuid);
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
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();

					// разбираем и сохраняем полученные данные
					SQLiteDatabase db = DatabaseHelper.getInstance(mContext).getWritableDatabase();
					db.beginTransaction();
					savePattern(gson.fromJson(jsonString, OperationPatternSrv.class));
					db.setTransactionSuccessful();
					db.endTransaction();
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Получаем только "новые" данные из справочника(ов)
	 * Если в таблице не окажется данных вообще, будет загружен весь справочник.
	 * @param bundle
	 * @return
	 */
	public boolean getReference(Bundle bundle) {

		URI requestUri = null;
		ArrayList<String> referenceNames = bundle
				.getStringArrayList(ReferenceServiceProvider.Methods.GET_REFERENCE_PARAMETER_NAME);
		Long lastChangedAt;
		StringBuilder postData = new StringBuilder();

		for (String name: referenceNames) {
			try {
				// получаем дату последней модификации содержимого таблицы
				lastChangedAt = getLastChanged(name);
				postData.setLength(0);
				if (lastChangedAt != null) {
					// TODO нужно изменить условие на сервере с >= на >
					// внятного ответа не получено, оставляем костыль в виде +1 секунды
					// это черевато тем, что если на сервере при создании записи дата изменения
					// не будет равна дате создания, новые данные не получим, до тех пор пока
					// запись не будет изменена
					postData.append('?').append("ChangedAfter=").append(DataUtils.getDate(lastChangedAt + 1000, "yyyy-MM-dd'T'HH:mm:ss"));
				}
				
				requestUri = new URI(mServerUrl + GET_REFERENCE_URL + "/" + name + postData.toString());
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
					Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'hh:mm:ss").create();
					if (name.equals(ReferenceNames.CriticalTypeName)) {
						CriticalTypeSrv.saveAll(gson.fromJson(jsonString,
								CriticalTypeSrv[].class), mContext);
					} else if(name.equals(ReferenceNames.DocumentTypeName)) {
						saveDocumentType(gson.fromJson(jsonString, DocumentationTypeSrv[].class));
					/*
					} else if(name.equals(ReferenceNames.EquipmentName)) {
						saveEquipment(gson.fromJson(jsonString, Equipment[].class));
					*/
					} else if(name.equals(ReferenceNames.EquipmentStatusName)) {
						saveEquipmentStatus(gson.fromJson(jsonString, EquipmentStatusSrv[].class));
					} else if(name.equals(ReferenceNames.EquipmentTypeName)) {
						saveEquipmentType(gson.fromJson(jsonString, EquipmentTypeSrv[].class));
					} else if(name.equals(ReferenceNames.MeasureTypeName)) {
						saveMeasureType(gson.fromJson(jsonString, MeasureTypeSrv[].class));
					} else if(name.equals(ReferenceNames.OperationResultName)) {
						saveOperationResult(gson.fromJson(jsonString, OperationResultSrv[].class));
					} else if(name.equals(ReferenceNames.OperationStatusName)) {
						saveOperationStatus(gson.fromJson(jsonString, OperationStatusSrv[].class));
					} else if(name.equals(ReferenceNames.OperationTypeName)) {
						saveOperationType(gson.fromJson(jsonString, OperationTypeSrv[].class));
					} else if(name.equals(ReferenceNames.TaskStatusName)) {
						saveTaskStatus(gson.fromJson(jsonString, TaskStatusSrv[].class));
					}
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	private long getLastChanged(String referenceName) {
		Long changed = null;
		if (referenceName.equals(ReferenceNames.CriticalTypeName)) {
			CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.DocumentTypeName)) {
			DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		/*
		} else if(referenceName.equals(ReferenceNames.EquipmentName)) {
			EquipmentDBAdapter adapter = new EquipmentDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		*/
		} else if(referenceName.equals(ReferenceNames.EquipmentStatusName)) {
			EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.EquipmentTypeName)) {
			EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.MeasureTypeName)) {
			MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.OperationResultName)) {
			OperationResultDBAdapter adapter = new OperationResultDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.OperationStatusName)) {
			OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.OperationTypeName)) {
			OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.TaskStatusName)) {
			TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		}
		return changed;
	}

	/**
	 * Сохраняем в базу информацию по шаблону операции и связанные с ним данные.
	 * 
	 * @param pattern
	 */
	private void savePattern(OperationPatternSrv pattern) {

		OperationPatternDBAdapter adapter0 = new OperationPatternDBAdapter(new TOiRDatabaseContext(mContext));
		adapter0.replace(pattern.getLocal());

		OperationPatternStepDBAdapter adapter1 = new OperationPatternStepDBAdapter(new TOiRDatabaseContext(mContext));
		adapter1.saveItems(ParseHelper.getOperationPatternSteps(pattern));

		OperationPatternStepResultDBAdapter adapter2 = new OperationPatternStepResultDBAdapter(new TOiRDatabaseContext(mContext));
		adapter2.saveItems(ParseHelper.getOperationPatternStepResults(pattern));

		MeasureTypeDBAdapter adapter3 = new MeasureTypeDBAdapter(new TOiRDatabaseContext(mContext));
		adapter3.saveItems(ParseHelper.getMeasureTypes(pattern.getSteps()));
	}

	private void saveDocumentType(DocumentationTypeSrv[] array) {

		if (array == null) {
			return;
		}
		
		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<DocumentationType> list = new ArrayList<DocumentationType>();
		
		for(DocumentationTypeSrv element : array) {
			DocumentationType item = element.getLocal();
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveDocuments(List<EquipmentDocumentationSrv> array, String equipmentUuid) {

		if (array == null) {
			return;
		}
		
		EquipmentDocumentationDBAdapter adapter = new EquipmentDocumentationDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentDocumentation> list = new ArrayList<EquipmentDocumentation>();
		
		for(EquipmentDocumentationSrv element : array) {
			EquipmentDocumentation item = element.getLocal(equipmentUuid);
			saveDocumentType(new DocumentationTypeSrv[] { element.getDocumentType() });
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveEquipmentStatus(EquipmentStatusSrv[] array) {

		if (array == null) {
			return;
		}
		
		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentStatus> list = new ArrayList<EquipmentStatus>();
		
		for(EquipmentStatusSrv element : array) {
			EquipmentStatus item = element.getLocal();
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveEquipmentType(EquipmentTypeSrv[] array) {

		if (array == null) {
			return;
		}
		
		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentType> list = new ArrayList<EquipmentType>();
		
		for(EquipmentTypeSrv element : array) {
			EquipmentType item = element.getLocal();
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveMeasureType(MeasureTypeSrv[] array) {

		if (array == null) {
			return;
		}
		
		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<MeasureType> list = new ArrayList<MeasureType>();
		
		for(MeasureTypeSrv element : array) {
			MeasureType item = element.getLocal();
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveOperationResult(OperationResultSrv[] array) {

		if (array == null) {
			return;
		}
		
		OperationResultDBAdapter adapter = new OperationResultDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<OperationResult> list = new ArrayList<OperationResult>();
		
		for(OperationResultSrv element : array) {
			OperationResult item = element.getLocal();
			saveOperationType(new OperationTypeSrv[] { element.getOperationType() });
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveOperationType(OperationTypeSrv[] array) {

		if (array == null) {
			return;
		}
		
		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<OperationType> list = new ArrayList<OperationType>();
		
		for(OperationTypeSrv element : array) {
			OperationType item = element.getLocal();
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveTaskStatus(TaskStatusSrv[] array) {

		if (array == null) {
			return;
		}
		
		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<TaskStatus> list = new ArrayList<TaskStatus>();
		
		for(TaskStatusSrv element : array) {
			TaskStatus item = element.getLocal();
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveEquipment(EquipmentSrv[] array) {

		if (array == null) {
			return;
		}
		
		EquipmentDBAdapter adapter = new EquipmentDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<Equipment> list = new ArrayList<Equipment>();
		
		for(EquipmentSrv element : array) {
			Equipment item = element.getLocal();
			saveEquipmentType(new EquipmentTypeSrv[] { element.getEquipmentType() });
			// TODO разобраться / переписать !!!!
			CriticalTypeSrv.saveAll(new CriticalTypeSrv[]{ element.getCriticalityType() }, mContext);
			saveEquipmentStatus(new EquipmentStatusSrv[] { element.getEquipmentStatus() });
			saveDocuments(element.getDocuments(), element.getId());
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveOperationStatus(OperationStatusSrv[] array) {
	
		if (array == null) {
			return;
		}
		
		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<OperationStatus> list = new ArrayList<OperationStatus>();
		
		for(OperationStatusSrv element : array) {
			OperationStatus item = element.getLocal();
			list.add(item);
		}
		adapter.saveItems(list);
	}

}
