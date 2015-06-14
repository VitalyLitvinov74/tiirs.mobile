/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationPatternStep {
	private long _id;
	private String uuid;
	private String operation_pattern_uuid;
	private String description;
	private String image;
	private boolean first_step;
	private boolean last_step;

	/**
	 * 
	 */
	public OperationPatternStep() {

	}
	
	public OperationPatternStep(long _id, String uuid, String operation_pattern_uuid, String description, String image, boolean first_step, boolean last_step) {
		this._id = _id;
		this.uuid = uuid;
		this.operation_pattern_uuid = operation_pattern_uuid;
		this.description = description;
		this.image = image;
		this.first_step = first_step;
		this.last_step = last_step;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the first_step
	 */
	public boolean isFirst_step() {
		return first_step;
	}

	/**
	 * @param first_step the first_step to set
	 */
	public void setFirst_step(boolean first_step) {
		this.first_step = first_step;
	}

	/**
	 * @return the last_step
	 */
	public boolean isLast_step() {
		return last_step;
	}

	/**
	 * @param last_step the last_step to set
	 */
	public void setLast_step(boolean last_step) {
		this.last_step = last_step;
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
