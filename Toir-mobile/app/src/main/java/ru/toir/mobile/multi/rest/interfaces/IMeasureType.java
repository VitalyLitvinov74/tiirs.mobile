package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.MeasureType;

/**
 * @author Dmitriy Loagachev
 *         Created by koputo on 05.10.16.
 */
public interface IMeasureType {
    @GET("/measure-type")
    Call<List<MeasureType>> get();

    @GET("/measure-type")
    Call<List<MeasureType>> get(@Query("changedAfter") String changedAfter);

    @GET("/measure-type")
    Call<List<MeasureType>> getById(@Query("id") String id);

    @GET("/measure-type")
    Call<List<MeasureType>> getById(@Query("id[]") String[] id);

    @GET("/measure-type")
    Call<List<MeasureType>> getByUuid(@Query("uuid") String uuid);

    @GET("/measure-type")
    Call<List<MeasureType>> getByUuid(@Query("uuid[]") String[] uuid);
}
