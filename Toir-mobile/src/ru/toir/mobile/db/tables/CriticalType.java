/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class CriticalType extends BaseTable {
	
	private int type;

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

	public String toString() {
		if (type == 0) {
			return "Любая критичность";
		}
		return "Критичность: " + type;
	}

}
