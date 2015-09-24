package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Типы документов 
 * @author Dmitriy Logachov
 *
 */
public class DocumentationTypeSrv extends BaseObjectSrv {

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
