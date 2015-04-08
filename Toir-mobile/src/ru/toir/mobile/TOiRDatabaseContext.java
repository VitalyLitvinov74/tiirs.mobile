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
import android.util.Log;

/**
 * @author koputo
 *
 */
public class TOiRDatabaseContext extends ContextWrapper {
	private static final String DEBUG_CONTEXT = "DatabaseContext";
	/**
	 * @param name
	 * @param mode
	 * @param factory
	 */
	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode,
			CursorFactory factory) {
		SQLiteDatabase result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
		// если передавать factory = null база на флешке не создаётся, почему не разбирался
		//SQLiteDatabase result = super.openOrCreateDatabase(name, mode, factory);
		if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
			Log.w(DEBUG_CONTEXT, "openOrCreateDatabase(" + name + ",,) = " + result.getPath());
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
	 */
	@Override
	public File getDatabasePath(String name) {
		File sdcard = Environment.getExternalStorageDirectory();    
		String dbfile = sdcard.getAbsolutePath() + File.separator + "databases" + File.separator + name;
		if (!dbfile.endsWith(".db")){
		    dbfile += ".db" ;
		}
		
		File result = new File(dbfile);
		
		if (!result.getParentFile().exists()) {
		    result.getParentFile().mkdirs();
		}
		
		if (Log.isLoggable(DEBUG_CONTEXT, Log.WARN)) {
			Log.w(DEBUG_CONTEXT, "getDatabasePath(" + name + ") = " + result.getAbsolutePath());
		}
		
		return result;
	}

	/**
	 * @param base
	 */
	public TOiRDatabaseContext(Context base) {
		super(base);
	}

}
