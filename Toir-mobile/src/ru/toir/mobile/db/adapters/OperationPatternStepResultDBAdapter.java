package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.OperationPatternStepResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OperationPatternStepResultDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "operation_pattern_step_result";
	
	public static final String FIELD_OPERATION_PATTERN_STEP_UUID = "operation_pattern_step_uuid";
	public static final String FIELD_NEXT_OPERATION_PATTERN_STEP_UUID = "next_operation_pattern_step_uuid";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_MEASURE_TYPE_UUID = "measure_type_uuid";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;

		public static final String OPERATION_PATTERN_STEP_UUID = TABLE_NAME + '_' + FIELD_OPERATION_PATTERN_STEP_UUID;
		public static final String NEXT_OPERATION_PATTERN_STEP_UUID = TABLE_NAME + '_' + FIELD_NEXT_OPERATION_PATTERN_STEP_UUID;
		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
		public static final String MEASURE_TYPE_UUID = TABLE_NAME + '_' + FIELD_MEASURE_TYPE_UUID;
		
	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE) + " AS " + Projection.TITLE);
		
	}
	
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationPatternStepResultDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}
	
	/**
	 * Возвращает результат шага по uuid
	 * @param uuid
	 * @return
	 */
	public OperationPatternStepResult getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);		
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
		
		getItem(cursor, result);
		result.setOperation_pattern_step_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_STEP_UUID)));
		result.setNext_operation_pattern_step_uuid(cursor.getString(cursor.getColumnIndex(FIELD_NEXT_OPERATION_PATTERN_STEP_UUID)));
		result.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		result.setMeasure_type_uuid(cursor.getString(cursor.getColumnIndex(FIELD_MEASURE_TYPE_UUID)));
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_PATTERN_STEP_UUID + "=?", new String[]{step_uuid}, null, null, null);		
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_PATTERN_STEP_UUID + " IN (" + stringBuilder.toString() + ")", null, null, null, null);		
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
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationPatternStepResult item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_OPERATION_PATTERN_STEP_UUID, item.getOperation_pattern_step_uuid());
		values.put(FIELD_NEXT_OPERATION_PATTERN_STEP_UUID, item.getNext_operation_pattern_step_uuid());
		values.put(FIELD_TITLE, item.getTitle());
		values.put(FIELD_MEASURE_TYPE_UUID, item.getMeasure_type_uuid());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}
	
	public void saveItems(ArrayList<OperationPatternStepResult> list) {

		for (OperationPatternStepResult item : list) {
			replace(item);
		}
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
