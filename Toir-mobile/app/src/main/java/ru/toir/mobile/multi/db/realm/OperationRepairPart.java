package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 2/20/18.
 */

public class OperationRepairPart extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String operationTemplateUuid;
    private RepairPart repairPart;
    private int quantity;
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

    public String getOperationTemplateUuid() {
        return operationTemplateUuid;
    }

    public void setOperationTemplateUuid(String operationTemplateUuid) {
        this.operationTemplateUuid = operationTemplateUuid;
    }

    public RepairPart getRepairPart() {
        return repairPart;
    }

    public void setRepairPart(RepairPart repairPart) {
        this.repairPart = repairPart;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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
