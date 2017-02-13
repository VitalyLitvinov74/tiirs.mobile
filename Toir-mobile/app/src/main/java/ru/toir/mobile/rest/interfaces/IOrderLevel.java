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
    Call<List<OrderLevel>> orderLevel();

    @GET("/references/order-level")
    Call<List<OrderLevel>> orderLevel(@Query("changedAfter") String changedAfter);

    @GET("/references/order-level")
    Call<List<OrderLevel>> orderLevelById(@Query("id") String id);
}
