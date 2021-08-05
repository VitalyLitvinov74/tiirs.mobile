package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.OrderLevel;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderLevel {
    @GET("/api/order-level")
    Call<List<OrderLevel>> get();

    @GET("/api/order-level")
    Call<List<OrderLevel>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/order-level")
    Call<List<OrderLevel>> getById(@Query("id") String id);

    @GET("/api/order-level")
    Call<List<OrderLevel>> getById(@Query("id[]") String[] id);

    @GET("/api/order-level")
    Call<List<OrderLevel>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/order-level")
    Call<List<OrderLevel>> getByUuid(@Query("uuid[]") String[] uuid);
}
