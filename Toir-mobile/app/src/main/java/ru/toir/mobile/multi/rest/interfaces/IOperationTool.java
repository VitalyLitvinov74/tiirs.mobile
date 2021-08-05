package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.OperationTool;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationTool {
    @GET("/api/operation-tool")
    Call<List<OperationTool>> get();

    @GET("/api/operation-tool")
    Call<List<OperationTool>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/operation-tool")
    Call<List<OperationTool>> getById(@Query("id") String id);

    @GET("/api/operation-tool")
    Call<List<OperationTool>> getById(@Query("id[]") String[] id);

    @GET("/api/operation-tool")
    Call<List<OperationTool>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/operation-tool")
    Call<List<OperationTool>> getByUuid(@Query("uuid[]") String[] uuid);
}
