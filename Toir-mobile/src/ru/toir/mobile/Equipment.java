package ru.toir.mobile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.content.Intent;
import ru.toir.mobile.TOiRDBAdapter;

/**
 * @author olejek
 * <p>Класс для работы с оборудованием</p>
 *
 */
public class Equipment extends Activity  {
	private TOiRDBAdapter adapter;
		
	public static final String TABLE_NAME = "equipment";
	private long id;
	public static final String ID_NAME = "id";
	public static final int ID_COLUMN = 0;
	private String name;
	public static final String NAME_NAME = "name";
	public static final int NAME_COLUMN = 1;
	private String link;
	public static final String LINK_NAME = "link";
	public static final int LINK_COLUMN = 2;
	private int type;
	public static final String TYPE_NAME = "type";
	public static final int TYPE_COLUMN = 3;
	private int year;
	public static final String YEAR_NAME = "year";
	public static final int YEAR_COLUMN = 4;
	private String manufacturer;
	public static final String MANUFACTURER_NAME = "year";
	public static final int MANUFACTURER_COLUMN = 5;	
	private String photo;
	public static final String PHOTO_NAME = "photo";
	public static final int PHOTO_COLUMN = 6;	
	private int priority;
	public static final String PRIORITY_NAME = "priority";
	public static final int PRIORITY_COLUMN = 7;	

	public Equipment(TOiRDBAdapter adapterFromMain){
		id = 0;
		name = "";
		link = "";
		type = 0;
		year = 2000;
		manufacturer="";
		photo=""; // file with default photo 
		priority=0;
		adapter=adapterFromMain;
	}
	
	public Equipment (long id, String name, String link, int type, int year, String manufacturer, String photo, int priority){
		this.id = id;
		this.name = name;
		this.link = link;
		this.type = type;
		this.year = year;
		this.manufacturer=manufacturer;
		this.photo=photo;
		this.priority=priority;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * @param link
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * @return
	 */
	public String getManufacturer() {
		return manufacturer;
	}
	
	/**
	 * @param pass
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	/**
	 * @return
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * <p>Добавляет запись в таблицу equipments</p>
	 * @param  long id;
	 * @param String name;
	 * @param String link;
	 * @param int type;
	 * @param int year;
	 * @param String manufacturer;
	 * @param String photo;
	 * @param int priority;
	 * @return long id столбца или -1 если не удалось добавить запись
	 */
	public long insertEquipment(String name, String link, int type,int year, String manufacturer, String photo, int priority){
		ContentValues values = new ContentValues();
		values.put(Equipment.NAME_NAME, name);
		values.put(Equipment.LINK_NAME, link);
		values.put(Equipment.TYPE_NAME, type);
		values.put(Equipment.YEAR_NAME, year);
		values.put(Equipment.MANUFACTURER_NAME, manufacturer);
		values.put(Equipment.PHOTO_NAME, photo);
		values.put(Equipment.PRIORITY_NAME, priority);
		return adapter.insert(Equipment.TABLE_NAME, null, values);
	}
	
	/**
	 * <p>Удаляет запись</p>
	 * @param id ид для удаления
	 * @return int количество удалённых записей
	 */
	public int deleteEquipment(long id){
		return adapter.delete(Equipment.TABLE_NAME, Equipment.ID_NAME + "=?", new String[]{String.valueOf(id)});
	}
}
