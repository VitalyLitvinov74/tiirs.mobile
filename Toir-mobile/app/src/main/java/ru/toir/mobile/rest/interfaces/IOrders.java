package ru.toir.mobile.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Orders;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17.10.16.
 */
public interface IOrders {
    @GET("/orders")
    Call<List<Orders>> get();

    @GET("/orders")
    Call<List<Orders>> get(@Query("changedAfter") String changedAfter);

    @GET("/orders")
    Call<List<Orders>> getById(@Query("id") String id);

    @GET("/orders")
    Call<List<Orders>> getById(@Query("id[]") String[] id);

    @GET("/orders")
    Call<List<Orders>> getByUuid(@Query("uuid") String uuid);

    @GET("/orders")
    Call<List<Orders>> getByUuid(@Query("uuid[]") String[] uuid);

    @GET("/orders")
    Call<List<Orders>> getByStatus(@Query("status") String status);

    @GET("/orders")
    Call<List<Orders>> getByStatus(@Query("status[]") List<String> status);

    @POST("/orders/results")
    Call<ResponseBody> send(@Body List<Orders> orders);

    @POST("/orders/in-work")
    Call<ResponseBody> setInWork(@Body String uuid);

    @POST("/orders/in-work")
    Call<ResponseBody> setInWork(@Body List<String> uuid);

    @POST("/orders/complete")
    Call<ResponseBody> setComplete(@Body String uuid);

    @POST("/orders/complete")
    Call<ResponseBody> setComplete(@Body List<String> uuid);

    @POST("/orders/un-complete")
    Call<ResponseBody> setUnComplete(@Body String uuid);

    @POST("/orders/un-complete")
    Call<ResponseBody> setUnComplete(@Body List<String> uuid);
}
