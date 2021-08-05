package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.TaskVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */
public interface ITaskVerdict {
    @GET("/api/task-verdict")
    Call<List<TaskVerdict>> get();

    @GET("/api/task-verdict")
    Call<List<TaskVerdict>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/task-verdict")
    Call<List<TaskVerdict>> getById(@Query("id") String id);

    @GET("/api/task-verdict")
    Call<List<TaskVerdict>> getById(@Query("id[]") String[] id);

    @GET("/api/task-verdict")
    Call<List<TaskVerdict>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/task-verdict")
    Call<List<TaskVerdict>> getByUuid(@Query("uuid[]") String[] uuid);
}
