package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.OperationPattern;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.db.tables.OperationPatternStepResult;

import com.google.gson.annotations.Expose;

/**
 * Шаблон операции
 * 
 * @author Dmitriy Logachov
 * 
 */
public class OperationPatternSrv extends BaseObjectSrv {

	@Expose
	private String Title;
	@Expose
	private OperationTypeSrv operationType;
	@Expose
	private ArrayList<OperationPatternStepSrv> Steps = new ArrayList<OperationPatternStepSrv>();

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
	 * 
	 * @return The Steps
	 */
	public ArrayList<OperationPatternStepSrv> getSteps() {
		return Steps;
	}

	/**
	 * 
	 * @param Steps
	 *            The Steps
	 */
	public void setSteps(ArrayList<OperationPatternStepSrv> Steps) {
		this.Steps = Steps;
	}

	/**
	 * @return the operationType
	 */
	public OperationTypeSrv getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType
	 *            the operationType to set
	 */
	public void setOperationType(OperationTypeSrv operationType) {
		this.operationType = operationType;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @return OperationPattern
	 */
	public OperationPattern getLocal() {

		OperationPattern item = new OperationPattern();

		item.set_id(0);
		item.setUuid(Id);
		item.setTitle(Title);
		// TODO когда на сервере появится - добавить
		item.setOperation_type_uuid("");
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<OperationPatternStep> getOperationPatternSteps(OperationPatternSrv pattern) {

		ArrayList<OperationPatternStep> list = new ArrayList<OperationPatternStep>();
		ArrayList<OperationPatternStepSrv> steps = pattern.getSteps();
		for (OperationPatternStepSrv step : steps) {
			list.add(step.getLocal(pattern.getId()));
		}
		return list;
	}

	public static ArrayList<OperationPatternStepResult> getOperationPatternStepResults(OperationPatternSrv pattern) {

		ArrayList<OperationPatternStepResult> list = new ArrayList<OperationPatternStepResult>();
		ArrayList<OperationPatternStepSrv> steps = pattern.getSteps();
		for (OperationPatternStepSrv step : steps) {
			ArrayList<OperationPatternStepResultSrv> results = step.getResults();
			for (OperationPatternStepResultSrv result : results) {
				list.add(result.getLocal(step.getId()));
			}
		}
		return list;
	}
	
}
