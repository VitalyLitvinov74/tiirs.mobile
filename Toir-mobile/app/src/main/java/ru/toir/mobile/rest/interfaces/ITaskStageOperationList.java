package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStageOperationList;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageOperationList {
    @GET("/references/task-stage-operation-list")
    Call<List<TaskStageOperationList>> taskStageOperationList();

    @GET("/references/")
    Call<List<TaskStageOperationList>> taskStageOperationList(@Query("changedAfter") String changedAfter);

    @GET("/references/")
    Call<List<TaskStageOperationList>> taskStageOperationListById(@Query("id") String id);

}
