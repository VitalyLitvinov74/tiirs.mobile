/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author koputo
 *
 */
public class GpsTrack {
	
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

	public GpsTrack(String uuid, String user_uuid, String cur_date, String latitude, String longitude) {
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
}
