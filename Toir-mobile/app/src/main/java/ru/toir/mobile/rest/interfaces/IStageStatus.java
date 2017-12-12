package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.StageStatus;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IStageStatus {
    @GET("/task-stage-status")
    Call<List<StageStatus>> get();

    @GET("/task-stage-status")
    Call<List<StageStatus>> get(@Query("changedAfter") String changedAfter);

    @GET("/task-stage-status")
    Call<List<StageStatus>> getById(@Query("id") String id);

    @GET("/task-stage-status")
    Call<List<StageStatus>> getById(@Query("id") String[] id);

    @GET("/task-stage-status")
    Call<List<StageStatus>> getByUuid(@Query("uuid") String uuid);

    @GET("/task-stage-status")
    Call<List<StageStatus>> getByUuid(@Query("uuid") String[] uuid);
}
