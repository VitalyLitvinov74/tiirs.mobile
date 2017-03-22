package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.TaskStages;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStages {
    @GET("/api/task-stage")
    Call<List<TaskStages>> taskStages();

    @GET("/api/task-stage")
    Call<List<TaskStages>> taskStages(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage")
    Call<List<TaskStages>> taskStagesById(@Query("id") String id);
}
