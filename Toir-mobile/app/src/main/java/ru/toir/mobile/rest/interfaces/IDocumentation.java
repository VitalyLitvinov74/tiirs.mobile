package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Documentation;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IDocumentation {
    @GET("/references/documentation")
    Call<List<Documentation>> documentation(@Header("Authorization") String token);

    @GET("/references/documentation")
    Call<List<Documentation>> documentation(@Header("Authorization") String token,
                         @Query("changedAfter") String changedAfter);
    @GET("/references/documentation")
    Call<List<Documentation>> documentationById(@Header("Authorization") String token,
                             @Query("id") String id);
}
