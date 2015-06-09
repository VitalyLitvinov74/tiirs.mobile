/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author koputo
 *
 */
public class Orders {
	private String uuid;
	private String users_uuid;
	private long create_date;
	private long modify_date;
	private long close_date;
	private String task_status_uuid;
	private long attempt_send_date;
	private int attempt_count;
	private int successefull_send;

	public Orders() {

	}

	public Orders(String uuid, String user_uuid, long create_date, long modify_date, 
			long close_date, String task_status_uuid, long attempt_send_date, int attempt_count, int successefull_send) 
	{
		this.uuid = uuid;
		this.users_uuid = users_uuid;
		this.create_date = create_date;
		this.modify_date = modify_date;
		this.close_date = close_date;
		this.task_status_uuid = task_status_uuid;
		this.attempt_send_date = attempt_send_date;
		this.attempt_count = attempt_count;
		this.successefull_send = successefull_send;
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
