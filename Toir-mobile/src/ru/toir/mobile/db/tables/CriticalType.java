/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class CriticalType {
	private String uuid;
	private int type;

	/**
	 * 
	 */
	public CriticalType() {

	}
	
	public CriticalType(String uuid, int type) {
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

}
