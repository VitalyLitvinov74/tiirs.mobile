package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.StageVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageVerdict {
    @GET("/api/task-stage/verdict")
    Call<List<StageVerdict>> taskStageVerdict();

    @GET("/api/task-stage/verdict")
    Call<List<StageVerdict>> taskStageVerdict(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/verdict")
    Call<List<StageVerdict>> taskStageVerdictById(@Query("id") String id);
}
