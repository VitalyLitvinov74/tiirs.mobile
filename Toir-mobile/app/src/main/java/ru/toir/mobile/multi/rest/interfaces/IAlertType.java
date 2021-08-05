package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.AlertType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IAlertType {
    @GET("/api/alert-type")
    Call<List<AlertType>> get();

    @GET("/api/alert-type")
    Call<List<AlertType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/alert-type")
    Call<List<AlertType>> getById(@Query("id") String id);

    @GET("/api/alert-type")
    Call<List<AlertType>> getById(@Query("id[]") String[] id);

    @GET("/api/alert-type")
    Call<List<AlertType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/alert-type")
    Call<List<AlertType>> getByUuid(@Query("uuid[]") String[] uuid);
}
