package ru.toir.mobile;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author koputo
 * <p>Класс реализует пользователей</p>
 *
 */
public class Users {
	public static final String TABLE_NAME = "users";
	private long id;
	public static final String ID_NAME = "_id";
	public static final int ID_COLUMN = 0;
	private String name;
	public static final String NAME_NAME = "name";
	public static final int NAME_COLUMN = 1;
	private String login;
	public static final String LOGIN_NAME = "login";
	public static final int LOGIN_COLUMN = 2;
	private String pass;
	public static final String PASS_NAME = "pass";
	public static final int PASS_COLUMN = 3;
	private int type;
	public static final String TYPE_NAME = "type";
	public static final int TYPE_COLUMN = 4;
	
	private TOiRDBAdapter adapter;
	
	/**
	 * @return
	 */
	public Users(TOiRDBAdapter adapter) {
		this.adapter = adapter;
		id = 0;
		name = "";
		login = "";
		pass = "";
		type = 0;
	}

	/**
	 * @param id
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 */
	public Users(TOiRDBAdapter adapter, long id, String name, String login, String pass, int type) {
		this.adapter = adapter;
		this.id = id;
		this.name = name;
		this.login = login;
		this.pass = pass;
		this.type = type;
	}

	/**
	 * <p>Создаёт обект Users, используя данные из базы</p>
	 * @param id
	 */
	public Users(TOiRDBAdapter adapter, long id) {
		this.adapter = adapter;
		Cursor user = adapter.read(TABLE_NAME, new String[]{ID_NAME, NAME_NAME, LOGIN_NAME, PASS_NAME, TYPE_NAME},
				ID_NAME + "=?",	new String[]{String.valueOf(id)}, null, null, null);
		if (user.moveToFirst()) {
			this.id = id;
			this.name = user.getString(NAME_COLUMN);
			this.login = user.getString(LOGIN_COLUMN);
			this.pass = user.getString(PASS_COLUMN);
			this.type = user.getInt(TYPE_COLUMN);
		}
	}

	/**
	 * <p>Возвращает все записи из таблицы users</p>
	 * @return Cursor
	 */
	public Cursor getUsers() {
		return adapter.read(TABLE_NAME, new String[]{ID_NAME, NAME_NAME, LOGIN_NAME, PASS_NAME, TYPE_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы users</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getUsers(long id) {
		return adapter.read(TABLE_NAME, new String[]{ID_NAME, NAME_NAME, LOGIN_NAME, PASS_NAME, TYPE_NAME}, ID_NAME + "=?", new String[]{String.valueOf(id)}, null, null, null);
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
		values.put(Users.NAME_NAME, name);
		values.put(Users.LOGIN_NAME, login);
		values.put(Users.PASS_NAME, pass);
		values.put(Users.TYPE_NAME, type);
		return adapter.insert(Users.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteUsers(long id) {
		return adapter.delete(TABLE_NAME, ID_NAME + "=?", new String[]{String.valueOf(id)});
	}
	
	/**
	 * <p>Метод добавляет новую или обновляет уже существующую запись в базе.</p>
	 * 
	 */
	public long saveUsers() {
		ContentValues values = new ContentValues();
		
		values.put(Users.NAME_NAME, name);
		values.put(Users.LOGIN_NAME, login);
		values.put(Users.PASS_NAME, pass);
		values.put(Users.TYPE_NAME, type);
		
		if (this.id == 0) {
			// возвращаем id
			return this.id = adapter.insert(TABLE_NAME, null, values);
		} else {
			// возвращаем количество обновлённых записей (1 - всё нормально, 0 - запись не найдена)
			return adapter.update(TABLE_NAME, values, ID_NAME + "=?", new String[]{String.valueOf(this.id)});
		}
	}
	
	/**
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
}
