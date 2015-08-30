package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.OperationPatternStep;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OperationPatternStepDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "operation_pattern_step";
	
	public static final String FIELD_OPERATION_PATTERN_UUID = "operation_pattern_uuid";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_IMAGE = "image";
	public static final String FIELD_FIRST_STEP = "first_step";
	public static final String FIELD_LAST_STEP = "last_step";
	public static final String FIELD_NAME = "name";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String OPERATION_PATTERN_UUID = TABLE_NAME + '_' + FIELD_OPERATION_PATTERN_UUID;
		public static final String DESCRIPTION = TABLE_NAME + '_' + FIELD_DESCRIPTION;
		public static final String IMAGE = TABLE_NAME + '_' + FIELD_IMAGE;
		public static final String FIRST_STEP = TABLE_NAME + '_' + FIELD_FIRST_STEP;
		public static final String LAST_STEP = TABLE_NAME + '_' + FIELD_LAST_STEP;
		public static final String NAME = TABLE_NAME + '_' + FIELD_NAME;


	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.OPERATION_PATTERN_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_PATTERN_UUID) + " AS " + Projection.OPERATION_PATTERN_UUID);
		mProjection.put(Projection.DESCRIPTION, getFullName(TABLE_NAME, FIELD_DESCRIPTION) + " AS " + Projection.DESCRIPTION);
		mProjection.put(Projection.IMAGE, getFullName(TABLE_NAME, FIELD_IMAGE) + " AS " + Projection.IMAGE);
		mProjection.put(Projection.FIRST_STEP, getFullName(TABLE_NAME, FIELD_FIRST_STEP) + " AS " + Projection.FIRST_STEP);
		mProjection.put(Projection.LAST_STEP, getFullName(TABLE_NAME, FIELD_LAST_STEP) + " AS " + Projection.LAST_STEP);
		mProjection.put(Projection.NAME, getFullName(TABLE_NAME, FIELD_NAME) + " AS " + Projection.NAME);

	}
	
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationPatternStepDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}
	
	/**
	 * Возвращает шаг операции по uuid
	 * @param uuid
	 * @return
	 */
	public OperationPatternStep getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}
	
	/**
	 * Возвращает шаг операции
	 * @param cursor
	 * @return
	 */
	public OperationPatternStep getItem(Cursor cursor) {		
		OperationPatternStep patternStep = new OperationPatternStep();
		
		getItem(cursor, patternStep);
		patternStep.setOperation_pattern_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_UUID)));
		patternStep.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION)));
		patternStep.setImage(cursor.getString(cursor.getColumnIndex(FIELD_IMAGE)));
		patternStep.setFirst_step(cursor.getInt(cursor.getColumnIndex(FIELD_FIRST_STEP)) == 1);
		patternStep.setLast_step(cursor.getInt(cursor.getColumnIndex(FIELD_LAST_STEP)) == 1);
		patternStep.setName(cursor.getString(cursor.getColumnIndex(FIELD_NAME)));		
		return patternStep;
	}

	/**
	 * Возвращает список шагов операции по uuid
	 * @param pattern_uuid
	 * @return
	 */
	public ArrayList<OperationPatternStep> getItems(String pattern_uuid) {
		ArrayList<OperationPatternStep> patternSteps = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_PATTERN_UUID + "=?", new String[]{pattern_uuid}, null, null, null);
		if (cursor.moveToFirst()) {
			patternSteps = new ArrayList<OperationPatternStep>();
			do	{
				 patternSteps.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return patternSteps;		
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
	public long replace(String uuid, String operation_pattern_uuid, String description, String image, boolean first_step, boolean last_step, String name) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID, uuid);
		values.put(FIELD_OPERATION_PATTERN_UUID, operation_pattern_uuid);
		values.put(FIELD_DESCRIPTION, description);
		values.put(FIELD_IMAGE, image);
		values.put(FIELD_FIRST_STEP, first_step ? 1 : 0);
		values.put(FIELD_LAST_STEP, last_step ? 1 : 0);
		values.put(FIELD_NAME, name);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationPatternStep step) {
		return replace(step.getUuid(), step.getOperation_pattern_uuid(), step.getDescription(), step.getImage(), step.isFirst_step(), step.isLast_step(), step.getName());
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
