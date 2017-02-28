package ru.toir.mobile.rest.interfaces;

import java.util.List;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
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
