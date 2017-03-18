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
    @GET("/api/operation/tool")
    Call<List<OperationTool>> operationTool();

    @GET("/api/operation/tool")
    Call<List<OperationTool>> operationTool(@Query("changedAfter") String changedAfter);

    @GET("/api/operation/tool")
    Call<List<OperationTool>> operationToolById(@Query("id") String id);

}
