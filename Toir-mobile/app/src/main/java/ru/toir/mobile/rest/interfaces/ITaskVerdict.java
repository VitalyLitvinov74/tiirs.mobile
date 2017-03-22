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
    @GET("/api/task/verdict")
    Call<List<TaskVerdict>> taskVerdict();

    @GET("/api/task/verdict")
    Call<List<TaskVerdict>> taskVerdict(@Query("changedAfter") String changedAfter);

    @GET("/api/task/verdict")
    Call<List<TaskVerdict>> taskVerdictById(@Query("id") String id);
}
