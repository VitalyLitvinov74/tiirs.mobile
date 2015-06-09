package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.GpsTrack;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

/**
 * @author Olejek
 * <p>Класс адаптера к таблице GPS</p>
 *
 */
public class GPSDBAdapter {
	public static final String TABLE_NAME = "gps_position";
	
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_USER_NAME = "user_uuid";
	public static final String FIELD_DATE_NAME = "cur_date";
	public static final String FIELD_LAT_NAME = "latitude";
	public static final String FIELD_LON_NAME = "longitude";
	
	String[] mColumns = {
			FIELD_UUID_NAME,
			FIELD_USER_NAME,
			FIELD_DATE_NAME,
			FIELD_LAT_NAME,
			FIELD_LON_NAME};
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	
	/**
	 * @param context
	 * @return GPSDBAdapter
	 */
	public GPSDBAdapter(Context context) {
		super();
		mContext = context;
	}
	
	/**
	 * Открываем базу данных
	 */
	public GPSDBAdapter open() {
		mDbHelper = new DatabaseHelper(mContext, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
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
	 * <p>Возвращает последнюю запись в треке</p>
	 */
	public GpsTrack getGPSByUuid(String uuid) {
		GpsTrack track = null;
		Cursor cur;
		cur = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, FIELD_DATE_NAME, null);
		if (cur.moveToFirst()) {
			track = new GpsTrack(cur.getString(cur.getColumnIndex(FIELD_UUID_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_USER_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_DATE_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_LAT_NAME)),
					cur.getString(cur.getColumnIndex(FIELD_LON_NAME)));
		}
		return track;
	}

	/**
	 * <p>Добавляет запись в трек перемещения</p>
	 * @param uuid
	 * @param user_uuid
	 * @param latitude
	 * @param longitude
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long addItem(String uuid, String user_uuid, String latitude, String longitude) {
		long id;
		Time now = new Time();
		now.setToNow();
		
		ContentValues values = new ContentValues();		
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_USER_NAME, user_uuid);
		values.put(FIELD_DATE_NAME, Long.toString(now.toMillis(false)));
		values.put(FIELD_LAT_NAME, latitude);
		values.put(FIELD_LON_NAME, longitude);
		id  = mDb.insert(TABLE_NAME, null, values);
		return id;
	}
	
	/**
	 * <p>Удаляет все записи</p>
	 * @return boolean
	 */
	public boolean removeAllItems() {
		// TODO так как в чистом виде мы записи удалять не будем, возможно этот метод не нужен
		boolean isDeleted;
		isDeleted = mDb.delete(TABLE_NAME, null, null) > 0;
		return isDeleted;
	}	
}
