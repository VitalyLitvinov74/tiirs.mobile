package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17/03/19.
 */
public class AttributeType extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String name;
    private boolean refresh;
    private String units;
    private int type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getAttributeTypeString() {
        switch (type) {
            case Type.ATTRIBUTE_TYPE_FILE:
                return "Файл";
            case Type.ATTRIBUTE_TYPE_VALUE:
                return "Значение";
            case Type.ATTRIBUTE_TYPE_STRING:
                return "Строка";
            default:
                return null;
        }
    }

    public class Type {
        public static final int ATTRIBUTE_TYPE_FILE = 1;
        public static final int ATTRIBUTE_TYPE_VALUE = 2;
        public static final int ATTRIBUTE_TYPE_STRING = 3;

    }

}
