package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.OperationType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IOperationType {
    @GET("/api/operation-type")
    Call<List<OperationType>> get();

    @GET("/api/operation-type")
    Call<List<OperationType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/operation-type")
    Call<List<OperationType>> getById(@Query("id") String id);

    @GET("/api/operation-type")
    Call<List<OperationType>> getById(@Query("id[]") String[] id);

    @GET("/api/operation-type")
    Call<List<OperationType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/operation-type")
    Call<List<OperationType>> getByUuid(@Query("uuid[]") String[] uuid);
}
