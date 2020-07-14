package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.DefectLevel;

public interface IDefectLevel {
    @GET("/defect-level")
    Call<List<DefectLevel>> get();

    @GET("/defect-level")
    Call<List<DefectLevel>> get(@Query("changedAfter") String changedAfter);

    @GET("/defect-level")
    Call<List<DefectLevel>> getById(@Query("id") String id);

    @GET("/defect-level")
    Call<List<DefectLevel>> getById(@Query("id[]") String[] id);

    @GET("/defect-level")
    Call<List<DefectLevel>> getByUuid(@Query("uuid") String uuid);

    @GET("/defect-level")
    Call<List<DefectLevel>> getByUuid(@Query("uuid[]") String[] uuid);
}
