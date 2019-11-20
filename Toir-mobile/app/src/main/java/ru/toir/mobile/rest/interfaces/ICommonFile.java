package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.CommonFile;
import ru.toir.mobile.db.realm.Documentation;

public interface ICommonFile {
    @GET("/common-file")
    Call<List<Documentation>> get();

    @GET("/common-file")
    Call<List<CommonFile>> get(@Query("changedAfter") String changedAfter);

    @GET("/common-file")
    Call<List<CommonFile>> getById(@Query("id") String id);

    @GET("/common-file")
    Call<List<CommonFile>> getById(@Query("id") String[] id);

    @GET("/common-file")
    Call<List<CommonFile>> getByUuid(@Query("uuid") String uuid);

    @GET("/common-file")
    Call<List<CommonFile>> getByUuid(@Query("uuid") String[] uuid);
}
