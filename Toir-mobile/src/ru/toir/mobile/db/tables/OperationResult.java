/**
 * 
 */
package ru.toir.mobile.db.tables;

//import android.R.string;
//import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationResult {
	private long _id;
	private String uuid;
	private String operation_type_uuid;
	private String title;

	/**
	 * 
	 */
	public OperationResult() {

	}
	
	public OperationResult(long _id, String uuid, String operation_type_uuid, String title) {
		this._id = _id;
		this.uuid = uuid;
		this.operation_type_uuid = operation_type_uuid;
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
