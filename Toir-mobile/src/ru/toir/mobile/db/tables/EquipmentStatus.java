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
	private String title;
	private String uuid;
	private int type;
	private long CreatedAt;
	private long ChangedAt;

	public static class Extras {
		//public static final String STATUS_UUID_UNCERTAINED = "PROVIDER_E";
	}

	public EquipmentStatus() {

	}
	
	public EquipmentStatus(long _id, String uuid, String title, int type) {
		this._id = _id;
		this.uuid = uuid;
		this.title = title;
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the createdAt
	 */
	public long getCreatedAt() {
		return CreatedAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(long createdAt) {
		CreatedAt = createdAt;
	}

	/**
	 * @return the changedAt
	 */
	public long getChangedAt() {
		return ChangedAt;
	}

	/**
	 * @param changedAt the changedAt to set
	 */
	public void setChangedAt(long changedAt) {
		ChangedAt = changedAt;
	}

}
