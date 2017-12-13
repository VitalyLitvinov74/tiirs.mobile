package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.TaskStageList;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageList {
    @GET("/task-stage-list")
    Call<List<TaskStageList>> get();

    @GET("/task-stage-list")
    Call<List<TaskStageList>> get(@Query("changedAfter") String changedAfter);

    @GET("/task-stage-list")
    Call<List<TaskStageList>> getById(@Query("id") String id);

    @GET("/task-stage-list")
    Call<List<TaskStageList>> getById(@Query("id") String[] id);

    @GET("/task-stage-list")
    Call<List<TaskStageList>> getByUuid(@Query("uuid") String uuid);

    @GET("/task-stage-list")
    Call<List<TaskStageList>> getByUuid(@Query("uuid") String[] uuid);
}
