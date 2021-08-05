package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 03.04.17.
 */
public class Objects extends RealmObject implements IToirDbObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private ObjectType objectType;
    private Objects parent;
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private String photo;
    private Organization organization;
    private Date createdAt;
    private Date changedAt;

    public static String getImageRoot() {
        return "object";
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Objects parent) {
        this.parent = parent;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return photo;
    }

    public void setImage(String image) {
        this.photo = image;
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
    public String getImageFileName() {
        if(photo == null) {
            return "";
        } else {
            return photo;
        }
    }

    @Override
    public String getImageFilePath(String dbName) {
        String dir;
        dir = dbName + "/" + getImageRoot();
        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
        return "storage/db/" + getImageRoot();
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
