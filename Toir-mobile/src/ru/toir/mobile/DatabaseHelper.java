/**
 * 
 */
package ru.toir.mobile;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author koputo
 * Класс используется в адаптерах таблиц базы данных.
 * Сама база создаётся/обновляется в адаптере {@link TOiRDBAdapter} при старте приложения. 
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "DatabaseHelper";

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 * @param errorHandler
	 */
	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// сдесь видимо нужно выбрасывать исключение, для фиксасии "нештатного" вызова
	}

	/* (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// сдесь видимо нужно выбрасывать исключение, для фиксасии "нештатного" вызова
	}

}
