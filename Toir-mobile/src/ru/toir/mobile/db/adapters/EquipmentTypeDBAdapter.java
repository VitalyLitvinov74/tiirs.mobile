package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.EquipmentType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EquipmentTypeDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "equipment_type";

	public static final String FIELD_TITLE = "title";

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
	 * <p>
	 * Добавляет/изменяет запись в таблице
	 * </p>
	 * 
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String title, long createdAt,
			long changedAt) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID, uuid);
		values.put(FIELD_TITLE, title);
		values.put(FIELD_CREATED_AT, createdAt);
		values.put(FIELD_CHANGED_AT, changedAt);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(EquipmentType type) {
		return replace(type.getUuid(), type.getTitle(), type.getCreatedAt(),
				type.getChangedAt());
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
		mDb.beginTransaction();
		for (EquipmentType item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}
	
}
