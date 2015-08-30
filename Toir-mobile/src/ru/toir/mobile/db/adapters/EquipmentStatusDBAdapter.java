package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.EquipmentStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EquipmentStatusDBAdapter extends BaseDBAdapter {
	
	public static final String TABLE_NAME = "equipment_status";

	public static final String FIELD_TITLE = "title";
	public static final String FIELD_TYPE = "type";

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
			return cur.getString(1);
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
	 * <p>Добавляет/изменяет запись</p>
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

	public void saveItems(ArrayList<EquipmentStatus> list) {
		mDb.beginTransaction();
		for (EquipmentStatus item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}

}
