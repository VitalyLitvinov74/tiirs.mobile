package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.StageType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface ITaskStageType {
    @GET("/task-stage-type")
    Call<List<StageType>> get();

    @GET("/task-stage-type")
    Call<List<StageType>> get(@Query("changedAfter") String changedAfter);

    @GET("/task-stage-type")
    Call<List<StageType>> getById(@Query("id") String id);

    @GET("/task-stage-type")
    Call<List<StageType>> getById(@Query("id") String[] id);

    @GET("/task-stage-type")
    Call<List<StageType>> getByUuid(@Query("uuid") String uuid);

    @GET("/task-stage-type")
    Call<List<StageType>> getByUuid(@Query("uuid") String[] uuid);
}
