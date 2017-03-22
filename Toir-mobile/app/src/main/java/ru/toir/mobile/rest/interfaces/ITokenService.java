package ru.toir.mobile.rest.interfaces;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
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
