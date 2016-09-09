package ru.toir.mobile.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 * Created on 10.09.16.
 */
public class Documentation extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String documentationTypeUuid;
    private String equipmentUuid;
    private String title;
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

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentationTypeUuid() {
        return documentationTypeUuid;
    }
    public void setDocumentationTypeUuid(String documentationTypeUuid) {
        this.documentationTypeUuid = documentationTypeUuid;
    }

    public String getEquipmentUuid() {
        return equipmentUuid;
    }
    public void setEquipmentUuid(String equipmentUuid) {
        this.equipmentUuid = equipmentUuid;
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
