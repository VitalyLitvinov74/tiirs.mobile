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
	public static final String FIELD_CREATED_AT_NAME = "CreatedAt";
	public static final String FIELD_CHANGED_AT_NAME = "ChangedAt";

	String[] mColumns = { FIELD__ID_NAME, FIELD_UUID_NAME,
			FIELD_EQUIPMENT_UUID_NAME, FIELD_DOCUMENTATION_TYPE_UUID_NAME,
			FIELD_TITLE_NAME, FIELD_PATH_NAME, FIELD_CREATED_AT_NAME,
			FIELD_CHANGED_AT_NAME };

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	/**
	 * 
	 * @param context
	 */
	public EquipmentDocumentationDBAdapter(Context context) {
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
	}

	/**
	 * <p>
	 * Возвращает запись из таблицы equipment_documentation
	 * </p>
	 * 
	 * @param uuid
	 * @return
	 */
	public EquipmentDocumentation getItem(String uuid) {
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
	 * Возвращает объект equipment_documentation
	 * </p>
	 * 
	 * @param cursor
	 * @return
	 */
	public static EquipmentDocumentation getItem(Cursor cursor) {
		EquipmentDocumentation item = new EquipmentDocumentation();

		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		item.setUuid(cursor.getString(cursor
				.getColumnIndex(FIELD_UUID_NAME)));
		item.setEquipment_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_UUID_NAME)));
		item.setDocumentation_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_DOCUMENTATION_TYPE_UUID_NAME)));
		item.setTitle(cursor.getString(cursor
				.getColumnIndex(FIELD_TITLE_NAME)));
		item.setPath(cursor.getString(cursor
				.getColumnIndex(FIELD_PATH_NAME)));
		item.setCreatedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CREATED_AT_NAME)));
		item.setChangedAt(cursor.getLong(cursor
				.getColumnIndex(FIELD_CHANGED_AT_NAME)));
		return item;
	}

	/**
	 * <p>
	 * Добавляет/заменяет запись в таблице equipment_operation
	 * </p>
	 * 
	 * @param uuid
	 * @param task_uuid
	 * @param equipment_uuid
	 * @param operation_type_uuid
	 * @param operation_pattern_uuid
	 * @param operation_status_uuid
	 * @return
	 */
	public long replace(String uuid, String equipment_uuid,
			String documentation_type_uuid, String title, String path,
			long createdAt, long changedAt) {
		ContentValues values = new ContentValues();
		values.put(EquipmentDocumentationDBAdapter.FIELD_UUID_NAME, uuid);
		values.put(EquipmentDocumentationDBAdapter.FIELD_EQUIPMENT_UUID_NAME,
				equipment_uuid);
		values.put(
				EquipmentDocumentationDBAdapter.FIELD_DOCUMENTATION_TYPE_UUID_NAME,
				documentation_type_uuid);
		values.put(EquipmentDocumentationDBAdapter.FIELD_TITLE_NAME, title);
		values.put(EquipmentDocumentationDBAdapter.FIELD_PATH_NAME, path);
		values.put(FIELD_CREATED_AT_NAME, createdAt);
		values.put(FIELD_CHANGED_AT_NAME, changedAt);
		return mDb.replace(TABLE_NAME, null, values);
	}

	/**
	 * <p>
	 * Добавляет/заменяет запись в таблице
	 * </p>
	 * 
	 * @param document
	 * @return
	 */
	public long replace(EquipmentDocumentation document) {
		return replace(document.getUuid(), document.getEquipment_uuid(),
				document.getDocumentation_type_uuid(), document.getTitle(),
				document.getPath(), document.getCreatedAt(),
				document.getChangedAt());
	}

	/**
	 * <p>
	 * Возвращает все записи из таблицы equipment documentation
	 * </p>
	 * 
	 * @return list
	 */
	public ArrayList<EquipmentDocumentation> getAllItems(String type) {
		ArrayList<EquipmentDocumentation> arrayList = new ArrayList<EquipmentDocumentation>();
		Cursor cursor;
		// можем или отобрать всю документацию или только определенного типа
		if (type.equals(""))
			cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null,
					null);
		else
			cursor = mDb.query(TABLE_NAME, mColumns,
					FIELD_DOCUMENTATION_TYPE_UUID_NAME + "=?",
					new String[] { type }, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (true) {
				EquipmentDocumentation equip = getItem(cursor);
				arrayList.add(equip);
				if (cursor.isLast())
					break;
				cursor.moveToNext();
			}
		}
		return arrayList;
	}

	public void saveItems(ArrayList<EquipmentDocumentation> list) {
		mDb.beginTransaction();
		for (EquipmentDocumentation item : list) {
			replace(item);
		}
		mDb.setTransactionSuccessful();
		mDb.endTransaction();
	}
}
