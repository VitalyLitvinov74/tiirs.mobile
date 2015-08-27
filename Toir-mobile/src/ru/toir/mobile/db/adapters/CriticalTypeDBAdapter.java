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

	public static final String FIELD_TYPE_NAME = "type";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID_NAME;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID_NAME;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT_NAME;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT_NAME;
		
		public static final String TYPE = TABLE_NAME + '_' + FIELD_TYPE_NAME;
	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID_NAME) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID_NAME) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT_NAME) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT_NAME) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TYPE, getFullName(TABLE_NAME, FIELD_TYPE_NAME) + " AS " + Projection.TYPE);
	}

	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME, FIELD_TYPE_NAME,
			FIELD_CREATED_AT_NAME, FIELD_CHANGED_AT_NAME };

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
	public Cursor getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
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
	 * <p>
	 * Добавляет/изменяет запись в таблице critical_type
	 * </p>
	 * 
	 * @param uuid
	 * @param type
	 * @return
	 */
	public long replace(String uuid, int type, long createdAt, long changedAt) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TYPE_NAME, type);
		values.put(FIELD_CREATED_AT_NAME, createdAt);
		values.put(FIELD_CHANGED_AT_NAME, changedAt);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице critical_type
	 * </p>
	 * 
	 * @param type
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(CriticalType type) {
		return replace(type.getUuid(), type.getType(), type.getCreatedAt(),
				type.getChangedAt());
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
				CriticalType item = new CriticalType();
				item.set_id(cursor.getLong(cursor
						.getColumnIndex(FIELD__ID_NAME)));
				item.setUuid(cursor.getString(cursor
						.getColumnIndex(FIELD_UUID_NAME)));
				item.setType(cursor.getInt(cursor
						.getColumnIndex(FIELD_TYPE_NAME)));
				item.setCreatedAt(cursor.getLong(cursor
						.getColumnIndex(FIELD_CREATED_AT_NAME)));
				item.setChangedAt(cursor.getLong(cursor
						.getColumnIndex(FIELD_CHANGED_AT_NAME)));
				arrayList.add(item);
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		return arrayList;
	}

	public String getNameByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return "" + cur.getInt(2);
		} else
			return "";
	}

	public String getUUIDByName(String name) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_TYPE_NAME + "=?",
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
