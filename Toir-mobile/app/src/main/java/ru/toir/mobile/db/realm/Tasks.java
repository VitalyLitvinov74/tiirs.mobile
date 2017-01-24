package ru.toir.mobile.db.realm;

import java.util.Date;

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
    //private String orderUuid;
    private Orders order;
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
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Date changedAt;
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

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public String getEquipmentUuid() {
        return equipmentUuid;
    }

    public void setEquipmentUuid(String equipmentUuid) {
        this.equipmentUuid = equipmentUuid;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public RealmList<TaskStages> getTaskStages() {
        return taskStages;
    }

    public void setTaskStages(RealmList<TaskStages> taskStages) {
        this.taskStages = taskStages;
    }

    public void addTaskStage(TaskStages taskStage) {
        this.taskStages.add(taskStage);
    }


}
