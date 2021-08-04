package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 10.09.16.
 */
public class Documentation extends RealmObject implements IToirDbObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private DocumentationType documentationType;
    private Equipment equipment;
    private EquipmentModel equipmentModel;
    private String title;
    private String path;
    private Organization organization;
    private Date createdAt;
    private Date changedAt;
    private boolean required;

    public static String getImageRoot() {
        return "doc";
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public DocumentationType getDocumentationType() {
        return documentationType;
    }

    public void setDocumentationType(DocumentationType documentationType) {
        this.documentationType = documentationType;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public EquipmentModel getEquipmentModel() {
        return equipmentModel;
    }

    public void setEquipmentModel(EquipmentModel equipmentModel) {
        this.equipmentModel = equipmentModel;
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

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String getImageFileName() {
        if(path == null) {
            return "";
        } else {
            return path;
        }
    }

    @Override
    public String getImageFilePath(String dbName) {
        String typeUuid;
        String dir;

        if (equipmentModel != null) {
            typeUuid = equipmentModel.getUuid();
        } else if (equipment.getEquipmentModel() != null) {
            typeUuid = equipment.getEquipmentModel().getUuid();
        } else {
            return null;
        }

        dir = dbName + "/" + getImageRoot() + '/' + typeUuid;
        return dir;
    }

    @Override
    public String getImageFileUrl(String userName) {
        String typeUuid;
        if (equipmentModel != null) {
            typeUuid = equipmentModel.getUuid();
        } else if (equipment.getEquipmentModel() != null) {
            typeUuid = equipment.getEquipmentModel().getUuid();
        } else {
            return null;
        }

        return "/storage/" + userName + "/" + getImageRoot() + "/" + typeUuid;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
}
