package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * Наряд
 * 
 * @author Dmitriy Logachov
 * 
 */
public class TaskSrv extends BaseObjectSrv {

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
	private String EmployeeId;

	/**
	 * @return
	 */
	public long getCloseDateTime() {
		return CloseDate == null ? 0 : CloseDate.getTime();
	}

	/**
	 * 
	 * @return The Items
	 */
	public List<EquipmentOperationSrv> getItems() {
		return Items;
	}

	/**
	 * 
	 * @param Items
	 *            The Items
	 */
	public void setItems(List<EquipmentOperationSrv> Items) {
		this.Items = Items;
	}

	/**
	 * 
	 * @return The Number
	 */
	public String getNumber() {
		return Number;
	}

	/**
	 * 
	 * @param Number
	 *            The Number
	 */
	public void setNumber(String Number) {
		this.Number = Number;
	}

	/**
	 * 
	 * @return The Comment
	 */
	public String getComment() {
		return Comment;
	}

	/**
	 * 
	 * @param Comment
	 *            The Comment
	 */
	public void setComment(String Comment) {
		this.Comment = Comment;
	}

	/**
	 * 
	 * @return The OrderStatus
	 */
	public TaskStatusSrv getOrderStatus() {
		return OrderStatus;
	}

	/**
	 * 
	 * @param OrderStatus
	 *            The OrderStatus
	 */
	public void setOrderStatus(TaskStatusSrv OrderStatus) {
		this.OrderStatus = OrderStatus;
	}

	/**
	 * 
	 * @return The CloseDate
	 */
	public Date getCloseDate() {
		return CloseDate;
	}

	/**
	 * 
	 * @param CloseDate
	 *            The CloseDate
	 */
	public void setCloseDate(Date CloseDate) {
		this.CloseDate = CloseDate;
	}

	/**
	 * @return the employeeId
	 */
	public String getEmployeeId() {
		return EmployeeId;
	}

	/**
	 * @param employeeId
	 *            the employeeId to set
	 */
	public void setEmployeeId(String employeeId) {
		EmployeeId = employeeId;
	}

}
