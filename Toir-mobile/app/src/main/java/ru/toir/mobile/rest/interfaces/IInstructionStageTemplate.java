package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.InstructionStageTemplate;

public interface IInstructionStageTemplate {
    @GET("/instruction-stage-template")
    Call<List<InstructionStageTemplate>> get();

    @GET("/instruction-stage-template")
    Call<List<InstructionStageTemplate>> get(@Query("changedAfter") String changedAfter);

    @GET("/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getById(@Query("id") String id);

    @GET("/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getById(@Query("id") String[] id);

    @GET("/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getByUuid(@Query("uuid") String uuid);

    @GET("/instruction-stage-template")
    Call<List<InstructionStageTemplate>> getByUuid(@Query("uuid") String[] uuid);
}
