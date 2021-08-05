package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.EquipmentAttribute;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 17/03/19.
 */

public interface IEquipmentAttribute {
    @GET("/api/equipment-attribute")
    Call<List<EquipmentAttribute>> get();

    @GET("/api/equipment-attribute")
    Call<List<EquipmentAttribute>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/equipment-attribute")
    Call<List<EquipmentAttribute>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/equipment-attribute")
    Call<List<EquipmentAttribute>> getByUuid(@Query("uuid[]") String[] uuid);

    @GET("/api/equipment-attribute")
    Call<List<EquipmentAttribute>> getByEquipment(@Query("equipment") String uuid);

    @GET("/api/equipment-attribute")
    Call<List<EquipmentAttribute>> getByEquipment(@Query("equipment[]") String[] uuid);

    @POST("/api/equipment-attribute/upload")
    Call<ResponseBody> send(@Body List<EquipmentAttribute> values);
}
