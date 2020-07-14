package ru.toir.mobile.multi.db.realm;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author Olejek
 *         Created on 07.09.16.
 */
public class Orders extends RealmObject implements ISend {
    @PrimaryKey
    private long _id;
    private String uuid;
    private String title;
    private User author;
    private User user;
    private Contragent customer;
    private Brigade perpetrator;
    private String comment;
    private String reason;
    private Date receivDate;   // дата получения наряда
    private Date startDate;     // дата назначения наряда
    private Date createdAt;
    private Date changedAt;
    private Date openDate;      // дата начала работы над нарядом
    private Date closeDate;     // дата окончания работы над нарядом
    private OrderLevel orderLevel;
    private OrderStatus orderStatus;
    private OrderVerdict orderVerdict;
    private Date attemptSendDate;
    private int attemptCount;
    private int updated;
    private RealmList<Task> tasks;
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

    public Date getReceivDate() {
        return receivDate;
    }

    public void setReceivDate(Date receivDate) {
        this.receivDate = receivDate;
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

    public RealmList<Task> getTasks() {
        return tasks;
    }

    public void setTasks(RealmList<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public Contragent getCustomer() {
        return customer;
    }

    public void setCustomer(Contragent customer) {
        this.customer = customer;
    }

    public Brigade getPerpetrator() {
        return perpetrator;
    }

    public void setPerpetrator(Brigade perpetrator) {
        this.perpetrator = perpetrator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public boolean isNew() {
        return orderStatus.getUuid().equals(OrderStatus.Status.NEW);
    }

    public boolean isInWork() {
        return orderStatus.getUuid().equals(OrderStatus.Status.IN_WORK);
    }

    public boolean isComplete() {
        return orderStatus.getUuid().equals(OrderStatus.Status.COMPLETE);
    }

    public boolean isUnComplete() {
        return orderStatus.getUuid().equals(OrderStatus.Status.UN_COMPLETE);
    }

    public boolean isCanceled() {
        return orderStatus.getUuid().equals(OrderStatus.Status.CANCELED);
    }

}
