package ru.toir.mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author olejek
 */
public class TypeDocsDBAdapter {
		public static final String TABLE_NAME = "doc_type";
		public static final String FIELD_ID_NAME = "_id";
		public static final int FIELD_ID_COLUMN = 0;
		public static final String FIELD_TYPE_NAME = "type";
		public static final int FIELD_TYPE_COLUMN = 1;
		public static final String FIELD_NAME_NAME = "name";
		public static final int FIELD_NAME_COLUMN = 2;
		
		private DatabaseHelper dbHelper;
		private SQLiteDatabase db;
		private final Context context;
		
		/**
		 * @param context
		 * @return TypeDocsDBAdapter
		 */
		public TypeDocsDBAdapter(Context context){
			this.context = context;
		}
		
		/**
		 * Получаем объект базы данных
		 * @return DocsDBAdapter
		 * @throws SQLException
		 */
		public TypeDocsDBAdapter open() throws SQLException {
			this.dbHelper = new DatabaseHelper(this.context, TOiRDBAdapter.getDbName(), null, TOiRDBAdapter.getAppDbVersion());
			this.db = dbHelper.getWritableDatabase();
			return this;
		}
		
		/**
		 * Закрываем базу данных
		 */
		public void close() {
			dbHelper.close();
		}
		
		/**
		 * <p>Возвращает все записи из таблицы TypeDocs</p>
		 * @return Cursor
		 */
		public Cursor getAllDocsType() {
			return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_TYPE_NAME, FIELD_NAME_NAME}, null, null, null, null, null);
		}
		
		/**
		 * <p>Возвращает запись из таблицы TypeDocs по типу</p>
		 * @param type !!!
		 * @return Cursor
		 */
		public Cursor getDocs(long type) {
			return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_TYPE_NAME, FIELD_NAME_NAME}, FIELD_TYPE_NAME + "=?", new String[]{String.valueOf(type)}, null, null, null);
		}

		/**
		 * <p>Возвращает тип из таблицы TypeDocs по имени</p>
		 * @param name
		 * @return Cursor
		 */
		public Cursor getDocs(String name) {
			return db.query(TABLE_NAME, new String[]{FIELD_ID_NAME, FIELD_TYPE_NAME, FIELD_NAME_NAME}, FIELD_NAME_NAME + "=?", new String[]{String.valueOf(name)}, null, null, null);
		}

}
