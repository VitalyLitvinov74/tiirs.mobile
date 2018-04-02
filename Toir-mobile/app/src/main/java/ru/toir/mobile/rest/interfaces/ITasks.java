package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Task;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITasks {
    @GET("/task")
    Call<List<Task>> get();

    @GET("/task")
    Call<List<Task>> get(@Query("changedAfter") String changedAfter);

    @GET("/task")
    Call<List<Task>> getById(@Query("id") String id);

    @GET("/task")
    Call<List<Task>> getById(@Query("id[]") String[] id);

    @GET("/task")
    Call<List<Task>> getByUuid(@Query("uuid") String uuid);

    @GET("/task")
    Call<List<Task>> getByUuid(@Query("uuid[]") String[] uuid);
}
