package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.MeasureValue;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MeasureValueDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "measure_value";
	
	public static final String FIELD_EQUIPMENT_OPERATION_UUID = "equipment_operation_uuid";
	public static final String FIELD_OPERATION_PATTERN_STEP_RESULT = "operation_pattern_step_result";
	public static final String FIELD_DATE = "date";
	public static final String FIELD_VALUE = "value";
	public static final String FIELD_ATTEMPT_SEND_DATE = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT = "attempt_count";
	public static final String FIELD_UPDATED = "updated";
	
	String[] mColumns = {
			FIELD__ID,
			FIELD_UUID,
			FIELD_EQUIPMENT_OPERATION_UUID,
			FIELD_OPERATION_PATTERN_STEP_RESULT,
			FIELD_DATE,
			FIELD_VALUE,
			FIELD_ATTEMPT_SEND_DATE,
			FIELD_ATTEMPT_COUNT,
			FIELD_UPDATED,
			FIELD_CREATED_AT,
			FIELD_CHANGED_AT};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public MeasureValueDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}
	
	/**
	 * Возвращает результат измерений по uuid
	 * @param uuid
	 * @return если результата нет, возвращает null
	 */
	public MeasureValue getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getMeasureValue(cursor);
		} else {
			return null;
		}
	}

	/**
	 * Возвращает результаты измерений по uuid операции над оборудованием
	 * @param uuid
	 * @return если результатов нет, список результатов пустой
	 */
	public ArrayList<MeasureValue> getItems(String operationUuid) {
		Cursor cursor;
		ArrayList<MeasureValue> arrayList = new ArrayList<MeasureValue>();
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_OPERATION_UUID + "=?", new String[]{operationUuid}, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				arrayList.add(getMeasureValue(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}

	/**
	 * <p>Возвращает объект MeasureValue</p>
	 * @param cursor
	 * @return
	 */
	public static MeasureValue getMeasureValue(Cursor cursor) {
		MeasureValue item = null;
		item = new MeasureValue(cursor.getLong(cursor.getColumnIndex(FIELD__ID)),
				cursor.getString(cursor.getColumnIndex(FIELD_UUID)),
				cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_OPERATION_UUID)),
				cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_STEP_RESULT)),
				cursor.getInt(cursor.getColumnIndex(FIELD_DATE)),
				cursor.getString(cursor.getColumnIndex(FIELD_VALUE)),
				cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE)),
				cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT)),
				cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED)) == 1 ? true : false);
		return item;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String equipment_operation_uuid, String operation_pattern_step_result, long date, String value, long attempt_send_date, int attempt_count, boolean updated) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID, uuid);
		values.put(FIELD_EQUIPMENT_OPERATION_UUID, equipment_operation_uuid);
		values.put(FIELD_OPERATION_PATTERN_STEP_RESULT, operation_pattern_step_result);
		values.put(FIELD_DATE, date);
		values.put(FIELD_VALUE, value);
		values.put(FIELD_ATTEMPT_SEND_DATE, attempt_send_date);
		values.put(FIELD_ATTEMPT_COUNT, attempt_count);
		values.put(FIELD_UPDATED, updated == true ? 1 : 0);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(MeasureValue value) {
		return replace(value.getUuid(), value.getEquipment_operation_uuid(), value.getOperation_pattern_step_result(), value.getDate(), value.getValue(), value.getAttempt_send_date(), value.getAttempt_count(), value.isUpdated());
	}
}
