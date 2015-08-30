package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.CriticalType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CriticalTypeDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "critical_type";

	public static final String FIELD_TYPE = "type";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String TYPE = TABLE_NAME + '_' + FIELD_TYPE;
	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TYPE, getFullName(TABLE_NAME, FIELD_TYPE) + " AS " + Projection.TYPE);
	}

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public CriticalTypeDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public CriticalType getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
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
	public CriticalType getItem(Cursor cursor) {
		CriticalType type = new CriticalType();

		getItem(cursor, type);
		type.setType(cursor.getInt(cursor
				.getColumnIndex(FIELD_TYPE)));
		return type;
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
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(CriticalType item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_TYPE, item.getType());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Возвращает все записи из таблицы equipmentType
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<CriticalType> getAllItems() {
		ArrayList<CriticalType> arrayList = new ArrayList<CriticalType>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				arrayList.add(getItem(cursor));
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		return arrayList;
	}

	public String getNameByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return "" + cur.getInt(2);
		} else
			return "";
	}

	public String getUUIDByName(String name) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_TYPE + "=?",
				new String[] { name }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(1);
		} else
			return "";
	}

	public void saveItems(ArrayList<CriticalType> list) {
		mDb.beginTransaction();
		for (CriticalType item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}

	/**
	 * @return the mProjection
	 */
	public static Map<String, String> getProjection() {
		Map<String, String> projection = new HashMap<String, String>();
		projection.putAll(mProjection);
		projection.remove(Projection._ID);
		return projection;
	}

}
