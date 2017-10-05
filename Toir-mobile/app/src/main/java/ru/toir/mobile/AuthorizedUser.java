package ru.toir.mobile;

/**
 * @author Dmitriy Logachev
 */
public class AuthorizedUser {

    private static AuthorizedUser mInstance;
    private String mUuid;
    private String mTagId;
    private String mToken;
    private String login;

    public static synchronized AuthorizedUser getInstance() {
        if (mInstance == null) {
            mInstance = new AuthorizedUser();
        }
        return mInstance;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the mUuid
     */
    public String getUuid() {
        return mUuid;
    }

    /**
     * @param Uuid the mUuid to set
     */
    public void setUuid(String Uuid) {
        this.mUuid = Uuid;
    }

    /**
     * @return the mTagId
     */
    public String getTagId() {
        return mTagId;
    }

    /**
     * @param TagId the mTagId to set
     */
    public void setTagId(String TagId) {
        this.mTagId = TagId;
    }

    /**
     * @return the mToken
     */
    public String getToken() {
        return mToken;
    }

    /**
     * @param Token the mToken to set
     */
    public void setToken(String Token) {
        this.mToken = Token;
    }

    /**
     * @return The bearer
     */
    public String getBearer() {
        return "bearer " + mToken;
    }
}
