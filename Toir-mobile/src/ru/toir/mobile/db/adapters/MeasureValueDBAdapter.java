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
			return getItem(cursor);
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
				arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}

	/**
	 * <p>Возвращает объект MeasureValue</p>
	 * @param cursor
	 * @return
	 */
	public MeasureValue getItem(Cursor cursor) {
		MeasureValue item = new MeasureValue();
		
		getItem(cursor, item);
		item.setEquipment_operation_uuid(cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_OPERATION_UUID)));
		item.setOperation_pattern_step_result(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_STEP_RESULT)));
		item.setDate(cursor.getInt(cursor.getColumnIndex(FIELD_DATE)));
		item.setValue(cursor.getString(cursor.getColumnIndex(FIELD_VALUE)));
		item.setAttempt_send_date(cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE)));
		item.setAttempt_count(cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT)));
		item.setUpdated(cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED)) == 1 ? true : false);
		return item;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(MeasureValue item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_EQUIPMENT_OPERATION_UUID, item.getEquipment_operation_uuid());
		values.put(FIELD_OPERATION_PATTERN_STEP_RESULT, item.getOperation_pattern_step_result());
		values.put(FIELD_DATE, item.getDate());
		values.put(FIELD_VALUE, item.getValue());
		values.put(FIELD_ATTEMPT_SEND_DATE, item.getAttempt_send_date());
		values.put(FIELD_ATTEMPT_COUNT, item.getAttempt_count());
		values.put(FIELD_UPDATED, item.isUpdated() == true ? 1 : 0);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

}
