package ru.toir.mobile.rest.interfaces;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.toir.mobile.db.realm.Defect;
import ru.toir.mobile.db.realm.Documentation;

/**
 * @author Olejek
 * Created by olejek on 12.05.17.
 */

public interface IDefect {
    @GET("/api/objects/defect")
    Call<List<Defect>> defect();

    @GET("/api/objects/defect")
    Call<List<Defect>> defect(@Query("changedAfter") String changedAfter);

    @GET("/api/objects/defect")
    Call<List<Defect>> defectById(@Query("id") String id);
}
