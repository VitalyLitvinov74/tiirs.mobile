package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
