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

	public static final String FIELD_USER_UUID = "users_uuid";
	public static final String FIELD_CLOSE_DATE = "close_date";
	public static final String FIELD_TASK_STATUS_UUID = "task_status_uuid";
	public static final String FIELD_ATTEMPT_SEND_DATE = "attempt_send_date";
	public static final String FIELD_ATTEMPT_COUNT = "attempt_count";
	public static final String FIELD_UPDATED = "updated";
	public static final String FIELD_TASK_NAME = "task_name";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String USER_UUID = TABLE_NAME + '_' + FIELD_USER_UUID;
		public static final String CLOSE_DATE = TABLE_NAME + '_' + FIELD_CLOSE_DATE;
		public static final String TASK_STATUS_UUID = TABLE_NAME + '_' + FIELD_TASK_STATUS_UUID;
		public static final String ATTEMPT_SEND_DATE = TABLE_NAME + '_' + FIELD_ATTEMPT_SEND_DATE;
		public static final String ATTEMPT_COUNT = TABLE_NAME + '_' + FIELD_ATTEMPT_COUNT;
		public static final String UPDATED = TABLE_NAME + '_' + FIELD_UPDATED;
		public static final String TASK_NAME = TABLE_NAME + '_' + FIELD_TASK_NAME;
		
	}
	
	private static Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.USER_UUID, getFullName(TABLE_NAME, FIELD_USER_UUID) + " AS " + Projection.USER_UUID);
		mProjection.put(Projection.CLOSE_DATE, getFullName(TABLE_NAME, FIELD_CLOSE_DATE) + " AS " + Projection.CLOSE_DATE);
		mProjection.put(Projection.TASK_STATUS_UUID, getFullName(TABLE_NAME, FIELD_TASK_STATUS_UUID) + " AS " + Projection.TASK_STATUS_UUID);
		mProjection.put(Projection.ATTEMPT_SEND_DATE, getFullName(TABLE_NAME, FIELD_ATTEMPT_SEND_DATE) + " AS " + Projection.ATTEMPT_SEND_DATE);
		mProjection.put(Projection.ATTEMPT_COUNT, getFullName(TABLE_NAME, FIELD_ATTEMPT_COUNT) + " AS " + Projection.ATTEMPT_COUNT);
		mProjection.put(Projection.UPDATED, getFullName(TABLE_NAME, FIELD_UPDATED) + " AS " + Projection.UPDATED);
		mProjection.put(Projection.TASK_NAME, getFullName(TABLE_NAME, FIELD_TASK_NAME) + " AS " + Projection.TASK_NAME);
	}

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
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID + "=?", new String[]{ uuid }, null, null, sort);
		} else {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID + "=? AND " + FIELD_TASK_STATUS_UUID + "=?", new String[]{uuid,type}, null, null, sort);
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

		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID
				+ "=? AND " + FIELD_TASK_STATUS_UUID + "=?", new String[] {
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
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(Task item) {
		long id;
		ContentValues values = putCommonFields(item);
		
		values.put(FIELD_USER_UUID, item.getUsers_uuid());
		values.put(FIELD_CLOSE_DATE, item.getClose_date());
		values.put(FIELD_TASK_STATUS_UUID, item.getTask_status_uuid());
		values.put(FIELD_ATTEMPT_SEND_DATE, item.getAttempt_send_date());
		values.put(FIELD_ATTEMPT_COUNT, item.getAttempt_count());
		values.put(FIELD_UPDATED, item.isUpdated() == true ? 1 : 0);
		values.put(FIELD_TASK_NAME, item.getTask_name());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	public String getStatusNameByUUID(String uuid) {
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
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
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
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
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
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
	public Task getItem(Cursor cursor) {
		Task item = new Task();
		
		getItem(cursor, item);
		item.setUsers_uuid(cursor.getString(cursor.getColumnIndex(FIELD_USER_UUID)));
		item.setClose_date(cursor.getLong(cursor.getColumnIndex(FIELD_CLOSE_DATE)));
		item.setTask_status_uuid(cursor.getString(cursor.getColumnIndex(FIELD_TASK_STATUS_UUID)));
		item.setAttempt_send_date(cursor.getLong(cursor.getColumnIndex(FIELD_ATTEMPT_SEND_DATE)));
		item.setAttempt_count(cursor.getInt(cursor.getColumnIndex(FIELD_ATTEMPT_COUNT)));
		item.setUpdated(cursor.getInt(cursor.getColumnIndex(FIELD_UPDATED)) == 0 ? false : true);
		item.setTask_name(cursor.getString(cursor.getColumnIndex(FIELD_TASK_NAME)));
		return item;
	}
	
	/**
	 * Возвращает наряд по uuid
	 * @param uuid
	 * @return если наряда нет, возвращает null
	 */
	public Task getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[] { uuid }, null, null, null);
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_UUID + "=? AND " + FIELD_UPDATED + "=1", new String[] { uuid }, null, null, null);
		
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=? AND " + FIELD_UPDATED + "=1", new String[] { uuid }, null, null, null);
		
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
				TaskStatusDBAdapter.TABLE_NAME, FIELD_TASK_STATUS_UUID,
				TaskStatusDBAdapter.FIELD_UUID, true));

		projection.putAll(TaskStatusDBAdapter.getProjection());
		queryBuilder.setProjectionMap(projection);

		if (statusUuid != null) {
			queryBuilder.appendWhere(FIELD_TASK_STATUS_UUID
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

	public boolean saveItems(ArrayList<Task> list) {

		for (Task item : list) {
			if (replace(item) == -1) {
				return false;
			}
		}

		return true;
	}

}
