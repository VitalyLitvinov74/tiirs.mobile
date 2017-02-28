package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskType {
    @GET("/api/task/type")
    Call<List<TaskType>> taskType();

    @GET("/api/task/type")
    Call<List<TaskType>> taskType(@Query("changedAfter") String changedAfter);

    @GET("/api/task/type")
    Call<List<TaskType>> taskTypeById(@Query("id") String id);
}
