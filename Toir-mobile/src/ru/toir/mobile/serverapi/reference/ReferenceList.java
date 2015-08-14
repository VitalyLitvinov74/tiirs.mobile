/**
 * 
 */
package ru.toir.mobile.serverapi.reference;

import java.util.Date;

/**
 * @author Dmitriy Logachov
 *
 */
public class ReferenceList {

	private String Name;
	private Date CreatedAt;
	private Date ChangedAt;
	private String Id;
	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
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

}
