package ru.toir.mobile;

/**
 * @author koputo
 * <p>Класс реализует пользователей</p>
 *
 */
public class Users {
	public static final String TABLE_NAME = "users";
	private long id;
	public static final String ID_NAME = "_id";
	public static final int ID_COLUMN = 0;
	private String name;
	public static final String NAME_NAME = "name";
	public static final int NAME_COLUMN = 1;
	private String login;
	public static final String LOGIN_NAME = "login";
	public static final int LOGIN_COLUMN = 2;
	private String pass;
	public static final String PASS_NAME = "pass";
	public static final int PASS_COLUMN = 3;
	private int type;
	public static final String TYPE_NAME = "type";
	public static final int TYPE_COLUMN = 4;
	
	/**
	 * @return
	 */
	public Users(){
		id = 0;
		name = "";
		login = "";
		pass = "";
		type = 0;
	}

	/**
	 * @param id
	 * @param name
	 * @param login
	 * @param pass
	 * @param type
	 */
	public Users(long id, String name, String login, String pass, int type){
		this.id = id;
		this.name = name;
		this.login = login;
		this.pass = pass;
		this.type = type;
	}

	/**
	 * @return
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * @param login
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * @return
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 */
	public void setType(int type) {
		this.type = type;
	}
}
