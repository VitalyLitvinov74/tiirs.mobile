/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperation extends BaseTable {

	private String task_uuid;
	private String equipment_uuid;
	private String operation_type_uuid;
	private String operation_pattern_uuid;
	private String operation_status_uuid;
	private int operation_time;
	private long attempt_send_date;
	private int attempt_count;
	private boolean updated;

	public EquipmentOperation() {
	}

	/**
	 * @return the task_uuid
	 */
	public String getTask_uuid() {
		return task_uuid;
	}

	/**
	 * @param task_uuid the task_uuid to set
	 */
	public void setTask_uuid(String task_uuid) {
		this.task_uuid = task_uuid;
	}

	/**
	 * @return the equipment_uuid
	 */
	public String getEquipment_uuid() {
		return equipment_uuid;
	}

	/**
	 * @param equipment_uuid the equipment_uuid to set
	 */
	public void setEquipment_uuid(String equipment_uuid) {
		this.equipment_uuid = equipment_uuid;
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
	 * @return the operation_pattern_uuid
	 */
	public String getOperation_pattern_uuid() {
		return operation_pattern_uuid;
	}

	/**
	 * @param operation_pattern_uuid the operation_pattern_uuid to set
	 */
	public void setOperation_pattern_uuid(String operation_pattern_uuid) {
		this.operation_pattern_uuid = operation_pattern_uuid;
	}

	/**
	 * @return the equipment_status_uuid
	 */
	public String getOperation_status_uuid() {
		return operation_status_uuid;
	}

	/**
	 * @param equipment_status_uuid the equipment_status_uuid to set
	 */
	public void setOperation_status_uuid(String equipment_status_uuid) {
		this.operation_status_uuid = equipment_status_uuid;
	}

	/**
	 * @return the operation_time
	 */
	public int getOperation_time() {
		return operation_time;
	}

	/**
	 * @param operation_time the operation_time to set
	 */
	public void setOperation_time(int operation_time ) {
		this.operation_time = operation_time;
	}

	/**
	 * @return the attempt_send_date
	 */
	public long getAttempt_send_date() {
		return attempt_send_date;
	}

	/**
	 * @param attempt_send_date the attempt_send_date to set
	 */
	public void setAttempt_send_date(long attempt_send_date) {
		this.attempt_send_date = attempt_send_date;
	}

	/**
	 * @return the attempt_count
	 */
	public int getAttempt_count() {
		return attempt_count;
	}

	/**
	 * @param attempt_count the attempt_count to set
	 */
	public void setAttempt_count(int attempt_count) {
		this.attempt_count = attempt_count;
	}

	/**
	 * @return the updated
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

}
