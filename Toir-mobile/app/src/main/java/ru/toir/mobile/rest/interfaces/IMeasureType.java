package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.MeasureType;

/**
 * @author Dmitriy Loagachev
 *         Created by koputo on 05.10.16.
 */
public interface IMeasureType {
    @GET("/api/references/measure_type")
    Call<List<MeasureType>> measureType(@Header("Authorization") String token,
                                        @Query("ChangedAfter") String changedAfter);
}
