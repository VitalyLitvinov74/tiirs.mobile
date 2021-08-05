package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.TaskStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface ITaskStatus {
    @GET("/api/task-status")
    Call<List<TaskStatus>> get();

    @GET("/api/task-status")
    Call<List<TaskStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/task-status")
    Call<List<TaskStatus>> getById(@Query("id") String id);

    @GET("/api/task-status")
    Call<List<TaskStatus>> getById(@Query("id[]") String[] id);

    @GET("/api/task-status")
    Call<List<TaskStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/task-status")
    Call<List<TaskStatus>> getByUuid(@Query("uuid[]") String[] uuid);
}
