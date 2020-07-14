package ru.toir.mobile.multi.db.realm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.HashMap;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 *         Created on 05.09.16.
 */
public class User extends RealmObject implements IToirDbObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String name;
    private String login;
    private String pass;
    private int type;
    private String tagId;
    private int active;
    private String whoIs;
    private String image;
    private String contact;
    private Date connectionDate;
    private Date createdAt;
    private Date changedAt;
    private Organization organization;

    public static String getImageRoot() {
        return "users";
    }

    /**
     * Список связей пользователей с их базами.
     *
     * @param context Context
     * @return HashMap
     */
    public static HashMap<String, String> getUsersDbLinks(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String usersDbLinkJson = sp.getString("usersDbLink", "[]");
        return new Gson().fromJson(usersDbLinkJson, new TypeToken<HashMap<String, String>>() {
        }.getType());
    }

    /**
     * @param context Context
     * @param login   String User login or tagId
     * @return String
     */
    public static String getUserDbName(Context context, String login) {
        HashMap<String, String> usersDbLinks = getUsersDbLinks(context);
        return usersDbLinks.get(login);
    }

    public static void saveUsersDbLinks(Context context, HashMap<String, String> usersDbLinks) {
        Gson gson = new Gson();
        String usersDbLinkJson = gson.toJson(usersDbLinks);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString("usersDbLink", usersDbLinkJson).apply();
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public int isActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getWhoIs() {
        return whoIs;
    }

    public void setWhoIs(String whoIs) {
        this.whoIs = whoIs;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getConnectionDate() {
        return connectionDate;
    }

    public void setConnectionDate(Date connectionDate) {
        this.connectionDate = connectionDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Date changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public String getImageFile() {
        return getImage();
    }

    @Override
    public String getImageFilePath() {
        String dir;
        dir = getImageRoot();
        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
        return "/storage/" + userName + "/" + getImageFilePath();
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
