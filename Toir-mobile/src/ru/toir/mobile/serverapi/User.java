/**
 * 
 */
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * @author Dmitriy Logachov
 *
 */
public class User {

	@Expose
	private String Id;
	@Expose
	private String UserName;
	@Expose
	private String Email;
	@Expose
	private boolean IsActive;
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
	 * @return the userName
	 */
	public String getUserName() {
		return UserName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return Email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		Email = email;
	}
	/**
	 * @return the isActive
	 */
	public boolean isIsActive() {
		return IsActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setIsActive(boolean isActive) {
		IsActive = isActive;
	}
}
