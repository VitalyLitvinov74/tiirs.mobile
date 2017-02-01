package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Tasks;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITasks {
    @GET("/references/tasks")
    Call<List<Tasks>> tasks(@Header("Authorization") String token);

    @GET("/references/tasks")
    Call<List<Tasks>> tasks(@Header("Authorization") String token,
                            @Query("changedAfter") String changedAfter);

    @GET("/references/tasks")
    Call<List<Tasks>> tasksById(@Header("Authorization") String token,
                                @Query("id") String id);
}
