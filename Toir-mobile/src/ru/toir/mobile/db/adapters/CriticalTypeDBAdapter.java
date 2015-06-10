package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.CriticalType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CriticalTypeDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "critical_type";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TYPE_NAME = "type";
	
	String[] mColumns = {
			FIELD_UUID_NAME,
			FIELD_TYPE_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public CriticalTypeDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public CriticalTypeDBAdapter open() {
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
	 * <p>Добавляет/изменяет запись в таблице critical_type</p>
	 * @param uuid
	 * @param type
	 * @return
	 */
	public long replace(String uuid, int type) {
		// TODO нужно сделать контроль, выполнилось выражение или нет
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TYPE_NAME, type);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице critical_type</p>
	 * 
	 * @param type
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(CriticalType type) {
		return replace(type.getUuid(), type.getType());
	}
}
