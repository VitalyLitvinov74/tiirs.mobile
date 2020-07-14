package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.TaskTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskTemplate {
    @GET("/task-template")
    Call<List<TaskTemplate>> get();

    @GET("/task-template")
    Call<List<TaskTemplate>> get(@Query("changedAfter") String changedAfter);

    @GET("/task-template")
    Call<List<TaskTemplate>> getById(@Query("id") String id);

    @GET("/task-template")
    Call<List<TaskTemplate>> getById(@Query("id[]") String[] id);

    @GET("/task-template")
    Call<List<TaskTemplate>> getByUuid(@Query("uuid") String uuid);

    @GET("/task-template")
    Call<List<TaskTemplate>> getByUuid(@Query("uuid[]") String[] uuid);
}
