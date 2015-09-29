package ru.toir.mobile.serverapi;

import java.util.ArrayList;

import ru.toir.mobile.db.tables.TaskStatus;
import com.google.gson.annotations.Expose;

/**
 * Текущий статус наряда
 * 
 * @author Dmitriy Logachov
 * 
 */
public class TaskStatusSrv extends BaseObjectSrv {

	@Expose
	private String Title;

	/**
	 * 
	 * @return The Title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * 
	 * @param Title
	 *            The Title
	 */
	public void setTitle(String Title) {
		this.Title = Title;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return TaskStatus
	 */
	public TaskStatus getLocal() {

		TaskStatus item = new TaskStatus();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<TaskStatus> getTaskStatuses(ArrayList<TaskStatusSrv> statuses) {

		ArrayList<TaskStatus> list = new ArrayList<TaskStatus>();
		for (TaskStatusSrv status : statuses) {
			list.add(status.getLocal());
		}
		return list;
	}

}
