package ru.toir.mobile.multi.db.realm;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 5/16/17.
 */

public class OperationFile extends RealmObject implements IToirDbObject, ISend {
    @Index
    private long _id;
    @PrimaryKey
    private String uuid;
    private Operation operation;
    private String fileName;
    private boolean sent;
    private Organization organization;
    private Date createdAt;
    private Date changedAt;

    public OperationFile() {
        uuid = UUID.randomUUID().toString().toUpperCase();
        sent = false;
        Date createDate = new Date();
        createdAt = createDate;
        changedAt = createDate;
    }

    public static long getLastId() {
        Realm realm = Realm.getDefaultInstance();

        Number lastId = realm.where(OperationFile.class).max("_id");
        if (lastId == null) {
            lastId = 0;
        }

        realm.close();
        return lastId.longValue();
    }

    public static String getImageRoot() {
        return "photo";
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

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
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
        return fileName;
    }

    @Override
    public String getImageFilePath(String dbName) {
//        String imageRoot = getImageRoot();
        String dir = null;
//        Stage stage = getStage();

//        if (stage != null) {
//            dir = imageRoot + '/' + getStage().getEquipment().getUuid();
//        }

        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
//        return "/storage/" + userName + "/" + getImageFilePath();
        return null;
    }

    private Stage getStage() {
        if (operation == null) {
            return null;
        }

        Realm realm = Realm.getDefaultInstance();
        Stage stage = realm.where(Stage.class).equalTo("uuid", operation.getStageUuid())
                .findFirst();
        realm.close();

        return stage;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
