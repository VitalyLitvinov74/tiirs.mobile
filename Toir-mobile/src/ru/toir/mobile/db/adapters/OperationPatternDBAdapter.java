package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.OperationPattern;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperationPatternDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "operation_pattern";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TITLE_NAME = "title";
	public static final String FIELD_OPERATION_TYPE_UUID_NAME = "operation_type_uuid";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_TITLE_NAME,
			FIELD_OPERATION_TYPE_UUID_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationPatternDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public OperationPatternDBAdapter open() {
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
	 * 
	 * @param uuid
	 * @return
	 */
	public OperationPattern getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}
	
	public OperationPattern getItem(Cursor cursor) {
		OperationPattern pattern = new OperationPattern();
		pattern.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		pattern.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		pattern.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
		pattern.setOperation_type_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID_NAME)));
		return pattern;
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
	 * <p>Добавляет/изменяет запись в таблице operation_type</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String title, String operation_type_uuid) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TITLE_NAME, title);
		values.put(FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице operation_type</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationPattern status) {
		return replace(status.getUuid(), status.getTitle(), status.getOperation_type_uuid());
	}

	/**
	 * <p>Возвращает список операций</p>
	 * @param uuid
	 * @return array
	 */
	public ArrayList<OperationPattern> getOperationByUUID(String uuid) {
		ArrayList<OperationPattern> arrayList = new ArrayList<OperationPattern>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);		
		cursor.moveToFirst();
		if (cursor.getCount()>0)
		while (true)		
			{
			OperationPattern operationPattern = new OperationPattern(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID_NAME)));
			 arrayList.add(operationPattern);
			 if (cursor.isLast()) break;
			 cursor.moveToNext();
			}
		return arrayList;
	}
}
