package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.MeasuredValue;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IMeasuredValue {
    @GET("/references/measured-value")
    Call<List<MeasuredValue>> measuredValue(@Header("Authorization") String token);

    @GET("/references/measured-value")
    Call<List<MeasuredValue>> measuredValue(@Header("Authorization") String token,
                                            @Query("changedAfter") String changedAfter);

    @GET("/references/measured-value")
    Call<List<MeasuredValue>> measuredValueById(@Header("Authorization") String token,
                                                @Query("id") String id);

}
