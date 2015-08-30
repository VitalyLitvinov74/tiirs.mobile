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

}
