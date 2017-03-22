package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
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
