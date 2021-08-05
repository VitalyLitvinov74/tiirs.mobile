package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Task;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITasks {
    @GET("/api/task")
    Call<List<Task>> get();

    @GET("/api/task")
    Call<List<Task>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/task")
    Call<List<Task>> getById(@Query("id") String id);

    @GET("/api/task")
    Call<List<Task>> getById(@Query("id[]") String[] id);

    @GET("/api/task")
    Call<List<Task>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/task")
    Call<List<Task>> getByUuid(@Query("uuid[]") String[] uuid);

    @POST("/api/task/set-status")
    Call<Boolean> setStatus(@Query("uuid") String uuid, @Query("statusUuid") String statusUuid);
}
