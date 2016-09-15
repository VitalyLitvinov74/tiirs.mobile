package ru.toir.mobile.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 * Created on 14.09.16.
 */
public class TaskStages extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String comment;
    private String taskUuid;
    private String equipmentUuid;
    private String taskStageVerdict;
    private String taskStageStatus;
    private String taskStageTemplate;
    private int startDate;
    private int endDate;
    private int flowOrder;
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

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTaskUuid() {
        return taskUuid;
    }
    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getEquipmentUuid() {
        return equipmentUuid;
    }
    public void setEquipmentUuid(String equipmentUuid) {
        this.equipmentUuid = equipmentUuid;
    }

    public String getTaskStageVerdict() {
        return taskStageVerdict;
    }
    public void setTaskStageVerdict(String taskStageVerdict) {
        this.taskStageVerdict = taskStageVerdict;
    }
    public String getTaskStageStatusUuid() {
        return taskStageStatus;
    }
    public void setTaskStageStatusUuid(String taskStageStatus) {
        this.taskStageStatus = taskStageStatus;
    }

    public String getTaskStageTemplateUuid() {
        return taskStageTemplate;
    }
    public void setTaskStageTemplateUuid(String taskStageTemplate) {
        this.taskStageTemplate = taskStageTemplate;
    }

    public int getFlowOrder() {
        return flowOrder;
    }
    public void setFlowOrder(int flowOrder) {
        this.flowOrder = flowOrder;
    }

    public int getStartDate() {
        return startDate;
    }
    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getEndDate() {
        return endDate;
    }
    public void setEndDate(int closeDate) {
        this.endDate = endDate;
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
