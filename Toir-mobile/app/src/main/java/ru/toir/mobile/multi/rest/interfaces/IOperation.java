package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Operation;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperation {
    @GET("/api/operation")
    Call<List<Operation>> get();

    @GET("/api/operation")
    Call<List<Operation>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/operation")
    Call<List<Operation>> getById(@Query("id") String id);

    @GET("/api/operation")
    Call<List<Operation>> getById(@Query("id[]") String[] id);

    @GET("/api/operation")
    Call<List<Operation>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/operation")
    Call<List<Operation>> getByUuid(@Query("uuid[]") String[] uuid);

    @POST("/api/operation/set-status")
    Call<Boolean> setStatus(@Query("uuid") String uuid, @Query("statusUuid") String statusUuid);
}
