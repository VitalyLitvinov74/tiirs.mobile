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

/**
 * @author olejek
 * <p>Класс для работы с оборудованием</p>
 *
 */
public class EquipmentOperationDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TASK_UUID_NAME = "task_uuid";
	public static final String FIELD_EQUIPMENT_UUID_NAME = "equipment_uuid";
	public static final String FIELD_OPERATION_TYPE_UUID_NAME = "operation_type_uuid";
	public static final String FIELD_OPERATION_PATTERN_UUID_NAME = "operation_pattern_uuid";
	public static final String FIELD_OPERATION_STATUS_UUID_NAME = "operation_status_uuid";
		
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_TASK_UUID_NAME,
			FIELD_EQUIPMENT_UUID_NAME,
			FIELD_OPERATION_TYPE_UUID_NAME,
			FIELD_OPERATION_PATTERN_UUID_NAME,
			FIELD_OPERATION_STATUS_UUID_NAME};

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	
	/**
	 * @param context
	 * @return EquipmentOpDBAdapter
	 */
	public EquipmentOperationDBAdapter(Context context){
		mContext = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return EquipmentOpDBAdapter
	 * @throws SQLException
	 */
	public EquipmentOperationDBAdapter open() throws SQLException {
		mDbHelper = DatabaseHelper.getInstance(mContext);
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


	/**
	 * Возвращает список операций над оборудованием по наряду
	 * @param orderId
	 * @param operation_type
	 * @param critical_type
	 * @return
	 */
	public ArrayList<EquipmentOperation> getEquipsByOrderId(String orderId, String operation_type, int critical_type) {
		ArrayList<EquipmentOperation> arrayList = new ArrayList<EquipmentOperation>();
		Cursor cursor;

		// можем или отобрать все оборудование или только с нужными параметрами
		if (operation_type.equals("")) {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=?", new String[]{orderId}, null, null, null);
		} else {
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=? AND " + FIELD_OPERATION_TYPE_UUID_NAME + "=?", new String[]{orderId,operation_type}, null, null, null);
		}

		if (cursor.moveToFirst()) {
			do	{
				 arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}

		return arrayList;
	}

	/**
	 * <p>Возвращает все записи из таблицы equipment_operation</p>
	 * @return Cursor
	 */
	public Cursor getAllOpEquipment() {
		return mDb.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_UUID_NAME, FIELD_EQUIPMENT_UUID_NAME, FIELD_OPERATION_TYPE_UUID_NAME, FIELD_OPERATION_PATTERN_UUID_NAME}, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы equipment_operation</p>
	 * @param id
	 * @return Cursor
	 */
	public Cursor getOpEquipment(long uuid) {
		return mDb.query(TABLE_NAME, new String[]{FIELD_UUID_NAME, FIELD_TASK_UUID_NAME, FIELD_EQUIPMENT_UUID_NAME, FIELD_OPERATION_TYPE_UUID_NAME, FIELD_OPERATION_PATTERN_UUID_NAME}, FIELD_UUID_NAME + "=?", new String[]{String.valueOf(uuid)}, null, null, null);
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
		return mDb.insert(EquipmentOperationDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return int количество удалённых записей
	 */
	public int deleteOpEquipment(){
		return mDb.delete(TABLE_NAME, null, null);
	}

	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteOpEquipment(String uuid){
		return mDb.delete(TABLE_NAME, FIELD_UUID_NAME + "=?", new String[]{String.valueOf(uuid)});
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
		ContentValues values = new ContentValues();
		values.put(EquipmentOperationDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_TASK_UUID_NAME, task_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID_NAME, equipment_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_TYPE_UUID_NAME, operation_type_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_PATTERN_UUID_NAME, operation_pattern_uuid);
		values.put(EquipmentOperationDBAdapter.FIELD_OPERATION_STATUS_UUID_NAME, operation_status_uuid);
		return mDb.replace(EquipmentOperationDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице equipment_operation</p>
	 * @param operation
	 * @return
	 */
	public long replace(EquipmentOperation operation) {
		return replace(operation.getUuid(), operation.getTask_uuid(), operation.getEquipment_uuid(), operation.getOperation_type_uuid(), operation.getOperation_pattern_uuid(), operation.getOperation_status_uuid());
	}
	
	/**
	 * Возвращает операцию над оборудованием по uuid
	 * @param uuid
	 * @return если операции нет, возвращает null
	 */
	public EquipmentOperation getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		} else {
			return null;
		}
	}
	
	/**
	 * Возвращает операцию по наряду и оборудованию
	 * @param task_uuid
	 * @param equipment_uuid
	 * @return
	 */
	public ArrayList<EquipmentOperation> getItemsByTaskAndEquipment(String task_uuid, String equipment_uuid) {
		ArrayList<EquipmentOperation> arrayList = null;
		Cursor cursor;
		if (task_uuid.equals(""))
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_UUID_NAME + "=?", new String[]{equipment_uuid}, null, null, "_id DESC");
		else
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=? AND " + FIELD_EQUIPMENT_UUID_NAME + "=?", new String[]{task_uuid, equipment_uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			arrayList = new ArrayList<EquipmentOperation>();
			do	{
				 arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}

	/**
	 * Возвращает объект операции над оборудованием
	 * @param cursor
	 * @return
	 */
	public EquipmentOperation getItem(Cursor cursor) {
		EquipmentOperation equipmentOperation = new EquipmentOperation();
		equipmentOperation.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		equipmentOperation.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		equipmentOperation.setTask_uuid(cursor.getString(cursor.getColumnIndex(FIELD_TASK_UUID_NAME)));
		equipmentOperation.setEquipment_uuid(cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_UUID_NAME)));
		equipmentOperation.setOperation_type_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID_NAME)));
		equipmentOperation.setOperation_pattern_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_PATTERN_UUID_NAME)));
		equipmentOperation.setOperation_status_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_STATUS_UUID_NAME)));
		return equipmentOperation;
	}
	
	/**
	 * Возвращает список операций над оборудованием по наряду
	 * @param taskUuid
	 * @return если нет наряда, возвращает null
	 */
	public ArrayList<EquipmentOperation> getItems(String taskUuid) {
		ArrayList<EquipmentOperation> arrayList = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TASK_UUID_NAME + "=?", new String[]{taskUuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			arrayList = new ArrayList<EquipmentOperation>();
			do	{
				 arrayList.add(getItem(cursor));
			} while(cursor.moveToNext());
		}
		return arrayList;
	}

	
}
