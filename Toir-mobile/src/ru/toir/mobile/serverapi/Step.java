
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * Шаг операции
 * @author Dmitriy Logachov
 *
 */
public class Step {

    @Expose
    protected String Id;
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
    protected List<Result> Results = new ArrayList<Result>();
    @Expose
	private Date CreatedAt;
	@Expose
	private Date ChangedAt;

    /**
     * 
     * @return
     *     The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * 
     * @param Id
     *     The Id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    /**
     * 
     * @return
     *     The Description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * 
     * @param Description
     *     The Description
     */
    public void setDescription(String Description) {
        this.Description = Description;
    }

    /**
     * 
     * @return
     *     The IsLastStep
     */
    public Integer getIsLastStep() {
        return IsLastStep;
    }

    /**
     * 
     * @param IsLastStep
     *     The IsLastStep
     */
    public void setIsLastStep(Integer IsLastStep) {
        this.IsLastStep = IsLastStep;
    }

    /**
     * 
     * @return
     *     The IsFirstStep
     */
    public Integer getIsFirstStep() {
        return IsFirstStep;
    }

    /**
     * 
     * @param IsFirstStep
     *     The IsFirstStep
     */
    public void setIsFirstStep(Integer IsFirstStep) {
        this.IsFirstStep = IsFirstStep;
    }

    /**
     * 
     * @return
     *     The ImagePath
     */
    public String getImagePath() {
        return ImagePath;
    }

    /**
     * 
     * @param ImagePath
     *     The ImagePath
     */
    public void setImagePath(String ImagePath) {
        this.ImagePath = ImagePath;
    }

    /**
     * 
     * @return
     *     The Results
     */
    public List<Result> getResults() {
        return Results;
    }

    /**
     * 
     * @param Results
     *     The Results
     */
    public void setResults(List<Result> Results) {
        this.Results = Results;
    }

	/**
	 * @return the title
	 */
	public String getTitle() {
		return Title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		Title = title;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return CreatedAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		CreatedAt = createdAt;
	}

	/**
	 * @return the changedAt
	 */
	public Date getChangedAt() {
		return ChangedAt;
	}

	/**
	 * @param changedAt the changedAt to set
	 */
	public void setChangedAt(Date changedAt) {
		ChangedAt = changedAt;
	}

}
