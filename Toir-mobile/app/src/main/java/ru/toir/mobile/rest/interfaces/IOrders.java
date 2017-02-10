package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17.10.16.
 */
public interface IOrders {
    @GET("/api/orders")
    Call<List<Orders>> orders(@Header("Authorization") String token);

    @GET("/api/orders")
    Call<List<Orders>> orders(@Header("Authorization") String token,
                              @Query("changedAfter") String changedAfter);

    @GET("/api/orders")
    Call<List<Orders>> ordersById(@Header("Authorization") String token,
                                  @Query("id") String id);

    @GET("/api/orders")
    Call<List<Orders>> ordersByStatus(@Header("Authorization") String token,
                                      @Query("status") String status);

    @GET("/api/orders")
    Call<List<Orders>> ordersByStatus(@Header("Authorization") String token,
                                      @Query("status") String status,
                                      @Query("XDEBUG_SESSION_START") String debugLabel);
}
