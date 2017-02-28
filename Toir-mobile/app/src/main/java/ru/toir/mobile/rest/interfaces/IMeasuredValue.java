package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.MeasuredValue;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IMeasuredValue {
    @GET("/api/objects/measured-value")
    Call<List<MeasuredValue>> measuredValue();

    @GET("/api/objects/measured-value")
    Call<List<MeasuredValue>> measuredValue(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/measured-value")
    Call<List<MeasuredValue>> measuredValueById(@Query("id") String id);

}
