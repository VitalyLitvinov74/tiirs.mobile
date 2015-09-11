
package ru.toir.mobile.serverapi;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * Вариант выполнения текущего шага операции
 * @author Dmitriy Logachov
 *
 */
public class Result {

    @Expose
    private String Id;
    @Expose
    private String Title;
    @Expose
    private ru.toir.mobile.serverapi.NextPatternStep NextPatternStep;
    @Expose
    private ru.toir.mobile.serverapi.MeasureType MeasureType;
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
     *     The Title
     */
    public String getTitle() {
        return Title;
    }

    /**
     * 
     * @param Title
     *     The Title
     */
    public void setTitle(String Title) {
        this.Title = Title;
    }

    /**
     * 
     * @return
     *     The NextPatternStep
     */
    public ru.toir.mobile.serverapi.NextPatternStep getNextPatternStep() {
        return NextPatternStep;
    }

    /**
     * 
     * @param NextPatternStep
     *     The NextPatternStep
     */
    public void setNextPatternStep(ru.toir.mobile.serverapi.NextPatternStep NextPatternStep) {
        this.NextPatternStep = NextPatternStep;
    }

    /**
     * 
     * @return
     *     The MeasureType
     */
    public ru.toir.mobile.serverapi.MeasureType getMeasureType() {
        return MeasureType;
    }

    /**
     * 
     * @param MeasureType
     *     The MeasureType
     */
    public void setMeasureType(ru.toir.mobile.serverapi.MeasureType MeasureType) {
        this.MeasureType = MeasureType;
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
