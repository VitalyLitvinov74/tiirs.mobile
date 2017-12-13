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
    @GET("/order-level")
    Call<List<OrderLevel>> get();

    @GET("/order-level")
    Call<List<OrderLevel>> get(@Query("changedAfter") String changedAfter);

    @GET("/order-level")
    Call<List<OrderLevel>> getById(@Query("id") String id);

    @GET("/order-level")
    Call<List<OrderLevel>> getById(@Query("id") String[] id);

    @GET("/order-level")
    Call<List<OrderLevel>> getByUuid(@Query("uuid") String uuid);

    @GET("/order-level")
    Call<List<OrderLevel>> getByUuid(@Query("uuid") String[] uuid);
}
