package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.AttributeType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17/03/19.
 */

public interface IAttributeType {
    @GET("/attribute-type")
    Call<List<AttributeType>> get();

    @GET("/attribute-type")
    Call<List<AttributeType>> get(@Query("changedAfter") String changedAfter);

    @GET("/attribute-type")
    Call<List<AttributeType>> getById(@Query("id") String id);

    @GET("/attribute-type")
    Call<List<AttributeType>> getById(@Query("id[]") String[] id);

    @GET("/attribute-type")
    Call<List<AttributeType>> getByUuid(@Query("uuid") String uuid);

    @GET("/attribute-type")
    Call<List<AttributeType>> getByUuid(@Query("uuid[]") String[] uuid);
}
