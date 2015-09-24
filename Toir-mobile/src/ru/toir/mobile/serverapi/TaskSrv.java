package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import ru.toir.mobile.db.tables.EquipmentOperation;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.db.tables.TaskStatus;

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

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return Task
	 */
	public Task getLocal() {

		Task item = new Task();

		item.set_id(0);
		item.setUuid(Id);
		item.setUsers_uuid(EmployeeId);
		item.setClose_date(getCloseDateTime());
		item.setTask_status_uuid(OrderStatus.getId());
		item.setTask_name(Number);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<Task> getTasks(TaskSrv[] tasks) {

		ArrayList<Task> list = new ArrayList<Task>();
		for (TaskSrv task : tasks) {
				list.add(task.getLocal());
		}
		return list;
	}

	public static ArrayList<EquipmentOperation> getEquipmentOperations(TaskSrv[] tasks) {

		ArrayList<EquipmentOperation> list = new ArrayList<EquipmentOperation>();
		for (TaskSrv task : tasks) {
			List<EquipmentOperationSrv> operations = task.getItems(); 
			for (EquipmentOperationSrv operation : operations) {
				list.add(operation.getLocal(task.getId()));
			}
		}
		return list;
	}

	public static ArrayList<EquipmentOperationSrv> getEquipmentOperationSrvs(TaskSrv[] tasks) {

		ArrayList<EquipmentOperationSrv> list = new ArrayList<EquipmentOperationSrv>();
		for (TaskSrv task : tasks) {
			list.addAll(task.getItems());
		}
		return list;
	}

	public static ArrayList<TaskStatus> getTaskStatuses(TaskSrv[] tasks) {

		ArrayList<TaskStatus> list = new ArrayList<TaskStatus>();
		for (TaskSrv task : tasks) {
			list.add(task.getOrderStatus().getLocal());
		}
		return list;
	}

}
