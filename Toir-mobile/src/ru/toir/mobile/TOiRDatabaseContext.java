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
	
	private static final String TAG = "TOiRDatabaseContext";
	// в тестах используется префикс для создания файлов
	private String databasePrefix;

	/**
	 * @param base Родительский контекст
	 * <p>При создании объекта, проверяем родителя объекта, в зависимости от этого меняем префикс для файлов базы данных.</p>
	 * <p>Это частный случай, так как пока в планах нет необходимости использовать класс RenamingDelegatingContext для работы.
	 * Используется только в тестах, для переименования базы, чтоб не затирать рабочую.</p>
	 */
	public TOiRDatabaseContext(Context base) {
		super(base);
		try {
			databasePrefix = ((RenamingDelegatingContext) base).getDatabasePrefix();
		} catch(NoClassDefFoundError e) {
			databasePrefix = "";
		}
		catch(Exception e) {
			databasePrefix = "";
		}
	}
	
	/**
	 * <p>Удаляем базу</p>
	 * <p>В оригинальном методе, удаление реализованно по другому. Сделал как проще на текущий момент.</p>
	 * @param name Имя базы данных
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
	 * @since v1
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
	 * @since v11
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
	 * <b>Обязательно реализовать проверку доступности внешнего накопителя!!!!</b>
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
