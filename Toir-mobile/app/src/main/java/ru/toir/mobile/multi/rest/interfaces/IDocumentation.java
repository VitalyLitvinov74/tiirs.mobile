package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Documentation;

/**
 * @author Dmitriy Loagachev
 * Created by koputo on 01.02.17.
 */

public interface IDocumentation {
    @GET("/documentation")
    Call<List<Documentation>> get();

    @GET("/documentation")
    Call<List<Documentation>> get(@Query("changedAfter") String changedAfter);

    @GET("/documentation")
    Call<List<Documentation>> getById(@Query("id") String id);

    @GET("/documentation")
    Call<List<Documentation>> getById(@Query("id") String[] id);

    @GET("/documentation")
    Call<List<Documentation>> getByUuid(@Query("uuid") String uuid);

    @GET("/documentation")
    Call<List<Documentation>> getByUuid(@Query("uuid") String[] uuid);

    @GET("/documentation")
    Call<List<Documentation>> getByEquipment(@Query("equipment") String uuid);

    @GET("/documentation")
    Call<List<Documentation>> getByEquipment(@Query("equipment[]") String[] uuid);

    @GET("/documentation")
    Call<List<Documentation>> getByEquipmentModel(@Query("equipmentModel") String uuid);

    @GET("/documentation")
    Call<List<Documentation>> getByEquipmentModel(@Query("equipmentModel[]") String[] uuid);
}
