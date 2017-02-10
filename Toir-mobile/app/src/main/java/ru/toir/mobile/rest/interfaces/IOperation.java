package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Operation;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperation {
    @GET("/references/operation")
    Call<List<Operation>> operation(@Header("Authorization") String token);

    @GET("/references/operation")
    Call<List<Operation>> operation(@Header("Authorization") String token,
                                    @Query("changedAfter") String changedAfter);

    @GET("/references/operation")
    Call<List<Operation>> operationById(@Header("Authorization") String token,
                                        @Query("id") String id);

}
