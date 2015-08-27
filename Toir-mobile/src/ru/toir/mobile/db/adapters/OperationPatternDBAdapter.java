package ru.toir.mobile.db.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ru.toir.mobile.db.tables.OperationPattern;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class OperationPatternDBAdapter extends BaseDBAdapter {

	public static final String TABLE_NAME = "operation_pattern";
	
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_OPERATION_TYPE_UUID = "operation_type_uuid";
	
	public static final class Projection {
		public static final String _ID = FIELD__ID;
		public static final String UUID = TABLE_NAME + '_' + FIELD_UUID;
		public static final String CREATED_AT = TABLE_NAME + '_' + FIELD_CREATED_AT;
		public static final String CHANGED_AT = TABLE_NAME + '_' + FIELD_CHANGED_AT;
		
		public static final String TITLE = TABLE_NAME + '_' + FIELD_TITLE;
		public static final String OPERATION_TYPE_UUID = TABLE_NAME + '_' + FIELD_OPERATION_TYPE_UUID;
	}
	
	private static final Map<String, String> mProjection = new HashMap<String, String>();
	static {
		mProjection.put(Projection._ID, getFullName(TABLE_NAME, FIELD__ID) + " AS " + Projection._ID);
		mProjection.put(Projection.UUID, getFullName(TABLE_NAME, FIELD_UUID) + " AS " + Projection.UUID);
		mProjection.put(Projection.CREATED_AT, getFullName(TABLE_NAME, FIELD_CREATED_AT) + " AS " + Projection.CREATED_AT);
		mProjection.put(Projection.CHANGED_AT, getFullName(TABLE_NAME, FIELD_CHANGED_AT) + " AS " + Projection.CHANGED_AT);

		mProjection.put(Projection.TITLE, getFullName(TABLE_NAME, FIELD_TITLE) + " AS " + Projection.TITLE);
		mProjection.put(Projection.OPERATION_TYPE_UUID, getFullName(TABLE_NAME, FIELD_OPERATION_TYPE_UUID) + " AS " + Projection.OPERATION_TYPE_UUID);
	}
	
	String[] mColumns = {
			FIELD__ID,
			FIELD_UUID,
			FIELD_TITLE,
			FIELD_OPERATION_TYPE_UUID,
			FIELD_CREATED_AT, FIELD_CHANGED_AT };
		
	/**
	 * @param context
	 * @return OrderDBAdapter
	 */
	public OperationPatternDBAdapter(Context context) {
		super(context, TABLE_NAME);
	}
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public OperationPattern getItem(String uuid) {		
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);		
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}
		return null;
	}
	
	public OperationPattern getItem(Cursor cursor) {
		OperationPattern pattern = new OperationPattern();
		pattern.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID)));
		pattern.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID)));
		pattern.setTitle(cursor.getString(cursor.getColumnIndex(FIELD_TITLE)));
		pattern.setOperation_type_uuid(cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID)));
		return pattern;
	}
	
	/**
	 * 
	 * @return
	 */
	public Cursor getAllItems() {
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);		
		if (cursor.moveToFirst()) {
			return cursor;
		}
		return null;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице operation_type</p>
	 * @param uuid
	 * @param title
	 * @return
	 */
	public long replace(String uuid, String title, String operation_type_uuid) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID, uuid);
		values.put(FIELD_TITLE, title);
		values.put(FIELD_OPERATION_TYPE_UUID, operation_type_uuid);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице operation_type</p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(OperationPattern status) {
		return replace(status.getUuid(), status.getTitle(), status.getOperation_type_uuid());
	}

	/**
	 * <p>Возвращает список операций</p>
	 * @param uuid
	 * @return array
	 */
	public ArrayList<OperationPattern> getOperationByUUID(String uuid) {
		ArrayList<OperationPattern> arrayList = new ArrayList<OperationPattern>();
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID + "=?", new String[]{uuid}, null, null, null);		
		cursor.moveToFirst();
		if (cursor.getCount()>0)
		while (true)		
			{
			OperationPattern operationPattern = new OperationPattern(
					cursor.getLong(cursor.getColumnIndex(FIELD__ID)),
					cursor.getString(cursor.getColumnIndex(FIELD_UUID)),
					cursor.getString(cursor.getColumnIndex(FIELD_TITLE)),
					cursor.getString(cursor.getColumnIndex(FIELD_OPERATION_TYPE_UUID)));
			 arrayList.add(operationPattern);
			 if (cursor.isLast()) break;
			 cursor.moveToNext();
			}
		return arrayList;
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

}
