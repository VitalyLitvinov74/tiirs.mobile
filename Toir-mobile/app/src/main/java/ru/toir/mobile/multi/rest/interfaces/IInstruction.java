package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.Instruction;

public interface IInstruction {
    @GET("/api/instruction")
    Call<List<Instruction>> get();

    @GET("/api/instruction")
    Call<List<Instruction>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/instruction")
    Call<List<Instruction>> getById(@Query("id") String id);

    @GET("/api/instruction")
    Call<List<Instruction>> getById(@Query("id") String[] id);

    @GET("/api/instruction")
    Call<List<Instruction>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/instruction")
    Call<List<Instruction>> getByUuid(@Query("uuid") String[] uuid);

    @GET("/api/instruction")
    Call<List<Instruction>> getByStageTemplate(@Query("template") String uuid);

    @GET("/api/instruction")
    Call<List<Instruction>> getByStageTemplate(@Query("template[]") String[] uuid);
}
