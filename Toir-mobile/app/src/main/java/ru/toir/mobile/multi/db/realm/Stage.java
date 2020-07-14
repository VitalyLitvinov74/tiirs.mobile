package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 14.09.16.
 */
public class Stage extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String comment;
    private String taskUuid;
    private Equipment equipment;
    private StageVerdict stageVerdict;
    private StageStatus stageStatus;
    private StageTemplate stageTemplate;
    private Date startDate;
    private Date endDate;
    private int type;
    private Date createdAt;
    private Date changedAt;
    private RealmList<Operation> operations;
    private RealmList<Tool> tools;

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

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public StageVerdict getStageVerdict() {
        return stageVerdict;
    }

    public void setStageVerdict(StageVerdict stageVerdict) {
        this.stageVerdict = stageVerdict;
    }

    public StageStatus getStageStatus() {
        return stageStatus;
    }

    public void setStageStatus(StageStatus stageStatus) {
        this.stageStatus = stageStatus;
    }

    public StageTemplate getStageTemplate() {
        return stageTemplate;
    }

    public void setStageTemplate(StageTemplate stageTemplate) {
        this.stageTemplate = stageTemplate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public void setEndDate(Date closeDate) {
        this.endDate = closeDate;
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

    public RealmList<Operation> getOperations() {
        return operations;
    }

    public void setOperations(RealmList<Operation> operations) {
        this.operations = operations;
    }

    public void addOperations(Operation operation) {
        this.operations.add(operation);
    }

    public StageStatus getStatus() {
        return stageStatus;
    }

    public boolean isNew() {
        return getStatus().getUuid().equals(StageStatus.Status.NEW);
    }

    public boolean isInWork() {
        return getStatus().getUuid().equals(StageStatus.Status.IN_WORK);
    }

    public boolean isComplete() {
        return getStatus().getUuid().equals(StageStatus.Status.COMPLETE);
    }

    public boolean isUnComplete() {
        return getStatus().getUuid().equals(StageStatus.Status.UN_COMPLETE);
    }

    public boolean isCanceled() {
        return getStatus().getUuid().equals(StageStatus.Status.CANCELED);
    }

    public RealmList<Tool> getTools() {
        return tools;
    }

    public void setTools(RealmList<Tool> tools) {
        this.tools = tools;
    }
}
