/**
 * 
 */
package ru.toir.mobile.serverapi;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * @author Dmitriy Logachov
 *
 */
public class BaseObjectSrv {
	
	@Expose
	protected String Id;
	@Expose
	protected Date CreatedAt;
	@Expose
	protected Date ChangedAt;

	/**
	 * 
	 * @return The Id
	 */
	public String getId() {
		return Id;
	}

	/**
	 * 
	 * @param Id
	 *            The Id
	 */
	public void setId(String Id) {
		this.Id = Id;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
	}

	/**
	 * @return
	 */
	public long getCreatedAtTime() {
		return CreatedAt == null ? 0 : CreatedAt.getTime();
	}

	/**
	 * @param createdAt
	 *            the createdAt to set
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
	 * @return
	 */
	public long getChangedAtTime() {
		return ChangedAt == null ? 0 : ChangedAt.getTime();
	}

	/**
	 * @param changedAt
	 *            the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
	}

}
