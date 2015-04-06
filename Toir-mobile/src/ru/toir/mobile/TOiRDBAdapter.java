package ru.toir.mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * <p>Класс для работы с базой данных ТОиР</p> 
 */
public class TOiRDBAdapter{
	/**
	 * <p>Имя файла базы данных</p>
	 */
	private static final String DATABASE_NAME = "toir.db";
	/**
	 * <p>Версия базы данных с которой работает приложение</p>
	 */
	private static final int DATABASE_VERSION = 1;
	
	// константы для тестовой таблицы
	private static final String TABLE_TEST = "testtable";
	public static final String TABLE_TEST_ID = "_id";
	public static final int TABLE_TEST_ID_COLUMN = 0;
	public static final String TABLE_TEST_NAME = "name";
	public static final int TABLE_TEST_NAME_COLUMN = 1;
	public static final String TABLE_TEST_VALUE = "value";
	public static final int TABLE_TEST_VALUE_COLUMN = 2;
	
	// база приложения
	private SQLiteDatabase db;
	
	// контекст приложения
	protected Context context;
	
	// вспомогательный класс для работы с базой
	private TOiRDbHelper dbHelper;
	/**
	 * <p>Конструктор адаптера</p>
	 * @param _context Контекст приложения
	 */
	public TOiRDBAdapter(Context _context){
		context = _context;
		dbHelper = new TOiRDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION); 
	}
	
	/**
	 * <p>Возвращает текущую версию базы</p>
	 * @return int Текущая версия базы
	 */
	public int getDbVersion(){
		return db.getVersion();
	}
	
	/**
	 * <p>Возвращает текущую версию базы с которой работет приложение</p>
	 * @return int Текущая версия базы с которой работает приложение
	 */
	public int getAppDbVersion(){
		return DATABASE_VERSION;
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
	public boolean isActual(){
		return DATABASE_VERSION == getDbVersion();
	}
	
	/**
	 * <p>"Открывает" базу для работы</p>
	 * @return Объект класса TOiRDBAdapter 
	 * @throws SQLException
	 */
	public TOiRDBAdapter open() throws SQLException{
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * <p>"Закрывает" базу для работы</p>
	 */
	public void close(){
		db.close();
	}
	
	/**
	 * <p>Возвращает все записи из таблицы test</p>
	 * @return Cursor
	 */
	public Cursor getAllTestEntry(){
		return db.query(TABLE_TEST, new String[]{TABLE_TEST_ID, TABLE_TEST_NAME, TABLE_TEST_VALUE}, null, null, null, null, null);
	}
	
	/**
	 * <p>Вспомогательный класс для работы с базой данных</p>
	 * @author koputo
	 *
	 */
	private class TOiRDbHelper extends SQLiteOpenHelper{
		
		private String updatePath = "updatedb";
		
		public TOiRDbHelper(Context _context, String _name, CursorFactory _factory, int _version){
			super(_context, _name, _factory, _version);
		}

		/**
		 * <p>Создаёт новую базу данных</p>
		 */
		@Override
		public void onCreate(SQLiteDatabase db){
			System.out.println("TOiRDbHelper.onCreate: db.version=" + db.getVersion());
			
			loadAndExecSQLUpdate(db, 0, DATABASE_VERSION);
		}

		/**
		 * <p>Обновляет текущую базу данных</p>
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			System.out.println("TOiRDbHelper.onUpgrade: db.version=" + db.getVersion());
			System.out.println("TOiRDbHelper.onUpgrade: oldVersion=" + oldVersion + ", newVersion=" + newVersion);
			
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
			AssetManager am = context.getAssets();
			boolean transactionSuccefful = true;

			db.beginTransaction();
			for(int i = oldVersion + 1; i <= newVersion; i++){
				try{
					InputStream is = am.open(updatePath + "/update" + i + ".sql");
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String line;
					while((line = br.readLine()) != null){
						try{
							db.execSQL(line);
						}catch(SQLException e){
							transactionSuccefful = false;
							System.out.println(e.toString());
						}
					}
				}catch(IOException e){
					transactionSuccefful = false;
					System.out.println(e.toString());
				}
			}
			if(transactionSuccefful){
				db.setTransactionSuccessful();
			}
			db.endTransaction();
		}
	}
}
