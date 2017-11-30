package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.MeasuredValue;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IMeasuredValue {
    @GET("/measured-value")
    Call<List<MeasuredValue>> measuredValue();

    @GET("/measured-value")
    Call<List<MeasuredValue>> measuredValue(@Query("changedAfter") String changedAfter);

    @GET("/measured-value")
    Call<List<MeasuredValue>> measuredValueByUuid(@Query("uuid") String uuid);

    @GET("/measured-value")
    Call<List<MeasuredValue>> measuredValueByUuid(@Query("uuid") String[] uuid);

}
