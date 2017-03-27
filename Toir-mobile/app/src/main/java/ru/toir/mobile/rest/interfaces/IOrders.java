package ru.toir.mobile.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.MeasuredValue;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17.10.16.
 */
public interface IOrders {
    @GET("/api/orders")
    Call<List<Orders>> orders();

    @GET("/api/orders")
    Call<List<Orders>> orders(@Query("changedAfter") String changedAfter);

    @GET("/api/orders")
    Call<List<Orders>> ordersById(@Query("id") String id);

    @GET("/api/orders")
    Call<List<Orders>> ordersByStatus(@Query("status") String status);

    @GET("/api/orders")
    Call<List<Orders>> ordersByStatus(@Query("status[]") List<String> status);

    @POST("/api/orders/results")
    Call<ResponseBody> sendOrders(@Body List<Orders> orders);

    @POST("/api/orders/upload-measured-value")
    Call<ResponseBody> sendMeasuredValues(@Body List<MeasuredValue> values);

}
