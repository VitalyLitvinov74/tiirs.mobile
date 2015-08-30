package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.MeasureType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MeasureTypeDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "measure_type";

	public static final String FIELD_TITLE = "title";

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public MeasureTypeDBAdapter(Context context) {
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

	public MeasureType getItem(Cursor cursor) {
		MeasureType item = new MeasureType();
		
		getItem(cursor, item);
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
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
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(MeasureType item) {
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
	public ArrayList<MeasureType> getAllItems() {
		ArrayList<MeasureType> arrayList = new ArrayList<MeasureType>();
		Cursor cursor;
		// можем или отобрать все оборудование или только определенного типа
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				MeasureType equip = getItem(cursor);
				arrayList.add(equip);
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		return arrayList;
	}

	public void saveItems(ArrayList<MeasureType> list) {
		mDb.beginTransaction();
		for (MeasureType item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}

}
