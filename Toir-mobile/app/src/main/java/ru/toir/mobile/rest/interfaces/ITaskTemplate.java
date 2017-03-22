package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
