/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class Equipment {
	private long _id;
	private String uuid;
	private String title;
	private String equipment_type_uuid;
	private String critical_type_uuid;
	private long start_date;
	private float latitude;
	private float longitude;
	private String tag_id;
	private String img;
	private String status_uuid;
	private int status_type;

	/**
	 * 
	 */
	public Equipment() {

	}
	
	/**
	 * 
	 */
	public Equipment(long _id, String uuid, String title, String equipment_type_uuid, String critical_type_uuid, long start_date, float latitude, float longitude, String tag_id, String img, String status_uuid, int status_type) {
		this._id = _id;
		this.uuid = uuid;
		this.title = title;
		this.equipment_type_uuid = equipment_type_uuid;
		this.critical_type_uuid = critical_type_uuid;
		this.start_date = start_date;
		this.latitude = latitude;
		this.longitude = longitude;
		this.tag_id = tag_id;
		this.img = img;
		this.status_uuid = status_uuid;
		this.status_type = status_type;		
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
	 * @return the equipment_type_uuid
	 */
	public String getEquipment_type_uuid() {
		return equipment_type_uuid;
	}

	/**
	 * @param equipment_type_uuid the equipment_type_uuid to set
	 */
	public void setEquipment_type_uuid(String equipment_type_uuid) {
		this.equipment_type_uuid = equipment_type_uuid;
	}

	/**
	 * @return the critical_type_uuid
	 */
	public String getCritical_type_uuid() {
		return critical_type_uuid;
	}

	/**
	 * @param critical_type_uuid the critical_type_uuid to set
	 */
	public void setCritical_type_uuid(String critical_type_uuid) {
		this.critical_type_uuid = critical_type_uuid;
	}

	/**
	 * @return the start_date
	 */
	public long getStart_date() {
		return start_date;
	}

	/**
	 * @param start_date the start_date to set
	 */
	public void setStart_date(long start_date) {
		this.start_date = start_date;
	}

	/**
	 * @return the tag_id
	 */
	public String getTag_id() {
		return tag_id;
	}

	/**
	 * @param tag_id the tag_id to set
	 */
	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
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

	/**
	 * @return the latitude
	 */
	public float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}


	/**
	 * @return the status_uuid
	 */
	public String getStatus_uuid() {
		return status_uuid;
	}

	/**
	 * @param status uuid to set
	 */
	public void setStatus_uuid(String status_uuid) {
		this.status_uuid = status_uuid;
	}

	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}
	/**
	 * @param img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}

	/**
	 * @return the status_uuid
	 */
	public int getStatus_type() {
		return status_type;
	}	
	
	/**
	 * @param type to set
	 */
	public void setType(int type) {
		this.status_type = type;
	}
}
