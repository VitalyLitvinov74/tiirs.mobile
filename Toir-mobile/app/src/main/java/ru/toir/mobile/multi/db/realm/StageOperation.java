package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/20/18.
 */

public class StageOperation extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private StageTemplate stageTemplate;
    private OperationTemplate operationTemplate;
    private Organization organization;
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

    public StageTemplate getStageTemplate() {
        return stageTemplate;
    }

    public void setStageTemplate(StageTemplate stageTemplate) {
        this.stageTemplate = stageTemplate;
    }

    public OperationTemplate getOperationTemplate() {
        return operationTemplate;
    }

    public void setOperationTemplate(OperationTemplate operationTemplate) {
        this.operationTemplate = operationTemplate;
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

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
