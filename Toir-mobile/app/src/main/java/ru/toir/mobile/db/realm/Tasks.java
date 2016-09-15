package ru.toir.mobile.db.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 12.09.16.
 */
public class Tasks extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String comment;
    private String orderUuid;
    private String equipmentUuid;
    private Equipment equipment;
    private String taskVerdictUuid;
    private TaskVerdict taskVerdict;
    private String taskStatusUuid;
    private TaskStatus taskStatus;
    private String taskTemplateUuid;
    private TaskTemplate taskTemplate;
    private int prevCode;
    private int nextCode;
    private int startDate;
    private int endDate;
    private long createdAt;
    private long changedAt;
    private RealmList<TaskStages> taskStages;

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
        return taskVerdictUuid;
    }

    public void setTaskVerdictUuid(String taskVerdictUuid) {
        this.taskVerdictUuid = taskVerdictUuid;
    }

    public TaskVerdict getTaskVerdict() {
        return taskVerdict;
    }

    public void setTaskVerdict(TaskVerdict taskVerdict) {
        this.taskVerdict = taskVerdict;
    }

    public String getTaskStatusUuid() {
        return taskStatusUuid;
    }

    public void setTaskStatusUuid(String taskStatusUuid) {
        this.taskStatusUuid = taskStatusUuid;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getTaskTemplateUuid() {
        return taskTemplateUuid;
    }

    public void setTaskTemplateUuid(String taskTemplateUuid) {
        this.taskTemplateUuid = taskTemplateUuid;
    }

    public TaskTemplate getTaskTemplate() {
        return taskTemplate;
    }

    public void setTaskTemplate(TaskTemplate taskTemplate) {
        this.taskTemplate = taskTemplate;
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

    public void setEndDate(int endDate) {
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

    public RealmList<TaskStages> getTaskStages() {
        return taskStages;
    }

    public void setTasks(RealmList<TaskStages> taskStages) {
        this.taskStages = taskStages;
    }

}
