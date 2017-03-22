package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
