/**
 * 
 */
package ru.toir.mobile.db.tables;

/**
 * @author Dmitriy Logachov
 *
 */
public class Token {
	private String token_type;
	private String access_token;
	private int expires_in;
	private String userName;
	private String issued;
	private String expires;

	/**
	 * 
	 */
	public Token() {
	}

	/**
	 * 
	 */
	public Token(String token_type, String access_token, int expires_in, String userName, String issued, String expires) {
		this.token_type = token_type;
		this.access_token = access_token;
		this.expires_in = expires_in;
		this.userName = userName;
		this.issued = issued;
		this.expires = expires;
	}

	/**
	 * @return the token_type
	 */
	public String getToken_type() {
		return token_type;
	}

	/**
	 * @param token_type the token_type to set
	 */
	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @param access_token the access_token to set
	 */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	/**
	 * @return the expires_in
	 */
	public int getExpires_in() {
		return expires_in;
	}

	/**
	 * @param expires_in the expires_in to set
	 */
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the issued
	 */
	public String getIssued() {
		return issued;
	}

	/**
	 * @param issued the issued to set
	 */
	public void setIssued(String issued) {
		this.issued = issued;
	}

	/**
	 * @return the expires
	 */
	public String getExpires() {
		return expires;
	}

	/**
	 * @param expires the expires to set
	 */
	public void setExpires(String expires) {
		this.expires = expires;
	}

}
