package ru.toir.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author koputo
 * <p>Класс адаптера к таблице users</p>
 *
 */
public class UsersDBAdapter {
	public static final String TABLE_NAME = "users";
	
	public static final String FIELD_ID_NAME = "_id";
	public static final int FIELD_ID_COLUMN = 0;
	public static final String FIELD_NAME_NAME = "name";
	public static final int FIELD_NAME_COLUMN = 1;
	public static final String FIELD_LOGIN_NAME = "login";
	public static final int FIELD_LOGIN_COLUMN = 2;
	public static final String FIELD_PASS_NAME = "pass";
	public static final int FIELD_PASS_COLUMN = 3;
	public static final String FIELD_TYPE_NAME = "type";
	public static final int FIELD_TYPE_COLUMN = 4;
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;
	
	/**
	 * @param context
	 * @return UsersDBAdapter
	 */
	public UsersDBAdapter(Context context) {
		this.context = context;
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

	/**
	 * <p>Возвращает все записи из таблицы users</p>
	 * @return Cursor
	 */
	public Cursor getAllUsers() {
		return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_NAME_NAME, FIELD_LOGIN_NAME, FIELD_PASS_NAME, FIELD_TYPE_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы users</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getUsers(long id) {
		return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_NAME_NAME, FIELD_LOGIN_NAME, FIELD_PASS_NAME, FIELD_TYPE_NAME}, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу users</p>
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertUsers(String name, String login, String pass, int type) {
		ContentValues values = new ContentValues();
		values.put(UsersDBAdapter.FIELD_NAME_NAME, name);
		values.put(UsersDBAdapter.FIELD_LOGIN_NAME, login);
		values.put(UsersDBAdapter.FIELD_PASS_NAME, pass);
		values.put(UsersDBAdapter.FIELD_TYPE_NAME, type);
		return db.insert(UsersDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return int количество удалённых записей
	 */
	public int deleteUsers() {
		return db.delete(TABLE_NAME, null, null);
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteUsers(long id) {
		return db.delete(TABLE_NAME, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)});
	}
	
	/**
	 * <p>Метод обновляет уже существующую запись в базе.</p>
	 * @param id
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 * @return long
	 */
	public int updateUsers(long id, String name, String login, String pass, int type) {
		ContentValues values = new ContentValues();
		
		values.put(UsersDBAdapter.FIELD_NAME_NAME, name);
		values.put(UsersDBAdapter.FIELD_LOGIN_NAME, login);
		values.put(UsersDBAdapter.FIELD_PASS_NAME, pass);
		values.put(UsersDBAdapter.FIELD_TYPE_NAME, type);
		
		return db.update(TABLE_NAME, values, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)});
	}
}
