/**
 * 
 */
package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.EquipmentDocumentation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * @author olejek
 * 
 */
public class EquipmentDocumentationDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "equipment_documentation";

	public static final String FIELD_EQUIPMENT_UUID = "equipment_uuid";
	public static final String FIELD_DOCUMENTATION_TYPE_UUID = "documentation_type_uuid";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_PATH = "path";

	/**
	 * 
	 * @param context
	 */
	public EquipmentDocumentationDBAdapter(Context context) {
		super(context, TABLE_NAME);
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
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?",
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
	public EquipmentDocumentation getItem(Cursor cursor) {
		EquipmentDocumentation item = new EquipmentDocumentation();

		getItem(cursor, item);
		item.setEquipment_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_EQUIPMENT_UUID)));
		item.setDocumentation_type_uuid(cursor.getString(cursor
				.getColumnIndex(FIELD_DOCUMENTATION_TYPE_UUID)));
		item.setTitle(cursor.getString(cursor
				.getColumnIndex(FIELD_TITLE)));
		item.setPath(cursor.getString(cursor
				.getColumnIndex(FIELD_PATH)));
		return item;
	}

	/**
	 * <p>Добавляет/заменяет запись в таблице</p>
	 * 
	 * @param item
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(EquipmentDocumentation item) {
		long id;
		ContentValues values = putCommonFields(item);

		values.put(FIELD_EQUIPMENT_UUID, item.getEquipment_uuid());
		values.put(FIELD_DOCUMENTATION_TYPE_UUID, item.getDocumentation_type_uuid());
		values.put(FIELD_TITLE, item.getTitle());
		values.put(FIELD_PATH, item.getPath());
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
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
					FIELD_DOCUMENTATION_TYPE_UUID + "=?",
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
