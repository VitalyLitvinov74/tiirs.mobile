package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Статус оборудования
 * @author Dmitriy Logachov
 *
 */
public class EquipmentStatusSrv extends BaseObjectSrv {

	@Expose
	private String Title;
	@Expose
	private int Type;

	/**
	 * 
	 * @return The Title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * 
	 * @param Title
	 *            The Title
	 */
	public void setTitle(String Title) {
		this.Title = Title;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return Type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		Type = type;
	}

}
