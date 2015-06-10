package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.Arrays;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.db.tables.Orders;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OrderDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "task";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_USER_UUID_NAME = "users_uuid";
	public static final String FIELD_CREATE_DATE_NAME = "create_date";
	public static final String FIELD_MODIFY_DATE_NAME = "modify_date";
	public static final String FIELD_CLOSE_DATE_NAME = "close_date";
	public static final String FIELD_STATUS_UUID_NAME = "task_status_uuid";
	public static final String FIELD_SEND_NAME = "attempt_send_date";
	public static final String FIELD_COUNT_NAME = "attempt_count";
	public static final String FIELD_SUCCESS_NAME = "successefull_send";
	
	String[] mColumns = {
			FIELD_UUID_NAME,
			FIELD_USER_UUID_NAME,
			FIELD_CREATE_DATE_NAME,
			FIELD_MODIFY_DATE_NAME,
			FIELD_CLOSE_DATE_NAME,
			FIELD_STATUS_UUID_NAME,
			FIELD_SEND_NAME,
			FIELD_COUNT_NAME,
			FIELD_SUCCESS_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OrderDBAdapter(Context context) {
		super();
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public OrderDBAdapter open() {
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

	public ArrayList<Orders> getOrdersByTagId(String tagId, Integer status) {		
		Orders	ord = new Orders();
//		ArrayList<Orders> arrayList = new ArrayList<Orders>(Arrays.asList(ord));
		ArrayList<Orders> arrayList = new ArrayList<Orders>();
		Cursor cur;
		Integer	cnt=0;
		// можем или отобрать все наряды, или те, что есть с определенным статусом 
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=?", new String[]{tagId}, null, null, null);		
		cur.moveToFirst();
		while (true)		
			{
			ord = new Orders(cur.getString(cur.getColumnIndex(FIELD_UUID_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_USER_UUID_NAME)),
					cur.getLong(cur.getColumnIndex(FIELD_CREATE_DATE_NAME)),
					cur.getLong(cur.getColumnIndex(FIELD_MODIFY_DATE_NAME)),
					cur.getLong(cur.getColumnIndex(FIELD_CLOSE_DATE_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_STATUS_UUID_NAME)),
					cur.getLong(cur.getColumnIndex(FIELD_SEND_NAME)),
					cur.getInt(cur.getColumnIndex(FIELD_COUNT_NAME)),
					cur.getInt(cur.getColumnIndex(FIELD_SUCCESS_NAME)));
			arrayList.add(ord);
			if (cur.isLast()) break;
			cur.moveToNext();
			cnt++;
		}
		return arrayList;
	}
}
