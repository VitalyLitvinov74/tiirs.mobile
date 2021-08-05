package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.OperationTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationTemplate {
    @GET("/api/operation-template")
    Call<List<OperationTemplate>> get();

    @GET("/api/operation-template")
    Call<List<OperationTemplate>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/operation-template")
    Call<List<OperationTemplate>> getById(@Query("id") String id);

    @GET("/api/operation-template")
    Call<List<OperationTemplate>> getById(@Query("id[]") String[] id);

    @GET("/api/operation-template")
    Call<List<OperationTemplate>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/operation-template")
    Call<List<OperationTemplate>> getByUuid(@Query("uuid[]") String[] uuid);
}
