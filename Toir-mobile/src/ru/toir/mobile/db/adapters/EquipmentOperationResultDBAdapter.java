package ru.toir.mobile.db.adapters;

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
public class EquipmentOperationResultDBAdapter {
		
	public static final String TABLE_NAME = "equipment_operation_result";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_EQUIPMENT_OPERATION_NAME = "equipment_operation_uuid";
	public static final String FIELD_START_DATE_NAME = "start_date";
	public static final String FIELD_END_DATE_NAME = "end_date";
	public static final String FIELD_OPERATION_RESULT_NAME = "operation_result_uuid";
		
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_EQUIPMENT_OPERATION_NAME,
			FIELD_START_DATE_NAME,
			FIELD_END_DATE_NAME,
			FIELD_OPERATION_RESULT_NAME};

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private final Context context;
	
	/**
	 * @param context
	 * @return EquipmentOpDBAdapter
	 */
	public EquipmentOperationResultDBAdapter(Context context){
		this.context = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return EquipmentOperationResultDBAdapter
	 * @throws SQLException
	 */
	public EquipmentOperationResultDBAdapter open() throws SQLException {
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
	 * <p>Возвращает по uuid</p>
	 * @param uuid
	 */
	public Long getStartDateByUUID(String uuid) {
		Cursor cursor;
		cursor = db.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getLong(cursor.getColumnIndex(FIELD_START_DATE_NAME));			 
			}
		else return 0l;
	}		

	/**
	 * <p>Возвращает по uuid</p>
	 * @param uuid
	 */
	public Long getEndDateByUUID(String uuid) {
		Cursor cursor;
		cursor = db.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getLong(cursor.getColumnIndex(FIELD_END_DATE_NAME));
			}
		else return 0l;
	}

	/**
	 * <p>Возвращает по uuid</p>
	 * @param uuid
	 */
	public String getOperationResultByUUID(String uuid) {
		Cursor cursor;
		cursor = db.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);				
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 return cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_RESULT_NAME));
			}
		else return "неизвестна";
	}			
}
