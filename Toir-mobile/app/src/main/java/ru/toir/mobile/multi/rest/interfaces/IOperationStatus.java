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
    @GET("/operation-status")
    Call<List<OperationStatus>> get();

    @GET("/operation-status")
    Call<List<OperationStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/operation-status")
    Call<List<OperationStatus>> getById(@Query("id") String id);

    @GET("/operation-status")
    Call<List<OperationStatus>> getById(@Query("id[]") String[] id);

    @GET("/operation-status")
    Call<List<OperationStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/operation-status")
    Call<List<OperationStatus>> getByUuid(@Query("uuid[]") String[] uuid);
}
