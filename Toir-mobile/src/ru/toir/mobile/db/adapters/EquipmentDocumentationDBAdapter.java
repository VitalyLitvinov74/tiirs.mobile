/**
 * 
 */
package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author olejek
 *
 */
public class EquipmentDocumentationDBAdapter {

	public static final String TABLE_NAME = "equipment_documentation";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_EQUIPMENT_UUID_NAME = "equipment_uuid";
	public static final String FIELD_DOCUMENTATION_TYPE_UUID_NAME = "documentation_type_uuid";
	public static final String FIELD_TITLE_NAME = "title";
	public static final String FIELD_PATH_NAME = "path";
		
	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_EQUIPMENT_UUID_NAME,
			FIELD_DOCUMENTATION_TYPE_UUID_NAME,
			FIELD_TITLE_NAME,
			FIELD_PATH_NAME};

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	

	/**
	 * 
	 * @param context
	 */
	public EquipmentDocumentationDBAdapter(Context context) {
		mContext = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return
	 * @throws SQLException
	 */
	public EquipmentDocumentationDBAdapter open() throws SQLException {
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
	 * <p>Возвращает запись из таблицы equipment_documentation</p>
	 * @param uuid
	 * @return
	 */
	public EquipmentDocumentation getItem(String uuid) {
		return getEquipmentDocumentation(mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null));
	}
	
	/**
	 * <p>Возвращает объект equipment_documentation</p>
	 * @param cursor
	 * @return
	 */
	public static EquipmentDocumentation getEquipmentDocumentation(Cursor cursor) {
		if (cursor.moveToFirst()) {
			EquipmentDocumentation item = null;
			item = new EquipmentDocumentation(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_DOCUMENTATION_TYPE_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_PATH_NAME)));
			return item;
		}
		return null;
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
	public long replace(String uuid, String equipment_uuid, String documentation_type_uuid, String title, String path) {
		ContentValues values = new ContentValues();
		values.put(EquipmentDocumentationDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentDocumentationDBAdapter.FIELD_EQUIPMENT_UUID_NAME, equipment_uuid);
		values.put(EquipmentDocumentationDBAdapter.FIELD_DOCUMENTATION_TYPE_UUID_NAME, documentation_type_uuid);
		values.put(EquipmentDocumentationDBAdapter.FIELD_TITLE_NAME, title);
		values.put(EquipmentDocumentationDBAdapter.FIELD_PATH_NAME, path);
		return mDb.replace(TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице</p>
	 * @param document
	 * @return
	 */
	public long replace(EquipmentDocumentation document) {
		return replace(document.getUuid(), document.getEquipment_uuid(), document.getDocumentation_type_uuid(), document.getTitle(), document.getPath());
	}

	/**
	 * <p>Возвращает все записи из таблицы equipment documentation</p>
	 * @return list
	 */
	public ArrayList<EquipmentDocumentation> getAllItems(String type) {
		ArrayList<EquipmentDocumentation> arrayList = new ArrayList<EquipmentDocumentation>();
		Cursor cursor;
		// можем или отобрать всю документацию или только определенного типа
		if (type.equals(""))
			cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
		else
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_DOCUMENTATION_TYPE_UUID_NAME + "=?", new String[]{type}, null, null, null);
		
		if (cursor.getCount()>0)
			{
			 cursor.moveToFirst();
			 while (true)		
			 	{			 
				 EquipmentDocumentation equip = new EquipmentDocumentation(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_EQUIPMENT_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_DOCUMENTATION_TYPE_UUID_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME)),
					cursor.getString(cursor.getColumnIndex(FIELD_PATH_NAME)));
				 	arrayList.add(equip);
				 	if (cursor.isLast()) break;
				 	cursor.moveToNext();
			 	}
			}
		return arrayList;
	}

}
