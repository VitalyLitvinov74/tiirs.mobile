/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author koputo
 *
 */
public class Users {
	
	private long _id;
	private String name;
	private String login;
	private String pass;
	private int type;
	private String tag_id;

	/**
	 * 
	 */
	public Users() {

	}

	public Users(long _id, String name, String login, String pass, int type, String tag_id) {
		this._id = _id;
		this.name = name;
		this.login = login;
		this.pass = pass;
		this.type = type;
		this.tag_id = tag_id;
	}

	/**
	 * @return the _id
	 */
	public long get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(long _id) {
		this._id = _id;
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

		return "_id=" + _id + ", name=" + name + ", login=" + login + ", pass=" + pass + ", type=" + type + ", tag_id=" + tag_id;
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

}
