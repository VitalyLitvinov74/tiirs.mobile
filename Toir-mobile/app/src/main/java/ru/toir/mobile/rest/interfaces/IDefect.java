package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.Documentation;

/**
 * @author Olejek
 * Created by olejek on 12.05.17.
 */

public interface IDefect {
    @GET("/defect")
    Call<List<Defect>> get();

    @GET("/defect")
    Call<List<Defect>> get(@Query("changedAfter") String changedAfter);

    @GET("/defect")
    Call<List<Defect>> getById(@Query("id") String id);

    @GET("/defect")
    Call<List<Defect>> getById(@Query("id") String[] id);

    @GET("/defect")
    Call<List<Defect>> getByUuid(@Query("uuid") String uuid);

    @GET("/defect")
    Call<List<Defect>> getByUuid(@Query("uuid") String[] uuid);
}
