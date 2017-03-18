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
    @GET("/api/task-stage/operation-list")
    Call<List<TaskStageOperationList>> taskStageOperationList();

    @GET("/api/task-stage/operation-list")
    Call<List<TaskStageOperationList>> taskStageOperationList(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/operation-list")
    Call<List<TaskStageOperationList>> taskStageOperationListById(@Query("id") String id);

}
