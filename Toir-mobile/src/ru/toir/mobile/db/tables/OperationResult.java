/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationResult extends BaseTable {

	private String operation_type_uuid;
	private String title;

	/**
	 * 
	 */
	public OperationResult() {

	}
	
	public OperationResult(long _id, String uuid, String operation_type_uuid, String title, long createdAt, long changedAt) {
		this._id = _id;
		this.uuid = uuid;
		this.operation_type_uuid = operation_type_uuid;
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

	/**
	 * @return the operation_type_uuid
	 */
	public String getOperation_type_uuid() {
		return operation_type_uuid;
	}

	/**
	 * @param operation_type_uuid the operation_type_uuid to set
	 */
	public void setOperation_type_uuid(String operation_type_uuid) {
		this.operation_type_uuid = operation_type_uuid;
	}

	public String toString() {
		return title;
	}

}
