package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Documentation;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IDocumentation {
    @GET("/documentation")
    Call<List<Documentation>> documentation();

    @GET("/documentation")
    Call<List<Documentation>> documentation(@Query("changedAfter") String changedAfter);

    @GET("/documentation")
    Call<List<Documentation>> documentationById(@Query("id") String id);

    @GET("/documentation")
    Call<List<Documentation>> documentationById(@Query("id") String[] id);

    @GET("/documentation")
    Call<List<Documentation>> documentationByUuid(@Query("uuid") String uuid);

    @GET("/documentation")
    Call<List<Documentation>> documentationByUuid(@Query("uuid") String[] uuid);

    @GET("/documentation")
    Call<List<Documentation>> documentationByEquipment(@Query("equipment") String uuid);

    @GET("/documentation")
    Call<List<Documentation>> documentationByEquipment(@Query("equipment") String[] uuid);

    @GET("/documentation")
    Call<List<Documentation>> documentationByEquipmentModel(@Query("equipmentModel") String uuid);

    @GET("/documentation")
    Call<List<Documentation>> documentationByEquipmentModel(@Query("equipmentModel") String[] uuid);
}
