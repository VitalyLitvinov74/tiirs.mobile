package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.TaskStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface ITaskStatus {
    @GET("/api/task/status")
    Call<List<TaskStatus>> taskStatus();

    @GET("/api/task/status")
    Call<List<TaskStatus>> taskStatus(@Query("changedAfter") String changedAfter);

    @GET("/api/task/status")
    Call<List<TaskStatus>> taskStatusById(@Query("id") String id);
}
