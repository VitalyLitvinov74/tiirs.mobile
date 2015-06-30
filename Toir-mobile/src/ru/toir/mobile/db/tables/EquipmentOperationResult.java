/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperationResult {
	private long _id;
	private String uuid;
	private String equipment_operation_uuid;
	private long start_date;
	private long end_date;
	private String operation_result_uuid;

	/**
	 * 
	 */
	public EquipmentOperationResult() {
	}
	
	/**
	 * 
	 */
	public EquipmentOperationResult(long _id, String uuid, String equipment_operation_uuid, long start_date, long end_date, String operation_result_uuid) {
		this._id = _id;
		this.uuid = uuid;
		this.equipment_operation_uuid = equipment_operation_uuid;
		this.start_date = start_date;
		this.end_date = end_date;
		this.operation_result_uuid = operation_result_uuid;
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
	 * @return the equipment_operation_uuid
	 */
	public String getEquipment_operation_uuid() {
		return equipment_operation_uuid;
	}

	/**
	 * @param equipment_operation_uuid the equipment_operation_uuid to set
	 */
	public void setEquipment_operation_uuid(String equipment_operation_uuid) {
		this.equipment_operation_uuid = equipment_operation_uuid;
	}

	/**
	 * @return the start_date
	 */
	public long getStart_date() {
		return start_date;
	}

	/**
	 * @param start_date the start_date to set
	 */
	public void setStart_date(long start_date) {
		this.start_date = start_date;
	}

	/**
	 * @return the end_date
	 */
	public long getEnd_date() {
		return end_date;
	}

	/**
	 * @param end_date the end_date to set
	 */
	public void setEnd_date(long end_date) {
		this.end_date = end_date;
	}

	/**
	 * @return the operation_result_uuid
	 */
	public String getOperation_result_uuid() {
		return operation_result_uuid;
	}

	/**
	 * @param operation_result_uuid the operation_result_uuid to set
	 */
	public void setOperation_result_uuid(String operation_result_uuid) {
		this.operation_result_uuid = operation_result_uuid;
	}

}
