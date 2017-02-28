package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.MeasureType;

/**
 * @author Dmitriy Loagachev
 *         Created by koputo on 05.10.16.
 */
public interface IMeasureType {
    @GET("/api/objects/measure-type")
    Call<List<MeasureType>> measureType();

    @GET("/api/objects/measure-type")
    Call<List<MeasureType>> measureType(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/measure-type")
    Call<List<MeasureType>> measureTypeById(@Query("id") String id);
}
