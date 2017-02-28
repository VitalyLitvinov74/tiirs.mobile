package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStageStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageStatus {
    @GET("/api/task-stage/status")
    Call<List<TaskStageStatus>> taskStageStatus();

    @GET("/api/task-stage/status")
    Call<List<TaskStageStatus>> taskStageStatus(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/status")
    Call<List<TaskStageStatus>> taskStageStatusById(@Query("id") String id);
}
