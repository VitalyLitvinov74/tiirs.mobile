/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class MeasureValue extends BaseTable {

	private String equipment_operation_uuid;
	private String operation_pattern_step_result_uuid;
	private String value;
	private long attempt_send_date;
	private int attempt_count;
	private boolean updated;

	/**
	 * 
	 */
	public MeasureValue() {
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
	 * @return the operation_pattern_step_result_uuid
	 */
	public String getOperation_pattern_step_result_uuid() {
		return operation_pattern_step_result_uuid;
	}

	/**
	 * @param uuid the operation_pattern_step_result_uuid to set
	 */
	public void setOperation_pattern_step_result_uuid(
			String uuid) {
		operation_pattern_step_result_uuid = uuid;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
