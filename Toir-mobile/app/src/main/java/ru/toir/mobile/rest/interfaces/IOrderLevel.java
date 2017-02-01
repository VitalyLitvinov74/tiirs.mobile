package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OrderLevel;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderLevel {
    @GET("/references/order-level")
    Call<List<OrderLevel>> orderLevel(@Header("Authorization") String token);

    @GET("/references/order-level")
    Call<List<OrderLevel>> orderLevel(@Header("Authorization") String token,
                                      @Query("changedAfter") String changedAfter);

    @GET("/references/order-level")
    Call<List<OrderLevel>> orderLevelById(@Header("Authorization") String token,
                                          @Query("id") String id);
}
