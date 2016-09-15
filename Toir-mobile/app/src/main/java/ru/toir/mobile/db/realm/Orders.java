package ru.toir.mobile.db.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 * Created on 07.09.16.
 */
public class Orders extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private String authorUuid;
    private String userUuid;
    private int receiveDate;
    private int startDate;
    private long createdAt;
    private long changedAt;
    private int openDate;
    private int closeDate;
    private String orderStatusUuid;
    private String orderVerdictUuid;
    private int attemptSendDate;
    private int attemptCount;
    private int updated;

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

    public String getAuthorUuid() {
        return authorUuid;
    }
    public void setAuthorUuid(String authorUuid) {
        this.authorUuid = authorUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }
    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public int getReceiveDate() {
        return receiveDate;
    }
    public void setReceiveDate(int receiveDate) {
        this.receiveDate = receiveDate;
    }

    public int getStartDate() {
        return startDate;
    }
    public void setStartDate(int startDate) {
        this.startDate = startDate;
    }

    public int getOpenDate() {
        return openDate;
    }
    public void setOpenDate(int openDate) {
        this.openDate = openDate;
    }

    public int getCloseDate() {
        return closeDate;
    }
    public void setCloseDate(int closeDate) {
        this.closeDate = closeDate;
    }

    public String getOrderStatusUuid() {
        return orderStatusUuid;
    }
    public void setOrderStatusUuid(String orderStatusUuid) {
        this.orderStatusUuid = orderStatusUuid;
    }

    public String getVerdictStatusUuid() {
        return orderVerdictUuid;
    }
    public void setVerdictStatusUuid(String orderVerdictUuid) {
        this.orderVerdictUuid = orderVerdictUuid;
    }

    public int getAttemptSendDate() {
        return attemptSendDate;
    }
    public void setAttemptSendDate(int attemptSendDate) {
        this.attemptSendDate = attemptSendDate;
    }

    public int getAttemptCount() {
        return attemptCount;
    }
    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public int getUpdate() {
        return updated;
    }
    public void setUpdate(int updated) {
        this.updated = updated;
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
