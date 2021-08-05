package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Equipment;

/**
 * @author Dmitriy Logachev
 * Created by koputo on 25.01.17.
 */

public interface IEquipment {
    @GET("/api/equipment")
    Call<List<Equipment>> get();

    @GET("/api/equipment")
    Call<List<Equipment>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment")
    Call<List<Equipment>> getById(@Query("id") String id);

    @GET("/api/equipment")
    Call<List<Equipment>> getById(@Query("id[]") String[] id);

    @GET("/api/equipment")
    Call<List<Equipment>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/equipment")
    Call<List<Equipment>> getByUuid(@Query("uuid[]") String[] uuid);

    // TODO: заменить на POST !!!!
    @GET("/api/equipment/set-tag")
    Call<Boolean> setTagId(@Query("uuid") String uuid, @Query("tagId") String tagId);

    @GET("/api/equipment")
    Call<Equipment> getByTagId(@Query("tagId") String tagId);

    @POST("/api/equipment/upload")
    Call<ResponseBody> send(@Body Equipment equipment);
}
