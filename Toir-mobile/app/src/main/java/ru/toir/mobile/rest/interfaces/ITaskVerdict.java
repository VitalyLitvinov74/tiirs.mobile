package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.TaskVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */
public interface ITaskVerdict {
    @GET("/task-verdict")
    Call<List<TaskVerdict>> get();

    @GET("/task-verdict")
    Call<List<TaskVerdict>> get(@Query("changedAfter") String changedAfter);

    @GET("/task-verdict")
    Call<List<TaskVerdict>> getById(@Query("id") String id);

    @GET("/task-verdict")
    Call<List<TaskVerdict>> getById(@Query("id[]") String[] id);

    @GET("/task-verdict")
    Call<List<TaskVerdict>> getByUuid(@Query("uuid") String uuid);

    @GET("/task-verdict")
    Call<List<TaskVerdict>> getByUuid(@Query("uuid[]") String[] uuid);
}
