package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Defect;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 20/03/19.
 */

public interface IDefect {
    @GET("/defect")
    Call<List<Defect>> get();

    @GET("/defect")
    Call<List<Defect>> get(@Query("changedAfter") String changedAfter);

    @GET("/defect")
    Call<List<Defect>> getByUuid(@Query("uuid") String uuid);

    @GET("/defect")
    Call<List<Defect>> getByUuid(@Query("uuid[]") String[] uuid);

    @GET("/defect")
    Call<List<Defect>> getByEquipment(@Query("equipment") String uuid);

    @GET("/defect")
    Call<List<Defect>> getByEquipment(@Query("equipment[]") String[] uuid);

    @GET("/defect")
    Call<List<Defect>> getByEquipment(@Query("equipment") String uuid, @Query("changedAfter") String changedAfter);

    @GET("/defect")
    Call<List<Defect>> getByEquipment(@Query("equipment[]") String[] uuid, @Query("changedAfter") String changedAfter);

    @POST("/defect/upload")
    Call<ResponseBody> send(@Body List<Defect> values);
}
