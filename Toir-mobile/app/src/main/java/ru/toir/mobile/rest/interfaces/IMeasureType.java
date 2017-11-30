package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.MeasureType;

/**
 * @author Dmitriy Loagachev
 *         Created by koputo on 05.10.16.
 */
public interface IMeasureType {
    @GET("/measure-type")
    Call<List<MeasureType>> measureType();

    @GET("/measure-type")
    Call<List<MeasureType>> measureType(@Query("changedAfter") String changedAfter);

    @GET("/measure-type")
    Call<List<MeasureType>> measureTypeById(@Query("id") String id);

    @GET("/measure-type")
    Call<List<MeasureType>> measureTypeById(@Query("id") String[] id);

    @GET("/measure-type")
    Call<List<MeasureType>> measureTypeByUuid(@Query("uuid") String uuid);

    @GET("/measure-type")
    Call<List<MeasureType>> measureTypeByUuid(@Query("uuid") String[] uuid);
}
