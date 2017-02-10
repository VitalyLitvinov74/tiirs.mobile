package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStageType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageType {
    @GET("/references/task-stage-type")
    Call<List<TaskStageType>> taskStageType(@Header("Authorization") String token);

    @GET("/references/task-stage-type")
    Call<List<TaskStageType>> taskStageType(@Header("Authorization") String token,
                                            @Query("changedAfter") String changedAfter);

    @GET("/references/task-stage-type")
    Call<List<TaskStageType>> taskStageTypeById(@Header("Authorization") String token,
                                                @Query("id") String id);
}
