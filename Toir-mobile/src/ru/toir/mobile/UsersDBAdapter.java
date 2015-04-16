package ru.toir.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author koputo
 * <p>Класс адаптера к таблице users</p>
 *
 */
public class UsersDBAdapter extends BaseAdapter {
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

	/**
	 * <p>Возвращает все записи из таблицы users</p>
	 * @return Cursor
	 */
	public Cursor getAllItems() {
		String[] columns = {
				FIELD_ID_NAME,
				FIELD_NAME_NAME,
				FIELD_LOGIN_NAME,
				FIELD_PASS_NAME,
				FIELD_TYPE_NAME};
		return db.query(TABLE_NAME, columns, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы users</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getItem(long id) {
		String[] columns = {
				FIELD_ID_NAME,
				FIELD_NAME_NAME,
				FIELD_LOGIN_NAME,
				FIELD_PASS_NAME,
				FIELD_TYPE_NAME};
		return db.query(TABLE_NAME, columns, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу users</p>
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long addItem(String name, String login, String pass, int type) {
		long id;
		ContentValues values = new ContentValues();
		values.put(UsersDBAdapter.FIELD_NAME_NAME, name);
		values.put(UsersDBAdapter.FIELD_LOGIN_NAME, login);
		values.put(UsersDBAdapter.FIELD_PASS_NAME, pass);
		values.put(UsersDBAdapter.FIELD_TYPE_NAME, type);
		id  = db.insert(UsersDBAdapter.TABLE_NAME, null, values);
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
	public boolean removeItem(long id) {
		boolean isDeleted;
		isDeleted = db.delete(TABLE_NAME, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)}) > 0;
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
	public boolean updateItem(long id, String name, String login, String pass, int type) {
		ContentValues values = new ContentValues();
		boolean isUpdated;
		
		values.put(FIELD_NAME_NAME, name);
		values.put(FIELD_LOGIN_NAME, login);
		values.put(FIELD_PASS_NAME, pass);
		values.put(FIELD_TYPE_NAME, type);
		
		isUpdated = db.update(TABLE_NAME, values, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)}) > 0;
		return isUpdated;
	}
	
	public boolean updateItem(Users user) {
		ContentValues values = new ContentValues();
		boolean isUpdated;
		
		values.put(FIELD_NAME_NAME, user.getName());
		values.put(FIELD_LOGIN_NAME, user.getLogin());
		values.put(FIELD_PASS_NAME, user.getPass());
		values.put(FIELD_TYPE_NAME, user.getType());
		
		isUpdated = db.update(TABLE_NAME, values, FIELD_ID_NAME + "=?", new String[]{String.valueOf(user.get_id())}) > 0;
		return isUpdated;
	}

	@Override
	public int getCount() {
		return cursor.getCount();
	}

	@Override
	public Users getItem(int position) {
		if (cursor.moveToPosition(position)) {
			Users user = new Users(cursor.getLong(FIELD_ID_COLUMN),
					cursor.getString(FIELD_NAME_COLUMN),
					cursor.getString(FIELD_LOGIN_COLUMN),
					cursor.getString(FIELD_PASS_COLUMN),
					cursor.getInt(FIELD_TYPE_COLUMN));
			return user;
		} else {
			throw new CursorIndexOutOfBoundsException("Cant move cursor to postion");
		}
	}

	@Override
	public long getItemId(int position) {
		Users usersOnPosition = getItem(position);
		return usersOnPosition.get_id();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TableRow row;
		// список задников для строк в списке
		int[] background = {R.drawable.fancy_list_background1, R.drawable.fancy_list_background2};
		
		if (convertView == null) {
			row = (TableRow) View.inflate(context, R.layout.user_item, null);
		} else {
			row = (TableRow) convertView;
		}
		
		// достаём из row textedit'ы и устанавливаем для них значения
		TextView tv;
		
		tv = (TextView)row.getChildAt(FIELD_ID_COLUMN);
		tv.setText(String.valueOf(getItem(position).get_id()));
		
		tv = (TextView)row.getChildAt(FIELD_NAME_COLUMN);
		tv.setText(getItem(position).getName());
		
		tv = (TextView)row.getChildAt(FIELD_LOGIN_COLUMN);
		tv.setText(getItem(position).getLogin());
		
		tv = (TextView)row.getChildAt(FIELD_PASS_COLUMN);
		tv.setText(getItem(position).getPass());
		
		tv = (TextView)row.getChildAt(FIELD_TYPE_COLUMN);
		tv.setText(String.valueOf(getItem(position).getType()));
		
		// устанавливаем задник в зависимости от чёт/нечет положения строки в списке
		row.setBackgroundResource(background[position % background.length]);
		
		return row;
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
