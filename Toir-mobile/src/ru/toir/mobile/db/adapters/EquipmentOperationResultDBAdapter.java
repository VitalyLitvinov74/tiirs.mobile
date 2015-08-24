package ru.toir.mobile.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.EquipmentOperationResult;

/**
 * @author olejek
 * <p>Класс адаптер для таблицы equipment_operation_result</p>
 *
 */
public class EquipmentOperationResultDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation_result";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_EQUIPMENT_OPERATION_UUID_NAME = "equipment_operation_uuid";
	public static final String FIELD_START_DATE_NAME = "start_date";
	public static final String FIELD_END_DATE_NAME = "end_date";
	public static final String FIELD_OPERATION_RESULT_UUID_NAME = "operation_result_uuid";
	public static final String FIELD_TYPE_NAME = "type";
	public static final String FIELD_ATTEMPT_SEND_DATE_NAME = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT_NAME = "attempt_count";
	public static final String FIELD_UPDATED_NAME = "updated";

	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_EQUIPMENT_OPERATION_UUID_NAME,
			FIELD_START_DATE_NAME,
			FIELD_END_DATE_NAME,
			FIELD_OPERATION_RESULT_UUID_NAME,
			FIELD_TYPE_NAME,
			FIELD_ATTEMPT_SEND_DATE_NAME,
			FIELD_ATTEMPT_COUNT_NAME,
			FIELD_UPDATED_NAME
		};


	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	
	/**
	 * @param context
	 * @return EquipmentOpDBAdapter
	 */
	public EquipmentOperationResultDBAdapter(Context context){
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
	}
	
	/**
	 * <p>Возвращает по uuid дату начала выполнения операции</p>
	 * @param uuid
	 */
	public Long getStartDateByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getLong(cursor.getColumnIndex(FIELD_START_DATE_NAME));			 
			}
		else return 0l;
	}		

	/**
	 * <p>Возвращает по uuid дату завершения операции</p>
	 * @param uuid
	 */
	public Long getEndDateByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getLong(cursor.getColumnIndex(FIELD_END_DATE_NAME));
			}
		else return 0l;
	}

	/**
	 * <p>Возвращает по uuid, uuid (статуса) результата выполнения операции</p>
	 * @param uuid
	 */
	public String getOperationResultByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_RESULT_UUID_NAME));
			}
		else return "неизвестна";
	}

	/**
	 * <p>Возвращает тип по uuid (статуса) результата выполнения операции</p>
	 * @param uuid
	 */
	public int getOperationResultTypeByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getInt(cursor.getColumnIndex(FIELD_TYPE_NAME));
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getEquipmentOperationResult(cursor);
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_OPERATION_UUID_NAME + "=?", new String[]{operationUuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getEquipmentOperationResult(cursor);
		} else {
			return null;
		}
	}
	
	/**
	 * <p>Возвращает результат выполнения операции</p>
	 * @param cursor
	 * @return EquipmentOperationResult
	 */
	static EquipmentOperationResult getEquipmentOperationResult(Cursor cursor) {
		EquipmentOperationResult result = new EquipmentOperationResult();
		result.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		result.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		result.setEquipment_operation_uuid(cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_OPERATION_UUID_NAME)));
		result.setStart_date(cursor.getLong(cursor.getColumnIndex(FIELD_START_DATE_NAME)));
		result.setEnd_date(cursor.getLong(cursor.getColumnIndex(FIELD_END_DATE_NAME)));
		result.setOperation_result_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_RESULT_UUID_NAME)));
		result.setType(cursor.getInt(cursor.getColumnIndex(FIELD_TYPE_NAME)));
		result.setAttempt_send_date(cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE_NAME)));
		result.setAttempt_count(cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT_NAME)));
		result.setUpdated(cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED_NAME)) == 0);
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
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_EQUIPMENT_OPERATION_UUID_NAME, equipment_operation_uuid);
		values.put(FIELD_START_DATE_NAME, start_date);
		values.put(FIELD_END_DATE_NAME, end_date);
		values.put(FIELD_OPERATION_RESULT_UUID_NAME, operation_result_uuid);
		values.put(FIELD_TYPE_NAME, type);
		values.put(FIELD_ATTEMPT_SEND_DATE_NAME, attempt_send_date);
		values.put(FIELD_ATTEMPT_COUNT_NAME, attempt_count);
		values.put(FIELD_UPDATED_NAME, updated == true ? 1 : 0);
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
	
}
