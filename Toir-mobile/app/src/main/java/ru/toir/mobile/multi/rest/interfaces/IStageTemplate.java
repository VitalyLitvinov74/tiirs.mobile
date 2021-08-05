package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.StageTemplate;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IStageTemplate {
    @GET("/api/stage-template")
    Call<List<StageTemplate>> get();

    @GET("/api/stage-template")
    Call<List<StageTemplate>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/stage-template")
    Call<List<StageTemplate>> getById(@Query("id") String id);

    @GET("/api/stage-template")
    Call<List<StageTemplate>> getById(@Query("id[]") String[] id);

    @GET("/api/stage-template")
    Call<List<StageTemplate>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/stage-template")
    Call<List<StageTemplate>> getByUuid(@Query("uuid[]") String[] uuid);
}
