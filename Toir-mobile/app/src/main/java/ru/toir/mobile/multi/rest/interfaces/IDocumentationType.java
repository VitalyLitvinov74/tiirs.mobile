package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.DocumentationType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IDocumentationType {
    @GET("/api/documentation-type")
    Call<List<DocumentationType>> get();

    @GET("/api/documentation-type")
    Call<List<DocumentationType>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/documentation-type")
    Call<List<DocumentationType>> getById(@Query("id") String id);

    @GET("/api/documentation-type")
    Call<List<DocumentationType>> getById(@Query("id[]") String[] id);

    @GET("/api/documentation-type")
    Call<List<DocumentationType>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/documentation-type")
    Call<List<DocumentationType>> getByUuid(@Query("uuid[]") String[] uuid);
}
