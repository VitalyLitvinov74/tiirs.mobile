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
    @GET("/api/object/type")
    Call<List<ObjectType>> objectType();

    @GET("/api/object/type")
    Call<List<ObjectType>> objectType(@Query("changedAfter") String changedAfter);

    @GET("/api/object/type")
    Call<List<ObjectType>> objcetTypeById(@Query("id") String id);

}
