package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.EquipmentStatus;
import ru.toir.mobile.db.tables.TaskStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EquipmentStatusDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "equipment_status";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_NAME_NAME = "name";
	public static final String FIELD_UUID_NAME = "status_uuid";
	public static final String FIELD_TYPE_NAME = "type";

	//public static final String STATUS_UUID_CREATED = "1e9b4d73-044c-471b-a08d-26f36ebb22ba";

	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_NAME_NAME,
			FIELD_TYPE_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public EquipmentStatusDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public EquipmentStatusDBAdapter open() {
		mDbHelper = DatabaseHelper.getInstance(mContext);
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
			 return cur.getString(1);
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
	public ArrayList<EquipmentStatus> getAllItems() {
		ArrayList<EquipmentStatus> arrayList = new ArrayList<EquipmentStatus>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);		
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 while (true)		
			 	{			 
				 EquipmentStatus equip = new EquipmentStatus(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_NAME_NAME)),
					cursor.getInt(cursor.getColumnIndex(FIELD_TYPE_NAME)));
				 	arrayList.add(equip);
				 	if (cursor.isLast()) break;
				 	cursor.moveToNext();
			 	}
			}
		return arrayList;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице equipment_status</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String title) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_NAME_NAME, title);
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
