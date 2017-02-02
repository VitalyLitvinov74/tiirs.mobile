package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 14.09.16.
 */
public class TaskStageTemplate extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String description;
    private String image;
    private int normative;
    private EquipmentModel equipmentModel;
    private TaskStageType taskStageType;
    private Date createdAt;
    private Date changedAt;


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

    public EquipmentModel getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(EquipmentModel equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public TaskStageType getTaskStageType() {
        return taskStageType;
    }

    public void setTaskStageType(TaskStageType taskStageType) {
        this.taskStageType = taskStageType;
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
}
