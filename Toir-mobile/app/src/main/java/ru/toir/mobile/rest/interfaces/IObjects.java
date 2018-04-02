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
    @GET("/objects")
    Call<List<Objects>> get();

    @GET("/objects")
    Call<List<Objects>> get(@Query("changedAfter") String changedAfter);

    @GET("/objects")
    Call<List<Objects>> getById(@Query("id") String id);

    @GET("/objects")
    Call<List<Objects>> getById(@Query("id[]") String[] id);

    @GET("/objects")
    Call<List<Objects>> getByUuid(@Query("uuid") String uuid);

    @GET("/objects")
    Call<List<Objects>> getByUuid(@Query("uuid[]") String[] uuid);
}
