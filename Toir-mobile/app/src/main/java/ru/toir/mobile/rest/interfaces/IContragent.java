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
    @GET("/clients")
    Call<List<Contragent>> get();

    @GET("/clients")
    Call<List<Contragent>> get(@Query("changedAfter") String changedAfter);

    @GET("/clients")
    Call<List<Contragent>> getById(@Query("id") String id);

    @GET("/clients")
    Call<List<Contragent>> getById(@Query("id[]") String[] id);

    @GET("/clients")
    Call<List<Contragent>> getByUuid(@Query("uuid") String uuid);

    @GET("/clients")
    Call<List<Contragent>> getByUuid(@Query("uuid[]") String[] uuid);
}
