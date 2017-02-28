package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.Documentation;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IDocumentation {
    @GET("/api/objects/documentation")
    Call<List<Documentation>> documentation();

    @GET("/api/objects/documentation")
    Call<List<Documentation>> documentation(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/documentation")
    Call<List<Documentation>> documentationById(@Query("id") String id);
}
