/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationStatus {
	private long _id;
	private String uuid;
	private String title;

	public static class Extras {
		public static final String STATUS_UUID_NEW = "18d3d5d4-336f-4b25-ba2b-00a6c7d5eb6c";
		public static final String STATUS_UUID_CANCELED = "0f733a22-b65a-4d96-af86-34f7e6a62b0b";
	}
	
	public OperationStatus() {
		
	}

	public OperationStatus(long _id, String uuid, String title) {
		this._id = _id;
		this.uuid = uuid;
		this.title = title;
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
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
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
		return this.title;
	}

}
