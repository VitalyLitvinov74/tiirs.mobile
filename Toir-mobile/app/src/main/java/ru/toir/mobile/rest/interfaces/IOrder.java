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
    @GET("/api/order")
    Call<List<Orders>> order(@Header("Authorization") String token,
                             @Query("UserUuid") String userUuid,
                             @Query("ChangedAfter") String changedAfter);
}
