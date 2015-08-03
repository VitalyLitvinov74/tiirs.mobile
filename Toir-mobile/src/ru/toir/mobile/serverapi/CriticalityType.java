
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

public class CriticalityType {

    @Expose
    private String Id;
    @Expose
    private Integer Value;

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
     *     The Value
     */
    public Integer getValue() {
        return Value;
    }

    /**
     * 
     * @param Value
     *     The Value
     */
    public void setValue(Integer Value) {
        this.Value = Value;
    }

}
