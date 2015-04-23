package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author olejek
 * <p>Класс для работы с документацией</p>
 */
public class DocsDBAdapter {
	public static final String TABLE_NAME = "docs";
	public static final String FIELD_ID_NAME = "_id";
	public static final int FIELD_ID_COLUMN = 0;
	public static final String FIELD_NAME_NAME = "name";
	public static final int FIELD_NAME_COLUMN = 1;
	public static final String FIELD_LINK_NAME = "link";
	public static final int FIELD_LINK_COLUMN = 2;
	public static final String FIELD_TYPE_NAME = "type";
	public static final int FIELD_TYPE_COLUMN = 3;
	public static final String FIELD_EQUIPMENT_NAME = "equipment";
	public static final int FIELD_EQUIPMENT_COLUMN = 4;
	
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;
	
	/**
	 * @param context
	 * @return DocsDBAdapter
	 */
	public DocsDBAdapter(Context context){
		this.context = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return DocsDBAdapter
	 * @throws SQLException
	 */
	public DocsDBAdapter open() throws SQLException {
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
	 * <p>Возвращает все записи из таблицы Docs</p>
	 * @return Cursor
	 */
	public Cursor getAllDocs() {
		return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_NAME_NAME, FIELD_LINK_NAME, FIELD_TYPE_NAME, FIELD_EQUIPMENT_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы Docs</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getDocs(long id) {
		return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_NAME_NAME, FIELD_LINK_NAME, FIELD_TYPE_NAME, FIELD_EQUIPMENT_NAME}, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу Docs</p>
	 * @param String name;
	 * @param String link;
	 * @param int type;
	 * @param int equipment;
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertDocs(String name, String link, int type, int equipment){
		ContentValues values = new ContentValues();
		values.put(DocsDBAdapter.FIELD_NAME_NAME, name);
		values.put(DocsDBAdapter.FIELD_LINK_NAME, link);
		values.put(DocsDBAdapter.FIELD_TYPE_NAME, type);
		values.put(DocsDBAdapter.FIELD_EQUIPMENT_NAME, equipment);
		return db.insert(DocsDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return int количество удалённых записей
	 */
	public int deleteDocs(){
		return db.delete(TABLE_NAME, null, null);
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteDocs(long id){
		return db.delete(TABLE_NAME, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)});
	}
	
	/**
	 * <p>Обновляет запись в таблице Docs</p>
	 * @param  long id;
	 * @param String name;
	 * @param String link;
	 * @param int type;
	 * @param int equipment;
	 * @return long количество обновлённых записей
	 */
	public long updateDocs(long id, String name, String link, int type, int equipment){
		ContentValues values = new ContentValues();
		values.put(DocsDBAdapter.FIELD_NAME_NAME, name);
		values.put(DocsDBAdapter.FIELD_LINK_NAME, link);
		values.put(DocsDBAdapter.FIELD_TYPE_NAME, type);
		values.put(DocsDBAdapter.FIELD_EQUIPMENT_NAME, equipment);
		return db.update(DocsDBAdapter.TABLE_NAME, values, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)});
	}
}
