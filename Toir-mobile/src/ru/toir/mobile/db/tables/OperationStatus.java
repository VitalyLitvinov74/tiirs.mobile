/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationStatus extends BaseTable {

	private String title;

	public static class Extras {
		public static final String STATUS_UUID_NEW = "18d3d5d4-336f-4b25-ba2b-00a6c7d5eb6c";
		public static final String STATUS_UUID_CANCELED = "0f733a22-b65a-4d96-af86-34f7e6a62b0b";
	}
	
	public OperationStatus() {
		
	}

	public OperationStatus(long _id, String uuid, String title, long createdAt, long changedAt) {
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
		return this.title;
	}

}
