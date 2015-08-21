package ru.toir.mobile.serverapi;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class EquipmentStatus {

	@Expose
	private String Id;
	@Expose
	private String Title;
	@Expose
	private int Type;
	@Expose
	private Date CreatedAt;
	@Expose
	private Date ChangedAt;

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

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
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
	 * @param changedAt
	 *            the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
	}

}
