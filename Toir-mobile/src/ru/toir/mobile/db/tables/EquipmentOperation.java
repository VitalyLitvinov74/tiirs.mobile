/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentOperation {
	private String uuid;
	private String task_uuid;
	private String equipment_uuid;
	private String operation_type_uuid;
	private String operation_pattern_uuid;
	private String equipment_status_uuid;

	public EquipmentOperation() {
	}

	public EquipmentOperation(String uuid, String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid, String equipment_status_uuid) 
	{
		this.uuid = uuid;
		this.task_uuid = task_uuid;
		this.equipment_uuid = equipment_uuid;
		this.operation_type_uuid = operation_type_uuid;
		this.operation_pattern_uuid = operation_pattern_uuid;
		this.equipment_status_uuid = equipment_status_uuid;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param _id the _id to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	public String getEquipment_status_uuid() {
		return equipment_status_uuid;
	}

	/**
	 * @param equipment_status_uuid the equipment_status_uuid to set
	 */
	public void setEquipment_status_uuid(String equipment_status_uuid) {
		this.equipment_status_uuid = equipment_status_uuid;
	}
}
