package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.TaskStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface ITaskStatus {
    @GET("/references/task-status")
    Call<List<TaskStatus>> taskStatus();

    @GET("/references/task-status")
    Call<List<TaskStatus>> taskStatus(@Query("changedAfter") String changedAfter);

    @GET("/references/task-status")
    Call<List<TaskStatus>> taskStatusById(@Query("id") String id);
}
