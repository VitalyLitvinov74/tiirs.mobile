package ru.toir.mobile.multi.rest.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.toir.mobile.multi.serverapi.TokenSrv;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface ITokenService {
    @FormUrlEncoded
    @POST("/api/token")
    Call<TokenSrv> getByLabel(@Field("label") String tagId, @Field("grant_type") String garantType);

    @FormUrlEncoded
    @POST("/api/token")
    Call<TokenSrv> getByPassword(@Field("login") String login, @Field("password") String password,
                                 @Field("grant_type") String garantType);
}
