package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStageType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageType {
    @GET("/api/task-stage/type")
    Call<List<TaskStageType>> taskStageType();

    @GET("/api/task-stage/type")
    Call<List<TaskStageType>> taskStageType(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/type")
    Call<List<TaskStageType>> taskStageTypeById(@Query("id") String id);
}
