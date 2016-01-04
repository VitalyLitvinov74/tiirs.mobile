package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import ru.toir.mobile.db.tables.OperationPatternStep;
import ru.toir.mobile.db.tables.MeasureType;
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
	protected ArrayList<OperationPatternStepResultSrv> Results = new ArrayList<OperationPatternStepResultSrv>();
	@Expose
	protected ArrayList<String> ImageLinks;

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
	public ArrayList<OperationPatternStepResultSrv> getResults() {
		return Results;
	}

	/**
	 * 
	 * @param Results
	 *            The Results
	 */
	public void setResults(ArrayList<OperationPatternStepResultSrv> Results) {
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
		if (ImageLinks.size() > 0) {
			item.setImage(ImageLinks.get(0));
		} else {
			item.setImage(null);
		}
		item.setFirst_step(getIsFirstStep() == 1 ? true : false);
		item.setLast_step(getIsLastStep() == 1 ? true : false);
		item.setTitle(Title);
		item.setCreatedAt(getCreatedAtTime());
		item.setChangedAt(getChangedAtTime());

		return item;
	}

	public static ArrayList<MeasureType> getMeasureTypes(ArrayList<OperationPatternStepSrv> patternSteps) {

		ArrayList<MeasureType> list = new ArrayList<MeasureType>();
		for (OperationPatternStepSrv step : patternSteps) {
			ArrayList<OperationPatternStepResultSrv> results = step.getResults();
			for (OperationPatternStepResultSrv result : results) {
				list.add(result.getMeasureType().getLocal());
			}
		}
		return list;
	}

	/**
	 * @return the imageLinks
	 */
	public ArrayList<String> getImageLinks() {
		return ImageLinks;
	}

	/**
	 * @param imageLinks the imageLinks to set
	 */
	public void setImageLinks(ArrayList<String> imageLinks) {
		ImageLinks = imageLinks;
	}

}
