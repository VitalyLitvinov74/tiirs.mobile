/**
 * 
 */
package ru.toir.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author koputo Класс используется в адаптерах таблиц базы данных. Сама база
 *         создаётся/обновляется в адаптере {@link TOiRDBAdapter} при старте
 *         приложения.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static DatabaseHelper sInstance;
	private static final String TAG = "DatabaseHelper";
	private static Context sContext;
	private String updatePath = "updatedb";
	private static final String DATABASE_NAME = "toir.db";
	private static final int DATABASE_VERSION = 16;



	public static synchronized DatabaseHelper getInstance(Context context) {

		if (sInstance == null) {
			sInstance = new DatabaseHelper(context);
			sContext = context;
		}
		return sInstance;
	}

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate: db.version=" + db.getVersion());
		loadAndExecSQLUpdate(db, 0, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade: db.version=" + db.getVersion());
		Log.d(TAG, "onUpgrade: oldVersion=" + oldVersion + ", newVersion=" + newVersion);
		loadAndExecSQLUpdate(db, oldVersion, newVersion);
	}
	
	/**
	 * <p>Метод загружает файлы с обновлениями структуры базы данных, и выполняет SQL инструкции из этих файлов</p>
	 * <p>Алгоритм работы работы следующий: в папке с обновлениями структуры базы данных
	 * ищутся все файлы от текущей версии базы данных до текущей версии с которой работает приложение.
	 * Последовательно выполняются. Если всё скрипты выполнились успешно, версия базы меняется до запрошенной.
	 * Если нет, остаётся равной oldVersion, то есть база не создаётся или не обновляется.</p>
	 * @param db SQLiteDatabase объект базы данных которую нужно обновить
	 * @param oldVersion int текущая версия базы данных
	 * @param newVersion int версия до которой нужно обновить базу данных
	 */
	private void loadAndExecSQLUpdate(SQLiteDatabase db, int oldVersion, int newVersion){
		AssetManager am = sContext.getAssets();
		boolean transactionSuccefful = true;

		db.beginTransaction();
		for(int i = oldVersion + 1; i <= newVersion; i++){
			try{
				InputStream is = am.open(updatePath + "/update" + i + ".sql");
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				while((line = br.readLine()) != null){
					if (!line.isEmpty()) {
						try{
							db.execSQL(line);
						}catch(SQLException e){
							transactionSuccefful = false;
							Log.d(TAG, e.toString());
						}
					}
				}
			}catch(IOException e){
				transactionSuccefful = false;
				Log.d(TAG, e.toString());
			}
		}
		if(transactionSuccefful){
			db.setTransactionSuccessful();
		}
		db.endTransaction();
		
		if (!transactionSuccefful) {
			throw new RuntimeException("при обновлении базы данных произошла ошибка");
		}
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.d(TAG, "onDowngrade: db.version=" + db.getVersion());
		Log.d(TAG, "onDowngrade: oldVersion=" + oldVersion + ", newVersion=" + newVersion);
	}
	
	/**
	 * <p>Проверяет актуальна ли версия базы</p>
	 * <p>Проверяется факт того, что версия базы данных совпадает с
	 * версией базы с которой работает приложение.</p>
	 * <p>Пример: Текущая версия базы 3, версия базы с которой работает приложение 3.
	 * Необходимо изменить структуру базы. В приложении меняем версию с которой работает приложение на 4.
	 * Подготавливаем sql скрипт для изменения структуры базы update4.sql
	 * Собираем приложение, устанавливаем на устройство. Но скрипт обновления по какой-то причине не выполняется.
	 * В этой ситуации версия базы данных останется 3, а версия приложения будет 4, что говорит нам о
	 * невозможности продолжать работу, так как в базе данных нет необходимых изменений.</p>
	 * @return true если версии совпадают, false в противном случае
	 */
	public boolean isDBActual() {
		return DATABASE_VERSION == sInstance.getReadableDatabase().getVersion();
	}
	
	public int getVersion() {
		return sInstance.getReadableDatabase().getVersion();
	}
	
	public String getDbName() {
		return DATABASE_NAME;
	}
}
