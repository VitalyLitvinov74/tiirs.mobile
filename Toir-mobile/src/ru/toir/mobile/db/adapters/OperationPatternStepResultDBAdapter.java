package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.OperationPatternStepResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperationPatternStepResultDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "operation_pattern_step_result";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_OPERATION_PATTERN_STEP_UUID_NAME = "operation_pattern_step_uuid";
	public static final String FIELD_NEXT_OPERATION_PATTERN_STEP_UUID_NAME = "next_operation_pattern_step_uuid";
	public static final String FIELD_TITLE_NAME = "title";
	public static final String FIELD_MEASURE_TYPE_UUID_NAME = "measure_type_uuid";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_OPERATION_PATTERN_STEP_UUID_NAME,
			FIELD_NEXT_OPERATION_PATTERN_STEP_UUID_NAME,
			FIELD_TITLE_NAME,
			FIELD_MEASURE_TYPE_UUID_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationPatternStepResultDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public OperationPatternStepResultDBAdapter open() {
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
	 * Возвращает результат шага по uuid
	 * @param uuid
	 * @return
	 */
	public OperationPatternStepResult getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}
	
	/**
	 * Возвращает результат шага
	 * @param cursor
	 * @return
	 */
	public OperationPatternStepResult getItem(Cursor cursor) {
		OperationPatternStepResult result = new OperationPatternStepResult();
		result.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		result.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		result.setOperation_pattern_step_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_STEP_UUID_NAME)));
		result.setNext_operation_pattern_step_uuid(cursor.getString(cursor.getColumnIndex(FIELD_NEXT_OPERATION_PATTERN_STEP_UUID_NAME)));
		result.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
		result.setMeasure_type_uuid(cursor.getString(cursor.getColumnIndex(FIELD_MEASURE_TYPE_UUID_NAME)));
		return result;
	}
	
	/**
	 * Возвращает все результаты связанные с шагом
	 * @param step_uuid
	 * @return
	 */
	public ArrayList<OperationPatternStepResult> getItems(String step_uuid) {
		ArrayList<OperationPatternStepResult> stepResults = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_PATTERN_STEP_UUID_NAME + "=?", new String[]{step_uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			stepResults = new ArrayList<OperationPatternStepResult>();
			do	{
				stepResults.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return stepResults;
	}
	
	/**
	 * Возвращает список результатов связанных с шагами переданными в качестве параметра
	 * @param steps_uuids
	 * @return
	 */
	public ArrayList<OperationPatternStepResult> getItems(ArrayList<String> steps_uuids) {
		ArrayList<OperationPatternStepResult> stepResults = null;
		Cursor cursor;
		StringBuilder stringBuilder = new StringBuilder();
		for (String uuid: steps_uuids) {
			if (stringBuilder.length() != 0) {
				stringBuilder.append(",");
			}
			stringBuilder.append("'").append(uuid).append("'");
		}
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_PATTERN_STEP_UUID_NAME + " IN (" + stringBuilder.toString() + ")", null, null, null, null);		
		if (cursor.moveToFirst()) {
			stepResults = new ArrayList<OperationPatternStepResult>();
			do	{
				stepResults.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return stepResults;
	}
	
	/**
	 * 
	 * @return
	 */
	public Cursor getAllItems() {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);		
		if (cursor.moveToFirst()) {
			return cursor;
		}
		return null;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String operation_pattern_step_uuid, String next_operation_pattern_step_uuid, String title, String measure_type_uuid) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_OPERATION_PATTERN_STEP_UUID_NAME, operation_pattern_step_uuid);
		values.put(FIELD_NEXT_OPERATION_PATTERN_STEP_UUID_NAME, next_operation_pattern_step_uuid);
		values.put(FIELD_TITLE_NAME, title);
		values.put(FIELD_MEASURE_TYPE_UUID_NAME, measure_type_uuid);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationPatternStepResult stepResult) {
		return replace(stepResult.getUuid(), stepResult.getOperation_pattern_step_uuid(), stepResult.getNext_operation_pattern_step_uuid(), stepResult.getTitle(), stepResult.getMeasure_type_uuid());
	}
}
