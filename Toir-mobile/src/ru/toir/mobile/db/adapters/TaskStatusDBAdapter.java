package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.TaskStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskStatusDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "task_status";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TITLE_NAME = "title";

	public static final String STATUS_UUID_CREATED = "1e9b4d73-044c-471b-a08d-26f36ebb22ba";
	public static final String STATUS_UUID_SENDED = "9f980db5-934c-4ddb-999a-04c6c3daca59";
	public static final String STATUS_UUID_RECIEVED = "9f980db5-934c-4ddb-999a-04c6c3daca59";
	public static final String STATUS_UUID_COMPLETED = "dc6dca37-2cc9-44da-aff9-19bf143e611a";
	public static final String STATUS_UUID_UNCOMPLETED = "363c08ec-89d9-47df-b7cf-63a05d56594c";
	// TODO add type archived
	public static final String STATUS_UUID_ARCHIVED = "363c08ec-89d9-47df-b7cf-63a05d56594d";

	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_TITLE_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public TaskStatusDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public TaskStatusDBAdapter open() {
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
	 * @param UUID
	 * @return
	 */
	public String getNameByUUID(String uuid) {		
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cur.getCount()>0)
			{
			 cur.moveToFirst();
			 return cur.getString(2);
			}
		else return "неизвестен";
	}

	/**
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

	/**
	 * <p>Добавляет/изменяет запись в таблице task_status</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String title) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TITLE_NAME, title);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице task_status</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(TaskStatus status) {
		return replace(status.getUuid(), status.getTitle());
	}
}
