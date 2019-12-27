package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.OperationVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationVerdict {
    @GET("/operation-verdict")
    Call<List<OperationVerdict>> get();

    @GET("/operation-verdict")
    Call<List<OperationVerdict>> get(@Query("changedAfter") String changedAfter);

    @GET("/operation-verdict")
    Call<List<OperationVerdict>> getById(@Query("id") String id);

    @GET("/operation-verdict")
    Call<List<OperationVerdict>> getById(@Query("id[]") String[] id);

    @GET("/operation-verdict")
    Call<List<OperationVerdict>> getByUuid(@Query("uuid") String uuid);

    @GET("/operation-verdict")
    Call<List<OperationVerdict>> getByUuid(@Query("uuid[]") String[] uuid);
}
