package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.EquipmentType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EquipmentTypeDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "equipment_type";

	public static final String FIELD_TITLE = "title";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE) + " AS " + Projection.TITLE);
	}

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public EquipmentTypeDBAdapter(Context context) {
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

	public EquipmentType getItem(Cursor cursor) {
		EquipmentType item = new EquipmentType();
		
		getItem(cursor, item);
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		return item;
	}

	/**
	 * 
	 * @return
	 */
	public Cursor getAllItems_() {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			return cursor;
		}
		return null;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(EquipmentType item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_TITLE, item.getTitle());
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
	public ArrayList<EquipmentType> getAllItems() {
		ArrayList<EquipmentType> arrayList = new ArrayList<EquipmentType>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				EquipmentType equip = getItem(cursor);
				arrayList.add(equip);
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
			return cur.getString(2);
		} else
			return "";
	}

	public String getUUIDByName(String name) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_TITLE + "=?",
				new String[] { name }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(1);
		} else
			return "";
	}

	public void saveItems(ArrayList<EquipmentType> list) {

		for (EquipmentType item : list) {
			replace(item);
		}
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
