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
    @GET("/operation")
    Call<List<Operation>> get();

    @GET("/operation")
    Call<List<Operation>> get(@Query("changedAfter") String changedAfter);

    @GET("/operation")
    Call<List<Operation>> getById(@Query("id") String id);

    @GET("/operation")
    Call<List<Operation>> getById(@Query("id") String[] id);

    @GET("/operation")
    Call<List<Operation>> getByUuid(@Query("uuid") String uuid);

    @GET("/operation")
    Call<List<Operation>> getByUuid(@Query("uuid") String[] uuid);
}
