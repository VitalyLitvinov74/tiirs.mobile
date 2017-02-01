package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.OperationStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IOperationStatus {
    @GET("/references/operation-status")
    Call<List<OperationStatus>> operationStatus(@Header("Authorization") String token);
    @GET("/references/operation-status")
    Call<List<OperationStatus>> operationStatus(@Header("Authorization") String token,
                                                @Query("changedAfter") String changedAfter);
    @GET("/references/operation-status")
    Call<List<OperationStatus>> operationStatusById(@Header("Authorization") String token,
                                                    @Query("id") String id);
}
