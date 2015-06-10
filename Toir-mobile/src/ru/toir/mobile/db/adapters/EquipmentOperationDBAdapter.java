package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.UUID;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.TOiRDBAdapter;

/**
 * @author olejek
 * <p>Класс для работы с оборудованием</p>
 *
 */
public class EquipmentOperationDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TASK_UUID_NAME = "task_uuid";
	public static final String FIELD_EQUIPMENT_UUID_NAME = "equipment_uuid";
	public static final String FIELD_OPERATION_TYPE_UUID_NAME = "operation_type_uuid";
	public static final String FIELD_OPERATION_PATTERN_UUID_NAME = "operation_pattern_uuid";
	public static final String FIELD_OPERATION_STATUS_UUID_NAME = "operation_status_uuid";
		
	String[] mColumns = {
			FIELD_UUID_NAME,
			FIELD_TASK_UUID_NAME,
			FIELD_EQUIPMENT_UUID_NAME,
			FIELD_OPERATION_TYPE_UUID_NAME,
			FIELD_OPERATION_PATTERN_UUID_NAME,
			FIELD_OPERATION_STATUS_UUID_NAME};

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;
	
	/**
	 * @param context
	 * @return EquipmentOpDBAdapter
	 */
	public EquipmentOperationDBAdapter(Context context){
		this.context = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return EquipmentOpDBAdapter
	 * @throws SQLException
	 */
	public EquipmentOperationDBAdapter open() throws SQLException {
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

	public ArrayList<EquipmentOperation> getEquipsByOrderId(String orderId, String status) {
		ArrayList<EquipmentOperation> arrayList = new ArrayList<EquipmentOperation>();
		Cursor cursor;

		// можем или отобрать все оборудование или только с нужным статусом 
		cursor = db.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=?", new String[]{orderId}, null, null, null);		
		cursor.moveToFirst();
		while (true)		
			{
			 EquipmentOperation equipOp = new EquipmentOperation(
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TASK_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_STATUS_UUID_NAME)));
			 arrayList.add(equipOp);
			 if (cursor.isLast()) break;
			 cursor.moveToNext();
		}
		return arrayList;
	}
	
	/**
	 * <p>Возвращает все записи из таблицы equipment_operation</p>
	 * @return Cursor
	 */
	public Cursor getAllOpEquipment() {
		return db.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_UUID_NAME, FIELD_EQUIPMENT_UUID_NAME, FIELD_OPERATION_TYPE_UUID_NAME, FIELD_OPERATION_PATTERN_UUID_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы equipment_operation</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getOpEquipment(long uuid) {
		return db.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_UUID_NAME, FIELD_EQUIPMENT_UUID_NAME, FIELD_OPERATION_TYPE_UUID_NAME, FIELD_OPERATION_PATTERN_UUID_NAME}, FIELD_UUID_NAME + "=?", new String[]{String.valueOf(uuid)}, null, null, null);
	}
	
	/**
	 * <p>Добавляет запись в таблицу equipments_operation</p>
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertOpEquipment(String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid){
		ContentValues values = new ContentValues();
		String uuid = UUID.randomUUID().toString();
		values.put(EquipmentOperationDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_TASK_UUID_NAME, task_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID_NAME, equipment_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID_NAME, operation_pattern_uuid);
		return db.insert(EquipmentOperationDBAdapter.TABLE_NAME, null, values);
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
	
	/**
	 * <p>Добавляет/заменяет запись в таблице equipment_operation</p>
	 * @param uuid
	 * @param task_uuid
	 * @param equipment_uuid
	 * @param operation_type_uuid
	 * @param operation_pattern_uuid
	 * @param operation_status_uuid
	 * @return
	 */
	public long replace(String uuid, String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid, String operation_status_uuid) {
		// TODO нужно сделать контроль, выполнилось выражение или нет
		ContentValues values = new ContentValues();
		values.put(EquipmentOperationDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_TASK_UUID_NAME, task_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID_NAME, equipment_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID_NAME, operation_pattern_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_STATUS_UUID_NAME, operation_status_uuid);
		return db.replace(EquipmentOperationDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице equipment_operation</p>
	 * @param operation
	 * @return
	 */
	public long replace(EquipmentOperation operation) {
		return replace(operation.getUuid(), operation.getTask_uuid(), operation.getEquipment_uuid(), operation.getOperation_type_uuid(), operation.getOperation_pattern_uuid(), operation.getOperation_status_uuid());
	}
}
