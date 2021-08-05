package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.DefectType;

/**
 * @author Olejek
 *         Created by olejek on 15.05.17.
 */
public interface IDefectType {
    @GET("/api/defect-type")
    Call<List<DefectType>> get();

    @GET("/api/defect-type")
    Call<List<DefectType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/defect-type")
    Call<List<DefectType>> getById(@Query("id") String id);

    @GET("/api/defect-type")
    Call<List<DefectType>> getById(@Query("id[]") String[] id);

    @GET("/api/defect-type")
    Call<List<DefectType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/defect-type")
    Call<List<DefectType>> getByUuid(@Query("uuid[]") String[] uuid);
}
