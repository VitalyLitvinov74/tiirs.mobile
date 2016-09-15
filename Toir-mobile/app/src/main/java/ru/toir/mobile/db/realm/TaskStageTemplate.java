package ru.toir.mobile.db.realm;

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
    private String equipmentModelUuid;
    private EquipmentModel equipmentModel;
    private String taskStageTypeUuid;
    private TaskStageType taskStageType;
    private long createdAt;
    private long changedAt;


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

    public String getEquipmentModelUuid() {
        return equipmentModelUuid;
    }

    public void setEquipmentModelUuid(String equipmentModelUuid) {
        this.equipmentModelUuid = equipmentModelUuid;
    }

    public EquipmentModel getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(EquipmentModel equipmentModel) {
        this.equipmentModel = equipmentModel;
    }

    public String getTaskStageTypeUuid() {
        return taskStageTypeUuid;
    }

    public void setTaskStageTypeUuid(String taskStageTypeUuid) {
        this.taskStageTypeUuid = taskStageTypeUuid;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(long changedAt) {
        this.changedAt = changedAt;
    }
}
