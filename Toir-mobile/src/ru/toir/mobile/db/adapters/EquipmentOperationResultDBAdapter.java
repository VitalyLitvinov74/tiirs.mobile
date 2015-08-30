package ru.toir.mobile.db.adapters;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import ru.toir.mobile.db.tables.EquipmentOperationResult;

/**
 * @author olejek
 * <p>Класс адаптер для таблицы equipment_operation_result</p>
 *
 */
public class EquipmentOperationResultDBAdapter extends BaseDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation_result";
	
	public static final String FIELD_EQUIPMENT_OPERATION_UUID = "equipment_operation_uuid";
	public static final String FIELD_START_DATE = "start_date";
	public static final String FIELD_END_DATE = "end_date";
	public static final String FIELD_OPERATION_RESULT_UUID = "operation_result_uuid";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_ATTEMPT_SEND_DATE = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT = "attempt_count";
	public static final String FIELD_UPDATED = "updated";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String EQUIPMENT_OPERATION_UUID = TABLE_NAME + '_' +  FIELD_EQUIPMENT_OPERATION_UUID;
		public static final String START_DATE = TABLE_NAME + '_' + FIELD_START_DATE;
		public static final String END_DATE = TABLE_NAME + '_' + FIELD_END_DATE;
		public static final String OPERATION_RESULT_UUID = TABLE_NAME + '_' + FIELD_OPERATION_RESULT_UUID;
		public static final String TYPE = TABLE_NAME + '_' + FIELD_TYPE;
		public static final String ATTEMPT_SEND_DATE = TABLE_NAME + '_' + FIELD_ATTEMPT_SEND_DATE;
		public static final String ATTEMPT_COUNT = TABLE_NAME + '_' + FIELD_ATTEMPT_COUNT;
		public static final String UPDATED = TABLE_NAME + '_' + FIELD_UPDATED;

	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.EQUIPMENT_OPERATION_UUID, getFullName(TABLE_NAME, FIELD_EQUIPMENT_OPERATION_UUID) + " AS " + Projection.EQUIPMENT_OPERATION_UUID);
		mProjection.put(Projection.START_DATE, getFullName(TABLE_NAME, FIELD_START_DATE) + " AS " + Projection.START_DATE);
		mProjection.put(Projection.END_DATE, getFullName(TABLE_NAME, FIELD_END_DATE) + " AS " + Projection.END_DATE);
		mProjection.put(Projection.OPERATION_RESULT_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_RESULT_UUID) + " AS " + Projection.OPERATION_RESULT_UUID);
		mProjection.put(Projection.TYPE, getFullName(TABLE_NAME, FIELD_TYPE) + " AS " + Projection.TYPE);
		mProjection.put(Projection.ATTEMPT_SEND_DATE, getFullName(TABLE_NAME, FIELD_ATTEMPT_SEND_DATE) + " AS " + Projection.ATTEMPT_SEND_DATE);
		mProjection.put(Projection.ATTEMPT_COUNT, getFullName(TABLE_NAME, FIELD_ATTEMPT_COUNT) + " AS " + Projection.ATTEMPT_COUNT);
		mProjection.put(Projection.UPDATED, getFullName(TABLE_NAME, FIELD_UPDATED) + " AS " + Projection.UPDATED);
		
	}

	/**
	 * @param context
	 * @return EquipmentOpDBAdapter
	 */
	public EquipmentOperationResultDBAdapter(Context context){
		super(context, TABLE_NAME);
	}
	
	/**
	 * <p>Возвращает по uuid дату начала выполнения операции</p>
	 * @param uuid
	 */
	public Long getStartDateByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getLong(cursor.getColumnIndex(FIELD_START_DATE));			 
			}
		else return 0l;
	}		

	/**
	 * <p>Возвращает по uuid дату завершения операции</p>
	 * @param uuid
	 */
	public Long getEndDateByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getLong(cursor.getColumnIndex(FIELD_END_DATE));
			}
		else return 0l;
	}

	/**
	 * <p>Возвращает по uuid, uuid (статуса) результата выполнения операции</p>
	 * @param uuid
	 */
	public String getOperationResultByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_RESULT_UUID));
			}
		else return "неизвестна";
	}

	/**
	 * <p>Возвращает тип по uuid (статуса) результата выполнения операции</p>
	 * @param uuid
	 */
	public int getOperationResultTypeByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getInt(cursor.getColumnIndex(FIELD_TYPE));
			}
		// unknown type
		else return 3;
	}

	/**
	 * <p>Возвращает результат выполнения операции</p>
	 * @param uuid
	 * @return EquipmentOperationResult если нет результата, возвращает null
	 */
	public EquipmentOperationResult getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		} else {
			return null;
		}
	}
	
	/**
	 * Возвращает результат выполнения операции по uuid операции
	 * @param operationUuid
	 * @return если нет результата, возвращает null 
	 */
	public EquipmentOperationResult getItemByOperation(String operationUuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_OPERATION_UUID + "=?", new String[]{operationUuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		} else {
			return null;
		}
	}
	
	/**
	 * <p>Возвращает результат выполнения операции</p>
	 * @param cursor
	 * @return EquipmentOperationResult
	 */
	public EquipmentOperationResult getItem(Cursor cursor) {
		EquipmentOperationResult result = new EquipmentOperationResult();
		
		getItem(cursor, result);
		result.setEquipment_operation_uuid(cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_OPERATION_UUID)));
		result.setStart_date(cursor.getLong(cursor.getColumnIndex(FIELD_START_DATE)));
		result.setEnd_date(cursor.getLong(cursor.getColumnIndex(FIELD_END_DATE)));
		result.setOperation_result_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_RESULT_UUID)));
		result.setType(cursor.getInt(cursor.getColumnIndex(FIELD_TYPE)));
		result.setAttempt_send_date(cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE)));
		result.setAttempt_count(cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT)));
		result.setUpdated(cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED)) == 0);
		return result;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * @param uuid
	 * @param equipment_operation_uuid
	 * @param start_date
	 * @param end_date
	 * @param operation_result_uuid
	 * @return
	 */

	public long replace(String uuid, String equipment_operation_uuid, long start_date, long end_date, String operation_result_uuid, long type, long attempt_send_date, int attempt_count, boolean updated) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID, uuid);
		values.put(FIELD_EQUIPMENT_OPERATION_UUID, equipment_operation_uuid);
		values.put(FIELD_START_DATE, start_date);
		values.put(FIELD_END_DATE, end_date);
		values.put(FIELD_OPERATION_RESULT_UUID, operation_result_uuid);
		values.put(FIELD_TYPE, type);
		values.put(FIELD_ATTEMPT_SEND_DATE, attempt_send_date);
		values.put(FIELD_ATTEMPT_COUNT, attempt_count);
		values.put(FIELD_UPDATED, updated == true ? 1 : 0);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * @param result
	 * @return
	 */
	public long replace(EquipmentOperationResult result) {
		return replace(result.getUuid(), result.getEquipment_operation_uuid(), result.getStart_date(), result.getEnd_date(), result.getOperation_result_uuid(), result.getType(), result.getAttempt_send_date(), result.getAttempt_count(), result.isUpdated());
	}
	
	/**
	 * @return the mProjection
	 */
	public static Map<String, String> getProjection() {
		Map<String, String> projection = new HashMap<String, String>();
		projection.putAll(mProjection);
		projection.remove(Projection._ID);
		return projection;
	}

}
