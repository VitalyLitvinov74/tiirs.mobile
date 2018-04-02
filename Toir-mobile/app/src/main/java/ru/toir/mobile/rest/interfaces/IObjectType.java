package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.ObjectType;
import ru.toir.mobile.db.realm.OperationType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IObjectType {
    @GET("/object-type")
    Call<List<ObjectType>> get();

    @GET("/object-type")
    Call<List<ObjectType>> get(@Query("changedAfter") String changedAfter);

    @GET("/object-type")
    Call<List<ObjectType>> getById(@Query("id") String id);

    @GET("/object-type")
    Call<List<ObjectType>> getById(@Query("id[]") String[] id);

    @GET("/object-type")
    Call<List<ObjectType>> getByUuid(@Query("uuid") String uuid);

    @GET("/object-type")
    Call<List<ObjectType>> getByUuid(@Query("uuid[]") String[] uuid);

}
