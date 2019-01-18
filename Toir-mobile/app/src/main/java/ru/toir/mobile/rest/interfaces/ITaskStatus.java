package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.TaskStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface ITaskStatus {
    @GET("/task-status")
    Call<List<TaskStatus>> get();

    @GET("/task-status")
    Call<List<TaskStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/task-status")
    Call<List<TaskStatus>> getById(@Query("id") String id);

    @GET("/task-status")
    Call<List<TaskStatus>> getById(@Query("id[]") String[] id);

    @GET("/task-status")
    Call<List<TaskStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/task-status")
    Call<List<TaskStatus>> getByUuid(@Query("uuid[]") String[] uuid);
}
