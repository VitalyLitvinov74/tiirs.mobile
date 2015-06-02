/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author koputo
 *
 */
public class Users {
	
	private String uuid;
	private String name;
	private String login;
	private String pass;
	private int type;
	private String tag_id;
	private boolean active;

	/**
	 * 
	 */
	public Users() {

	}

	public Users(String uuid, String name, String login, String pass, int type, String tag_id, boolean active) {
		this.uuid = uuid;
		this.name = name;
		this.login = login;
		this.pass = pass;
		this.type = type;
		this.tag_id = tag_id;
		this.active = active;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param _id the _id to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}


	@Override
	public String toString() {

		return "uuid=" + uuid + ", name=" + name + ", login=" + login + ", pass=" + pass + ", type=" + type + ", tag_id=" + tag_id + ", active=" + active;
	}

	/**
	 * @return the tag_id
	 */
	public String getTag_id() {
		return tag_id;
	}

	/**
	 * @param tag_id the tag_id to set
	 */
	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

}
