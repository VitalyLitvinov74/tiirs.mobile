/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationType {
	private long _id;
	private String uuid;
	private String title;
	private long CreatedAt;
	private long ChangedAt;

	/**
	 * 
	 */
	public OperationType() {

	}
	
	public OperationType(long _id, String uuid, String title, long createdAt, long changedAt) {
		this._id = _id;
		this.uuid = uuid;
		this.title = title;
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
