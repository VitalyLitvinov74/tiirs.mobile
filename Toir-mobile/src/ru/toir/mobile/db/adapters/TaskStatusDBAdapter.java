package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.TaskStatus;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class TaskStatusDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "task_status";

	public static final String FIELD_TITLE = "title";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE) + " AS " + Projection.TITLE);
	}

	public static final String STATUS_UUID_CREATED = "1e9b4d73-044c-471b-a08d-26f36ebb22ba";
	public static final String STATUS_UUID_SENDED = "9f980db5-934c-4ddb-999a-04c6c3daca59";
	public static final String STATUS_UUID_RECIEVED = "9f980db5-934c-4ddb-999a-04c6c3daca59";
	public static final String STATUS_UUID_COMPLETED = "dc6dca37-2cc9-44da-aff9-19bf143e611a";
	public static final String STATUS_UUID_UNCOMPLETED = "363c08ec-89d9-47df-b7cf-63a05d56594c";
	// TODO add type archived
	public static final String STATUS_UUID_ARCHIVED = "363c08ec-89d9-47df-b7cf-63a05d56594d";

	String[] mColumns = { FIELD__ID, FIELD_UUID, FIELD_TITLE,
			FIELD_CREATED_AT, FIELD_CHANGED_AT };

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public TaskStatusDBAdapter(Context context) {
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

	public TaskStatus getItem(Cursor cursor) {
		TaskStatus item = new TaskStatus();
		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID)));
		item.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID)));
		item.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		item.setCreatedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CREATED_AT)));
		item.setChangedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CHANGED_AT)));
		return item;
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
			return cur.getString(2);
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
	public ArrayList<TaskStatus> getAllItems() {
		ArrayList<TaskStatus> arrayList = new ArrayList<TaskStatus>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				TaskStatus equip = getItem(cursor);
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
	 * Добавляет/изменяет запись в таблице task_status
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
	 * Добавляет/изменяет запись в таблице task_status
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(TaskStatus status) {
		return replace(status.getUuid(), status.getTitle(),
				status.getCreatedAt(), status.getChangedAt());
	}

	public void saveItems(ArrayList<TaskStatus> list) {
		mDb.beginTransaction();
		for (TaskStatus item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
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
