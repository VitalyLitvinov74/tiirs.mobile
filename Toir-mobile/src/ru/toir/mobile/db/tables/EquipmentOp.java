/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author koputo
 *
 */
public class EquipmentOp {
	private String uuid;
	private String task_uuid;
	private String equipment_uuid;
	private String operation_type_uuid;
	private String operation_pattern_uuid;
	private String equipment_status_uuid;

	public EquipmentOp() {
	}

	public EquipmentOp(String uuid, String task_uuid, String equipment_uuid, String operation_type_uuid, String operation_pattern_uuid, String equipment_status_uuid) 
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
}
