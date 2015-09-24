package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Результат выполнения операции (вердикт)
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
	 * @param operationType the operationType to set
	 */
	public void setOperationType(OperationTypeSrv operationType) {
		OperationType = operationType;
	}

}
