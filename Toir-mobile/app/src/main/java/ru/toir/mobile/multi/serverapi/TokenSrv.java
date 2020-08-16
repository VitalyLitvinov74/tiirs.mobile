package ru.toir.mobile.multi.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Токен
 *
 * @author Dmitriy Logachov
 */
public class TokenSrv {

    @Expose
    private String accessToken;
    @Expose
    private String tokenType;
    @Expose
    private Integer expiresIn;
    @Expose
    private String userName;
    @Expose
    private String issued;
    @Expose
    private String expires;
    @Expose
    private String db;

    /**
     * @return The accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * @param accessToken The access_token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return The tokenType
     */
    public String getTokenType() {
        return tokenType;
    }

    /**
     * @param tokenType The token_type
     */
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    /**
     * @return The expiresIn
     */
    public Integer getExpiresIn() {
        return expiresIn;
    }

    /**
     * @param expiresIn The expires_in
     */
    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * @return The userName
     */
    public String getLogin() {
        return userName;
    }

    /**
     * @param userName The userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return The Issued
     */
    public String getIssued() {
        return issued;
    }

    /**
     * @param issued The issued
     */
    public void setIssued(String issued) {
        this.issued = issued;
    }

    /**
     * @return The Expires
     */
    public String getExpires() {
        return expires;
    }

    /**
     * @param expires The expires
     */
    public void setExpires(String expires) {
        this.expires = expires;
    }

    /**
     * @return Db name
     */
    public String getDb() {
        return db;
    }

    /**
     * @param db Db name
     */
    public void setDb(String db) {
        this.db = db;
    }

    @Override
    public String toString() {
        return accessToken + ", " + tokenType + ", " + expiresIn + ", " + userName + ", " + issued + ", " + expires;
    }

    public class Type {
        public static final String LABEL = "label";
        public static final String PASSWORD = "password";
    }
}
