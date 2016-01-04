/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author koputo
 *
 */
public class GpsTrack {
	
	private long _id;
	private String uuid;
	private String user_uuid;
	private String cur_date;
	private String latitude;
	private String longitude;

	/**
	 * 
	 */
	public GpsTrack() {

	}

	public GpsTrack(long _id, String uuid, String user_uuid, String cur_date, String latitude, String longitude) {
		this._id = _id;
		this.uuid = uuid;
		this.user_uuid = user_uuid;
		this.cur_date = cur_date;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @param name the name to set
	 */
	public void setTrack(String latitude, String longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getUUID() {
		return uuid;
	}
	public String getUser_UUID() {
		return user_uuid;
	}
	public String getDate() {
		return cur_date;
	}
	public String getLatitude() {
		return latitude;
	}
	public String getLongitude() {
		return longitude;
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
	 * @return the user_uuid
	 */
	public String getUser_uuid() {
		return user_uuid;
	}

	/**
	 * @param user_uuid the user_uuid to set
	 */
	public void setUser_uuid(String user_uuid) {
		this.user_uuid = user_uuid;
	}

	/**
	 * @return the cur_date
	 */
	public String getCur_date() {
		return cur_date;
	}

	/**
	 * @param cur_date the cur_date to set
	 */
	public void setCur_date(String cur_date) {
		this.cur_date = cur_date;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
