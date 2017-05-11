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
    @GET("/api/objects/defect-type")
    Call<List<DefectType>> defectType();

    @GET("/api/objects/defect-type")
    Call<List<DefectType>> defectType(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/defect-type")
    Call<List<DefectType>> defectTypeById(@Query("id") String id);
}
