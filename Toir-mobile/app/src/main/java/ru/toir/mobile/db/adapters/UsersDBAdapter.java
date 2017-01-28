package ru.toir.mobile.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ru.toir.mobile.DatabaseHelper;
import ru.toir.mobile.db.tables.Users;

/**
 * @author Dmitriy Logachov
 * <p>Класс адаптера к таблице users</p>
 *
 */
public class UsersDBAdapter {
	public static final String TABLE_NAME = "users";
	
	public static final String FIELD__ID_NAME = "_id";
	public static final String FIELD_UUID_NAME = "uuid";
	public static final String FIELD_NAME_NAME = "name";
	public static final String FIELD_LOGIN_NAME = "login";
	public static final String FIELD_PASS_NAME = "pass";
	public static final String FIELD_TYPE_NAME = "type";
	public static final String FIELD_TAGID_NAME = "tag_id";
	public static final String FIELD_ACTIVE_NAME = "active";
	public static final String FIELD_WHOIS_NAME = "whois";
    public static final String FIELD_IMAGE_NAME = "image";

	String[] mColumns = {
			FIELD__ID_NAME,
			FIELD_UUID_NAME,
			FIELD_NAME_NAME,
			FIELD_LOGIN_NAME,
			FIELD_PASS_NAME,
			FIELD_TYPE_NAME,
			FIELD_TAGID_NAME,
			FIELD_ACTIVE_NAME,
            FIELD_IMAGE_NAME,
			FIELD_WHOIS_NAME};
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private final Context mContext;
	
	/**
	 * @param context
	 * @return UsersDBAdapter
	 */
	public UsersDBAdapter(Context context) {
		mContext = context;
		mDbHelper = DatabaseHelper.getInstance(mContext);
		mDb = mDbHelper.getWritableDatabase();
	}
	
	/**
	 * 
	 * @param login
	 * @param pass
	 * @return
	 */
	public Users getUserByLoginAndPass(String login, String pass) {
		Cursor cursor;

		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_LOGIN_NAME + "=? AND " + FIELD_PASS_NAME + "=?", new String[]{login, pass}, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}

		return null;
	}

    public Users getActiveUser() {
        Cursor cursor;
        cursor = mDb.query(TABLE_NAME, mColumns, FIELD_ACTIVE_NAME  + "=?",
                new String[] { "1" }, null, null, null);
        if (cursor.moveToFirst()) {
            return getItem(cursor);
        }
        return null;
    }

    public void setActiveUser(long id) {
        ContentValues values = new ContentValues();
        // снимаем всем статус
        values.put(FIELD_ACTIVE_NAME, "0");
        mDb.update(TABLE_NAME, values, null, null);
        values.put(FIELD_ACTIVE_NAME, "1");
        mDb.update(TABLE_NAME, values, FIELD__ID_NAME + "=?", new String[] { String.valueOf(id) });
    }

    /**
	 * 
	 * @param tagId
	 * @return
	 */
	public Users getUserByTagId(String tagId) {
		Cursor cursor;

		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_TAGID_NAME + "=?", new String[]{tagId}, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}

		return null;
	}

	/**
	 * <p>Возвращает запись из таблицы users</p>
	 * @param uuid
	 * @return Users
	 */
	public Users getItem(String uuid) {
		Cursor cursor;

		cursor = mDb.query(TABLE_NAME, mColumns, FIELD_UUID_NAME + "=?", new String[]{uuid}, null, null, null);
		if (cursor.moveToFirst()) {
			return getItem(cursor);
		}

		return null;
	}

    /**
     * <p>
     * Возвращает все записи из таблицы
     * </p>
     *
     * @return list
     */
    public ArrayList<Users> getAllItems() {
        ArrayList<Users> arrayList = new ArrayList<>();
        Cursor cursor;
        cursor = mDb.query(TABLE_NAME, mColumns, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (true) {
                arrayList.add(getItem(cursor));
                if (cursor.isLast())
                    break;
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrayList;
    }

    /**
	 * 
	 * @param cursor
	 * @return
	 */
	public Users getItem(Cursor cursor) {
		Users item = new Users();

		item.set_id(cursor.getLong(cursor.getColumnIndex(FIELD__ID_NAME)));
		item.setUuid(cursor.getString(cursor.getColumnIndex(FIELD_UUID_NAME)));
		item.setName(cursor.getString(cursor.getColumnIndex(FIELD_NAME_NAME)));
		item.setLogin(cursor.getString(cursor.getColumnIndex(FIELD_LOGIN_NAME)));
		item.setPass(cursor.getString(cursor.getColumnIndex(FIELD_PASS_NAME)));
		item.setType(cursor.getInt(cursor.getColumnIndex(FIELD_TYPE_NAME)));
		item.setTag_id(cursor.getString(cursor.getColumnIndex(FIELD_TAGID_NAME)));
		item.setWhois(cursor.getString(cursor.getColumnIndex(FIELD_WHOIS_NAME)));
        item.setImage(cursor.getString(cursor.getColumnIndex(FIELD_IMAGE_NAME)));
		item.setActive((cursor.getInt(cursor.getColumnIndex(FIELD_ACTIVE_NAME)) != 0));

		return item;
	}

	/**
	 * <p>Добавляет/изменяет запись в таблице users</p>
	 * @param uuid
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 * @param tag_id
	 * @param active
     * @param whois
     * @param image
     * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replaceItem(String uuid, String name, String login, String pass, int type, String tag_id, boolean active, String whois, String image, boolean up_ins) {
		long id;
		ContentValues values = new ContentValues();
		values.put(FIELD_UUID_NAME, uuid);
		values.put(FIELD_NAME_NAME, name);
		values.put(FIELD_LOGIN_NAME, login);
		values.put(FIELD_PASS_NAME, pass);
		values.put(FIELD_TYPE_NAME, type);
		values.put(FIELD_TAGID_NAME, tag_id);
		values.put(FIELD_ACTIVE_NAME, active);
		values.put(FIELD_WHOIS_NAME, whois);
        values.put(FIELD_IMAGE_NAME, image);
        if (up_ins)
            id  = mDb.replace(TABLE_NAME, null, values);
        else
            id  = mDb.update(TABLE_NAME, values, FIELD_UUID_NAME + "=?", new String[] { String.valueOf(uuid) });
        values.clear();
		return id;
	}
	
	/**
	 * <p>Добавляет/изменяет запись в таблице users</p>
	 * @param user
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long replaceItem(Users user) {
		long id  = replaceItem(user.getUuid(), user.getName(), user.getLogin(), user.getPass(), user.getType(), user.getTag_id(), user.isActive(), user.getWhoIs(), user.getImage(), true);
		return id;
	}

    /**
     * <p>Удаляет запись в таблице users</p>
     * @param tag_id - id пользователя который нужно удалить
     * @return long id столбца или -1 если не удалось добавить запись
     */
    public long deleteItem(String tag_id) {
        long id;
        id  = mDb.delete(TABLE_NAME, FIELD_TAGID_NAME + "=?", new String[] { String.valueOf(tag_id) });
        return id;
    }

}
