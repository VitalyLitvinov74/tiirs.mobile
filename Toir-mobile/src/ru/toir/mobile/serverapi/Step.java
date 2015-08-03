
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Step {

    @Expose
    private String Id;
    @Expose
    private String Description;
    @Expose
    private Integer IsLastStep;
    @Expose
    private Integer IsFirstStep;
    @Expose
    private Object ImagePath;
    @Expose
    private List<Result> Results = new ArrayList<Result>();

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
    public Object getImagePath() {
        return ImagePath;
    }

    /**
     * 
     * @param ImagePath
     *     The ImagePath
     */
    public void setImagePath(Object ImagePath) {
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

}
