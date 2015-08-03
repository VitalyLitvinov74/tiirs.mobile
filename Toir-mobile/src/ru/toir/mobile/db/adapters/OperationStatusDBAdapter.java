package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.OperationStatus;
import ru.toir.mobile.db.tables.TaskStatus;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperationStatusDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "operation_status";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TITLE_NAME = "title";

	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_TITLE_NAME};
		
	/**
	 * 
	 * @param context
	 */
	public OperationStatusDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public OperationStatusDBAdapter open() {
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
	public OperationStatus getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}

	/**
	 * 
	 * @param cursor
	 * @return
	 */
	public OperationStatus getItem(Cursor cursor) {		
		OperationStatus status = new OperationStatus();
		status.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		status.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		status.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
		return status;
	}

	/**
	 * @return
	 */
	public ArrayList<OperationStatus> getItems() {
		ArrayList<OperationStatus> arrayList = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);		
		if (cursor.moveToFirst()) {
			arrayList = new ArrayList<OperationStatus>();
			do {
				arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}
	
	/**
	 * <p>Возвращает все записи из таблицы</p>
	 * @return list
	 */
	public ArrayList<TaskStatus> getAllItems() {
		ArrayList<TaskStatus> arrayList = new ArrayList<TaskStatus>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);		
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 while (true)		
			 	{			 
				 TaskStatus equip = new TaskStatus(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
				 	arrayList.add(equip);
				 	if (cursor.isLast()) break;
				 	cursor.moveToNext();
			 	}
			}
		return arrayList;
	}
}
