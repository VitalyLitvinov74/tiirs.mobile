package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.MeasureValue;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MeasureValueDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "measure_value";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_EQUIPMENT_OPERATION_UUID_NAME = "equipment_operation_uuid";
	public static final String FIELD_OPERATION_PATTERN_STEP_RESULT_NAME = "operation_pattern_step_result";
	public static final String FIELD_DATE_NAME = "date";
	public static final String FIELD_VALUE_NAME = "value";
	public static final String FIELD_ATTEMPT_SEND_DATE_NAME = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT_NAME = "attempt_count";
	public static final String FIELD_UPDATED_NAME = "updated";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_EQUIPMENT_OPERATION_UUID_NAME,
			FIELD_OPERATION_PATTERN_STEP_RESULT_NAME,
			FIELD_DATE_NAME,
			FIELD_VALUE_NAME,
			FIELD_ATTEMPT_SEND_DATE_NAME,
			FIELD_ATTEMPT_COUNT_NAME,
			FIELD_UPDATED_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public MeasureValueDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public MeasureValueDBAdapter open() {
		mDbHelper = new DatabaseHelper(mContext, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Закрываем базу данных
	 */
	public void close() {
		mDb.close();
		mDbHelper.close();
	}

	/**
	 * Возвращает результат измерений по uuid
	 * @param uuid
	 * @return если результата нет, возвращает null
	 */
	public MeasureValue getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_OPERATION_UUID_NAME + "=?", new String[]{operationUuid}, null, null, null);
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
		item = new MeasureValue(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
				cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
				cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_OPERATION_UUID_NAME)),
				cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_STEP_RESULT_NAME)),
				cursor.getInt(cursor.getColumnIndex(FIELD_DATE_NAME)),
				cursor.getString(cursor.getColumnIndex(FIELD_VALUE_NAME)),
				cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE_NAME)),
				cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT_NAME)),
				cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED_NAME)) == 1 ? true : false);
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
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_EQUIPMENT_OPERATION_UUID_NAME, equipment_operation_uuid);
		values.put(FIELD_OPERATION_PATTERN_STEP_RESULT_NAME, operation_pattern_step_result);
		values.put(FIELD_DATE_NAME, date);
		values.put(FIELD_VALUE_NAME, value);
		values.put(FIELD_ATTEMPT_SEND_DATE_NAME, attempt_send_date);
		values.put(FIELD_ATTEMPT_COUNT_NAME, attempt_count);
		values.put(FIELD_UPDATED_NAME, updated == true ? 1 : 0);
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
