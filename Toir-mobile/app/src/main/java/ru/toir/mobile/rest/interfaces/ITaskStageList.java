package ru.toir.mobile.rest.interfaces;

import java.util.List;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStageList;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageList {
    @GET("/api/task-stage/list")
    Call<List<TaskStageList>> taskStageList();

    @GET("/api/task-stage/list")
    Call<List<TaskStageList>> taskStageList(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/list")
    Call<List<TaskStageList>> taskStageListById(@Query("id") String id);
}
