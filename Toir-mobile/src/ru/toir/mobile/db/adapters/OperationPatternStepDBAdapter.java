package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.OperationPatternStep;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperationPatternStepDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "operation_pattern_step";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_OPERATION_PATTERN_UUID_NAME = "operation_pattern_uuid";
	public static final String FIELD_DESCRIPTION_NAME = "description";
	public static final String FIELD_IMAGE_NAME = "image";
	public static final String FIELD_FIRST_STEP_NAME = "first_step";
	public static final String FIELD_LAST_STEP_NAME = "last_step";
	public static final String FIELD_NAME_NAME = "name";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_OPERATION_PATTERN_UUID_NAME,
			FIELD_DESCRIPTION_NAME,
			FIELD_IMAGE_NAME,
			FIELD_FIRST_STEP_NAME,
			FIELD_LAST_STEP_NAME,
			FIELD_NAME_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationPatternStepDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public OperationPatternStepDBAdapter open() {
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Закрываем базу данных
	 */
	public void close() {
	}

	/**
	 * Возвращает шаг операции по uuid
	 * @param uuid
	 * @return
	 */
	public OperationPatternStep getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);		
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
		patternStep.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		patternStep.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		patternStep.setOperation_pattern_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_UUID_NAME)));
		patternStep.setDescription(cursor.getString(cursor.getColumnIndex(FIELD_DESCRIPTION_NAME)));
		patternStep.setImage(cursor.getString(cursor.getColumnIndex(FIELD_IMAGE_NAME)));
		patternStep.setFirst_step(cursor.getInt(cursor.getColumnIndex(FIELD_FIRST_STEP_NAME)) == 1);
		patternStep.setLast_step(cursor.getInt(cursor.getColumnIndex(FIELD_LAST_STEP_NAME)) == 1);
		patternStep.setName(cursor.getString(cursor.getColumnIndex(FIELD_NAME_NAME)));		
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_PATTERN_UUID_NAME + "=?", new String[]{pattern_uuid}, null, null, null);
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
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_OPERATION_PATTERN_UUID_NAME, operation_pattern_uuid);
		values.put(FIELD_DESCRIPTION_NAME, description);
		values.put(FIELD_IMAGE_NAME, image);
		values.put(FIELD_FIRST_STEP_NAME, first_step ? 1 : 0);
		values.put(FIELD_LAST_STEP_NAME, last_step ? 1 : 0);
		values.put(FIELD_NAME_NAME, name);
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
}
