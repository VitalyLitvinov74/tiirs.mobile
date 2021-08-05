package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Contragent;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IContragent {
    @GET("/api/clients")
    Call<List<Contragent>> get();

    @GET("/api/clients")
    Call<List<Contragent>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/clients")
    Call<List<Contragent>> getById(@Query("id") String id);

    @GET("/api/clients")
    Call<List<Contragent>> getById(@Query("id[]") String[] id);

    @GET("/api/clients")
    Call<List<Contragent>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/clients")
    Call<List<Contragent>> getByUuid(@Query("uuid[]") String[] uuid);
}
