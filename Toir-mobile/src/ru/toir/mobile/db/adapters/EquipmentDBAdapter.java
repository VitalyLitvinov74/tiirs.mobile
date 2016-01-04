package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import ru.toir.mobile.db.tables.Equipment;

/**
 * @author olejek
 *         <p>
 *         Класс для работы с оборудованием
 *         </p>
 * 
 */
public class EquipmentDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "equipment";

	public static final String FIELD_TITLE = "title";
	public static final String FIELD_EQUIPMENT_TYPE_UUID = "equipment_type_uuid";
	public static final String FIELD_CRITICAL_TYPE_UUID = "critical_type_uuid";
	public static final String FIELD_START_DATE = "start_date";
	public static final String FIELD_LATITUDE = "latitude";
	public static final String FIELD_LONGITUDE = "longitude";
	public static final String FIELD_TAG_ID = "tag_id";
	public static final String FIELD_IMAGE = "image";
	public static final String FIELD_EQUIPMENT_STATUS_UUID = "equipment_status_uuid";
	public static final String FIELD_INVENTORY_NUMBER = "inventory_number";
	public static final String FIELD_LOCATION = "location";

	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_'
				+ FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_'
				+ FIELD_CHANGED_AT;

		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
		public static final String EQUIPMENT_TYPE_UUID = TABLE_NAME + '_'
				+ FIELD_EQUIPMENT_TYPE_UUID;
		public static final String CRITICAL_TYPE_UUID = TABLE_NAME + '_'
				+ FIELD_CRITICAL_TYPE_UUID;
		public static final String START_DATE = TABLE_NAME + '_'
				+ FIELD_START_DATE;
		public static final String LATITUDE = TABLE_NAME + '_' + FIELD_LATITUDE;
		public static final String LONGITUDE = TABLE_NAME + '_'
				+ FIELD_LONGITUDE;
		public static final String TAG_ID = TABLE_NAME + '_' + FIELD_TAG_ID;
		public static final String IMAGE = TABLE_NAME + '_' + FIELD_IMAGE;
		public static final String EQUIPMENT_STATUS_UUID = TABLE_NAME + '_'
				+ FIELD_EQUIPMENT_STATUS_UUID;
		public static final String INVENTORY_NUMBER = TABLE_NAME + '_'
				+ FIELD_INVENTORY_NUMBER;
		public static final String LOCATION = TABLE_NAME + '_' + FIELD_LOCATION;

	}

	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID)
				+ " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID)
				+ " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT,
				getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS "
						+ Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT,
				getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS "
						+ Projection.CHANGED_AT);

		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE)
				+ " AS " + Projection.TITLE);
		mProjection.put(Projection.EQUIPMENT_TYPE_UUID,
				getFullName(TABLE_NAME, FIELD_EQUIPMENT_TYPE_UUID) + " AS "
						+ Projection.EQUIPMENT_TYPE_UUID);
		mProjection.put(Projection.CRITICAL_TYPE_UUID,
				getFullName(TABLE_NAME, FIELD_CRITICAL_TYPE_UUID) + " AS "
						+ Projection.CRITICAL_TYPE_UUID);
		mProjection.put(Projection.START_DATE,
				getFullName(TABLE_NAME, FIELD_START_DATE) + " AS "
						+ Projection.START_DATE);
		mProjection.put(Projection.LATITUDE,
				getFullName(TABLE_NAME, FIELD_LATITUDE) + " AS "
						+ Projection.LATITUDE);
		mProjection.put(Projection.LONGITUDE,
				getFullName(TABLE_NAME, FIELD_LONGITUDE) + " AS "
						+ Projection.LONGITUDE);
		mProjection.put(Projection.TAG_ID,
				getFullName(TABLE_NAME, FIELD_TAG_ID) + " AS "
						+ Projection.TAG_ID);
		mProjection.put(Projection.IMAGE, getFullName(TABLE_NAME, FIELD_IMAGE)
				+ " AS " + Projection.IMAGE);
		mProjection.put(Projection.EQUIPMENT_STATUS_UUID,
				getFullName(TABLE_NAME, FIELD_EQUIPMENT_STATUS_UUID) + " AS "
						+ Projection.EQUIPMENT_STATUS_UUID);
		mProjection.put(Projection.INVENTORY_NUMBER,
				getFullName(TABLE_NAME, FIELD_INVENTORY_NUMBER) + " AS "
						+ Projection.INVENTORY_NUMBER);
		mProjection.put(Projection.LOCATION,
				getFullName(TABLE_NAME, FIELD_LOCATION) + " AS "
						+ Projection.LOCATION);

	}

	/**
	 * @param context
	 * @return EquipmentDBAdapter
	 */
	public EquipmentDBAdapter(Context context) {
		super(context, TABLE_NAME);
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
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_CRITICAL_TYPE_UUID
					+ "=?", new String[] { critical_type }, null, null, null);
		}
		if (!type.equals("") && critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_EQUIPMENT_TYPE_UUID
					+ "=?", new String[] { type }, null, null, null);
		}
		if (!type.equals("") && !critical_type.equals("")) {
			cursor.close();
			cursor = mDb.query(TABLE_NAME, mColumns, FIELD_CRITICAL_TYPE_UUID
					+ "=? AND " + FIELD_EQUIPMENT_TYPE_UUID + "=?",
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}

	/**
	 * <p>
	 * Возвращает запись из таблицы equipment
	 * </p>
	 * 
	 * @param uuid
	 * @return Equipment
	 */
	public Cursor getItemCursor(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		return cursor;
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
		getItem(cursor, equipment);
		equipment
				.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		equipment.setEquipment_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_TYPE_UUID)));
		equipment.setCritical_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_CRITICAL_TYPE_UUID)));
		equipment.setStart_date(cursor.getLong(cursor
				.getColumnIndex(FIELD_START_DATE)));
		equipment.setLatitude(cursor.getFloat(cursor
				.getColumnIndex(FIELD_LATITUDE)));
		equipment.setLongitude(cursor.getFloat(cursor
				.getColumnIndex(FIELD_LONGITUDE)));
		equipment.setTag_id(cursor.getString(cursor
				.getColumnIndex(FIELD_TAG_ID)));
		equipment
				.setImage(cursor.getString(cursor.getColumnIndex(FIELD_IMAGE)));
		equipment.setEquipmentStatus_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_STATUS_UUID)));
		equipment.setInventoryNumber(cursor.getString(cursor
				.getColumnIndex(FIELD_INVENTORY_NUMBER)));
		equipment.setLocation(cursor.getString(cursor
				.getColumnIndex(FIELD_LOCATION)));

		return equipment;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице
	 * </p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(Equipment item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_TITLE, item.getTitle());
		values.put(FIELD_EQUIPMENT_TYPE_UUID, item.getEquipment_type_uuid());
		values.put(FIELD_CRITICAL_TYPE_UUID, item.getCritical_type_uuid());
		values.put(FIELD_START_DATE, item.getStart_date());
		values.put(FIELD_LATITUDE, item.getLatitude());
		values.put(FIELD_LONGITUDE, item.getLongitude());
		values.put(FIELD_TAG_ID, item.getTag_id());
		values.put(FIELD_IMAGE, item.getImage());
		values.put(FIELD_EQUIPMENT_STATUS_UUID, item.getEquipmentStatus_uuid());
		values.put(FIELD_INVENTORY_NUMBER, item.getInventoryNumber());
		values.put(FIELD_LOCATION, item.getLocation());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_TITLE));
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_EQUIPMENT_TYPE_UUID));
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_CRITICAL_TYPE_UUID));
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
	public String getLocationCoordinatesByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getFloat(cursor.getColumnIndex(FIELD_LATITUDE)) + " "
					+ cursor.getFloat(cursor.getColumnIndex(FIELD_LONGITUDE));
		} else
			return "неизвестны";
	}

	/**
	 * <p>
	 * Возвращает местоположение по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getLocationByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_LOCATION));
		} else
			return "неизвестно";
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_IMAGE));
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor.getColumnIndex(FIELD_TAG_ID));
		} else
			return "0-0-0";
	}

	/**
	 * <p>
	 * Возвращает inventory number по uuid
	 * </p>
	 * 
	 * @param uuid
	 */
	public String getInventoryNumberByUUID(String uuid) {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
				new String[] { uuid }, null, null, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			return cursor.getString(cursor
					.getColumnIndex(FIELD_INVENTORY_NUMBER));
		} else
			return "-";
	}

	public boolean saveItems(ArrayList<Equipment> list) {

		for (Equipment item : list) {
			if (replace(item) == -1) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @return the mProjection
	 */
	public static Map<String, String> getProjection() {
		Map<String, String> projection = new HashMap<String, String>();
		projection.putAll(mProjection);
		projection.remove(Projection._ID);
		return projection;
	}

	/**
	 * Возвращает курсор для ListView списка оборудования.
	 * 
	 * @param typeUuid
	 *            UUID типа оборудования, null все типы.
	 * @param orderByField
	 *            Поле по которому будет осуществлятся сортировка, null
	 *            сортировка не осуществляется.
	 * @return
	 */
	public Cursor getItemsWithInfo(String typeUuid, String orderByField) {

		Cursor cursor = null;

		String sortOrder = null;
		String paramArray[] = null;
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Map<String, String> projection = new HashMap<String, String>();

		projection.putAll(mProjection);

		String table;
		StringBuilder tables = new StringBuilder();

		tables.append(TABLE_NAME);

		// типы оборудования
		table = getLeftJoinTables(TABLE_NAME,
				EquipmentTypeDBAdapter.TABLE_NAME, FIELD_EQUIPMENT_TYPE_UUID,
				EquipmentTypeDBAdapter.FIELD_UUID);
		tables.append(' ').append(table);
		projection.putAll(EquipmentTypeDBAdapter.getProjection());

		// операции над оборудованием
		table = getLeftJoinTables(TABLE_NAME,
				EquipmentOperationDBAdapter.TABLE_NAME, FIELD_UUID,
				EquipmentOperationDBAdapter.FIELD_EQUIPMENT_UUID);
		tables.append(' ').append(table);
		projection.putAll(EquipmentOperationDBAdapter.getProjection());

		// статусы оборудования
		table = getLeftJoinTables(TABLE_NAME,
				EquipmentStatusDBAdapter.TABLE_NAME,
				FIELD_EQUIPMENT_STATUS_UUID,
				EquipmentStatusDBAdapter.FIELD_UUID);
		tables.append(' ').append(table);
		projection.putAll(EquipmentStatusDBAdapter.getProjection());

		// типы критичности оборудования
		table = getLeftJoinTables(TABLE_NAME, CriticalTypeDBAdapter.TABLE_NAME,
				FIELD_CRITICAL_TYPE_UUID, EquipmentTypeDBAdapter.FIELD_UUID);
		tables.append(' ').append(table);
		projection.putAll(CriticalTypeDBAdapter.getProjection());

		queryBuilder.setTables(tables.toString());
		queryBuilder.setProjectionMap(projection);

		if (typeUuid != null) {
			queryBuilder.appendWhere(FIELD_EQUIPMENT_TYPE_UUID + "=?");
			paramArray = new String[] { typeUuid };
		}

		if (orderByField != null) {
			sortOrder = orderByField;
		}

		cursor = queryBuilder.query(mDb, null, null, paramArray,
				getFullName(TABLE_NAME, FIELD_UUID), null, sortOrder);

		return cursor;
	}

}
