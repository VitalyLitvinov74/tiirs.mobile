package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
