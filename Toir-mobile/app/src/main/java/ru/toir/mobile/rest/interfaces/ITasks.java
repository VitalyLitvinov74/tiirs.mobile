package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Tasks;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITasks {
    @GET("/task")
    Call<List<Tasks>> get();

    @GET("/task")
    Call<List<Tasks>> get(@Query("changedAfter") String changedAfter);

    @GET("/task")
    Call<List<Tasks>> getById(@Query("id") String id);

    @GET("/task")
    Call<List<Tasks>> getById(@Query("id") String[] id);

    @GET("/task")
    Call<List<Tasks>> getByUuid(@Query("uuid") String uuid);

    @GET("/task")
    Call<List<Tasks>> getByUuid(@Query("uuid") String[] uuid);
}
