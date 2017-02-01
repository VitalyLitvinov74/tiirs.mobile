package ru.toir.mobile.rest.interfaces;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import ru.toir.mobile.serverapi.TokenSrv;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface ITokenService {
    @FormUrlEncoded
    @POST("/token")
    Call<TokenSrv> tokenByLabel(@Field("label") String tagId, @Field("grant_type") String garantType);

    @FormUrlEncoded
    @POST("/token")
    Call<TokenSrv> tokenByPassword(@Field("password") String tagId, @Field("grant_type") String garantType);
}
