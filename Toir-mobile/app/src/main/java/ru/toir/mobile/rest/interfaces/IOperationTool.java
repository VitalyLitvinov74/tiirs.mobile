package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.OperationTool;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationTool {
    @GET("/operation-tool")
    Call<List<OperationTool>> get();

    @GET("/operation-tool")
    Call<List<OperationTool>> get(@Query("changedAfter") String changedAfter);

    @GET("/operation-tool")
    Call<List<OperationTool>> getById(@Query("id") String id);

    @GET("/operation-tool")
    Call<List<OperationTool>> getById(@Query("id") String[] id);

    @GET("/operation-tool")
    Call<List<OperationTool>> getByUuid(@Query("uuid") String uuid);

    @GET("/operation-tool")
    Call<List<OperationTool>> getByUuid(@Query("uuid") String[] uuid);
}
