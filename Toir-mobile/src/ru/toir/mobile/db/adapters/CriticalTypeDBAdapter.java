package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
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
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TYPE_NAME = "type";
	
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_TYPE_NAME};
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public CriticalTypeDBAdapter(Context context) {
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
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
	 * <p>Добавляет/изменяет запись в таблице critical_type</p>
	 * @param uuid
	 * @param type
	 * @return
	 */
	public long replace(String uuid, int type) {
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
	
	/**
	 * <p>Возвращает все записи из таблицы equipmentType</p>
	 * @return list
	 */
	public ArrayList<CriticalType> getAllItems() {
		ArrayList<CriticalType> arrayList = new ArrayList<CriticalType>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 while (true)		
			 	{			 
				 CriticalType equip = new CriticalType(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getInt(cursor.getColumnIndex(FIELD_TYPE_NAME)));
				 	arrayList.add(equip);
				 	if (cursor.isLast()) break;
				 	cursor.moveToNext();
			 	}
			}
		return arrayList;
	}

	public String getNameByUUID(String uuid) {	
		Cursor cur;		
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cur.getCount()>0)
			{
			 cur.moveToFirst();
			 return ""+cur.getInt(2);
			}
		else
			return "";
	}

	public String getUUIDByName(String name) {	
		Cursor cur;		
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_TYPE_NAME + "=?", new String[]{name}, null, null, null);
		if (cur.getCount()>0)
			{
			 cur.moveToFirst();
			 return cur.getString(1);
			}
		else
			return "";
	}
	
	public void saveItems(ArrayList<CriticalType> list) {
		mDb.beginTransaction();
		for(CriticalType item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
	}
	
}
