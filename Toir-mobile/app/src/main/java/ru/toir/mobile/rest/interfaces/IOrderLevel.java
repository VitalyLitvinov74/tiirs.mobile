package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
