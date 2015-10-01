package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.MeasureValue;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MeasureValueDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "measure_value";
	
	public static final String FIELD_EQUIPMENT_OPERATION_UUID = "equipment_operation_uuid";
	public static final String FIELD_OPERATION_PATTERN_STEP_RESULT_UUID = "operation_pattern_step_result_uuid";
	public static final String FIELD_VALUE = "value";
	public static final String FIELD_ATTEMPT_SEND_DATE = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT = "attempt_count";
	public static final String FIELD_UPDATED = "updated";

	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String EQUIPMENT_OPERATION_UUID = TABLE_NAME + '_' +  FIELD_EQUIPMENT_OPERATION_UUID;
		public static final String OPERATION_PATTERN_STEP_RESULT_UUID = TABLE_NAME + '_' + FIELD_OPERATION_PATTERN_STEP_RESULT_UUID;
		public static final String VALUE = TABLE_NAME + '_' + FIELD_VALUE;
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
		mProjection.put(Projection.OPERATION_PATTERN_STEP_RESULT_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_PATTERN_STEP_RESULT_UUID) + " AS " + Projection.OPERATION_PATTERN_STEP_RESULT_UUID);
		mProjection.put(Projection.VALUE, getFullName(TABLE_NAME, FIELD_VALUE) + " AS " + Projection.VALUE);
		mProjection.put(Projection.ATTEMPT_SEND_DATE, getFullName(TABLE_NAME, FIELD_ATTEMPT_SEND_DATE) + " AS " + Projection.ATTEMPT_SEND_DATE);
		mProjection.put(Projection.ATTEMPT_COUNT, getFullName(TABLE_NAME, FIELD_ATTEMPT_COUNT) + " AS " + Projection.ATTEMPT_COUNT);
		mProjection.put(Projection.UPDATED, getFullName(TABLE_NAME, FIELD_UPDATED) + " AS " + Projection.UPDATED);
		
	}

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
		item.setOperation_pattern_step_result_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_STEP_RESULT_UUID)));
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
		values.put(FIELD_OPERATION_PATTERN_STEP_RESULT_UUID, item.getOperation_pattern_step_result_uuid());
		values.put(FIELD_VALUE, item.getValue());
		values.put(FIELD_ATTEMPT_SEND_DATE, item.getAttempt_send_date());
		values.put(FIELD_ATTEMPT_COUNT, item.getAttempt_count());
		values.put(FIELD_UPDATED, item.isUpdated() == true ? 1 : 0);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
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

	public void saveItems(ArrayList<MeasureValue> list) {

		for (MeasureValue item : list) {
			replace(item);
		}
	}

}
