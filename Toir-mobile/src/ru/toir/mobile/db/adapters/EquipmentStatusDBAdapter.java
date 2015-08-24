package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.EquipmentStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EquipmentStatusDBAdapter {
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "equipment_status";

	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TITLE_NAME = "title";
	public static final String FIELD_TYPE_NAME = "type";
	public static final String FIELD_CREATED_AT_NAME = "CreatedAt";
	public static final String FIELD_CHANGED_AT_NAME = "ChangedAt";

	// public static final String STATUS_UUID_CREATED =
	// "1e9b4d73-044c-471b-a08d-26f36ebb22ba";

	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME, FIELD_TITLE_NAME,
			FIELD_TYPE_NAME, FIELD_CREATED_AT_NAME, FIELD_CHANGED_AT_NAME };

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public EquipmentStatusDBAdapter(Context context) {
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
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

	/**
	 * 
	 * @param UUID
	 * @return
	 */
	public String getNameByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
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
		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		item.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)));
		item.setType(cursor.getInt(cursor.getColumnIndex(FIELD_TYPE_NAME)));
		item.setCreatedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CREATED_AT_NAME)));
		item.setChangedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CHANGED_AT_NAME)));
		return item;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице equipment_status
	 * </p>
	 * 
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String title, int type, long createdAt,
			long changedAt) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_TITLE_NAME, title);
		values.put(FIELD_TYPE_NAME, type);
		values.put(FIELD_CREATED_AT_NAME, createdAt);
		values.put(FIELD_CHANGED_AT_NAME, changedAt);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(EquipmentStatus status) {
		return replace(status.getUuid(), status.getTitle(), status.getType(),
				status.getCreatedAt(), status.getChangedAt());
	}

	public void saveItems(ArrayList<EquipmentStatus> list) {
		mDb.beginTransaction();
		for (EquipmentStatus item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
	}

}
