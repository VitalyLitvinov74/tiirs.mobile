/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationType extends BaseTable {

	private String title;

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


	public String toString() {
		return title;
	}

}
