/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationPatternStepResult {
	private String uuid;
	private String operation_pattern_step_uuid;
	private String next_operation_pattern_step_uuid;
	private String title;
	private String measure_type_uuid;

	/**
	 * 
	 */
	public OperationPatternStepResult() {

	}
	
	public OperationPatternStepResult(String uuid, String operation_pattern_step_uuid, String next_operation_pattern_step_uuid, String title, String measure_type_uuid) {
		this.uuid = uuid;
		this.operation_pattern_step_uuid = operation_pattern_step_uuid;
		this.next_operation_pattern_step_uuid = next_operation_pattern_step_uuid;
		this.title = title;
		this.measure_type_uuid = measure_type_uuid;
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
	 * @return the operation_pattern_step_uuid
	 */
	public String getOperation_pattern_step_uuid() {
		return operation_pattern_step_uuid;
	}

	/**
	 * @param operation_pattern_step_uuid the operation_pattern_step_uuid to set
	 */
	public void setOperation_pattern_step_uuid(String operation_pattern_step_uuid) {
		this.operation_pattern_step_uuid = operation_pattern_step_uuid;
	}

	/**
	 * @return the next_operation_pattern_step_uuid
	 */
	public String getNext_operation_pattern_step_uuid() {
		return next_operation_pattern_step_uuid;
	}

	/**
	 * @param next_operation_pattern_step_uuid the next_operation_pattern_step_uuid to set
	 */
	public void setNext_operation_pattern_step_uuid(
			String next_operation_pattern_step_uuid) {
		this.next_operation_pattern_step_uuid = next_operation_pattern_step_uuid;
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
	 * @return the measure_type_uuid
	 */
	public String getMeasure_type_uuid() {
		return measure_type_uuid;
	}

	/**
	 * @param measure_type_uuid the measure_type_uuid to set
	 */
	public void setMeasure_type_uuid(String measure_type_uuid) {
		this.measure_type_uuid = measure_type_uuid;
	}

}
