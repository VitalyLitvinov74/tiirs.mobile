package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.Users;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @author Dmitriy Logachov
 * <p>Класс адаптера к таблице users</p>
 *
 */
public class UsersDBAdapter extends BaseAdapter {
	public static final String TABLE_NAME = "users";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_NAME_NAME = "name";
	public static final String FIELD_LOGIN_NAME = "login";
	public static final String FIELD_PASS_NAME = "pass";
	public static final String FIELD_TYPE_NAME = "type";
	public static final String FIELD_TAGID_NAME = "tag_id";
	public static final String FIELD_ACTIVE_NAME = "active";
	
	String[] columns = {
			FIELD_UUID_NAME,
			FIELD_NAME_NAME,
			FIELD_LOGIN_NAME,
			FIELD_PASS_NAME,
			FIELD_TYPE_NAME,
			FIELD_TAGID_NAME};
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;
	private Cursor cursor;
	
	/**
	 * @param context
	 * @return UsersDBAdapter
	 */
	public UsersDBAdapter(Context context) {
		super();
		this.context = context;
		init();
	}
	
	/**
	 * Получаем объект базы данных
	 * @return UsersDBAdapter
	 * @throws SQLException
	 */
	public UsersDBAdapter open() throws SQLException {
		this.dbHelper = new DatabaseHelper(this.context, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
		this.db = dbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * Закрываем базу данных
	 */
	public void close() {
		dbHelper.close();
	}
	
	public Users getUserByLoginAndPass(String login, String pass) {
		
		Users user = null;
		Cursor cur;
		cur = db.query(TABLE_NAME, columns, FIELD_LOGIN_NAME + "=? AND " + FIELD_PASS_NAME + "=?", new String[]{login, pass}, null, null, null);
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
		cur = db.query(TABLE_NAME, columns, FIELD_TAGID_NAME + "=?", new String[]{tagId}, null, null, null);
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
	 * <p>Возвращает все записи из таблицы users</p>
	 * @return Cursor
	 */
	public Cursor getAllItems() {
		return db.query(TABLE_NAME, columns, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы users</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getItem(String id) {
		return db.query(TABLE_NAME, columns, FIELD_UUID_NAME + "=?", new String[]{id}, null, null, null);
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
		long id;
		ContentValues values = new ContentValues();
		values.put(UsersDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(UsersDBAdapter.FIELD_NAME_NAME, name);
		values.put(UsersDBAdapter.FIELD_LOGIN_NAME, login);
		values.put(UsersDBAdapter.FIELD_PASS_NAME, pass);
		values.put(UsersDBAdapter.FIELD_TYPE_NAME, type);
		id  = db.insert(UsersDBAdapter.TABLE_NAME, null, values);
		refresh();
		return id;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице users</p>
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
		values.put(UsersDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(UsersDBAdapter.FIELD_NAME_NAME, name);
		values.put(UsersDBAdapter.FIELD_LOGIN_NAME, login);
		values.put(UsersDBAdapter.FIELD_PASS_NAME, pass);
		values.put(UsersDBAdapter.FIELD_TYPE_NAME, type);
		values.put(UsersDBAdapter.FIELD_TAGID_NAME, tag_id);
		values.put(UsersDBAdapter.FIELD_ACTIVE_NAME, active);
		id  = db.replace(UsersDBAdapter.TABLE_NAME, null, values);
		refresh();
		return id;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице users</p>
	 * @param user
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replaceItem(Users user) {
		long id  = replaceItem(user.getUuid(), user.getName(), user.getLogin(), user.getPass(), user.getType(), user.getTag_id(), user.isActive());
		refresh();
		return id;
	}

	/**
	 * <p>Добавляет запись в таблицу users</p>
	 * @param user Users
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long addItem(Users user) {
		long id;
		ContentValues values = new ContentValues();
		values.put(UsersDBAdapter.FIELD_UUID_NAME, user.getUuid());
		values.put(FIELD_NAME_NAME, user.getName());
		values.put(FIELD_LOGIN_NAME, user.getLogin());
		values.put(FIELD_PASS_NAME, user.getPass());
		values.put(FIELD_TYPE_NAME, user.getType());
		id = db.insert(TABLE_NAME, null, values);
		refresh();
		return id;
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return boolean
	 */
	public boolean removeAllItems() {
		boolean isDeleted;
		isDeleted = db.delete(TABLE_NAME, null, null) > 0;
		refresh();
		return isDeleted;
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return boolean
	 */
	public boolean removeItem(String uuid) {
		boolean isDeleted;
		isDeleted = db.delete(TABLE_NAME, FIELD_UUID_NAME + "=?", new String[]{uuid}) > 0;
		refresh();
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
		ContentValues values = new ContentValues();
		boolean isUpdated;
		
		values.put(FIELD_NAME_NAME, name);
		values.put(FIELD_LOGIN_NAME, login);
		values.put(FIELD_PASS_NAME, pass);
		values.put(FIELD_TYPE_NAME, type);
		
		isUpdated = db.update(TABLE_NAME, values, FIELD_UUID_NAME + "=?", new String[]{uuid}) > 0;
		return isUpdated;
	}
	
	public boolean updateItem(Users user) {
		ContentValues values = new ContentValues();
		boolean isUpdated;
		
		values.put(FIELD_NAME_NAME, user.getName());
		values.put(FIELD_LOGIN_NAME, user.getLogin());
		values.put(FIELD_PASS_NAME, user.getPass());
		values.put(FIELD_TYPE_NAME, user.getType());
		
		isUpdated = db.update(TABLE_NAME, values, FIELD_UUID_NAME + "=?", new String[]{user.getUuid()}) > 0;
		return isUpdated;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Users getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return null;
	}
	
	private void refresh() {
		cursor = getAllItems();
		notifyDataSetChanged();
	}
	
	public void onDestroy() {
		dbHelper.close();
	}
	
	private void init() {
		open();
		cursor = getAllItems();
	}
}
