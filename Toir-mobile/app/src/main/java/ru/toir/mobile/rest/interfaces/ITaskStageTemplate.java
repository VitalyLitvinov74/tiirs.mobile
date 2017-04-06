package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.StageTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageTemplate {
    @GET("/api/task-stage/template")
    Call<List<StageTemplate>> taskStageTemplate();

    @GET("/api/task-stage/template")
    Call<List<StageTemplate>> taskStageTemplate(@Query("changedAfter") String changedAfter);

    @GET("/api/task-stage/template")
    Call<List<StageTemplate>> taskStageTemplateById(@Query("id") String id);
}
