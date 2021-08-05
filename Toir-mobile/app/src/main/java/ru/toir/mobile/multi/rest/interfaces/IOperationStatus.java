package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.OperationStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IOperationStatus {
    @GET("/api/operation-status")
    Call<List<OperationStatus>> get();

    @GET("/api/operation-status")
    Call<List<OperationStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/operation-status")
    Call<List<OperationStatus>> getById(@Query("id") String id);

    @GET("/api/operation-status")
    Call<List<OperationStatus>> getById(@Query("id[]") String[] id);

    @GET("/api/operation-status")
    Call<List<OperationStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/operation-status")
    Call<List<OperationStatus>> getByUuid(@Query("uuid[]") String[] uuid);
}
