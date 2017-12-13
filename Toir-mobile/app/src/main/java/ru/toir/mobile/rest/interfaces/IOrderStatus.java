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
    @GET("/order-status")
    Call<List<OrderStatus>> get();

    @GET("/order-status")
    Call<List<OrderStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/order-status")
    Call<List<OrderStatus>> getById(@Query("id") String id);

    @GET("/order-status")
    Call<List<OrderStatus>> getById(@Query("id") String[] id);

    @GET("/order-status")
    Call<List<OrderStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/order-status")
    Call<List<OrderStatus>> getByUuid(@Query("uuid") String[] uuid);
}
