package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.TaskType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskType {
    @GET("/api/task-type")
    Call<List<TaskType>> get();

    @GET("/api/task-type")
    Call<List<TaskType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/task-type")
    Call<List<TaskType>> getById(@Query("id") String id);

    @GET("/api/task-type")
    Call<List<TaskType>> getById(@Query("id[]") String[] id);

    @GET("/api/task-type")
    Call<List<TaskType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/task-type")
    Call<List<TaskType>> getByUuid(@Query("uuid[]") String[] uuid);
}
