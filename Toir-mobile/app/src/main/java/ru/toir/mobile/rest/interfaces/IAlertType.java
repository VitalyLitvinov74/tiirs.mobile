package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.AlertType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 01.02.17.
 */

public interface IAlertType {
    @GET("/references/alert-type")
    Call<List<AlertType>> alertType();

    @GET("/references/alert-type")
    Call<List<AlertType>> alertType(@Query("changedAfter") String changedAfter);

    @GET("/references/alert-type")
    Call<List<AlertType>> alertTypeById(@Query("id") String id);
}
