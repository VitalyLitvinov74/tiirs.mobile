package ru.toir.mobile.multi.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.multi.db.realm.InstructionStageTemplate;

public interface IInstructionStageTemplate {
    @GET("/api/instruction-stage-template")
    Call<List<InstructionStageTemplate>> get();

    @GET("/api/instruction-stage-template")
    Call<List<InstructionStageTemplate>> get(@Query("changedAfter") String changedAfter);

    @GET("/api/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getById(@Query("id") String id);

    @GET("/api/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getById(@Query("id") String[] id);

    @GET("/api/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getByUuid(@Query("uuid") String uuid);

    @GET("/api/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getByUuid(@Query("uuid") String[] uuid);
}
