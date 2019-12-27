package ru.toir.mobile.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.EquipmentAttribute;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17/03/19.
 */

public interface IEquipmentAttribute {
    @GET("/equipment-attribute")
    Call<List<EquipmentAttribute>> get();

    @GET("/equipment-attribute")
    Call<List<EquipmentAttribute>> get(@Query("changedAfter") String changedAfter);

    @GET("/equipment-attribute")
    Call<List<EquipmentAttribute>> getByUuid(@Query("uuid") String uuid);

    @GET("/equipment-attribute")
    Call<List<EquipmentAttribute>> getByUuid(@Query("uuid[]") String[] uuid);

    @GET("/equipment-attribute")
    Call<List<EquipmentAttribute>> getByEquipment(@Query("equipment") String uuid);

    @GET("/equipment-attribute")
    Call<List<EquipmentAttribute>> getByEquipment(@Query("equipment[]") String[] uuid);

    @POST("/equipment-attribute/upload")
    Call<ResponseBody> send(@Body List<EquipmentAttribute> values);
}
