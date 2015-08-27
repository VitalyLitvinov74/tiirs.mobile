/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class EquipmentStatus extends BaseTable {

	private String title;
	private int type;

	public static class Extras {
		//public static final String STATUS_UUID_UNCERTAINED = "PROVIDER_E";
	}

	public EquipmentStatus() {

	}
	
	public EquipmentStatus(long _id, String uuid, String title, int type, long createdAt, long changedAt) {
		this._id = _id;
		this.uuid = uuid;
		this.title = title;
		this.type = type;
		this.CreatedAt = createdAt;
		this.ChangedAt = changedAt;
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

}
