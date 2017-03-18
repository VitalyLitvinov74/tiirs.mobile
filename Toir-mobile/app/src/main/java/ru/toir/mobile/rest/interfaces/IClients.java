package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Clients;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IClients {
    @GET("/api/objects/clients")
    Call<List<Clients>> clients();

    @GET("/api/objects/clients")
    Call<List<Clients>> clients(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/clients")
    Call<List<Clients>> clientsById(@Query("id") String id);
}
