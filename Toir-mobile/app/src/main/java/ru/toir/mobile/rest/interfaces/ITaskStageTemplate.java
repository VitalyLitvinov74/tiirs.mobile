package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStageTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageTemplate {
    @GET("/api/task-stage/template")
    Call<List<TaskStageTemplate>> taskStageTemplate();

    @GET("/api/task-stage/template")
    Call<List<TaskStageTemplate>> taskStageTemplate(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/template")
    Call<List<TaskStageTemplate>> taskStageTemplateById(@Query("id") String id);
}
