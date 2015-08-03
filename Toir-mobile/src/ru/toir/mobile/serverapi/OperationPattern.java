
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class OperationPattern {

    @Expose
    private String Id;
    @Expose
    private String Title;
    @Expose
    private List<Step> Steps = new ArrayList<Step>();

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
     *     The Steps
     */
    public List<Step> getSteps() {
        return Steps;
    }

    /**
     * 
     * @param Steps
     *     The Steps
     */
    public void setSteps(List<Step> Steps) {
        this.Steps = Steps;
    }

}
