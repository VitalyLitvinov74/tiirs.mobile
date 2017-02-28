package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OrderStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderStatus {
    @GET("/api/orders/status")
    Call<List<OrderStatus>> orderStatus();

    @GET("/api/orders/status")
    Call<List<OrderStatus>> orderStatus(@Query("changedAfter") String changedAfter);

    @GET("/api/orders/status")
    Call<List<OrderStatus>> orderStatusById(@Query("id") String id);

}
