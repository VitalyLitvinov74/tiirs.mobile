package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.OperationResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OperationResultDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "operation_result";

	public static final String FIELD_OPERATION_TYPE_UUID = "operation_type_uuid";
	public static final String FIELD_TITLE = "title";

	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String OPERATION_TYPE_UUID = TABLE_NAME + '_' + FIELD_OPERATION_TYPE_UUID;
		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.OPERATION_TYPE_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_TYPE_UUID) + " AS " + Projection.OPERATION_TYPE_UUID);
		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE) + " AS " + Projection.TITLE);
	}

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationResultDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}

	/**
	 * Возвращает результат выполнения операции по uuid
	 * 
	 * @param uuid
	 * @return
	 */
	public OperationResult getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}

	/**
	 * Возвращает результат выполнения операции
	 * 
	 * @param cursor
	 * @return
	 */
	public OperationResult getItem(Cursor cursor) {
		OperationResult item = new OperationResult();

		getItem(cursor, item);
		item.setOperation_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_OPERATION_TYPE_UUID)));
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		return item;
	}

	public ArrayList<OperationResult> getItems(String type_uuid) {
		ArrayList<OperationResult> list = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_TYPE_UUID
				+ "=?", new String[] { type_uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			list = new ArrayList<OperationResult>();
			do {
				list.add(getItem(cursor));
			} while (cursor.moveToNext());
		}
		return list;
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
	public ArrayList<OperationResult> getAllItems() {
		ArrayList<OperationResult> arrayList = new ArrayList<OperationResult>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				OperationResult equip = getItem(cursor);
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
	public long replace(OperationResult item) {
		long id;
		
		ContentValues values = putCommonFields(item);
		values.put(FIELD_OPERATION_TYPE_UUID, item.getOperation_type_uuid());
		values.put(FIELD_TITLE, item.getTitle());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Возвращает расшифровку статуса из таблицы
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public String getNameByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(3);
		} else
			return "";
	}

	public void saveItems(ArrayList<OperationResult> list) {

		for (OperationResult item : list) {
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
