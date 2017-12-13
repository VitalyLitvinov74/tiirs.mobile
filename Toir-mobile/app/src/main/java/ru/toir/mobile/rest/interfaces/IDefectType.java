package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.DefectType;
import ru.toir.mobile.db.realm.DocumentationType;

/**
 * @author Olejek
 *         Created by olejek on 15.05.17.
 */
public interface IDefectType {
    @GET("/defect-type")
    Call<List<DefectType>> get();

    @GET("/defect-type")
    Call<List<DefectType>> get(@Query("changedAfter") String changedAfter);

    @GET("/defect-type")
    Call<List<DefectType>> getById(@Query("id") String id);

    @GET("/defect-type")
    Call<List<DefectType>> getById(@Query("id") String[] id);

    @GET("/defect-type")
    Call<List<DefectType>> getByUuid(@Query("uuid") String uuid);

    @GET("/defect-type")
    Call<List<DefectType>> getByUuid(@Query("uuid") String[] uuid);
}
