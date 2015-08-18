/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class CriticalType {
	private long _id;
	private String uuid;
	private int type;

	/**
	 * 
	 */
	public CriticalType() {

	}
	
	public CriticalType(long _id, String uuid, int type) {
		this._id = _id;
		this.uuid = uuid;
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
	public int getType() {
		return type;
	}

	/**
	 * @param title the title to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the _id
	 */
	public long get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(long _id) {
		this._id = _id;
	}
	
	public String toString() {
		if (type == 0) {
			return "Любая критичность";
		}
		return "Критичность: " + type;
	}

}
