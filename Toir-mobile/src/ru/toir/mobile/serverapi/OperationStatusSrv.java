package ru.toir.mobile.serverapi;

import ru.toir.mobile.db.tables.OperationStatus;
import com.google.gson.annotations.Expose;

/**
 * Текущий статус операции
 * 
 * @author Dmitriy Logachov
 * 
 */
public class OperationStatusSrv extends BaseObjectSrv {

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
	 * @return OperationStatus
	 */
	public OperationStatus getLocal() {

		OperationStatus item = new OperationStatus();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
