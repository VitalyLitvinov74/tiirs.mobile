package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.CriticalType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface ICriticalType {

    @GET("/critical-type")
    Call<List<CriticalType>> get();

    @GET("/critical-type")
    Call<List<CriticalType>> get(@Query("changedAfter") String changedAfter);

    @GET("/critical-type")
    Call<List<CriticalType>> getById(@Query("id") String id);

    @GET("/critical-type")
    Call<List<CriticalType>> getById(@Query("id[]") String[] id);

    @GET("/critical-type")
    Call<List<CriticalType>> getByUuid(@Query("uuid") String uuid);

    @GET("/critical-type")
    Call<List<CriticalType>> getByUuid(@Query("uuid[]") String[] uuid);
}
