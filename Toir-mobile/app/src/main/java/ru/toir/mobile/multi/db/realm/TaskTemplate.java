package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 13.09.16.
 */
public class TaskTemplate extends RealmObject implements IToirDbObject {
    public static final String DEFAULT_TASK = "138C39D3-F0F0-443C-95E7-698A5CAC6E74";

    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String description;
    private String image;
    private int normative;
    private TaskType taskType;
    private Date createdAt;
    private Date changedAt;

    public static String getImageRoot() {
        return "ttype";
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

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
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
    public String getImageFile() {
        return getImage();
    }

    @Override
    public String getImageFilePath() {
        String imageRoot = getImageRoot();
        String typeUuid;
        String dir;

        typeUuid = taskType.getUuid();
        dir = imageRoot + '/' + typeUuid;
        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
        return "/storage/" + userName + "/" + getImageFilePath();
    }
}
