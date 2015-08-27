package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import ru.toir.mobile.db.tables.EquipmentOperation;

/**
 * @author olejek
 * <p>Класс для работы с оборудованием</p>
 *
 */
public class EquipmentOperationDBAdapter extends BaseDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation";
	
	public static final String FIELD_TASK_UUID_NAME = "task_uuid";
	public static final String FIELD_EQUIPMENT_UUID_NAME = "equipment_uuid";
	public static final String FIELD_OPERATION_TYPE_UUID_NAME = "operation_type_uuid";
	public static final String FIELD_OPERATION_PATTERN_UUID_NAME = "operation_pattern_uuid";
	public static final String FIELD_OPERATION_STATUS_UUID_NAME = "operation_status_uuid";
	public static final String FIELD_OPERATION_TIME_NAME = "operation_time";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID_NAME;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID_NAME;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT_NAME;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT_NAME;
		
		public static final String TASK_UUID = TABLE_NAME + '_' + FIELD_TASK_UUID_NAME;
		public static final String EQUIPMENT_UUID = TABLE_NAME + '_' + FIELD_EQUIPMENT_UUID_NAME;
		public static final String OPERATION_TYPE_UUID = TABLE_NAME + '_' + FIELD_OPERATION_TYPE_UUID_NAME;
		public static final String OPERATION_PATTERN_UUID = TABLE_NAME + '_' + FIELD_OPERATION_PATTERN_UUID_NAME;
		public static final String OPERATION_STATUS_UUID = TABLE_NAME + '_' + FIELD_OPERATION_STATUS_UUID_NAME;
		public static final String OPERATION_TIME = TABLE_NAME + '_' + FIELD_OPERATION_TIME_NAME;
		
	}
	
	private static Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID_NAME) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID_NAME) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT_NAME) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT_NAME) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TASK_UUID, getFullName(TABLE_NAME, FIELD_TASK_UUID_NAME) + " AS " + Projection.TASK_UUID);
		mProjection.put(Projection.EQUIPMENT_UUID, getFullName(TABLE_NAME, FIELD_EQUIPMENT_UUID_NAME) + " AS " + Projection.EQUIPMENT_UUID);
		mProjection.put(Projection.OPERATION_TYPE_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_TYPE_UUID_NAME) + " AS " + Projection.OPERATION_TYPE_UUID);
		mProjection.put(Projection.OPERATION_PATTERN_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_PATTERN_UUID_NAME) + " AS " + Projection.OPERATION_PATTERN_UUID);
		mProjection.put(Projection.OPERATION_STATUS_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_STATUS_UUID_NAME) + " AS " + Projection.OPERATION_STATUS_UUID);
		mProjection.put(Projection.OPERATION_TIME, getFullName(TABLE_NAME, FIELD_OPERATION_TIME_NAME) + " AS " + Projection.OPERATION_TIME);
	}
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_TASK_UUID_NAME,
			FIELD_EQUIPMENT_UUID_NAME,
			FIELD_OPERATION_TYPE_UUID_NAME,
			FIELD_OPERATION_PATTERN_UUID_NAME,
			FIELD_OPERATION_STATUS_UUID_NAME,
			FIELD_OPERATION_TIME_NAME};

	/**
	 * @param context
	 * @return EquipmentOpDBAdapter
	 */
	public EquipmentOperationDBAdapter(Context context){
		super(context, TABLE_NAME);
	}
	
	/**
	 * Возвращает список операций над оборудованием по наряду
	 * @param orderId
	 * @param operation_type
	 * @param critical_type
	 * @return
	 */
	public ArrayList<EquipmentOperation> getEquipsByOrderId(String orderId, String operation_type, int critical_type) {
		ArrayList<EquipmentOperation> arrayList = new ArrayList<EquipmentOperation>();
		Cursor cursor;

		// можем или отобрать все оборудование или только с нужными параметрами
		if (operation_type.equals("")) {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=?", new String[]{orderId}, null, null, null);
		} else {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=? AND " + FIELD_OPERATION_TYPE_UUID_NAME + "=?", new String[]{orderId,operation_type}, null, null, null);
		}

		if (cursor.moveToFirst()) {
			do	{
				 arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}

		return arrayList;
	}

	/**
	 * <p>Возвращает все записи из таблицы equipment_operation</p>
	 * @return Cursor
	 */
	public Cursor getAllOpEquipment() {
		return mDb.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_UUID_NAME, FIELD_EQUIPMENT_UUID_NAME, FIELD_OPERATION_TYPE_UUID_NAME, FIELD_OPERATION_PATTERN_UUID_NAME, FIELD_OPERATION_STATUS_UUID_NAME, FIELD_OPERATION_TIME_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы equipment_operation</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getOpEquipment(long uuid) {
		return mDb.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_UUID_NAME, FIELD_EQUIPMENT_UUID_NAME, FIELD_OPERATION_TYPE_UUID_NAME, FIELD_OPERATION_PATTERN_UUID_NAME, FIELD_OPERATION_STATUS_UUID_NAME, FIELD_OPERATION_TIME_NAME}, FIELD_UUID_NAME + "=?", new String[]{String.valueOf(uuid)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу equipments_operation</p>
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertOpEquipment(String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid, int operation_time){
		ContentValues values = new ContentValues();
		String uuid = UUID.randomUUID().toString();
		values.put(EquipmentOperationDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_TASK_UUID_NAME, task_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID_NAME, equipment_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID_NAME, operation_pattern_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TIME_NAME, operation_time);
		return mDb.insert(EquipmentOperationDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return int количество удалённых записей
	 */
	public int deleteOpEquipment(){
		return mDb.delete(TABLE_NAME, null, null);
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteOpEquipment(String uuid){
		return mDb.delete(TABLE_NAME, FIELD_UUID_NAME + "=?", new String[]{String.valueOf(uuid)});
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице equipment_operation</p>
	 * @param uuid
	 * @param task_uuid
	 * @param equipment_uuid
	 * @param operation_type_uuid
	 * @param operation_pattern_uuid
	 * @param operation_status_uuid
	 * @return
	 */
	public long replace(String uuid, String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid, String operation_status_uuid, int operation_time) {
		ContentValues values = new ContentValues();
		values.put(EquipmentOperationDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_TASK_UUID_NAME, task_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID_NAME, equipment_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID_NAME, operation_pattern_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_STATUS_UUID_NAME, operation_status_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TIME_NAME, operation_time);
		return mDb.replace(EquipmentOperationDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице equipment_operation</p>
	 * @param operation
	 * @return
	 */
	public long replace(EquipmentOperation operation) {
		return replace(operation.getUuid(), operation.getTask_uuid(), operation.getEquipment_uuid(), operation.getOperation_type_uuid(), operation.getOperation_pattern_uuid(), operation.getOperation_status_uuid(), operation.getOperation_time());
	}
	
	/**
	 * Возвращает операцию над оборудованием по uuid
	 * @param uuid
	 * @return если операции нет, возвращает null
	 */
	public EquipmentOperation getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		} else {
			return null;
		}
	}
	
	/**
	 * Возвращает операцию по наряду и оборудованию
	 * @param task_uuid
	 * @param equipment_uuid
	 * @return
	 */
	public ArrayList<EquipmentOperation> getItemsByTaskAndEquipment(String task_uuid, String equipment_uuid) {
		ArrayList<EquipmentOperation> arrayList = null;
		Cursor cursor;
		if (task_uuid.equals(""))
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_UUID_NAME + "=?", new String[]{equipment_uuid}, null, null, "_id DESC");
		else
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=? AND " + FIELD_EQUIPMENT_UUID_NAME + "=?", new String[]{task_uuid, equipment_uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			arrayList = new ArrayList<EquipmentOperation>();
			do	{
				 arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}

	/**
	 * Возвращает объект операции над оборудованием
	 * @param cursor
	 * @return
	 */
	public EquipmentOperation getItem(Cursor cursor) {
		EquipmentOperation equipmentOperation = new EquipmentOperation();
		equipmentOperation.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		equipmentOperation.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		equipmentOperation.setTask_uuid(cursor.getString(cursor.getColumnIndex(FIELD_TASK_UUID_NAME)));
		equipmentOperation.setEquipment_uuid(cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_UUID_NAME)));
		equipmentOperation.setOperation_type_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID_NAME)));
		equipmentOperation.setOperation_pattern_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_UUID_NAME)));
		equipmentOperation.setOperation_status_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_STATUS_UUID_NAME)));
		equipmentOperation.setOperation_time(cursor.getInt(cursor.getColumnIndex(FIELD_OPERATION_TIME_NAME)));
		return equipmentOperation;
	}
	
	/**
	 * Возвращает список операций над оборудованием по наряду
	 * @param taskUuid
	 * @return если нет наряда, возвращает null
	 */
	public ArrayList<EquipmentOperation> getItems(String taskUuid) {
		ArrayList<EquipmentOperation> arrayList = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=?", new String[]{taskUuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			arrayList = new ArrayList<EquipmentOperation>();
			do	{
				 arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}

	/**
	 * Устанавливаем статус операции
	 * @param uuid
	 * @param status
	 */
	public void setOperationStatus(String uuid, String status) {
		EquipmentOperation operation = getItem(uuid);
		operation.setOperation_status_uuid(status);
		replace(operation);
	}
	
	/**
	 * Возвращает курсор для ListView списка операций в наряде
	 * @param taskUuid
	 * @param operationTypeUuid
	 * @param criticalTypeUuid
	 * @return
	 */
	public Cursor getOperationWithInfo(String taskUuid, String operationTypeUuid, String criticalTypeUuid) {
		Cursor cursor = null;
		String query = "select eo._id, eo.uuid as 'operation_uuid', eo.task_uuid, eo.equipment_uuid, ot.title as 'operation_title', e.title as 'equipment_title', ct.type as 'critical_type', os.title as 'operation_status_title' from equipment_operation as eo left join operation_type as ot on eo.operation_type_uuid=ot.uuid left join equipment as e on eo.equipment_uuid=e.uuid left join critical_type as ct on e.critical_type_uuid=ct.uuid left join operation_status as os on eo.operation_status_uuid=os.uuid where eo.task_uuid='" + taskUuid + "'";
		
		if (operationTypeUuid != null) {
			query += " and " + FIELD_OPERATION_TYPE_UUID_NAME + "='" + operationTypeUuid + "'";
		}
		
		if (criticalTypeUuid != null) {
			query += " and " + EquipmentDBAdapter.FIELD_CRITICAL_TYPE_UUID_NAME + "='" + criticalTypeUuid + "'";
		}
		cursor = mDb.rawQuery(query, null);		
		return cursor;
	}
	
	/**
	 * тестовый метод для отработки работы с QueryBuilder
	 * @param taskUuid
	 * @param operationTypeUuid
	 * @param criticalTypeUuid
	 * @return
	 */
	public Cursor getOperationWithInfoQB(String taskUuid, String operationTypeUuid, String criticalTypeUuid) {
		/*
		 * equipment as e on eo.equipment_uuid=e.uuid
		 * critical_type as ct on e.critical_type_uuid=ct.uuid
		 */
		Cursor cursor;
		String sortOrder = null;
		List<String> paramArray = new ArrayList<String>();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Map<String, String> projection = new HashMap<String, String>();
		
		projection.putAll(mProjection);
		
		queryBuilder.appendWhere(FIELD_TASK_UUID_NAME
				+ "=?");
		paramArray.add(taskUuid);

		String table;
		StringBuilder tables = new StringBuilder();
		// операции с типами операций
		table = getLeftJoinTables(TABLE_NAME,
				OperationTypeDBAdapter.TABLE_NAME,
				FIELD_OPERATION_TYPE_UUID_NAME,
				OperationTypeDBAdapter.FIELD_UUID_NAME, true);
		tables.append(table);
		projection.putAll(OperationTypeDBAdapter.getProjection());
		
		// оборудование 
		table = getLeftJoinTables(TABLE_NAME,
				EquipmentDBAdapter.TABLE_NAME, FIELD_EQUIPMENT_UUID_NAME,
				EquipmentDBAdapter.FIELD_UUID_NAME, false);
		tables.append(' ').append(table);
		projection.putAll(EquipmentDBAdapter.getProjection());

		// типы критичности оборудования
		table = getLeftJoinTables(EquipmentDBAdapter.TABLE_NAME,
				CriticalTypeDBAdapter.TABLE_NAME,
				EquipmentDBAdapter.FIELD_CRITICAL_TYPE_UUID_NAME,
				CriticalTypeDBAdapter.FIELD_UUID_NAME, false);
		tables.append(' ').append(table);
		projection.putAll(CriticalTypeDBAdapter.getProjection());
		
		// статусы операций
		table = getLeftJoinTables(TABLE_NAME,
				OperationStatusDBAdapter.TABLE_NAME, FIELD_OPERATION_STATUS_UUID_NAME,
				OperationStatusDBAdapter.FIELD_UUID_NAME, false);
		tables.append(' ').append(table);
		projection.putAll(OperationStatusDBAdapter.getProjection());
		
		queryBuilder.setTables(tables.toString());
		queryBuilder.setProjectionMap(projection);
		
		if (operationTypeUuid != null) {
			queryBuilder.appendWhere(FIELD_OPERATION_TYPE_UUID_NAME
					+ "=?");
			paramArray.add(operationTypeUuid);
		}
		
		if (criticalTypeUuid != null) {
			sortOrder = criticalTypeUuid;
		}
		
		String[] pa = new String[paramArray.size()];
		pa = paramArray.toArray(pa);
		cursor = queryBuilder.query(mDb, null, null, pa, null, null,
				sortOrder);		
		return cursor;
	}

	/**
	 * Возвращает курсор для ListView списка всех операций (временная функция - тестовая) 
	 * @return
	 */
	public Cursor getOperationWithInfo() {
		Cursor cursor = null;
		String query = "select eo._id, eo.uuid as 'operation_uuid', eo.task_uuid, eo.equipment_uuid, ot.title as 'operation_title', e.title as 'equipment_title', ct.type as 'critical_type', os.title as 'operation_status_title' from equipment_operation as eo left join operation_type as ot on eo.operation_type_uuid=ot.uuid left join equipment as e on eo.equipment_uuid=e.uuid left join critical_type as ct on e.critical_type_uuid=ct.uuid left join operation_status as os on eo.operation_status_uuid=os.uuid";		
		cursor = mDb.rawQuery(query, null);		
		return cursor;
	}
	
}
