/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationPattern {
	private long _id;
	private String uuid;
	private String title;
	private String operation_type_uuid;
	
	/**
	 * 
	 */
	public OperationPattern() {

	}
	
	public OperationPattern(long _id, String uuid, String title, String operation_type_uuid) {
		this._id = _id;
		this.uuid = uuid;
		this.title = title;
		this.operation_type_uuid = operation_type_uuid;
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
	 * @return the title
	 */
	public String getOperation_type_uuid() {
		return operation_type_uuid;
	}

	/**
	 * @param title the title to set
	 */
	public void setOperation_type_uuid(String operation_type_uuid) {
		this.operation_type_uuid = operation_type_uuid;
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

}
