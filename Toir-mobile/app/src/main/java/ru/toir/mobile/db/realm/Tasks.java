package ru.toir.mobile.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 * Created on 12.09.16.
 */
public class Tasks extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String comment;
    private String orderUuid;
    private String equipmentUuid;
    private String taskVerdictUuid;
    private String taskStatusUuid;
    private String taskTemplateUuid;
    private int prevCode;
    private int nextCode;
    private int startDate;
    private int endDate;
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

    public String getOrderUuid() {
        return orderUuid;
    }
    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getEquipmentUuid() {
        return equipmentUuid;
    }
    public void setEquipmentUuid(String equipmentUuid) {
        this.equipmentUuid = equipmentUuid;
    }

    public String getTaskVerdictUuid() {
        return equipmentUuid;
    }
    public void setTaskVerdictUuid(String taskVerdictUuid) {
        this.taskVerdictUuid = taskVerdictUuid;
    }
    public String getTaskStatusUuid() {
        return taskStatusUuid;
    }
    public void setTaskStatusUuid(String taskStatusUuid) {
        this.taskStatusUuid = taskStatusUuid;
    }

    public String getTaskTemplateUuid() {
        return taskTemplateUuid;
    }
    public void setTaskTemplateUuid(String taskTemplateUuid) {
        this.taskTemplateUuid = taskTemplateUuid;
    }

    public int getPrevCode() {
        return prevCode;
    }
    public void setPrevCode(int prevCode) {
        this.prevCode = prevCode;
    }

    public int getNextCode() {
        return nextCode;
    }
    public void setNextCode(int nextCode) {
        this.nextCode = nextCode;
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
