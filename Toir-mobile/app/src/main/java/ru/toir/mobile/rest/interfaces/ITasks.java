package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Tasks;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITasks {
    @GET("/api/task")
    Call<List<Tasks>> tasks();

    @GET("/api/task")
    Call<List<Tasks>> tasks(@Query("changedAfter") String changedAfter);

    @GET("/api/task")
    Call<List<Tasks>> tasksById(@Query("id") String id);
}
