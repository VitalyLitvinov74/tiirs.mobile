package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.OperationResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperationResultDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "operation_result";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_OPERATION_TYPE_UUID_NAME = "operation_type_uuid";
	public static final String FIELD_TITLE_NAME = "title";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_OPERATION_TYPE_UUID_NAME,
			FIELD_TITLE_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationResultDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public OperationResultDBAdapter open() {
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
	public Cursor getAllItems_cursor() {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);		
		if (cursor.moveToFirst()) {
			return cursor;
		}
		return null;
	}
	
	/**
	 * <p>Возвращает все записи из таблицы</p>
	 * @return list
	 */
	public ArrayList<OperationResult> getAllItems() {
		ArrayList<OperationResult> arrayList = new ArrayList<OperationResult>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);		
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 while (true)		
			 	{			 
				 OperationResult equip = new OperationResult(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
				 	arrayList.add(equip);
				 	if (cursor.isLast()) break;
				 	cursor.moveToNext();
			 	}
			}
		return arrayList;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице operation_type</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String operation_type_uuid, String title) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
		values.put(FIELD_TITLE_NAME, title);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице operation_type</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationResult result) {
		return replace(result.getUuid(), result.getOperation_type_uuid(), result.getTitle());
	}

	/**
	 * <p>Возвращает расшифровку статуса из таблицы</p>
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public String getNameByUUID(String uuid) {	
		Cursor cur;		
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cur.getCount()>0)
			{
			 cur.moveToFirst();
			 return cur.getString(3);
			}
		else
			return "";
	}

}
