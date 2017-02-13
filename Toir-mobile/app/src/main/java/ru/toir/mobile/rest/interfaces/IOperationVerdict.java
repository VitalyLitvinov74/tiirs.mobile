package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OperationVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperationVerdict {
    @GET("/references/operation-verdict")
    Call<List<OperationVerdict>> operationVerdict();

    @GET("/references/operation-verdict")
    Call<List<OperationVerdict>> operationVerdict(@Query("changedAfter") String changedAfter);

    @GET("/references/operation-verdict")
    Call<List<OperationVerdict>> operationVerdictById(@Query("id") String id);
}
