package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.DocumentationType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DocumentationTypeDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "documentation_type";

	public static final String FIELD_TITLE = "title";

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public DocumentationTypeDBAdapter(Context context) {
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
	
	public DocumentationType getItem(Cursor cursor) {
		DocumentationType type = new DocumentationType();

		getItem(cursor, type);
		type.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
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
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(DocumentationType item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_TITLE, item.getTitle());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Возвращает все записи из таблицы documentation type
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<DocumentationType> getAllItems() {
		ArrayList<DocumentationType> arrayList = new ArrayList<DocumentationType>();
		Cursor cursor;
		// можем или отобрать все оборудование или только определенного типа
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

	public void saveItems(ArrayList<DocumentationType> array) {
		mDb.beginTransaction();
		for (DocumentationType item : array) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}
	
}
