/**
 * 
 */
package ru.toir.mobile.serverapi.reference;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * @author Dmitriy Logachov
 *
 */
public class OperationResult {
	
	@Expose
	private String Title;
	@Expose
	private String Id;
	@Expose
	private Date CreatedAt;
	@Expose
	private Date ChangedAt;
	/**
	 * @return the title
	 */
	public String getTitle() {
		return Title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		Title = title;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return Id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		Id = id;
	}
	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
	}
	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		CreatedAt = createdAt;
	}
	/**
	 * @return the changedAt
	 */
	public Date getChangedAt() {
		return ChangedAt;
	}
	/**
	 * @param changedAt the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
	}

}
