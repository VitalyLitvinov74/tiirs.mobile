/**
 * 
 */
package ru.toir.mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author koputo Класс используется в адаптерах таблиц базы данных. Сама база
 *         создаётся/обновляется в адаптере {@link TOiRDBAdapter} при старте
 *         приложения.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper sInstance;

	public static synchronized DatabaseHelper getInstance(Context context) {

		if (sInstance == null) {
			sInstance = new DatabaseHelper(context);
		}
		return sInstance;
	}

	private DatabaseHelper(Context context) {
		super(context, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// сдесь видимо нужно выбрасывать исключение, для фиксасии "нештатного"
		// вызова
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// сдесь видимо нужно выбрасывать исключение, для фиксасии "нештатного"
		// вызова
	}

}
