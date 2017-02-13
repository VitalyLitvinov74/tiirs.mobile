package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Clients;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IClients {
    @GET("/references/clients")
    Call<List<Clients>> clients();

    @GET("/references/clients")
    Call<List<Clients>> clients(@Query("changedAfter") String changedAfter);

    @GET("/references/clients")
    Call<List<Clients>> clientsById(@Query("id") String id);
}
