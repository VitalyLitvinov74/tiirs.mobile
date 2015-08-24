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
	private long CreatedAt;
	private long ChangedAt;

	/**
	 * 
	 */
	public CriticalType() {

	}
	
	public CriticalType(long _id, String uuid, int type, long createdAt, long changedAt) {
		this._id = _id;
		this.uuid = uuid;
		this.type = type;
		this.CreatedAt = createdAt;
		this.ChangedAt = changedAt;
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
