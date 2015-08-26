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
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.CriticalTypeDBAdapter;
import ru.toir.mobile.db.adapters.DocumentationTypeDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentDocumentationDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentStatusDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentTypeDBAdapter;
import ru.toir.mobile.db.adapters.MeasureTypeDBAdapter;
import ru.toir.mobile.db.adapters.OperationResultDBAdapter;
import ru.toir.mobile.db.adapters.OperationStatusDBAdapter;
import ru.toir.mobile.db.adapters.OperationTypeDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import ru.toir.mobile.db.tables.DocumentationType;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.TaskStatus;
import ru.toir.mobile.rest.RestClient.Method;
import ru.toir.mobile.serverapi.CriticalityType;
import ru.toir.mobile.serverapi.Document;
import ru.toir.mobile.serverapi.DocumentType;
import ru.toir.mobile.serverapi.Equipment;
import ru.toir.mobile.serverapi.EquipmentStatus;
import ru.toir.mobile.serverapi.EquipmentType;
import ru.toir.mobile.serverapi.MeasureType;
import ru.toir.mobile.serverapi.OperationResult;
import ru.toir.mobile.serverapi.OperationType;
import ru.toir.mobile.serverapi.OrderStatus;
import ru.toir.mobile.serverapi.Status;
import ru.toir.mobile.utils.DataUtils;
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
	private Context mContext;
	public static class ReferenceNames {
		// TODO отдельно нет справочника EquipmentDocumentation
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
	
	// TODO реализовать получение справочника(ов) в качестве параметра передаём список строк с именами справочников которые хотим получить
	// решение: ни чего сверх имени справочника не передавать, прямо в процессоре будем получать крайнии даты модификации и отправлять их

	// TODO нужно подумать о том как наполнить пустые таблицы или тотально обновить справочники 

	/**
	 * Получаем только "новые" данные из справочника(ов)
	 * @param bundle
	 * @return
	 */
	public boolean getReference(Bundle bundle) {

		URI requestUri = null;
		ArrayList<String> referenceNames = bundle
				.getStringArrayList(ReferenceServiceProvider.Methods.GET_REFERENCE_PARAMETER_NAME);
		long lastChangedAt;
		StringBuilder postData = new StringBuilder();

		for (String name: referenceNames) {
			try {
				// получаем дату последней модификации содержимого таблицы
				lastChangedAt = getLastChanged(name);
				postData.setLength(0);
				if (lastChangedAt > -1) {
					// формируем параметры для запроса
					postData.append('?').append("ChangedAfter=").append(DataUtils.getDate(lastChangedAt, "yyyy-MM-dd'T'hh:mm:ss"));
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
						saveCriticalType(gson.fromJson(jsonString, CriticalityType[].class));
					} else if(name.equals(ReferenceNames.DocumentTypeName)) {
						saveDocumentType(gson.fromJson(jsonString, DocumentType[].class));
					} else if(name.equals(ReferenceNames.EquipmentName)) {
						saveEquipment(gson.fromJson(jsonString, Equipment[].class));
					} else if(name.equals(ReferenceNames.EquipmentStatusName)) {
						saveEquipmentStatus(gson.fromJson(jsonString, EquipmentStatus[].class));
					} else if(name.equals(ReferenceNames.EquipmentTypeName)) {
						saveEquipmentType(gson.fromJson(jsonString, EquipmentType[].class));
					} else if(name.equals(ReferenceNames.MeasureTypeName)) {
						saveMeasureType(gson.fromJson(jsonString, MeasureType[].class));
					} else if(name.equals(ReferenceNames.OperationResultName)) {
						saveOperationResult(gson.fromJson(jsonString, OperationResult[].class));
					} else if(name.equals(ReferenceNames.OperationStatusName)) {
						saveOperationStatus(gson.fromJson(jsonString, Status[].class));
					} else if(name.equals(ReferenceNames.OperationTypeName)) {
						saveOperationType(gson.fromJson(jsonString, OperationType[].class));
					} else if(name.equals(ReferenceNames.TaskStatusName)) {
						saveTaskStatus(gson.fromJson(jsonString, OrderStatus[].class));
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
		long changed = -1;
		if (referenceName.equals(ReferenceNames.CriticalTypeName)) {
			CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.DocumentTypeName)) {
			DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
		} else if(referenceName.equals(ReferenceNames.EquipmentName)) {
			EquipmentDBAdapter adapter = new EquipmentDBAdapter(new TOiRDatabaseContext(mContext));
			changed = adapter.getLastChangedAt();
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
	
	private void saveCriticalType(CriticalityType[] array) {
		
		if (array == null) {
			return;
		}

		CriticalTypeDBAdapter adapter = new CriticalTypeDBAdapter(
				new TOiRDatabaseContext(mContext));
		ArrayList<CriticalType> list = new ArrayList<CriticalType>();
		
		for (CriticalityType element : array) {
			CriticalType item = new CriticalType();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setType(element.getValue());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}
		
		adapter.saveItems(list);
	}
	
	private void saveDocumentType(DocumentType[] array) {

		if (array == null) {
			return;
		}
		
		DocumentationTypeDBAdapter adapter = new DocumentationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<DocumentationType> list = new ArrayList<DocumentationType>();
		
		for(DocumentType element : array) {
			DocumentationType item = new DocumentationType();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveDocuments(List<Document> array, String equipmentUuid) {

		if (array == null) {
			return;
		}
		
		EquipmentDocumentationDBAdapter adapter = new EquipmentDocumentationDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<EquipmentDocumentation> list = new ArrayList<EquipmentDocumentation>();
		
		for(Document element : array) {
			EquipmentDocumentation item = new EquipmentDocumentation();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setEquipment_uuid(equipmentUuid);
			item.setDocumentation_type_uuid(element.getDocumentType().getId());
			saveDocumentType(new DocumentType[] { element.getDocumentType() });
			item.setTitle(element.getTitle());
			item.setPath(element.getPath());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveEquipmentStatus(EquipmentStatus[] array) {

		if (array == null) {
			return;
		}
		
		EquipmentStatusDBAdapter adapter = new EquipmentStatusDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<ru.toir.mobile.db.tables.EquipmentStatus> list = new ArrayList<ru.toir.mobile.db.tables.EquipmentStatus>();
		
		for(EquipmentStatus element : array) {
			ru.toir.mobile.db.tables.EquipmentStatus item = new ru.toir.mobile.db.tables.EquipmentStatus();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			item.setType(element.getType());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveEquipmentType(EquipmentType[] array) {

		if (array == null) {
			return;
		}
		
		EquipmentTypeDBAdapter adapter = new EquipmentTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<ru.toir.mobile.db.tables.EquipmentType> list = new ArrayList<ru.toir.mobile.db.tables.EquipmentType>();
		
		for(EquipmentType element : array) {
			ru.toir.mobile.db.tables.EquipmentType item = new ru.toir.mobile.db.tables.EquipmentType();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveMeasureType(MeasureType[] array) {

		if (array == null) {
			return;
		}
		
		MeasureTypeDBAdapter adapter = new MeasureTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<ru.toir.mobile.db.tables.MeasureType> list = new ArrayList<ru.toir.mobile.db.tables.MeasureType>();
		
		for(MeasureType element : array) {
			ru.toir.mobile.db.tables.MeasureType item = new ru.toir.mobile.db.tables.MeasureType();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveOperationResult(OperationResult[] array) {

		if (array == null) {
			return;
		}
		
		OperationResultDBAdapter adapter = new OperationResultDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<ru.toir.mobile.db.tables.OperationResult> list = new ArrayList<ru.toir.mobile.db.tables.OperationResult>();
		
		for(OperationResult element : array) {
			ru.toir.mobile.db.tables.OperationResult item = new ru.toir.mobile.db.tables.OperationResult();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			item.setOperation_type_uuid(element.getOperationType().getId());
			saveOperationType(new OperationType[] { element.getOperationType() });
			// TODO в ответе нет дат создания и модификации
			item.setCreatedAt(0); //element.getCreatedAt().getTime());
			item.setChangedAt(0); //element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveOperationType(OperationType[] array) {

		if (array == null) {
			return;
		}
		
		OperationTypeDBAdapter adapter = new OperationTypeDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<ru.toir.mobile.db.tables.OperationType> list = new ArrayList<ru.toir.mobile.db.tables.OperationType>();
		
		for(OperationType element : array) {
			ru.toir.mobile.db.tables.OperationType item = new ru.toir.mobile.db.tables.OperationType();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			// TODO в ответе нет дат создания и модификации
			item.setCreatedAt(0); //element.getCreatedAt().getTime());
			item.setChangedAt(0); //element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}

	private void saveTaskStatus(OrderStatus[] array) {

		if (array == null) {
			return;
		}
		
		TaskStatusDBAdapter adapter = new TaskStatusDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<TaskStatus> list = new ArrayList<TaskStatus>();
		
		for(OrderStatus element : array) {
			TaskStatus item = new TaskStatus();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveEquipment(Equipment[] array) {

		if (array == null) {
			return;
		}
		
		EquipmentDBAdapter adapter = new EquipmentDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<ru.toir.mobile.db.tables.Equipment> list = new ArrayList<ru.toir.mobile.db.tables.Equipment>();
		
		for(Equipment element : array) {
			ru.toir.mobile.db.tables.Equipment item = new ru.toir.mobile.db.tables.Equipment();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getName());
			item.setEquipment_type_uuid(element.getEquipmentType().getId());
			saveEquipmentType(new EquipmentType[] { element.getEquipmentType() });
			item.setCritical_type_uuid(element.getCriticalityType().getId());
			saveCriticalType(new CriticalityType[]{ element.getCriticalityType() });
			item.setStart_date(element.getStartupDate().getTime());
			item.setLatitude(element.getGeoCoordinates().getLatitude());
			item.setLongitude(element.getGeoCoordinates().getLongitude());
			item.setTag_id(element.getTag());
			// TODO когда на сервере появится - добавить
			item.setImg("");
			item.setEquipmentStatus_uuid(element.getEquipmentStatus().getId());
			saveEquipmentStatus(new EquipmentStatus[] { element.getEquipmentStatus() });
			// TODO когда на сервере появится - добавить
			item.setInventoryNumber("");
			// TODO когда на сервере появится - добавить
			item.setLocation("");
			item.setCreatedAt(element.getCreatedAt().getTime());
			item.setChangedAt(element.getChangedAt().getTime());
			
			saveDocuments(element.getDocuments(), element.getId());
			
			list.add(item);
		}
		adapter.saveItems(list);
	}
	
	private void saveOperationStatus(Status[] array) {
	
		if (array == null) {
			return;
		}
		
		OperationStatusDBAdapter adapter = new OperationStatusDBAdapter(new TOiRDatabaseContext(mContext));
		ArrayList<OperationStatus> list = new ArrayList<OperationStatus>();
		
		for(Status element : array) {
			OperationStatus item = new OperationStatus();
			item.set_id(0);
			item.setUuid(element.getId());
			item.setTitle(element.getTitle());
			// TODO в ответе нет дат создания и модификации
			item.setCreatedAt(0); //element.getCreatedAt().getTime());
			item.setChangedAt(0); //element.getChangedAt().getTime());
			list.add(item);
		}
		adapter.saveItems(list);
	}

}
