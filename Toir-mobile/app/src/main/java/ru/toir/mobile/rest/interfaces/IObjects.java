package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Objects;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IObjects {
    @GET("/api/objects/objects")
    Call<List<Objects>> objects();

    @GET("/api/objects/objects")
    Call<List<Objects>> objects(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/objects")
    Call<List<Objects>> objectsById(@Query("id") String id);
}
