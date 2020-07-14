package ru.toir.mobile.multi.db.realm;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CommonFile extends RealmObject implements IToirDbObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String path;
    private String name;
    private String description;
    private boolean require;
    private Organization organization;
    private Date createdAt;
    private Date changedAt;

    public CommonFile() {
        uuid = UUID.randomUUID().toString().toUpperCase();
        Date createDate = new Date();
        createdAt = createDate;
        changedAt = createDate;
    }

    public static String getImageRoot() {
        return "common";
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        return name;
    }

    @Override
    public String getImageFilePath() {
        return path + '/' + name;
    }

    @Override
    public String getImageFileUrl(String userName) {
        return null;
    }

    public boolean isRequire() {
        return require;
    }

    public void setRequire(boolean require) {
        this.require = require;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
