/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class BaseTable {
	
	protected long _id;
	protected String uuid;
	protected long CreatedAt;
	protected long ChangedAt;


	/**
	 * 
	 */
	public BaseTable() {
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
