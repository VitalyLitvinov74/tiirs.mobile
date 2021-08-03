package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 14.09.16.
 */
public class StageTemplate extends RealmObject implements IToirDbObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String description;
    private String image;
    private int normative;
    private StageType stageType;
    private Organization organization;
    private Date createdAt;
    private Date changedAt;

    public static String getImageRoot() {
        return "stype";
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType = stageType;
    }

    public int getNormative() {
        return normative;
    }

    public void setNormative(int normative) {
        this.normative = normative;
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
        if(image == null) {
            return "";
        } else {
            return image;
        }
    }

    @Override
    public String getImageFilePath(String dbName) {
        String dir;
        dir = dbName + "/" + getImageRoot() + '/' + stageType.getUuid();
        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
        return "/storage/db/"  + getImageRoot() + '/' + stageType.getUuid();
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
