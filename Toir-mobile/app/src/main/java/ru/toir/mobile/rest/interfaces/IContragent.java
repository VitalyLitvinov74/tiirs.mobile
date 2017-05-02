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
    @GET("/api/objects/contragents")
    Call<List<Contragent>> contragents();

    @GET("/api/objects/contragents")
    Call<List<Contragent>> contragents(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/contragents")
    Call<List<Contragent>> contragentsById(@Query("id") String id);
}
