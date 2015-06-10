package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.EquipmentOp;
import ru.toir.mobile.TOiRDBAdapter;

/**
 * @author olejek
 * <p>Класс для работы с оборудованием</p>
 *
 */
public class EquipmentOpDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TASK_NAME = "task_uuid";
	public static final String FIELD_EQUIPMENT_NAME = "equipment_uuid";
	public static final String FIELD_OPERATION_NAME = "operation_type_uuid";
	public static final String FIELD_PATTERN_NAME = "operation_pattern_uuid";
	public static final String FIELD_STATUS_NAME = "operation_status_uuid";
		
	String[] mColumns = {
			FIELD_UUID_NAME,
			FIELD_TASK_NAME,
			FIELD_EQUIPMENT_NAME,
			FIELD_OPERATION_NAME,
			FIELD_PATTERN_NAME,
			FIELD_STATUS_NAME};

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

	public ArrayList<EquipmentOp> getEquipsByOrderId(String orderId, String status) {
		ArrayList<EquipmentOp> arrayList = new ArrayList<EquipmentOp>();
		Cursor cursor;
		// можем или отобрать все оборудование или только с нужным статусом 
		cursor = db.query(TABLE_NAME, mColumns, FIELD_TASK_NAME + "=?", new String[]{orderId}, null, null, null);
		if (cursor.getCount()>0)
			{
			cursor.moveToFirst();
			while (true)		
				{
				 EquipmentOp equipOp;
				 equipOp = new EquipmentOp(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					 cursor.getString(cursor.getColumnIndex(FIELD_TASK_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_PATTERN_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_STATUS_NAME)));
				 arrayList.add(equipOp);
				 if (cursor.isLast()) break;
				 cursor.moveToNext();
				}
			}
		return arrayList;
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
