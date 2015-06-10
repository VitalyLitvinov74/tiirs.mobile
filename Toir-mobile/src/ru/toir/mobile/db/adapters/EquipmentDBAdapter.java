package ru.toir.mobile.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.Equipment;

/**
 * @author olejek
 * <p>Класс для работы с оборудованием</p>
 *
 */
public class EquipmentDBAdapter {
		
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "equipment";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TITLE_NAME = "title";
	public static final String FIELD_EQUIPMENT_TYPE_UUID_NAME = "equipment_type_uuid";
	public static final String FIELD_CRITICAL_TYPE_UUID_NAME = "critical_type_uuid";
	public static final String FIELD_START_DATE_NAME = "start_date";
	public static final String FIELD_LOCATION_NAME = "location";
	public static final String FIELD_TAG_ID_NAME = "tag_id";
	
	private static String mColumns[] = {
		FIELD_UUID_NAME,
		FIELD_TITLE_NAME,
		FIELD_EQUIPMENT_TYPE_UUID_NAME,
		FIELD_CRITICAL_TYPE_UUID_NAME,
		FIELD_START_DATE_NAME,
		FIELD_LOCATION_NAME,
		FIELD_TAG_ID_NAME};
	
	/**
	 * @param context
	 * @return EquipmentDBAdapter
	 */
	public EquipmentDBAdapter(Context context){
		this.mContext = context;
	}
	
	/**
	 * Получаем объект базы данных
	 * @return EquipmentDBAdapter
	 * @throws SQLException
	 */
	public EquipmentDBAdapter open() throws SQLException {
		this.mDbHelper = new DatabaseHelper(this.mContext, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
		this.mDb = mDbHelper.getWritableDatabase();
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
	 * <p>Возвращает все записи из таблицы equipment</p>
	 * @return Cursor
	 */
	public Cursor getAllItems() {
		return mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
	}
	
	/**
	 * <p>Возвращает запись из таблицы equipment</p>
	 * @param uuid
	 * @return Cursor
	 */
	public Cursor getItem(String uuid) {
		return mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице equipments</p>
	 * @param uuid
	 * @param title
	 * @param equipment_type_uuid
	 * @param critical_type_uuid
	 * @param start_date
	 * @param location
	 * @param tag_id
	 * @return
	 */
	public long replace(String uuid, String title, String equipment_type_uuid, String critical_type_uuid, long start_date, String location, String tag_id){
		// TODO нужно сделать контроль, выполнилось выражение или нет
		ContentValues values = new ContentValues();
		values.put(EquipmentDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentDBAdapter.FIELD_TITLE_NAME, title);
		values.put(EquipmentDBAdapter.FIELD_EQUIPMENT_TYPE_UUID_NAME, equipment_type_uuid);
		values.put(EquipmentDBAdapter.FIELD_CRITICAL_TYPE_UUID_NAME, critical_type_uuid);
		values.put(EquipmentDBAdapter.FIELD_START_DATE_NAME, start_date);
		values.put(EquipmentDBAdapter.FIELD_LOCATION_NAME, location);
		values.put(EquipmentDBAdapter.FIELD_TAG_ID_NAME, tag_id);
		return mDb.replace(EquipmentDBAdapter.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Добавляет/заменяет запись в таблице equipments</p>
	 * @param equipment
	 * @return
	 */
	public long replace(Equipment equipment) {
		return replace(equipment.getUuid(), equipment.getTitle(), equipment.getEquipment_type_uuid(), equipment.getCritical_type_uuid(), equipment.getStart_date(), equipment.getLocation(), equipment.getTag_id());
	}

}
