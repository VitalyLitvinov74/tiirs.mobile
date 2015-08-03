
package ru.toir.mobile.serverapi;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;

public class Task {

    @Expose
    private List<Item> Items = new ArrayList<Item>();
    @Expose
    private Integer Number;
    @Expose
    private String Comment;
    @Expose
    private ru.toir.mobile.serverapi.OrderStatus OrderStatus;
    @Expose
    private Integer CloseDate;
    @Expose
    private String Id;
    @Expose
    private String CreatedAt;
    @Expose
    private String ChangedAt;

    /**
     * 
     * @return
     *     The Items
     */
    public List<Item> getItems() {
        return Items;
    }

    /**
     * 
     * @param Items
     *     The Items
     */
    public void setItems(List<Item> Items) {
        this.Items = Items;
    }

    /**
     * 
     * @return
     *     The Number
     */
    public Integer getNumber() {
        return Number;
    }

    /**
     * 
     * @param Number
     *     The Number
     */
    public void setNumber(Integer Number) {
        this.Number = Number;
    }

    /**
     * 
     * @return
     *     The Comment
     */
    public String getComment() {
        return Comment;
    }

    /**
     * 
     * @param Comment
     *     The Comment
     */
    public void setComment(String Comment) {
        this.Comment = Comment;
    }

    /**
     * 
     * @return
     *     The OrderStatus
     */
    public ru.toir.mobile.serverapi.OrderStatus getOrderStatus() {
        return OrderStatus;
    }

    /**
     * 
     * @param OrderStatus
     *     The OrderStatus
     */
    public void setOrderStatus(ru.toir.mobile.serverapi.OrderStatus OrderStatus) {
        this.OrderStatus = OrderStatus;
    }

    /**
     * 
     * @return
     *     The CloseDate
     */
    public Integer getCloseDate() {
        return CloseDate;
    }

    /**
     * 
     * @param CloseDate
     *     The CloseDate
     */
    public void setCloseDate(Integer CloseDate) {
        this.CloseDate = CloseDate;
    }

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
     *     The CreatedAt
     */
    public String getCreatedAt() {
        return CreatedAt;
    }

    /**
     * 
     * @param CreatedAt
     *     The CreatedAt
     */
    public void setCreatedAt(String CreatedAt) {
        this.CreatedAt = CreatedAt;
    }

    /**
     * 
     * @return
     *     The ChangedAt
     */
    public String getChangedAt() {
        return ChangedAt;
    }

    /**
     * 
     * @param ChangedAt
     *     The ChangedAt
     */
    public void setChangedAt(String ChangedAt) {
        this.ChangedAt = ChangedAt;
    }

}
