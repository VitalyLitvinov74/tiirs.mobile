/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentStatus {
	private long _id;
	private String name;
	private String uuid;
	private int type;

	public static class Extras {
		//public static final String STATUS_UUID_UNCERTAINED = "PROVIDER_E";
	}

	public EquipmentStatus() {

	}
	
	public EquipmentStatus(long _id, String uuid, String name, int type) {
		this._id = _id;
		this.uuid = uuid;
		this.name = name;
		this.type = type;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return name;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.name = title;
	}

	/**
	 * @return the _id
	 */
	public long get_id() {
		return _id;
	}

	/**
	 * @return the type
	 */
	public long get_type() {
		return type;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(long _id) {
		this._id = _id;
	}

}
