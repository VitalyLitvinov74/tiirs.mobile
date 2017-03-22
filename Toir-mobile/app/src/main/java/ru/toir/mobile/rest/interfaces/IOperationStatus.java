package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.OperationStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IOperationStatus {
    @GET("/api/operation/status")
    Call<List<OperationStatus>> operationStatus();

    @GET("/api/operation/status")
    Call<List<OperationStatus>> operationStatus(@Query("changedAfter") String changedAfter);

    @GET("/api/operation/status")
    Call<List<OperationStatus>> operationStatusById(@Query("id") String id);
}
