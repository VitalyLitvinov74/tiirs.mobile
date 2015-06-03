package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.Users;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Dmitriy Logachov
 * <p>Класс адаптера к таблице users</p>
 *
 */
public class UsersDBAdapter {
	public static final String TABLE_NAME = "users";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_NAME_NAME = "name";
	public static final String FIELD_LOGIN_NAME = "login";
	public static final String FIELD_PASS_NAME = "pass";
	public static final String FIELD_TYPE_NAME = "type";
	public static final String FIELD_TAGID_NAME = "tag_id";
	public static final String FIELD_ACTIVE_NAME = "active";
	
	String[] mColumns = {
			FIELD_UUID_NAME,
			FIELD_NAME_NAME,
			FIELD_LOGIN_NAME,
			FIELD_PASS_NAME,
			FIELD_TYPE_NAME,
			FIELD_TAGID_NAME,
			FIELD_ACTIVE_NAME};
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	
	/**
	 * @param context
	 * @return UsersDBAdapter
	 */
	public UsersDBAdapter(Context context) {
		super();
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public UsersDBAdapter open() {
		mDbHelper = new DatabaseHelper(mContext, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Закрываем базу данных
	 */
	public void close() {
		mDb.close();
		mDbHelper.close();
	}
	
	public Users getUserByLoginAndPass(String login, String pass) {
		
		Users user = null;
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_LOGIN_NAME + "=? AND " + FIELD_PASS_NAME + "=?", new String[]{login, pass}, null, null, null);
		if (cur.moveToFirst()) {
			user = new Users(cur.getString(cur.getColumnIndex(FIELD_UUID_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_NAME_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_LOGIN_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_PASS_NAME)),
					cur.getInt(cur.getColumnIndex(FIELD_TYPE_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_TAGID_NAME)),
					(cur.getInt(cur.getColumnIndex(FIELD_ACTIVE_NAME)) == 0 ? false : true));
		}
		return user;
	}

	public Users getUserByTagId(String tagId) {
		
		Users user = null;
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_TAGID_NAME + "=?", new String[]{tagId}, null, null, null);
		if (cur.moveToFirst()) {
			user = new Users(cur.getString(cur.getColumnIndex(FIELD_UUID_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_NAME_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_LOGIN_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_PASS_NAME)),
					cur.getInt(cur.getColumnIndex(FIELD_TYPE_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_TAGID_NAME)),
					(cur.getInt(cur.getColumnIndex(FIELD_ACTIVE_NAME)) == 0 ? false : true));
		}
		return user;
	}

	/**
	 * <p>Возвращает запись из таблицы users</p>
	 * @param uuid
	 * @return Cursor
	 */
	public Cursor getItem(String uuid) {
		return mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу users</p>
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long addItem(String uuid, String name, String login, String pass, int type) {
		// TODO так как в чистом виде мы записи в эту таблицу не будем добавлять, возможно этот метод не нужен
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_NAME_NAME, name);
		values.put(FIELD_LOGIN_NAME, login);
		values.put(FIELD_PASS_NAME, pass);
		values.put(FIELD_TYPE_NAME, type);
		id  = mDb.insert(TABLE_NAME, null, values);
		return id;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице users</p>
	 * @param uuid
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 * @param tag_id
	 * @param active
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replaceItem(String uuid, String name, String login, String pass, int type, String tag_id, boolean active) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_NAME_NAME, name);
		values.put(FIELD_LOGIN_NAME, login);
		values.put(FIELD_PASS_NAME, pass);
		values.put(FIELD_TYPE_NAME, type);
		values.put(FIELD_TAGID_NAME, tag_id);
		values.put(FIELD_ACTIVE_NAME, active);
		id  = mDb.replace(TABLE_NAME, null, values);
		return id;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице users</p>
	 * @param user
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replaceItem(Users user) {
		long id  = replaceItem(user.getUuid(), user.getName(), user.getLogin(), user.getPass(), user.getType(), user.getTag_id(), user.isActive());
		return id;
	}

	/**
	 * <p>Добавляет запись в таблицу users</p>
	 * @param user Users
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long addItem(Users user) {
		// TODO так как в чистом виде мы записи в эту таблицу не будем добавлять, возможно этот метод не нужен
		long id;
		ContentValues values = new ContentValues();
		values.put(UsersDBAdapter.FIELD_UUID_NAME, user.getUuid());
		values.put(FIELD_NAME_NAME, user.getName());
		values.put(FIELD_LOGIN_NAME, user.getLogin());
		values.put(FIELD_PASS_NAME, user.getPass());
		values.put(FIELD_TYPE_NAME, user.getType());
		id = mDb.insert(TABLE_NAME, null, values);
		return id;
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return boolean
	 */
	public boolean removeAllItems() {
		// TODO так как в чистом виде мы записи удалять не будем, возможно этот метод не нужен
		boolean isDeleted;
		isDeleted = mDb.delete(TABLE_NAME, null, null) > 0;
		return isDeleted;
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return boolean
	 */
	public boolean removeItem(String uuid) {
		// TODO так как в чистом виде мы записи удалять не будем, возможно этот метод не нужен
		boolean isDeleted;
		isDeleted = mDb.delete(TABLE_NAME, FIELD_UUID_NAME + "=?", new String[]{uuid}) > 0;
		return isDeleted;
	}
	
	/**
	 * <p>Метод обновляет уже существующую запись в базе.</p>
	 * @param id
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 * @return boolean
	 */
	public boolean updateItem(String uuid, String name, String login, String pass, int type) {
		// TODO так как в чистом виде мы записи обновлять не будем, возможно этот метод не нужен
		ContentValues values = new ContentValues();
		boolean isUpdated;
		
		values.put(FIELD_NAME_NAME, name);
		values.put(FIELD_LOGIN_NAME, login);
		values.put(FIELD_PASS_NAME, pass);
		values.put(FIELD_TYPE_NAME, type);
		
		isUpdated = mDb.update(TABLE_NAME, values, FIELD_UUID_NAME + "=?", new String[]{uuid}) > 0;
		return isUpdated;
	}
	
	public boolean updateItem(Users user) {
		// TODO так как в чистом виде мы записи обновлять не будем, возможно этот метод не нужен
		ContentValues values = new ContentValues();
		boolean isUpdated;
		
		values.put(FIELD_NAME_NAME, user.getName());
		values.put(FIELD_LOGIN_NAME, user.getLogin());
		values.put(FIELD_PASS_NAME, user.getPass());
		values.put(FIELD_TYPE_NAME, user.getType());
		
		isUpdated = mDb.update(TABLE_NAME, values, FIELD_UUID_NAME + "=?", new String[]{user.getUuid()}) > 0;
		return isUpdated;
	}
}
