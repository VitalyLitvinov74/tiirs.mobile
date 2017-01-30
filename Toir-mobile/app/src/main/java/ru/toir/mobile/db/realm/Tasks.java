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
    private Equipment equipment;
    private TaskVerdict taskVerdict;
    private TaskStatus taskStatus;
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

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public TaskVerdict getTaskVerdict() {
        return taskVerdict;
    }

    public void setTaskVerdict(TaskVerdict taskVerdict) {
        this.taskVerdict = taskVerdict;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
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
