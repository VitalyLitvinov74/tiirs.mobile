package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.AlertType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IAlertType {
    @GET("/alert-type")
    Call<List<AlertType>> get();

    @GET("/alert-type")
    Call<List<AlertType>> get(@Query("changedAfter") String changedAfter);

    @GET("/alert-type")
    Call<List<AlertType>> getById(@Query("id") String id);

    @GET("/alert-type")
    Call<List<AlertType>> getById(@Query("id[]") String[] id);

    @GET("/alert-type")
    Call<List<AlertType>> getByUuid(@Query("uuid") String uuid);

    @GET("/alert-type")
    Call<List<AlertType>> getByUuid(@Query("uuid[]") String[] uuid);
}
