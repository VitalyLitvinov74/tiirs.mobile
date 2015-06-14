package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
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
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_OPERATION_PATTERN_UUID_NAME,
			FIELD_DESCRIPTION_NAME,
			FIELD_IMAGE_NAME,
			FIELD_FIRST_STEP_NAME,
			FIELD_LAST_STEP_NAME};
		
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
	 * 
	 * @param uuid
	 * @return
	 */
	public Cursor getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			return cursor;
		}
		return null;
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
	public long replace(String uuid, String operation_pattern_uuid, String description, String image, boolean first_step, boolean last_step) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_OPERATION_PATTERN_UUID_NAME, operation_pattern_uuid);
		values.put(FIELD_DESCRIPTION_NAME, description);
		values.put(FIELD_IMAGE_NAME, image);
		values.put(FIELD_FIRST_STEP_NAME, first_step ? 1 : 0);
		values.put(FIELD_LAST_STEP_NAME, last_step ? 1 : 0);
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
		return replace(step.getUuid(), step.getOperation_pattern_uuid(), step.getDescription(), step.getImage(), step.isFirst_step(), step.isLast_step());
	}
}
