package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.utils.DataUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TaskDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	private TaskStatusDBAdapter taskstdbadapter;
	
	public static final String TABLE_NAME = "task";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_USER_UUID_NAME = "users_uuid";
	public static final String FIELD_CREATE_DATE_NAME = "create_date";
	public static final String FIELD_MODIFY_DATE_NAME = "modify_date";
	public static final String FIELD_CLOSE_DATE_NAME = "close_date";
	public static final String FIELD_TASK_STATUS_UUID_NAME = "task_status_uuid";
	public static final String FIELD_ATTEMPT_SEND_DATE_NAME = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT_NAME = "attempt_count";
	public static final String FIELD_SUCCESSEFULL_SEND_NAME = "successefull_send";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_USER_UUID_NAME,
			FIELD_CREATE_DATE_NAME,
			FIELD_MODIFY_DATE_NAME,
			FIELD_CLOSE_DATE_NAME,
			FIELD_TASK_STATUS_UUID_NAME,
			FIELD_ATTEMPT_SEND_DATE_NAME,
			FIELD_ATTEMPT_COUNT_NAME,
			FIELD_SUCCESSEFULL_SEND_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public TaskDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public TaskDBAdapter open() {
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

	public ArrayList<Task> getOrdersByTagId(String uuid, String type, String sort) {		
		ArrayList<Task> arrayList = new ArrayList<Task>();
		Cursor cursor;
		if (sort.equals("")) sort=null;
		if (type.equals(""))
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=?", new String[]{uuid}, null, null, sort);
		else
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=? AND " + FIELD_TASK_STATUS_UUID_NAME + "=?", new String[]{uuid,type}, null, null, sort);
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 while (true)		
				{
				Task task;
				task = new Task(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_USER_UUID_NAME)),
					cursor.getLong(cursor.getColumnIndex(FIELD_CREATE_DATE_NAME)),
					cursor.getLong(cursor.getColumnIndex(FIELD_MODIFY_DATE_NAME)),
					cursor.getLong(cursor.getColumnIndex(FIELD_CLOSE_DATE_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TASK_STATUS_UUID_NAME)),
					cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE_NAME)),
					cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT_NAME)),
					cursor.getInt(cursor.getColumnIndex(FIELD_SUCCESSEFULL_SEND_NAME)) == 0 ? false : true);
				arrayList.add(task);
				if (cursor.isLast()) break;
				cursor.moveToNext();			
				}
			}
		return arrayList;
	}
	
	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице task
	 * </p>
	 * 
	 * @param token_type
	 * @param access_token
	 * @param expires_in
	 * @param userName
	 * @param issued
	 * @param expires
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(String uuid, String users_uuid, long create_date, long modify_date, long close_date,
			String task_status_uuid, long attempt_send_date, int attempt_count, boolean successefull_send) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_USER_UUID_NAME, users_uuid);
		values.put(FIELD_CREATE_DATE_NAME, create_date);
		values.put(FIELD_MODIFY_DATE_NAME, modify_date);
		values.put(FIELD_CLOSE_DATE_NAME, close_date);
		values.put(FIELD_TASK_STATUS_UUID_NAME, task_status_uuid);
		values.put(FIELD_ATTEMPT_SEND_DATE_NAME, attempt_send_date);
		values.put(FIELD_ATTEMPT_COUNT_NAME, attempt_count);
		values.put(FIELD_SUCCESSEFULL_SEND_NAME, successefull_send ? 1 : 0);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице token
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(Task task) {
		return replace(task.getUuid(), task.getUsers_uuid(), task.getCreate_date(),
				task.getModify_date(), task.getClose_date(), task.getTask_status_uuid(),
				task.getAttempt_send_date(), task.getAttempt_count(), task.isSuccessefull_send());
	}

	public String getStatusNameByUUID(String uuid) {	
		Cursor cur;		
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		//taskstatus = new TaskStatus();
		//String getNameByUUID(String uuid)
		if (cur.getCount()>0)
			{
			 cur.moveToFirst();
			 return taskstdbadapter.getNameByUUID(cur.getString(1));
			}
		else
			return "неизвестен";
	}
	
	public String getCompleteTimeByUUID(String uuid) {	
		Cursor cur;		
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cur.getCount()>0)
			{
			 cur.moveToFirst();
			 return DataUtils.getDate(cur.getLong(5),"dd-MM-yyyy hh:mm:ss");
			}
		else
			return "";
	}
}
