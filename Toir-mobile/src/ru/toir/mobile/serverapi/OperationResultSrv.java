package ru.toir.mobile.serverapi;

import ru.toir.mobile.db.tables.OperationResult;
import com.google.gson.annotations.Expose;

/**
 * Результат выполнения операции (вердикт)
 * 
 * @author Dmitriy Logachov
 * 
 */
public class OperationResultSrv extends BaseObjectSrv {

	@Expose
	private String Title;
	@Expose
	private OperationTypeSrv OperationType;

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
	 * @return the operationType
	 */
	public OperationTypeSrv getOperationType() {
		return OperationType;
	}

	/**
	 * @param operationType
	 *            the operationType to set
	 */
	public void setOperationType(OperationTypeSrv operationType) {
		OperationType = operationType;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return OperationResult
	 */
	public OperationResult getLocal() {

		OperationResult item = new OperationResult();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		item.setOperation_type_uuid(OperationType.getId());
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
