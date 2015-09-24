package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.List;

import ru.toir.mobile.db.tables.OperationPatternStep;

import com.google.gson.annotations.Expose;

/**
 * Шаг операции
 * 
 * @author Dmitriy Logachov
 * 
 */
public class OperationPatternStepSrv extends BaseObjectSrv {

	@Expose
	protected String Description;
	@Expose
	protected Integer IsLastStep;
	@Expose
	protected Integer IsFirstStep;
	@Expose
	protected String ImagePath;
	@Expose
	protected String Title;
	@Expose
	protected List<OperationPatternStepResultSrv> Results = new ArrayList<OperationPatternStepResultSrv>();

	/**
	 * 
	 * @return The Description
	 */
	public String getDescription() {
		return Description;
	}

	/**
	 * 
	 * @param Description
	 *            The Description
	 */
	public void setDescription(String Description) {
		this.Description = Description;
	}

	/**
	 * 
	 * @return The IsLastStep
	 */
	public Integer getIsLastStep() {
		return IsLastStep;
	}

	/**
	 * 
	 * @param IsLastStep
	 *            The IsLastStep
	 */
	public void setIsLastStep(Integer IsLastStep) {
		this.IsLastStep = IsLastStep;
	}

	/**
	 * 
	 * @return The IsFirstStep
	 */
	public Integer getIsFirstStep() {
		return IsFirstStep;
	}

	/**
	 * 
	 * @param IsFirstStep
	 *            The IsFirstStep
	 */
	public void setIsFirstStep(Integer IsFirstStep) {
		this.IsFirstStep = IsFirstStep;
	}

	/**
	 * 
	 * @return The ImagePath
	 */
	public String getImagePath() {
		return ImagePath;
	}

	/**
	 * 
	 * @param ImagePath
	 *            The ImagePath
	 */
	public void setImagePath(String ImagePath) {
		this.ImagePath = ImagePath;
	}

	/**
	 * 
	 * @return The Results
	 */
	public List<OperationPatternStepResultSrv> getResults() {
		return Results;
	}

	/**
	 * 
	 * @param Results
	 *            The Results
	 */
	public void setResults(List<OperationPatternStepResultSrv> Results) {
		this.Results = Results;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		Title = title;
	}

	/**
	 * Возвращает объект в локальном представлении
	 * 
	 * @param uuid
	 *            шаблона операции
	 * @return OperationPatternStep
	 */
	public OperationPatternStep getLocal(String uuid) {

		OperationPatternStep item = new OperationPatternStep();

		item.set_id(0);
		item.setUuid(Id);
		item.setOperation_pattern_uuid(uuid);
		item.setDescription(Description);
		item.setImage(ImagePath);
		item.setFirst_step(getIsFirstStep() == 1 ? true : false);
		item.setLast_step(getIsLastStep() == 1 ? true : false);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

}
