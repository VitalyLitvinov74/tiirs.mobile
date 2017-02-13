package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStageVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageVerdict {
    @GET("/references/task-stage-verdict")
    Call<List<TaskStageVerdict>> taskStageVerdict();

    @GET("/references/task-stage-verdict")
    Call<List<TaskStageVerdict>> taskStageVerdict(@Query("changedAfter") String changedAfter);

    @GET("/references/task-stage-verdict")
    Call<List<TaskStageVerdict>> taskStageVerdictById(@Query("id") String id);
}
