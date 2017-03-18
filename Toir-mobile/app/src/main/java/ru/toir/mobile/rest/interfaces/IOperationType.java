package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.OperationType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IOperationType {
    @GET("/api/operation/type")
    Call<List<OperationType>> operationType();

    @GET("/api/operation/type")
    Call<List<OperationType>> operationType(@Query("changedAfter") String changedAfter);

    @GET("/api/operation/type")
    Call<List<OperationType>> operationTypeById(@Query("id") String id);

}
