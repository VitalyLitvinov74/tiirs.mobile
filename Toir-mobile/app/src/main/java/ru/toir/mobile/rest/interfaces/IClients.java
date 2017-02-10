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
    Call<List<Clients>> clients(@Header("Authorization") String token);

    @GET("/references/clients")
    Call<List<Clients>> clients(@Header("Authorization") String token,
                                    @Query("changedAfter") String changedAfter);
    @GET("/references/clients")
    Call<List<Clients>> clientsById(@Header("Authorization") String token,
                                        @Query("id") String id);
}
