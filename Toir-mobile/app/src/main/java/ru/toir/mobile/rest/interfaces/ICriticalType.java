package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.CriticalType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface ICriticalType {

    @GET("/references/critical-type")
    Call<List<CriticalType>> criticalType();

    @GET("/references/critical-type")
    Call<List<CriticalType>> criticalType(@Query("changedAfter") String changedAfter);

    @GET("/references/critical-type")
    Call<List<CriticalType>> criticalTypeById(@Query("id") String id);
}
