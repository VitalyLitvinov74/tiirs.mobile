/**
 * 
 */
package ru.toir.mobile.db.adapters;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.TOiRDBAdapter;
import ru.toir.mobile.db.tables.Token;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Dmitriy Logachov
 * 
 */
public class TokenDBAdapter {

	public static final String TABLE_NAME = "token";

	public static final String FIELD_TOKEN_TYPE_NAME = "token_type";
	public static final String FIELD_ACCESS_TOKEN_NAME = "access_token";
	public static final String FIELD_EXPIRES_IN_NAME = "expires_in";
	public static final String FIELD_USER_NAME_NAME = "username";
	public static final String FIELD_ISSUED_NAME = ".issued";
	public static final String FIELD_EXPIRES_NAME = ".expires";

	// TODO решить что делать с полями .issued .expires
	public String[] mColumns = { FIELD_TOKEN_TYPE_NAME,
			FIELD_ACCESS_TOKEN_NAME, FIELD_EXPIRES_IN_NAME,
			FIELD_USER_NAME_NAME, "'" + FIELD_ISSUED_NAME + "' as issued", "'" + FIELD_EXPIRES_NAME + "' as expires"};

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;

	/**
	 * 
	 * @param context
	 */
	public TokenDBAdapter(Context context) {
		mContext = context;
	}

	/**
	 * Открываем базу данных
	 */
	public TokenDBAdapter open() {
		mDbHelper = new DatabaseHelper(mContext, TOiRDBAdapter.getDbName(),
				null, TOiRDBAdapter.getAppDbVersion());
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
	 * 
	 * @param cursor
	 * @return
	 */
	private Token getTokenData(Cursor cursor) {
		Token token = new Token();
		token.setToken_type(cursor.getString(cursor.getColumnIndex(FIELD_TOKEN_TYPE_NAME)));
		token.setAccess_token(cursor.getString(cursor.getColumnIndex(FIELD_ACCESS_TOKEN_NAME)));
		token.setExpires_in(cursor.getInt(cursor.getColumnIndex(FIELD_EXPIRES_IN_NAME)));
		token.setUserName(cursor.getString(cursor.getColumnIndex(FIELD_USER_NAME_NAME)));
		token.setIssued(cursor.getString(cursor.getColumnIndex("issued")));
		token.setExpires(cursor.getString(cursor.getColumnIndex("expires")));
		return token;
	}

	/**
	 * 
	 * @param userName
	 * @return
	 */
	public Token getTokenByUserName(String userName) {

		Token token = null;
		Cursor cursor;
		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_USER_NAME_NAME + "=?",
				new String[] { userName }, null, null, null);
		if (cursor.moveToFirst()) {
			token = getTokenData(cursor);
		}
		return token;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице token
	 * </p>
	 * 
	 * @param token_type
	 * @param access_token
	 * @param expires_in
	 * @param userName
	 * @param issued
	 * @param expires
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(String token_type, String access_token, int expires_in,
			String userName, String issued, String expires) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_TOKEN_TYPE_NAME, token_type);
		values.put(FIELD_ACCESS_TOKEN_NAME, access_token);
		values.put(FIELD_EXPIRES_IN_NAME, expires_in);
		values.put(FIELD_USER_NAME_NAME, userName);
		values.put(new StringBuilder("'").append(FIELD_ISSUED_NAME).append("'").toString(), issued);
		values.put(new StringBuilder("'").append(FIELD_EXPIRES_NAME).append("'").toString(), expires);
		id = mDb.replace(TABLE_NAME, null, values);
		return id;
	}

	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице token
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(Token token) {
		return replace(token.getToken_type(), token.getAccess_token(),
				token.getExpires_in(), token.getUserName(), token.getIssued(),
				token.getExpires());
	}
	
	/**
	 * <p>
	 * Добавляет/изменяет запись в таблице token
	 * </p>
	 * 
	 * @param token
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replace(ru.toir.mobile.serverapi.Token token) {
		return replace(token.getTokenType(), token.getAccessToken(),
				token.getExpiresIn(), token.getUserName(), token.getIssued(),
				token.getExpires());
	}
	
	/**
	 * Удаляет все данные из таблицы
	 * @return
	 */
	public int deleteAll() {
		return mDb.delete(TABLE_NAME, null, null);
	}
}
