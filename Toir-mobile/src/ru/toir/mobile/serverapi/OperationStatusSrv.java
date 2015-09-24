package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Текущий статус операции
 * @author Dmitriy Logachov
 *
 */
public class OperationStatusSrv extends BaseObjectSrv {

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
