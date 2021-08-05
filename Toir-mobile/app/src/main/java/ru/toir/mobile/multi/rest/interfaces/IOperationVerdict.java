package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.OperationVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationVerdict {
    @GET("/api/operation-verdict")
    Call<List<OperationVerdict>> get();

    @GET("/api/operation-verdict")
    Call<List<OperationVerdict>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/operation-verdict")
    Call<List<OperationVerdict>> getById(@Query("id") String id);

    @GET("/api/operation-verdict")
    Call<List<OperationVerdict>> getById(@Query("id[]") String[] id);

    @GET("/api/operation-verdict")
    Call<List<OperationVerdict>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/operation-verdict")
    Call<List<OperationVerdict>> getByUuid(@Query("uuid[]") String[] uuid);
}
