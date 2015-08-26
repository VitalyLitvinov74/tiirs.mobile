/**
 * 
 */
package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Dmitriy Logachov
 *
 */
public class BaseDBAdapter {
	private DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDb;
	private final Context mContext;
	
	private String TABLE_NAME;

	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_CREATED_AT_NAME = "CreatedAt";
	public static final String FIELD_CHANGED_AT_NAME = "ChangedAt";



	/**
	 * 
	 */
	public BaseDBAdapter(Context context, String tableName) {
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
		TABLE_NAME = tableName;
	}

	/**
	 * Возвращает дату последней модификации среди всех элементов таблицы
	 * @return Количество милисекунд, если записей нет, null
	 */
	public Long getLastChangedAt() {
		Long changed = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, new String[]{ FIELD_CHANGED_AT_NAME }, null, null, FIELD_CHANGED_AT_NAME, null, FIELD_CHANGED_AT_NAME + " DESC" + " LIMIT 1");
		if (cursor.moveToFirst()) {
			changed = cursor.getLong(cursor.getColumnIndex(FIELD_CHANGED_AT_NAME));
		}
		return changed;
	}

}
