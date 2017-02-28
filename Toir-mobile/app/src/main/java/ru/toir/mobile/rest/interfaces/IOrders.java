package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17.10.16.
 */
public interface IOrders {
    @GET("/api/orders")
    Call<List<Orders>> orders();

    @GET("/api/orders")
    Call<List<Orders>> orders(@Query("changedAfter") String changedAfter);

    @GET("/api/orders")
    Call<List<Orders>> ordersById(@Query("id") String id);

    @GET("/api/orders")
    Call<List<Orders>> ordersByStatus(@Query("status") String status);
}
