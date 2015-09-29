package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.OperationStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OperationStatusDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "operation_status";

	public static final String FIELD_TITLE = "title";
	
	public static final class Projection {
		public static final String _ID = "_id";
		public static final String UUID = TABLE_NAME + '_' + "uuid";
		public static final String CREATED_AT = TABLE_NAME + '_' + "CreatedAt";
		public static final String CHANGED_AT = TABLE_NAME + '_' + "ChangedAt";
		
		public static final String TITLE = TABLE_NAME + '_' + "title";
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
	 * Класс констант статуса операции
	 * @author Dmitriy Logachov
	 *
	 */
	public class Status {
		public static final String NEW = "18d3d5d4-336f-4b25-ba2b-00a6c7d5eb6c";
		public static final String COMPLETE = "626fc9e9-9f1f-4de7-937d-74dad54ed751";
		public static final String NOTCOMPLETE = "0f733a22-b65a-4d96-af86-34f7e6a62b0b";
	}

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
	public OperationStatus getItem(Cursor cursor) {
		OperationStatus item = new OperationStatus();
		
		getItem(cursor, item);
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
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
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationStatus item) {
		long id;
		
		ContentValues values = putCommonFields(item);
		values.put(FIELD_TITLE, item.getTitle());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	public boolean saveItems(ArrayList<OperationStatus> list) {

		for (OperationStatus item : list) {
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
