package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 12.09.16.
 */
public class Task extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String orderUuid;
    private String comment;
    private TaskVerdict taskVerdict;
    private TaskStatus taskStatus;
    private TaskTemplate taskTemplate;
    private int prevCode;
    private int nextCode;
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Date changedAt;
    private RealmList<Stage> stages;
    private Organization organization;

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

    public RealmList<Stage> getStages() {
        return stages;
    }

    public void setStages(RealmList<Stage> stages) {
        this.stages = stages;
    }

    public void addStage(Stage stage) {
        this.stages.add(stage);
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }

    public boolean isNew() {
        return getStatus().getUuid().equals(TaskStatus.Status.NEW);
    }

    public boolean isInWork() {
        return getStatus().getUuid().equals(TaskStatus.Status.IN_WORK);
    }

    public boolean isComplete() {
        return getStatus().getUuid().equals(TaskStatus.Status.COMPLETE);
    }

    public boolean isUnComplete() {
        return getStatus().getUuid().equals(TaskStatus.Status.UN_COMPLETE);
    }

    public boolean isCanceled() {
        return getStatus().getUuid().equals(TaskStatus.Status.CANCELED);
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
