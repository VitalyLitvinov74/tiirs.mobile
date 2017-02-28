package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OrderVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOrderVerdict {
    @GET("/api/orders/verdict")
    Call<List<OrderVerdict>> orderVerdict();

    @GET("/api/orders/verdict")
    Call<List<OrderVerdict>> orderVerdict(@Query("changedAfter") String changedAfter);

    @GET("/api/orders/verdict")
    Call<List<OrderVerdict>> orderVerdictById(@Query("id") String id);
}
