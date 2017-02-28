package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OrderLevel;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderLevel {
    @GET("/api/orders/level")
    Call<List<OrderLevel>> orderLevel();

    @GET("/api/orders/level")
    Call<List<OrderLevel>> orderLevel(@Query("changedAfter") String changedAfter);

    @GET("/api/orders/level")
    Call<List<OrderLevel>> orderLevelById(@Query("id") String id);
}
