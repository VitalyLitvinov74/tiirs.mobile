package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskTemplate {
    @GET("/api/task/template")
    Call<List<TaskTemplate>> taskTemplate();

    @GET("/api/task/template")
    Call<List<TaskTemplate>> taskTemplate(@Query("changedAfter") String changedAfter);

    @GET("/api/task/template")
    Call<List<TaskTemplate>> taskTemplateById(@Query("id") String id);
}
