package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Contragent;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IContragent {
    @GET("/contragents")
    Call<List<Contragent>> get();

    @GET("/contragents")
    Call<List<Contragent>> get(@Query("changedAfter") String changedAfter);

    @GET("/contragents")
    Call<List<Contragent>> getById(@Query("id") String id);

    @GET("/contragents")
    Call<List<Contragent>> getById(@Query("id[]") String[] id);

    @GET("/contragents")
    Call<List<Contragent>> getByUuid(@Query("uuid") String uuid);

    @GET("/contragents")
    Call<List<Contragent>> getByUuid(@Query("uuid[]") String[] uuid);
}
