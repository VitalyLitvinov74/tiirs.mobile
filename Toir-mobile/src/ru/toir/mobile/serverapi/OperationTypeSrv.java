package ru.toir.mobile.serverapi;

import ru.toir.mobile.db.tables.OperationType;
import com.google.gson.annotations.Expose;

/**
 * Тип операции
 * 
 * @author Dmitriy Logachov
 * 
 */
public class OperationTypeSrv extends BaseObjectSrv {

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
	 * @return OperationType
	 */
	public OperationType getLocal() {

		OperationType item = new OperationType();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
