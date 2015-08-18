/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class TaskStatus {
	private long _id;
	private String uuid;
	private String title;

	public static class Extras {
		//public static final String STATUS_UUID_UNCERTAINED = "PROVIDER_E";
		public static final String STATUS_UUID_CREATED = "1e9b4d73-044c-471b-a08d-26f36ebb22ba";
		public static final String STATUS_UUID_SENDED = "9f980db5-934c-4ddb-999a-04c6c3daca59";
		public static final String STATUS_UUID_RECIEVED = "9f980db5-934c-4ddb-999a-04c6c3daca59";
		public static final String STATUS_UUID_COMPLETED = "dc6dca37-2cc9-44da-aff9-19bf143e611a";
		public static final String STATUS_UUID_UNCOMPLETED = "363c08ec-89d9-47df-b7cf-63a05d56594c";
	}

	/**
	 * 
	 */
	public TaskStatus() {

	}
	
	public TaskStatus(long _id, String uuid, String title) {
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
		return title;
	}

}
