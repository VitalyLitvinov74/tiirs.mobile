package ru.toir.mobile.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class Orders extends RealmObject {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private User author;
    private User user;
    private Date receiveDate;
    private Date startDate;
    private Date createdAt;
    private Date changedAt;
    private Date openDate;
    private Date closeDate;
    private OrderLevel orderLevel;
    private OrderStatus orderStatus;
    private OrderVerdict orderVerdict;
    private Date attemptSendDate;
    private int attemptCount;
    private int updated;
    private RealmList<Tasks> tasks;
    private boolean sent;

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public Date getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(Date closeDate) {
        this.closeDate = closeDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderVerdict getOrderVerdict() {
        return orderVerdict;
    }

    public void setOrderVerdict(OrderVerdict orderVerdict) {
        this.orderVerdict = orderVerdict;
    }

    public OrderLevel getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(OrderLevel orderLevel) {
        this.orderLevel = orderLevel;
    }

    public Date getAttemptSendDate() {
        return attemptSendDate;
    }

    public void setAttemptSendDate(Date attemptSendDate) {
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

    public RealmList<Tasks> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<Tasks> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Tasks task) {
        this.tasks.add(task);
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }
}
