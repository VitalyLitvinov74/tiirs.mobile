package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Stage;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IStage {
    @GET("/stage")
    Call<List<Stage>> get();

    @GET("/stage")
    Call<List<Stage>> get(@Query("changedAfter") String changedAfter);

    @GET("/stage")
    Call<List<Stage>> getById(@Query("id") String id);

    @GET("/stage")
    Call<List<Stage>> getById(@Query("id") String[] id);

    @GET("/stage")
    Call<List<Stage>> getByUuid(@Query("uuid") String uuid);

    @GET("/stage")
    Call<List<Stage>> getByUuid(@Query("uuid") String[] uuid);
}
