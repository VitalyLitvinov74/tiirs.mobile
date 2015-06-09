package ru.toir.mobile.db.adapters;

import java.util.UUID;

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
public class EquipmentOpDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final int FIELD_UUID_COLUMN = 0;
	public static final String FIELD_TASK_NAME = "task_uuid";
	public static final int FIELD_TASK_COLUMN = 1;
	public static final String FIELD_EQUIPMENT_NAME = "equipment_uuid";
	public static final int FIELD_EQUIPMENT_COLUMN = 2;
	public static final String FIELD_OPERATION_NAME = "operation_type_uuid";
	public static final int FIELD_OPERATION_COLUMN = 3;
	public static final String FIELD_PATTERN_NAME = "operation_pattern_uuid";
	public static final int FIELD_PATTERN_COLUMN = 4;

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;
	
	/**
	 * @param context
	 * @return EquipmentOpDBAdapter
	 */
	public EquipmentOpDBAdapter(Context context){
		this.context = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return EquipmentOpDBAdapter
	 * @throws SQLException
	 */
	public EquipmentOpDBAdapter open() throws SQLException {
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
	 * <p>Возвращает все записи из таблицы equipment_operation</p>
	 * @return Cursor
	 */
	public Cursor getAllOpEquipment() {
		return db.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_NAME, FIELD_EQUIPMENT_NAME, FIELD_OPERATION_NAME, FIELD_PATTERN_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы equipment_operation</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getOpEquipment(long uuid) {
		return db.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_NAME, FIELD_EQUIPMENT_NAME, FIELD_OPERATION_NAME, FIELD_PATTERN_NAME}, FIELD_UUID_NAME + "=?", new String[]{String.valueOf(uuid)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу equipments_operation</p>
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertOpEquipment(String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid){
		ContentValues values = new ContentValues();
		String uuid = UUID.randomUUID().toString();
		values.put(EquipmentOpDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentOpDBAdapter.FIELD_TASK_NAME, task_uuid);
		values.put(EquipmentOpDBAdapter.FIELD_EQUIPMENT_NAME, equipment_uuid);
		values.put(EquipmentOpDBAdapter.FIELD_OPERATION_NAME, operation_type_uuid);
		values.put(EquipmentOpDBAdapter.FIELD_PATTERN_NAME, operation_pattern_uuid);
		return db.insert(EquipmentOpDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return int количество удалённых записей
	 */
	public int deleteOpEquipment(){
		return db.delete(TABLE_NAME, null, null);
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteOpEquipment(String uuid){
		return db.delete(TABLE_NAME, FIELD_UUID_NAME + "=?", new String[]{String.valueOf(uuid)});
	}	
}
