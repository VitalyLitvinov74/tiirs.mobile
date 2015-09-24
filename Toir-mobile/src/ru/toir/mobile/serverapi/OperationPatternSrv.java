
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

/**
 * Шаблон операции
 * @author Dmitriy Logachov
 *
 */
public class OperationPatternSrv extends BaseObjectSrv {

    @Expose
    private String Title;
    @Expose
    private OperationTypeSrv operationType;
    @Expose
    private List<OperationPatternStepSrv> Steps = new ArrayList<OperationPatternStepSrv>();

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
    public List<OperationPatternStepSrv> getSteps() {
        return Steps;
    }

    /**
     * 
     * @param Steps
     *     The Steps
     */
    public void setSteps(List<OperationPatternStepSrv> Steps) {
        this.Steps = Steps;
    }

	/**
	 * @return the operationType
	 */
	public OperationTypeSrv getOperationType() {
		return operationType;
	}

	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(OperationTypeSrv operationType) {
		this.operationType = operationType;
	}

}
