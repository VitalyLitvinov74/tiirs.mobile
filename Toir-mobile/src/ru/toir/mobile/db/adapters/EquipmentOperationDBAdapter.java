package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.Calendar;
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
	
	public static final String FIELD_TASK_UUID = "task_uuid";
	public static final String FIELD_EQUIPMENT_UUID = "equipment_uuid";
	public static final String FIELD_OPERATION_TYPE_UUID = "operation_type_uuid";
	public static final String FIELD_OPERATION_PATTERN_UUID = "operation_pattern_uuid";
	public static final String FIELD_OPERATION_STATUS_UUID = "operation_status_uuid";
	public static final String FIELD_OPERATION_TIME = "operation_time";
	public static final String FIELD_ATTEMPT_SEND_DATE = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT = "attempt_count";
	public static final String FIELD_UPDATED = "updated";

	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String TASK_UUID = TABLE_NAME + '_' + FIELD_TASK_UUID;
		public static final String EQUIPMENT_UUID = TABLE_NAME + '_' + FIELD_EQUIPMENT_UUID;
		public static final String OPERATION_TYPE_UUID = TABLE_NAME + '_' + FIELD_OPERATION_TYPE_UUID;
		public static final String OPERATION_PATTERN_UUID = TABLE_NAME + '_' + FIELD_OPERATION_PATTERN_UUID;
		public static final String OPERATION_STATUS_UUID = TABLE_NAME + '_' + FIELD_OPERATION_STATUS_UUID;
		public static final String OPERATION_TIME = TABLE_NAME + '_' + FIELD_OPERATION_TIME;
		public static final String ATTEMPT_SEND_DATE = TABLE_NAME + '_' + FIELD_ATTEMPT_SEND_DATE;
		public static final String ATTEMPT_COUNT = TABLE_NAME + '_' + FIELD_ATTEMPT_COUNT;
		public static final String UPDATED = TABLE_NAME + '_' + FIELD_UPDATED;
		
	}
	
	private static Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TASK_UUID, getFullName(TABLE_NAME, FIELD_TASK_UUID) + " AS " + Projection.TASK_UUID);
		mProjection.put(Projection.EQUIPMENT_UUID, getFullName(TABLE_NAME, FIELD_EQUIPMENT_UUID) + " AS " + Projection.EQUIPMENT_UUID);
		mProjection.put(Projection.OPERATION_TYPE_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_TYPE_UUID) + " AS " + Projection.OPERATION_TYPE_UUID);
		mProjection.put(Projection.OPERATION_PATTERN_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_PATTERN_UUID) + " AS " + Projection.OPERATION_PATTERN_UUID);
		mProjection.put(Projection.OPERATION_STATUS_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_STATUS_UUID) + " AS " + Projection.OPERATION_STATUS_UUID);
		mProjection.put(Projection.OPERATION_TIME, getFullName(TABLE_NAME, FIELD_OPERATION_TIME) + " AS " + Projection.OPERATION_TIME);
		mProjection.put(Projection.ATTEMPT_SEND_DATE, getFullName(TABLE_NAME, FIELD_ATTEMPT_SEND_DATE) + " AS " + Projection.ATTEMPT_SEND_DATE);
		mProjection.put(Projection.ATTEMPT_COUNT, getFullName(TABLE_NAME, FIELD_ATTEMPT_COUNT) + " AS " + Projection.ATTEMPT_COUNT);
		mProjection.put(Projection.UPDATED, getFullName(TABLE_NAME, FIELD_UPDATED) + " AS " + Projection.UPDATED);
	}
	
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
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID + "=?", new String[]{orderId}, null, null, null);
		} else {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID + "=? AND " + FIELD_OPERATION_TYPE_UUID + "=?", new String[]{orderId,operation_type}, null, null, null);
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
		return mDb.query(TABLE_NAME, new String[]{FIELD_UUID, FIELD_TASK_UUID, FIELD_EQUIPMENT_UUID, FIELD_OPERATION_TYPE_UUID, FIELD_OPERATION_PATTERN_UUID, FIELD_OPERATION_STATUS_UUID, FIELD_OPERATION_TIME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы equipment_operation</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getOpEquipment(long uuid) {
		return mDb.query(TABLE_NAME, new String[]{FIELD_UUID, FIELD_TASK_UUID, FIELD_EQUIPMENT_UUID, FIELD_OPERATION_TYPE_UUID, FIELD_OPERATION_PATTERN_UUID, FIELD_OPERATION_STATUS_UUID, FIELD_OPERATION_TIME}, FIELD_UUID + "=?", new String[]{String.valueOf(uuid)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу equipments_operation</p>
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertOpEquipment(String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid, int operation_time){
		ContentValues values = new ContentValues();
		String uuid = UUID.randomUUID().toString();
		values.put(EquipmentOperationDBAdapter.FIELD_UUID, uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_TASK_UUID, task_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID, equipment_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID, operation_type_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID, operation_pattern_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TIME, operation_time);
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
		return mDb.delete(TABLE_NAME, FIELD_UUID + "=?", new String[]{String.valueOf(uuid)});
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице</p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(EquipmentOperation item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_TASK_UUID, item.getTask_uuid());
		values.put(FIELD_EQUIPMENT_UUID, item.getEquipment_uuid());
		values.put(FIELD_OPERATION_TYPE_UUID, item.getOperation_type_uuid());
		values.put(FIELD_OPERATION_PATTERN_UUID, item.getOperation_pattern_uuid());
		values.put(FIELD_OPERATION_STATUS_UUID, item.getOperation_status_uuid());
		values.put(FIELD_OPERATION_TIME, item.getOperation_time());
		values.put(FIELD_ATTEMPT_SEND_DATE, item.getAttempt_send_date());
		values.put(FIELD_ATTEMPT_COUNT, item.getAttempt_count());
		values.put(FIELD_UPDATED, item.isUpdated() == true ? 1 : 0);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * Метод делает то же что и replace, дополнительно меняет ChangedAt на текущее время.
	 * @param item
	 * @return
	 */
	public long update(EquipmentOperation item) {
		item.setChangedAt(Calendar.getInstance().getTime().getTime());
		return replace(item);
	}

	/**
	 * Возвращает операцию над оборудованием по uuid
	 * @param uuid
	 * @return если операции нет, возвращает null
	 */
	public EquipmentOperation getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);
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
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_UUID + "=?", new String[]{equipment_uuid}, null, null, "_id DESC");
		else
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID + "=? AND " + FIELD_EQUIPMENT_UUID + "=?", new String[]{task_uuid, equipment_uuid}, null, null, null);		
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
		EquipmentOperation operation = new EquipmentOperation();

		getItem(cursor, operation);
		operation.setTask_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_TASK_UUID)));
		operation.setEquipment_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_UUID)));
		operation.setOperation_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_OPERATION_TYPE_UUID)));
		operation.setOperation_pattern_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_OPERATION_PATTERN_UUID)));
		operation.setOperation_status_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_OPERATION_STATUS_UUID)));
		operation.setOperation_time(cursor.getInt(cursor
				.getColumnIndex(FIELD_OPERATION_TIME)));
		operation.setAttempt_send_date(cursor.getLong(cursor
				.getColumnIndex(FIELD_ATTEMPT_SEND_DATE)));
		operation.setAttempt_count(cursor.getInt(cursor
				.getColumnIndex(FIELD_ATTEMPT_COUNT)));
		operation
				.setUpdated(cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED)) == 0 ? false
						: true);
		return operation;
	}
	
	/**
	 * Возвращает список операций над оборудованием по наряду
	 * @param taskUuid
	 * @return если нет наряда, возвращает null
	 */
	public ArrayList<EquipmentOperation> getItems(String taskUuid) {
		ArrayList<EquipmentOperation> arrayList = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID + "=?", new String[]{taskUuid}, null, null, null);		
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

		Cursor cursor;
		String sortOrder = null;
		List<String> paramArray = new ArrayList<String>();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Map<String, String> projection = new HashMap<String, String>();
		
		projection.putAll(mProjection);
		
		queryBuilder.appendWhere(FIELD_TASK_UUID
				+ "=?");
		paramArray.add(taskUuid);

		String table;
		StringBuilder tables = new StringBuilder();
		// операции с типами операций
		table = getLeftJoinTables(TABLE_NAME,
				OperationTypeDBAdapter.TABLE_NAME,
				FIELD_OPERATION_TYPE_UUID,
				OperationTypeDBAdapter.FIELD_UUID, true);
		tables.append(table);
		projection.putAll(OperationTypeDBAdapter.getProjection());
		
		// оборудование 
		table = getLeftJoinTables(TABLE_NAME,
				EquipmentDBAdapter.TABLE_NAME, FIELD_EQUIPMENT_UUID,
				EquipmentDBAdapter.FIELD_UUID, false);
		tables.append(' ').append(table);
		projection.putAll(EquipmentDBAdapter.getProjection());

		// типы критичности оборудования
		table = getLeftJoinTables(EquipmentDBAdapter.TABLE_NAME,
				CriticalTypeDBAdapter.TABLE_NAME,
				EquipmentDBAdapter.FIELD_CRITICAL_TYPE_UUID,
				CriticalTypeDBAdapter.FIELD_UUID, false);
		tables.append(' ').append(table);
		projection.putAll(CriticalTypeDBAdapter.getProjection());
		
		// статусы операций
		table = getLeftJoinTables(TABLE_NAME,
				OperationStatusDBAdapter.TABLE_NAME, FIELD_OPERATION_STATUS_UUID,
				OperationStatusDBAdapter.FIELD_UUID, false);
		tables.append(' ').append(table);
		projection.putAll(OperationStatusDBAdapter.getProjection());
		
		// результаты выполнения операций
		table = getLeftJoinTables(TABLE_NAME,
				EquipmentOperationResultDBAdapter.TABLE_NAME, FIELD_UUID,
				EquipmentOperationResultDBAdapter.FIELD_EQUIPMENT_OPERATION_UUID, false);
		tables.append(' ').append(table);
		projection.putAll(EquipmentOperationResultDBAdapter.getProjection());
		
		// справочник вариантов результатов выполнения операции
		table = getLeftJoinTables(EquipmentOperationResultDBAdapter.TABLE_NAME,
				OperationResultDBAdapter.TABLE_NAME, EquipmentOperationResultDBAdapter.FIELD_OPERATION_RESULT_UUID,
				OperationResultDBAdapter.FIELD_UUID, false);
		tables.append(' ').append(table);
		projection.putAll(OperationResultDBAdapter.getProjection());
		
		queryBuilder.setTables(tables.toString());
		queryBuilder.setProjectionMap(projection);
		
		if (operationTypeUuid != null) {
			queryBuilder.appendWhere(FIELD_OPERATION_TYPE_UUID
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

	public boolean saveItems(ArrayList<EquipmentOperation> list) {

		for (EquipmentOperation item : list) {
			if (replace(item) == -1) {
				return false;
			}
		}

		return true;
	}

}
