package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.TaskStageOperationList;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageOperationList {
    @GET("/task-stage-operation-list")
    Call<List<TaskStageOperationList>> get();

    @GET("/task-stage-operation-list")
    Call<List<TaskStageOperationList>> get(@Query("changedAfter") String changedAfter);

    @GET("/task-stage-operation-list")
    Call<List<TaskStageOperationList>> getById(@Query("id") String id);

    @GET("/task-stage-operation-list")
    Call<List<TaskStageOperationList>> getById(@Query("id") String[] id);

    @GET("/task-stage-operation-list")
    Call<List<TaskStageOperationList>> getByUuid(@Query("uuid") String uuid);

    @GET("/task-stage-operation-list")
    Call<List<TaskStageOperationList>> getByUuid(@Query("uuid") String[] uuid);
}
