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

    @GET("/api/references/critical-type")
    Call<List<CriticalType>> criticalType();

    @GET("/api/references/critical-type")
    Call<List<CriticalType>> criticalType(@Query("changedAfter") String changedAfter);

    @GET("/api/references/critical-type")
    Call<List<CriticalType>> criticalTypeById(@Query("id") String id);
}
