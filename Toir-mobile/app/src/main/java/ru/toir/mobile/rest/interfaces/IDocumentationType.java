package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;
import ru.toir.mobile.db.realm.DocumentationType;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 05.10.16.
 */
public interface IDocumentationType {
    @GET("/api/objects/documentation-type")
    Call<List<DocumentationType>> documentationType();

    @GET("/api/objects/documentation-type")
    Call<List<DocumentationType>> documentationType(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/documentation-type")
    Call<List<DocumentationType>> documentationTypeById(@Query("id") String id);
}
