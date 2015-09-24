package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Текущий статус наряда
 * @author Dmitriy Logachov
 *
 */
public class TaskStatusSrv extends BaseObjectSrv {

	@Expose
	private String Title;

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

}
