package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStages;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStages {
    @GET("/references/task-stages")
    Call<List<TaskStages>> taskStages(@Header("Authorization") String token);

    @GET("/references/task-stages")
    Call<List<TaskStages>> taskStages(@Header("Authorization") String token,
                                      @Query("changedAfter") String changedAfter);

    @GET("/references/task-stages")
    Call<List<TaskStages>> taskStagesById(@Header("Authorization") String token,
                                          @Query("id") String id);
}
