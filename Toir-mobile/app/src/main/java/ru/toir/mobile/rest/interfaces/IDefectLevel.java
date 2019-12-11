package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.DefectLevel;
import ru.toir.mobile.db.realm.DefectType;

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
