package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 13.09.16.
 */
public class OperationTemplate extends RealmObject implements IToirDbObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String description;
    private String image;
    private int normative;
    private OperationType operationType;
    private Date createdAt;
    private Date changedAt;
    private Organization organization;
    private RealmList<OperationTool> operationTools;
    private RealmList<OperationRepairPart> operationRepairParts;

    public static String getImageRoot() {
        return "otype";
    }

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

    public int getNormative() {
        return normative;
    }

    public void setNormative(int normative) {
        this.normative = normative;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
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

    @Override
    public String getImageFileName() {
        return image;
    }

    public RealmList<OperationTool> getOperationTools() {
        return operationTools;
    }

    public void setOperationTools(RealmList<OperationTool> operationTools) {
        this.operationTools = operationTools;
    }

    public RealmList<OperationRepairPart> getOperationRepairParts() {
        return operationRepairParts;
    }

    public void setOperationRepairParts(RealmList<OperationRepairPart> operationRepairParts) {
        this.operationRepairParts = operationRepairParts;
    }

    @Override
    public String getImageFilePath(String dbName) {
        String dir;
        dir = dbName + "/" + getImageRoot() + "/" + operationType.getUuid();
        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
        return "/storage/" + userName + "/" + getImageRoot() + "/" + operationType.getUuid();
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
