package ru.toir.mobile.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;

/**
 * @author olejek
 * <p>Класс для работы с оборудованием</p>
 *
 */
public class EquipmentDBAdapter {
		
	public static final String TABLE_NAME = "equipment";
	
	public static final String FIELD_ID_NAME = "_id";
	public static final int FIELD_ID_COLUMN = 0;
	public static final String FIELD_NAME_NAME = "name";
	public static final int FIELD_NAME_COLUMN = 1;
	public static final String FIELD_LINK_NAME = "link";
	public static final int FIELD_LINK_COLUMN = 2;
	public static final String FIELD_TYPE_NAME = "type";
	public static final int FIELD_TYPE_COLUMN = 3;
	public static final String FIELD_YEAR_NAME = "year";
	public static final int FIELD_YEAR_COLUMN = 4;
	public static final String FIELD_MANUFACTURER_NAME = "manufacturer";
	public static final int FIELD_MANUFACTURER_COLUMN = 5;	
	public static final String FIELD_PHOTO_NAME = "photo";
	public static final int FIELD_PHOTO_COLUMN = 6;	
	public static final String FIELD_PRIORITY_NAME = "priority";
	public static final int FIELD_PRIORITY_COLUMN = 7;	

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;
	
	/**
	 * @param context
	 * @return EquipmentDBAdapter
	 */
	public EquipmentDBAdapter(Context context){
		this.context = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return EquipmentDBAdapter
	 * @throws SQLException
	 */
	public EquipmentDBAdapter open() throws SQLException {
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
	 * <p>Возвращает все записи из таблицы equipment</p>
	 * @return Cursor
	 */
	public Cursor getAllEquipment() {
		return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_NAME_NAME, FIELD_LINK_NAME, FIELD_TYPE_NAME, FIELD_YEAR_NAME, FIELD_MANUFACTURER_NAME, FIELD_PHOTO_NAME, FIELD_PRIORITY_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы equipment</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getEquipment(long id) {
		return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_NAME_NAME, FIELD_LINK_NAME, FIELD_TYPE_NAME, FIELD_YEAR_NAME, FIELD_MANUFACTURER_NAME, FIELD_PHOTO_NAME, FIELD_PRIORITY_NAME}, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу equipments</p>
	 * @param String name;
	 * @param String link;
	 * @param int type;
	 * @param int year;
	 * @param String manufacturer;
	 * @param String photo;
	 * @param int priority;
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertEquipment(String name, String link, int type, int year, String manufacturer, String photo, int priority){
		ContentValues values = new ContentValues();
		values.put(EquipmentDBAdapter.FIELD_NAME_NAME, name);
		values.put(EquipmentDBAdapter.FIELD_LINK_NAME, link);
		values.put(EquipmentDBAdapter.FIELD_TYPE_NAME, type);
		values.put(EquipmentDBAdapter.FIELD_YEAR_NAME, year);
		values.put(EquipmentDBAdapter.FIELD_MANUFACTURER_NAME, manufacturer);
		values.put(EquipmentDBAdapter.FIELD_PHOTO_NAME, photo);
		values.put(EquipmentDBAdapter.FIELD_PRIORITY_NAME, priority);
		return db.insert(EquipmentDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return int количество удалённых записей
	 */
	public int deleteEquipment(){
		return db.delete(TABLE_NAME, null, null);
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteEquipment(long id){
		return db.delete(TABLE_NAME, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)});
	}
	
	/**
	 * <p>Обновляет запись в таблице equipments</p>
	 * @param  long id;
	 * @param String name;
	 * @param String link;
	 * @param int type;
	 * @param int year;
	 * @param String manufacturer;
	 * @param String photo;
	 * @param int priority;
	 * @return long количество обновлённых записей
	 */
	public long updateEquipment(long id, String name, String link, int type, int year, String manufacturer, String photo, int priority){
		ContentValues values = new ContentValues();
		values.put(EquipmentDBAdapter.FIELD_NAME_NAME, name);
		values.put(EquipmentDBAdapter.FIELD_LINK_NAME, link);
		values.put(EquipmentDBAdapter.FIELD_TYPE_NAME, type);
		values.put(EquipmentDBAdapter.FIELD_YEAR_NAME, year);
		values.put(EquipmentDBAdapter.FIELD_MANUFACTURER_NAME, manufacturer);
		values.put(EquipmentDBAdapter.FIELD_PHOTO_NAME, photo);
		values.put(EquipmentDBAdapter.FIELD_PRIORITY_NAME, priority);
		return db.update(EquipmentDBAdapter.TABLE_NAME, values, FIELD_ID_NAME + "=?", new String[]{String.valueOf(id)});
	}

}
