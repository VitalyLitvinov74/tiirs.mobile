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
public interface IOrder {
    @GET("/references/orders")
    Call<List<Orders>> orders(@Header("Authorization") String token,
                             @Query("userUuid") String userUuid);
    @GET("/references/orders")
    Call<List<Orders>> orders(@Header("Authorization") String token,
                              @Query("userUuid") String userUuid,
                              @Query("changedAfter") String changedAfter);
    @GET("/references/orders")
    Call<List<Orders>> ordersById(@Header("Authorization") String token,
                                  @Query("id") String id,
                                  @Query("userUuid") String userUuid);
}
