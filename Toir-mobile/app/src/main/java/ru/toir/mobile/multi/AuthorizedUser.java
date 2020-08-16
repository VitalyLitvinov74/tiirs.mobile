package ru.toir.mobile.multi;

/**
 * @author Dmitriy Logachev
 */
public class AuthorizedUser {

    private static AuthorizedUser mInstance;
    private String mUuid;
    private String mTagId;
    private String mToken;
    private String mLogin;
    private String mOrganizationUuid;
    private String mIdentity;
    private boolean mIsLogged;
    private boolean mIsServerLogged;
    private boolean mIsLocalLogged;
    private int mLoginType;
    private String mPassword;
    private String mDbName;

    public static synchronized AuthorizedUser getInstance() {
        if (mInstance == null) {
            mInstance = new AuthorizedUser();
        }
        return mInstance;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String login) {
        mLogin = login;
    }

    /**
     * @return the mUuid
     */
    public String getUuid() {
        return mUuid;
    }

    /**
     * @param uuid the mUuid to set
     */
    public void setUuid(String uuid) {
        mUuid = uuid;
    }

    /**
     * @return the mTagId
     */
    public String getTagId() {
        return mTagId;
    }

    /**
     * @param tagId the mTagId to set
     */
    public void setTagId(String tagId) {
        mTagId = tagId;
    }

    /**
     * @return the mToken
     */
    public String getToken() {
        return mToken;
    }

    /**
     * @param token the mToken to set
     */
    public void setToken(String token) {
        mToken = token;
    }

    /**
     * @return The bearer
     */
    public String getBearer() {
        return "bearer " + mToken;
    }

    /**
     * Обнуляем информацию о текущем пользователе.
     */
    public void reset() {
        mLogin = null;
        mTagId = null;
        mToken = null;
        mUuid = null;
        mOrganizationUuid = null;
        mIdentity = null;
        mIsLogged = false;
        mIsServerLogged = false;
        mIsLocalLogged = false;
        mLoginType = -1;
        mPassword = null;
        mDbName = null;
    }

    public String getOrganizationUuid() {
        return mOrganizationUuid;
    }

    public void setOrganizationUuid(String organizationUuid) {
        mOrganizationUuid = organizationUuid;
    }

    public String getIdentity() {
        return mIdentity;
    }

    public void setIdentity(String identity) {
        mIdentity = identity;
    }

    public boolean isLogged() {
        return mIsLogged;
    }

    public void setLogged(boolean isLogged) {
        mIsLogged = isLogged;
    }

    public boolean isLocalLogged() {
        return mIsLocalLogged;
    }

    public void setLocalLogged(boolean isLocalLogged) {
        mIsLocalLogged = isLocalLogged;
    }

    public int loginType() {
        return mLoginType;
    }

    public void setLoginType(int loginType) {
        mLoginType = loginType;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public String getDbName() {
        return mDbName;
    }

    public void setDbName(String dbName) {
        mDbName = dbName;
    }

    public boolean isServerLogged() {
        return mIsServerLogged;
    }

    public void setServerLogged(boolean isServerLogged) {
        mIsServerLogged = isServerLogged;
    }
}
