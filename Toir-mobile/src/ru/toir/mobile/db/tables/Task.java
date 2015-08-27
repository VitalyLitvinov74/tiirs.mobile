/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class Task {
	private long _id;
	private String uuid;
	private String users_uuid;
	private long CreatedAt;
	private long ChangedAt;
	private long close_date;
	private String task_status_uuid;
	private long attempt_send_date;
	private int attempt_count;
	private boolean updated;
	private String task_name;

	public Task() {

	}

	public Task(long _id, String uuid, String users_uuid, long create_date, long modify_date, 
			long close_date, String task_status_uuid, long attempt_send_date, int attempt_count, boolean updated, String task_name) 
	{
		this._id = _id;
		this.uuid = uuid;
		this.users_uuid = users_uuid;
		this.CreatedAt = create_date;
		this.ChangedAt = modify_date;
		this.close_date = close_date;
		this.task_status_uuid = task_status_uuid;
		this.attempt_send_date = attempt_send_date;
		this.attempt_count = attempt_count;
		this.updated = updated;
		this.task_name = task_name;
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
	 * @return the users_uuid
	 */
	public String getUsers_uuid() {
		return users_uuid;
	}

	/**
	 * @param users_uuid the users_uuid to set
	 */
	public void setUsers_uuid(String users_uuid) {
		this.users_uuid = users_uuid;
	}

	/**
	 * @return the create_date
	 */
	public long getCreate_date() {
		return CreatedAt;
	}

	/**
	 * @param create_date the create_date to set
	 */
	public void setCreate_date(long create_date) {
		this.CreatedAt = create_date;
	}

	/**
	 * @return the modify_date
	 */
	public long getModify_date() {
		return ChangedAt;
	}

	/**
	 * @param modify_date the modify_date to set
	 */
	public void setModify_date(long modify_date) {
		this.ChangedAt = modify_date;
	}

	/**
	 * @return the close_date
	 */
	public long getClose_date() {
		return close_date;
	}

	/**
	 * @param close_date the close_date to set
	 */
	public void setClose_date(long close_date) {
		this.close_date = close_date;
	}

	/**
	 * @return the task_status_uuid
	 */
	public String getTask_status_uuid() {
		return task_status_uuid;
	}

	/**
	 * @param task_status_uuid the task_status_uuid to set
	 */
	public void setTask_status_uuid(String task_status_uuid) {
		this.task_status_uuid = task_status_uuid;
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

	/**
	 * @return the task_name
	 */
	public String getTask_name() {
		return task_name;
	}

	/**
	 * @param task_name to set
	 */
	public void setTask_name(String task_name) {
		this.task_name = task_name;
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
