package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Operation;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IOperation {
    @GET("/api/operation")
    Call<List<Operation>> operation();

    @GET("/api/operation")
    Call<List<Operation>> operation(@Query("changedAfter") String changedAfter);

    @GET("/api/operation")
    Call<List<Operation>> operationById(@Query("id") String id);

}
