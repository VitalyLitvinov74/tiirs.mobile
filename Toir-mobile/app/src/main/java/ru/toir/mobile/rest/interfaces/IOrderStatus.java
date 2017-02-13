package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OrderStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderStatus {
    @GET("/references/order-status")
    Call<List<OrderStatus>> orderStatus();

    @GET("/references/order-status")
    Call<List<OrderStatus>> orderStatus(@Query("changedAfter") String changedAfter);

    @GET("/references/order-status")
    Call<List<OrderStatus>> orderStatusById(@Query("id") String id);

}
