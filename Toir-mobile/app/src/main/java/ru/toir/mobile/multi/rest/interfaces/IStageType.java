package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.StageType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IStageType {
    @GET("/api/stage-type")
    Call<List<StageType>> get();

    @GET("/api/stage-type")
    Call<List<StageType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/stage-type")
    Call<List<StageType>> getById(@Query("id") String id);

    @GET("/api/stage-type")
    Call<List<StageType>> getById(@Query("id[]") String[] id);

    @GET("/api/stage-type")
    Call<List<StageType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/stage-type")
    Call<List<StageType>> getByUuid(@Query("uuid[]") String[] uuid);
}
