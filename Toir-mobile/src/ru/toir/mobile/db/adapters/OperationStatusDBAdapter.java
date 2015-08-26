package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.OperationStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OperationStatusDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "operation_status";

	public static final String FIELD_TITLE_NAME = "title";

	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME, FIELD_TITLE_NAME,
			FIELD_CREATED_AT_NAME, FIELD_CHANGED_AT_NAME };

	/**
	 * 
	 * @param context
	 */
	public OperationStatusDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}

	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public OperationStatus getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
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
	public OperationStatus getItem(Cursor cursor) {
		OperationStatus item = new OperationStatus();
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
	 * @return
	 */
	public ArrayList<OperationStatus> getItems() {
		ArrayList<OperationStatus> arrayList = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.moveToFirst()) {
			arrayList = new ArrayList<OperationStatus>();
			do {
				arrayList.add(getItem(cursor));
			} while (cursor.moveToNext());
		}
		return arrayList;
	}

	/**
	 * <p>
	 * Возвращает все записи из таблицы
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<OperationStatus> getAllItems() {
		ArrayList<OperationStatus> arrayList = new ArrayList<OperationStatus>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				OperationStatus equip = getItem(cursor);
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
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TITLE_NAME, title);
		values.put(FIELD_CREATED_AT_NAME, createdAt);
		values.put(FIELD_CHANGED_AT_NAME, changedAt);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице
	 * </p>
	 * 
	 * @param status
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationStatus status) {
		return replace(status.getUuid(), status.getTitle(),
				status.getCreatedAt(), status.getChangedAt());
	}

	public void saveItems(ArrayList<OperationStatus> list) {
		mDb.beginTransaction();
		for (OperationStatus item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}

}
