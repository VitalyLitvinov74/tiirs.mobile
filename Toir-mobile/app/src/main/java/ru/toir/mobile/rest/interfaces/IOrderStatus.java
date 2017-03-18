package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
