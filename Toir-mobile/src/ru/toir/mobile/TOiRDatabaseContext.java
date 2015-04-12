/**
 * 
 */
package ru.toir.mobile;

import java.io.File;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Environment;
import android.test.RenamingDelegatingContext;
import android.util.Log;

/**
 * @author koputo
 * <p>Класс переопределяет контекст для создания и работы базы данных</p>
 */
public class TOiRDatabaseContext extends ContextWrapper {
	// в тестах используется префикс для создания файлов 
	private String databasePrefix;
	private static final String TAG = "TOiRDatabaseContext";

	/**
	 * @param base
	 * <p>При создании объекта, проверяем на выполнение тестов, в зависимости от этого меняем префикс для базы данных.</p>
	 */
	public TOiRDatabaseContext(Context base) {
		super(base);
		try {
			databasePrefix = ((RenamingDelegatingContext) base).getDatabasePrefix();
		} catch(NoClassDefFoundError e) {
			databasePrefix = "";
		}
	}
	
	/**
	 * удаляем базу
	 */
	@Override
    public boolean deleteDatabase(String name) {
		boolean result = false;
		File dbFile = getDatabasePath(name);
		File journalFile = new File(dbFile.toString() + "-journal");
		result |= dbFile.delete();
		result |= journalFile.delete();
        return result;
    }

	/**
	 * @param name
	 * @param mode
	 * @param factory
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		SQLiteDatabase result;
		if (factory == null) {
			result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
		} else {
			// если передавать factory = null база на флешке не создаётся, почему не разбирался
			result = super.openOrCreateDatabase(name, mode, factory);
		}
		if (Log.isLoggable(TAG, Log.WARN)) {
			Log.w(TAG, "openOrCreateDatabase(" + name + ",,) = " + result.getPath());
		}
		return result;
	}

	/**
	 * @param name
	 * @param mode
	 * @param factory
	 * @param errorHandler
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory, DatabaseErrorHandler errorHandler) {
		return openOrCreateDatabase(name, mode, factory);
	}

	/**
	 * @param name
	 * <p>Возвращает абсолютный путь к базе данных.</p>
	 * <p>Сейчас принудительно база создаётся на внешнем накопителе.</p>
	 */
	@Override
	public File getDatabasePath(String name) {
		File sdcard = Environment.getExternalStorageDirectory();    
		String dbfile = sdcard.getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getPackageName() + File.separator + "databases" + File.separator + databasePrefix + name;
		if (!dbfile.endsWith(".db")){
		    dbfile += ".db" ;
		}
		
		File result = new File(dbfile);
		
		if (!result.getParentFile().exists()) {
		    result.getParentFile().mkdirs();
		}
		
		if (Log.isLoggable(TAG, Log.WARN)) {
			Log.w(TAG, "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
		}
		
		return result;
	}
}
