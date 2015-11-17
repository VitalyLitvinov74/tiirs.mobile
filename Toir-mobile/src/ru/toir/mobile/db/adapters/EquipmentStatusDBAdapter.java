package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.EquipmentStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EquipmentStatusDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "equipment_status";

	public static final String FIELD_TITLE = "title";
	public static final String FIELD_TYPE = "type";

	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_'
				+ FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_'
				+ FIELD_CHANGED_AT;

		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
	}

	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID)
				+ " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID)
				+ " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT,
				getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS "
						+ Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT,
				getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS "
						+ Projection.CHANGED_AT);

		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE)
				+ " AS " + Projection.TITLE);
	}

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public EquipmentStatusDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public Cursor getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
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
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(2);
		} else
			return "неизвестен";
	}

	/**
	 * 
	 * @param UUID
	 * @return
	 */
	public String getNameByPartOfUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + " LIKE ?",
				new String[] { "%" + uuid + "%" }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(2);
		} else
			return "неизвестен";
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
	 * <p>
	 * Возвращает все записи из таблицы
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<EquipmentStatus> getAllItems() {
		ArrayList<EquipmentStatus> arrayList = new ArrayList<EquipmentStatus>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				EquipmentStatus equip = getItem(cursor);
				arrayList.add(equip);
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		return arrayList;
	}

	public EquipmentStatus getItem(Cursor cursor) {
		EquipmentStatus item = new EquipmentStatus();

		getItem(cursor, item);
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		item.setType(cursor.getInt(cursor.getColumnIndex(FIELD_TYPE)));
		return item;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись
	 * </p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(EquipmentStatus item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_TITLE, item.getTitle());
		values.put(FIELD_TYPE, item.getType());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	public boolean saveItems(ArrayList<EquipmentStatus> list) {

		for (EquipmentStatus item : list) {
			if (replace(item) == -1) {
				return false;
			}
		}

		return true;
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
