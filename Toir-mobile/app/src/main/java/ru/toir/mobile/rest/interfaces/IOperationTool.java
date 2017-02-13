package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OperationTool;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationTool {
    @GET("/references/operation-tool")
    Call<List<OperationTool>> operationTool();

    @GET("/references/operation-tool")
    Call<List<OperationTool>> operationTool(@Query("changedAfter") String changedAfter);

    @GET("/references/operation-tool")
    Call<List<OperationTool>> operationToolById(@Query("id") String id);

}
