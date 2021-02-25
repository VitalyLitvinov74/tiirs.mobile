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
    @GET("/equipment")
    Call<List<Equipment>> get();

    @GET("/equipment")
    Call<List<Equipment>> get(@Query("changedAfter") String changedAfter);

    @GET("/equipment")
    Call<List<Equipment>> getById(@Query("id") String id);

    @GET("/equipment")
    Call<List<Equipment>> getById(@Query("id[]") String[] id);

    @GET("/equipment")
    Call<List<Equipment>> getByUuid(@Query("uuid") String uuid);

    @GET("/equipment")
    Call<List<Equipment>> getByUuid(@Query("uuid[]") String[] uuid);

    // TODO: заменить на POST !!!!
    @GET("/equipment/set-tag")
    Call<Boolean> setTagId(@Query("uuid") String uuid, @Query("tagId") String tagId);

    @GET("/equipment")
    Call<Equipment> getByTagId(@Query("tagId") String tagId);

    @POST("/equipment/upload")
    Call<ResponseBody> send(@Body Equipment equipment);
}
