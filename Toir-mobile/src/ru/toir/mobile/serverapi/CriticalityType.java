package ru.toir.mobile.serverapi;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class CriticalityType {

	@Expose
	private String Id;
	@Expose
	private Integer Value;
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
	 * @return The Value
	 */
	public Integer getValue() {
		return Value;
	}

	/**
	 * 
	 * @param Value
	 *            The Value
	 */
	public void setValue(Integer Value) {
		this.Value = Value;
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
