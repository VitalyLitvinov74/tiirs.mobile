
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * Наряд
 * @author Dmitriy Logachov
 *
 */
public class TaskSrv {

    @Expose
    private List<EquipmentOperationSrv> Items = new ArrayList<EquipmentOperationSrv>();
    @Expose
    private String Number;
    @Expose
    private String Comment;
    @Expose
    private TaskStatusSrv OrderStatus;
    @Expose
    private Date CloseDate;
    @Expose
    private String Id;
    @Expose
    private String EmployeeId;
    @Expose
    private Date CreatedAt;
    @Expose
    private Date ChangedAt;

    /**
     * 
     * @return
     *     The Items
     */
    public List<EquipmentOperationSrv> getItems() {
        return Items;
    }

    /**
     * 
     * @param Items
     *     The Items
     */
    public void setItems(List<EquipmentOperationSrv> Items) {
        this.Items = Items;
    }

    /**
     * 
     * @return
     *     The Number
     */
    public String getNumber() {
        return Number;
    }

    /**
     * 
     * @param Number
     *     The Number
     */
    public void setNumber(String Number) {
        this.Number = Number;
    }

    /**
     * 
     * @return
     *     The Comment
     */
    public String getComment() {
        return Comment;
    }

    /**
     * 
     * @param Comment
     *     The Comment
     */
    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    /**
     * 
     * @return
     *     The OrderStatus
     */
    public TaskStatusSrv getOrderStatus() {
        return OrderStatus;
    }

    /**
     * 
     * @param OrderStatus
     *     The OrderStatus
     */
    public void setOrderStatus(TaskStatusSrv OrderStatus) {
        this.OrderStatus = OrderStatus;
    }

    /**
     * 
     * @return
     *     The CloseDate
     */
    public Date getCloseDate() {
        return CloseDate;
    }

    /**
     * 
     * @param CloseDate
     *     The CloseDate
     */
    public void setCloseDate(Date CloseDate) {
        this.CloseDate = CloseDate;
    }

    /**
     * 
     * @return
     *     The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The Id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

	/**
	 * @return the employeeId
	 */
	public String getEmployeeId() {
		return EmployeeId;
	}

	/**
	 * @param employeeId the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		EmployeeId = employeeId;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		CreatedAt = createdAt;
	}

	/**
	 * @param changedAt the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
	}

	/**
	 * @return the changedAt
	 */
	public Date getChangedAt() {
		return ChangedAt;
	}

}
