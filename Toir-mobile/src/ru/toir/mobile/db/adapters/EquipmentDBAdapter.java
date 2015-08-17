package ru.toir.mobile.db.adapters;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.Equipment;

/**
 * @author olejek
 *         <p>
 *         Класс для работы с оборудованием
 *         </p>
 * 
 */
public class EquipmentDBAdapter {

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	public static final String TABLE_NAME = "equipment";

	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_TITLE_NAME = "title";
	public static final String FIELD_EQUIPMENT_TYPE_UUID_NAME = "equipment_type_uuid";
	public static final String FIELD_CRITICAL_TYPE_UUID_NAME = "critical_type_uuid";
	public static final String FIELD_START_DATE_NAME = "start_date";
	public static final String FIELD_LATITUDE_NAME = "latitude";
	public static final String FIELD_LONGITUDE_NAME = "longitude";
	public static final String FIELD_TAG_ID_NAME = "tag_id";
	public static final String FIELD_IMG_NAME = "img";
	public static final String FIELD_EQUIPMENT_STATUS_UUID_NAME = "equipment_status_uuid";

	private static String mColumns[] = { FIELD__ID_NAME, FIELD_UUID_NAME,
			FIELD_TITLE_NAME, FIELD_EQUIPMENT_TYPE_UUID_NAME,
			FIELD_CRITICAL_TYPE_UUID_NAME, FIELD_START_DATE_NAME,
			FIELD_LATITUDE_NAME, FIELD_LONGITUDE_NAME, FIELD_TAG_ID_NAME,
			FIELD_IMG_NAME, FIELD_EQUIPMENT_STATUS_UUID_NAME };

	/**
	 * @param context
	 * @return EquipmentDBAdapter
	 */
	public EquipmentDBAdapter(Context context) {
		this.mContext = context;
	}

	/**
	 * Получаем объект базы данных
	 * 
	 * @return EquipmentDBAdapter
	 * @throws SQLException
	 */
	public EquipmentDBAdapter open() throws SQLException {
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Закрываем базу данных
	 */
	public void close() {
	}

	/**
	 * <p>
	 * Возвращает все записи из таблицы equipment
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<Equipment> getAllItems(String type, String critical_type) {
		ArrayList<Equipment> arrayList = new ArrayList<Equipment>();
		Cursor cursor;
		// можем или отобрать все оборудование или только определенного типа
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);

		if (type.equals("") && !critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns,
					FIELD_CRITICAL_TYPE_UUID_NAME + "=?",
					new String[] { critical_type }, null, null, null);
		}
		if (!type.equals("") && critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns,
					FIELD_EQUIPMENT_TYPE_UUID_NAME + "=?",
					new String[] { type }, null, null, null);
		}
		if (!type.equals("") && !critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns,
					FIELD_CRITICAL_TYPE_UUID_NAME + "=? AND "
							+ FIELD_EQUIPMENT_TYPE_UUID_NAME + "=?",
					new String[] { critical_type, type }, null, null, null);
		}
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				arrayList.add(getItem(cursor));
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		cursor.close();
		return arrayList;
	}

	/**
	 * <p>
	 * Возвращает запись из таблицы equipment
	 * </p>
	 * 
	 * @param uuid
	 * @return Equipment
	 */
	public Equipment getItem(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}

	/**
	 * <p>
	 * Возвращает Equipment
	 * </p>
	 * 
	 * @param uuid
	 * @return Equipment
	 */
	public Equipment getItem(Cursor cursor) {
		Equipment equipment = new Equipment();
		equipment.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		equipment.setUuid(cursor.getString(cursor
				.getColumnIndex(FIELD_UUID_NAME)));
		equipment.setTitle(cursor.getString(cursor
				.getColumnIndex(FIELD_TITLE_NAME)));
		equipment.setEquipment_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_TYPE_UUID_NAME)));
		equipment.setCritical_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_CRITICAL_TYPE_UUID_NAME)));
		equipment.setStart_date(cursor.getLong(cursor
				.getColumnIndex(FIELD_START_DATE_NAME)));
		equipment.setLatitude(cursor.getFloat(cursor
				.getColumnIndex(FIELD_LATITUDE_NAME)));
		equipment.setLongitude(cursor.getFloat(cursor
				.getColumnIndex(FIELD_LONGITUDE_NAME)));
		equipment.setTag_id(cursor.getString(cursor
				.getColumnIndex(FIELD_TAG_ID_NAME)));
		equipment
				.setImg(cursor.getString(cursor.getColumnIndex(FIELD_IMG_NAME)));
		equipment.setEquipmentStatus_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_STATUS_UUID_NAME)));
		return equipment;
	}

	/**
	 * <p>
	 * Добавляет/заменяет запись в таблице equipments
	 * </p>
	 * 
	 * @param uuid
	 * @param title
	 * @param equipment_type_uuid
	 * @param critical_type_uuid
	 * @param start_date
	 * @param location
	 * @param tag_id
	 * @param img
	 * @param status_uuid
	 * @param type
	 * @return
	 */
	public long replace(String uuid, String title, String equipment_type_uuid,
			String critical_type_uuid, long start_date, float latitude,
			float longitude, String tag_id, String img, String status_uuid) {
		ContentValues values = new ContentValues();
		values.put(EquipmentDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentDBAdapter.FIELD_TITLE_NAME, title);
		values.put(EquipmentDBAdapter.FIELD_EQUIPMENT_TYPE_UUID_NAME,
				equipment_type_uuid);
		values.put(EquipmentDBAdapter.FIELD_CRITICAL_TYPE_UUID_NAME,
				critical_type_uuid);
		values.put(EquipmentDBAdapter.FIELD_START_DATE_NAME, start_date);
		values.put(EquipmentDBAdapter.FIELD_LATITUDE_NAME, latitude);
		values.put(EquipmentDBAdapter.FIELD_LONGITUDE_NAME, longitude);
		values.put(EquipmentDBAdapter.FIELD_TAG_ID_NAME, tag_id);
		values.put(EquipmentDBAdapter.FIELD_IMG_NAME, img);
		values.put(EquipmentDBAdapter.FIELD_EQUIPMENT_STATUS_UUID_NAME, status_uuid);
		return mDb.replace(EquipmentDBAdapter.TABLE_NAME, null, values);
	}

	/**
	 * <p>
	 * Добавляет/заменяет запись в таблице equipments
	 * </p>
	 * 
	 * @param equipment
	 * @return
	 */
	public long replace(Equipment equipment) {
		return replace(equipment.getUuid(), equipment.getTitle(),
				equipment.getEquipment_type_uuid(),
				equipment.getCritical_type_uuid(), equipment.getStart_date(),
				equipment.getLatitude(), equipment.getLongitude(),
				equipment.getTag_id(), equipment.getImg(),
				equipment.getEquipmentStatus_uuid());
	}

	/**
	 * <p>
	 * Возвращает название оборудования по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getEquipsNameByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_TITLE_NAME));
		} else
			return "неизвестно";
	}

	/**
	 * <p>
	 * Возвращает тип оборудования по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getEquipsTypeByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_EQUIPMENT_TYPE_UUID_NAME));
		} else
			return "неизвестно";
	}

	/**
	 * <p>
	 * Возвращает тип оборудования по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getCriticalByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_CRITICAL_TYPE_UUID_NAME));
		} else
			return "неизвестен";
	}

	/**
	 * <p>
	 * Возвращает координаты по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getLocationByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getFloat(cursor.getColumnIndex(FIELD_LATITUDE_NAME))
					+ " "
					+ cursor.getFloat(cursor
							.getColumnIndex(FIELD_LONGITUDE_NAME));
		} else
			return "неизвестны";
	}

	/**
	 * <p>
	 * Возвращает фото по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getImgByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_IMG_NAME));
		} else
			return "/data/img/img.png";
	}

	/**
	 * <p>
	 * Возвращает tag_id по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getTagIDByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getColumnCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_TAG_ID_NAME));
		} else
			return "0-0-0";
	}

}
