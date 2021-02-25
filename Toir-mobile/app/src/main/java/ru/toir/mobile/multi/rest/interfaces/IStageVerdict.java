package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.StageVerdict;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IStageVerdict {
    @GET("/stage-verdict")
    Call<List<StageVerdict>> get();

    @GET("/stage-verdict")
    Call<List<StageVerdict>> get(@Query("changedAfter") String changedAfter);

    @GET("/stage-verdict")
    Call<List<StageVerdict>> getById(@Query("id") String id);

    @GET("/stage-verdict")
    Call<List<StageVerdict>> getById(@Query("id[]") String[] id);

    @GET("/stage-verdict")
    Call<List<StageVerdict>> getByUuid(@Query("uuid") String uuid);

    @GET("/stage-verdict")
    Call<List<StageVerdict>> getByUuid(@Query("uuid[]") String[] uuid);
}
