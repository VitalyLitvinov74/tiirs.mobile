package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.ObjectType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IObjectType {
    @GET("/api/object-type")
    Call<List<ObjectType>> get();

    @GET("/api/object-type")
    Call<List<ObjectType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/object-type")
    Call<List<ObjectType>> getById(@Query("id") String id);

    @GET("/api/object-type")
    Call<List<ObjectType>> getById(@Query("id[]") String[] id);

    @GET("/api/object-type")
    Call<List<ObjectType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/object-type")
    Call<List<ObjectType>> getByUuid(@Query("uuid[]") String[] uuid);

}
