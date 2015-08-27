package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.utils.DataUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

public class TaskDBAdapter extends BaseDBAdapter {

	private TaskStatusDBAdapter taskstdbadapter;

	public static final String TABLE_NAME = "task";

	public static final String FIELD_USER_UUID_NAME = "users_uuid";
	public static final String FIELD_CLOSE_DATE_NAME = "close_date";
	public static final String FIELD_TASK_STATUS_UUID_NAME = "task_status_uuid";
	public static final String FIELD_ATTEMPT_SEND_DATE_NAME = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT_NAME = "attempt_count";
	public static final String FIELD_UPDATED_NAME = "updated";
	public static final String FIELD_TASK_NAME_NAME = "task_name";
	
	public static final class Projection {
		public static final String _ID = "_id";
		public static final String UUID = TABLE_NAME + '_' + "uuid";
		public static final String CREATED_AT = TABLE_NAME + '_' + "CreatedAt";
		public static final String CHANGED_AT = TABLE_NAME + '_' + "ChangedAt";
		
		public static final String USER_UUID = TABLE_NAME + '_' + "users_uuid";
		public static final String CLOSE_DATE = TABLE_NAME + '_' + "close_date";
		public static final String TASK_STATUS_UUID = TABLE_NAME + '_' + "task_status_uuid";
		public static final String TASK_NAME = TABLE_NAME + '_' + "task_name";
		
	}
	
	private static Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID_NAME) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID_NAME) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT_NAME) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT_NAME) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.USER_UUID, getFullName(TABLE_NAME, FIELD_USER_UUID_NAME) + " AS " + Projection.USER_UUID);
		mProjection.put(Projection.CLOSE_DATE, getFullName(TABLE_NAME, FIELD_CLOSE_DATE_NAME) + " AS " + Projection.CLOSE_DATE);
		mProjection.put(Projection.TASK_STATUS_UUID, getFullName(TABLE_NAME, FIELD_TASK_STATUS_UUID_NAME) + " AS " + Projection.TASK_STATUS_UUID);
		mProjection.put(Projection.TASK_NAME, getFullName(TABLE_NAME, FIELD_TASK_NAME_NAME) + " AS " + Projection.TASK_NAME);
	}


	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME,
			FIELD_USER_UUID_NAME, FIELD_CREATED_AT_NAME,
			FIELD_CHANGED_AT_NAME, FIELD_CLOSE_DATE_NAME,
			FIELD_TASK_STATUS_UUID_NAME, FIELD_ATTEMPT_SEND_DATE_NAME,
			FIELD_ATTEMPT_COUNT_NAME, FIELD_UPDATED_NAME, FIELD_TASK_NAME_NAME};

	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public TaskDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}

	/**
	 * Возвращает список всех нарядов пользователя
	 * 
	 * @param uuid
	 * @return
	 */
	public ArrayList<Task> getOrdersByUser(String uuid, String type, String sort) {
		ArrayList<Task> arrayList = new ArrayList<Task>();
		Cursor cursor;

		if (sort == null || sort.equals("")) sort=null;
		if (type == null || type.equals("")) {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=?", new String[]{ uuid }, null, null, sort);
		} else {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=? AND " + FIELD_TASK_STATUS_UUID_NAME + "=?", new String[]{uuid,type}, null, null, sort);
		}

		if (cursor.moveToFirst()) {
			do {
				arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}

		return arrayList;
	}

	/**
	 * Возвращает список нарядов по пользователю и статусу наряда
	 * 
	 * @param user_uuid
	 * @param status_uuid
	 * @return Если ни одной записи нет, возвращает null
	 */
	public ArrayList<Task> getTaskByUserAndStatus(String user_uuid,
			String status_uuid) {
		ArrayList<Task> list = null;
		Cursor cursor;

		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME
				+ "=? AND " + FIELD_TASK_STATUS_UUID_NAME + "=?", new String[] {
				user_uuid, status_uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			list = new ArrayList<Task>();
			do {
				list.add(getItem(cursor));
			} while (cursor.moveToNext());
		}
		return list;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param uuid
	 * @param users_uuid
	 * @param create_date
	 * @param modify_date
	 * @param close_date
	 * @param task_status_uuid
	 * @param attempt_send_date
	 * @param attempt_count
	 * @param updated
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(String uuid, String users_uuid, long create_date,
			long modify_date, long close_date, String task_status_uuid,
			long attempt_send_date, int attempt_count, boolean updated, 
			String task_name) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_USER_UUID_NAME, users_uuid);
		values.put(FIELD_CREATED_AT_NAME, create_date);
		values.put(FIELD_CHANGED_AT_NAME, modify_date);
		values.put(FIELD_CLOSE_DATE_NAME, close_date);
		values.put(FIELD_TASK_STATUS_UUID_NAME, task_status_uuid);
		values.put(FIELD_ATTEMPT_SEND_DATE_NAME, attempt_send_date);
		values.put(FIELD_ATTEMPT_COUNT_NAME, attempt_count);
		values.put(FIELD_UPDATED_NAME, updated == true ? 1 : 0);
		values.put(FIELD_TASK_NAME_NAME, task_name);		
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице</p>
	 * 
	 * @param task
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(Task task) {
		return replace(task.getUuid(), task.getUsers_uuid(),
				task.getCreatedAt(), task.getChangedAt(),
				task.getClose_date(), task.getTask_status_uuid(),
				task.getAttempt_send_date(), task.getAttempt_count(),
				task.isUpdated(),task.getTask_name());
	}

	public String getStatusNameByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		// taskstatus = new TaskStatus();
		// String getNameByUUID(String uuid)
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return taskstdbadapter.getNameByUUID(cur.getString(1));
		} else
			return "неизвестен";
	}
	/**
	 * <p>Возвращает дату завершения наряда</p>
	 * @param uuid
	 * @return format create date
	 */
	public String getCompleteTimeByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return DataUtils.getDate(cur.getLong(5), "dd-MM-yyyy hh:mm:ss");
		} else
			return "";
	}
	/**
	 * <p>Возвращает дату создания наряда</p>
	 * @param uuid
	 * @return format create date
	 */
	public String getCreateTimeByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return DataUtils.getDate(cur.getLong(5), "dd-MM-yyyy hh:mm:ss");
		} else
			return "";
	}
	
	/**
	 * Возвращает объект Task
	 * @param cursor
	 * @return
	 */
	public static Task getItem(Cursor cursor) {
		Task item = new Task();
		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		item.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		item.setUsers_uuid(cursor.getString(cursor.getColumnIndex(FIELD_USER_UUID_NAME)));
		item.setCreatedAt(cursor.getLong(cursor.getColumnIndex(FIELD_CREATED_AT_NAME)));
		item.setChangedAt(cursor.getLong(cursor.getColumnIndex(FIELD_CHANGED_AT_NAME)));
		item.setClose_date(cursor.getLong(cursor.getColumnIndex(FIELD_CLOSE_DATE_NAME)));
		item.setTask_status_uuid(cursor.getString(cursor.getColumnIndex(FIELD_TASK_STATUS_UUID_NAME)));
		item.setAttempt_send_date(cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE_NAME)));
		item.setAttempt_count(cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT_NAME)));
		item.setUpdated(cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED_NAME)) == 0 ? false : true);
		item.setTask_name(cursor.getString(cursor.getColumnIndex(FIELD_TASK_NAME_NAME)));
		return item;
	}
	
	/**
	 * Возвращает наряд по uuid
	 * @param uuid
	 * @return если наряда нет, возвращает null
	 */
	public Task getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[] { uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		} else {
			return null;
		}
	}

	/**
	 * Возвращает список нарядов по пользователю и с флагом updated=1
	 * @param uuid
	 * @return Если ни одной записи нет, возвращает null
	 */
	public ArrayList<Task> getTaskByUserAndUpdated(String uuid) {
		ArrayList<Task> list = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID_NAME + "=? AND " + FIELD_UPDATED_NAME + "=1", new String[] { uuid }, null, null, null);
		
		if (cursor.moveToFirst()) {
			list = new ArrayList<Task>();
			do {
				list.add(getItem(cursor));
			} while(cursor.moveToNext());
		}

		return list;
	}
	
	/**
	 * Возвращает наряд по uuid с флагом updated=1
	 * @param uuid
	 * @return Если записи нет, возвращает null
	 */
	public Task getTaskByUuidAndUpdated(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=? AND " + FIELD_UPDATED_NAME + "=1", new String[] { uuid }, null, null, null);
		
		if (cursor.moveToFirst()) {
				return getItem(cursor);
		} else {
			return null;
		}
	}
	
	/**
	 * Возвращает курсор для ListView списка нарядов
	 * @param userUuid
	 * @param statusUuid
	 * @param orderByField
	 * @return
	 */
	public Cursor getTaskWithInfo(String userUuid, String statusUuid,
			String orderByField) {
		
		Cursor cursor;
		String sortOrder = null;
		String paramArray[] = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Map<String, String> projection = new HashMap<String, String>();
		
		projection.putAll(mProjection);

		queryBuilder.setTables(getLeftJoinTables(TABLE_NAME,
				TaskStatusDBAdapter.TABLE_NAME, FIELD_TASK_STATUS_UUID_NAME,
				TaskStatusDBAdapter.FIELD_UUID_NAME, true));

		projection.putAll(TaskStatusDBAdapter.getProjection());
		queryBuilder.setProjectionMap(projection);

		if (statusUuid != null) {
			queryBuilder.appendWhere(FIELD_TASK_STATUS_UUID_NAME
					+ "=?");
			paramArray = new String[] { statusUuid };
		}

		if (orderByField != null) {
			sortOrder = orderByField;
		}

		cursor = queryBuilder.query(mDb, null, null, paramArray, null, null,
				sortOrder);

		return cursor;
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
