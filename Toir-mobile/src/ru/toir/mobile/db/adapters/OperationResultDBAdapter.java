package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.OperationResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class OperationResultDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "operation_result";

	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_OPERATION_TYPE_UUID_NAME = "operation_type_uuid";
	public static final String FIELD_TITLE_NAME = "title";
	public static final String FIELD_CREATED_AT_NAME = "CreatedAt";
	public static final String FIELD_CHANGED_AT_NAME = "ChangedAt";

	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME,
			FIELD_OPERATION_TYPE_UUID_NAME, FIELD_TITLE_NAME,
			FIELD_CREATED_AT_NAME, FIELD_CHANGED_AT_NAME };

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationResultDBAdapter(Context context) {
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
	}

	/**
	 * Возвращает результат выполнения операции по uuid
	 * 
	 * @param uuid
	 * @return
	 */
	public OperationResult getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
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
		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		item.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		item.setOperation_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_OPERATION_TYPE_UUID_NAME)));
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
		item.setCreatedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CREATED_AT_NAME)));
		item.setChangedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CHANGED_AT_NAME)));
		return item;
	}

	public ArrayList<OperationResult> getItems(String type_uuid) {
		ArrayList<OperationResult> list = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_OPERATION_TYPE_UUID_NAME
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
	 * <p>
	 * Добавляет/изменяет запись в таблице operation_type
	 * </p>
	 * 
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String operation_type_uuid, String title,
			long createdAt, long changedAt) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
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
	public long replace(OperationResult result) {
		return replace(result.getUuid(), result.getOperation_type_uuid(),
				result.getTitle(), result.getCreatedAt(), result.getChangedAt());
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
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(3);
		} else
			return "";
	}

	public void saveItems(ArrayList<OperationResult> list) {
		mDb.beginTransaction();
		for (OperationResult item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
	}
}
