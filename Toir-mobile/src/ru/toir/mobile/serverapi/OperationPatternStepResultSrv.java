
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Вариант выполнения текущего шага операции
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
    public String getNextPatternStepId() {
        return NextPatternStepId;
    }

    /**
     * 
     * @param NextPatternStep
     *     The NextPatternStep
     */
    public void setNextPatternStepId(String NextPatternStepId) {
        this.NextPatternStepId = NextPatternStepId;
    }

    /**
     * 
     * @return
     *     The MeasureType
     */
    public MeasureTypeSrv getMeasureType() {
        return MeasureType;
    }

    /**
     * 
     * @param MeasureType
     *     The MeasureType
     */
    public void setMeasureType(MeasureTypeSrv MeasureType) {
        this.MeasureType = MeasureType;
    }

}
