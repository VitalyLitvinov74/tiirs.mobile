package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.OperationTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationTemplate {
    @GET("/operation-template")
    Call<List<OperationTemplate>> get();

    @GET("/operation-template")
    Call<List<OperationTemplate>> get(@Query("changedAfter") String changedAfter);

    @GET("/operation-template")
    Call<List<OperationTemplate>> getById(@Query("id") String id);

    @GET("/operation-template")
    Call<List<OperationTemplate>> getById(@Query("id[]") String[] id);

    @GET("/operation-template")
    Call<List<OperationTemplate>> getByUuid(@Query("uuid") String uuid);

    @GET("/operation-template")
    Call<List<OperationTemplate>> getByUuid(@Query("uuid[]") String[] uuid);
}
