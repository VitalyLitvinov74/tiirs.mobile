/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class Task extends BaseTable {

	private String users_uuid;
	private long close_date;
	private String task_status_uuid;
	private long attempt_send_date;
	private int attempt_count;
	private boolean updated;
	private String task_name;

	public Task() {
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

}
