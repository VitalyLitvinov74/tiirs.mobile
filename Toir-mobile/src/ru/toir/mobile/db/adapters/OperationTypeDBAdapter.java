package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.OperationType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OperationTypeDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "operation_type";

	public static final String FIELD_TITLE_NAME = "title";

	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME, FIELD_TITLE_NAME,
			FIELD_CREATED_AT_NAME, FIELD_CHANGED_AT_NAME };

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationTypeDBAdapter(Context context) {
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

	public OperationType getItem(Cursor cursor) {
		OperationType item = new OperationType();
		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		item.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
		item.setCreatedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CREATED_AT_NAME)));
		item.setChangedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CHANGED_AT_NAME)));
		return item;
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
	 * Возвращает все записи из таблицы
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<OperationType> getAllItems() {
		ArrayList<OperationType> arrayList = new ArrayList<OperationType>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				OperationType equip = getItem(cursor);
				arrayList.add(equip);
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		return arrayList;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице operation_type
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
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TITLE_NAME, title);
		values.put(FIELD_CREATED_AT_NAME, createdAt);
		values.put(FIELD_CHANGED_AT_NAME, changedAt);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице operation_type
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationType status) {
		return replace(status.getUuid(), status.getTitle(),
				status.getCreatedAt(), status.getChangedAt());
	}

	/**
	 * <p>
	 * Возвращает название типа обслуживания по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getOperationTypeByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME));
		} else
			return "неизвестно";
	}

	public void saveItems(ArrayList<OperationType> list) {
		mDb.beginTransaction();
		for (OperationType item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}
	
}
