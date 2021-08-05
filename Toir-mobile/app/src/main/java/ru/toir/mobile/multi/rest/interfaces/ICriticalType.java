package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.CriticalType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface ICriticalType {

    @GET("/api/critical-type")
    Call<List<CriticalType>> get();

    @GET("/api/critical-type")
    Call<List<CriticalType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/critical-type")
    Call<List<CriticalType>> getById(@Query("id") String id);

    @GET("/api/critical-type")
    Call<List<CriticalType>> getById(@Query("id[]") String[] id);

    @GET("/api/critical-type")
    Call<List<CriticalType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/critical-type")
    Call<List<CriticalType>> getByUuid(@Query("uuid[]") String[] uuid);
}
