package ru.toir.mobile.serverapi;

import ru.toir.mobile.db.tables.OperationPatternStepResult;

import com.google.gson.annotations.Expose;

/**
 * Вариант выполнения текущего шага операции
 * 
 * @author Dmitriy Logachov
 * 
 */
public class OperationPatternStepResultSrv extends BaseObjectSrv {

	@Expose
	private String Title;
	@Expose
	private String NextPatternStepId;
	@Expose
	private MeasureTypeSrv MeasureType;

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
	 * @return The NextPatternStep
	 */
	public String getNextPatternStepId() {
		return NextPatternStepId;
	}

	/**
	 * 
	 * @param NextPatternStep
	 *            The NextPatternStep
	 */
	public void setNextPatternStepId(String NextPatternStepId) {
		this.NextPatternStepId = NextPatternStepId;
	}

	/**
	 * 
	 * @return The MeasureType
	 */
	public MeasureTypeSrv getMeasureType() {
		return MeasureType;
	}

	/**
	 * 
	 * @param MeasureType
	 *            The MeasureType
	 */
	public void setMeasureType(MeasureTypeSrv MeasureType) {
		this.MeasureType = MeasureType;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @param uuid
	 *            шага шаблона операции
	 * @return OperationPatternStepResult
	 */
	public OperationPatternStepResult getLocal(String uuid) {

		OperationPatternStepResult item = new OperationPatternStepResult();

		item.set_id(0);
		item.setUuid(Id);
		item.setOperation_pattern_step_uuid(uuid);
		item.setNext_operation_pattern_step_uuid(NextPatternStepId == null ? "00000000-0000-0000-0000-000000000000" : NextPatternStepId);
		item.setTitle(Title);
		item.setMeasure_type_uuid(MeasureType.getId());
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
