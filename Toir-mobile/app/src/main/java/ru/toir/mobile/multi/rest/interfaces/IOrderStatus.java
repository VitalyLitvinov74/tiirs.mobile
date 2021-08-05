package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.OrderStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderStatus {
    @GET("/api/order-status")
    Call<List<OrderStatus>> get();

    @GET("/api/order-status")
    Call<List<OrderStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/order-status")
    Call<List<OrderStatus>> getById(@Query("id") String id);

    @GET("/api/order-status")
    Call<List<OrderStatus>> getById(@Query("id[]") String[] id);

    @GET("/api/order-status")
    Call<List<OrderStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/order-status")
    Call<List<OrderStatus>> getByUuid(@Query("uuid[]") String[] uuid);
}
