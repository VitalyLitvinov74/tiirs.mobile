package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.TaskStageVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageVerdict {
    @GET("/api/task-stage/verdict")
    Call<List<TaskStageVerdict>> taskStageVerdict();

    @GET("/api/task-stage/verdict")
    Call<List<TaskStageVerdict>> taskStageVerdict(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/verdict")
    Call<List<TaskStageVerdict>> taskStageVerdictById(@Query("id") String id);
}
