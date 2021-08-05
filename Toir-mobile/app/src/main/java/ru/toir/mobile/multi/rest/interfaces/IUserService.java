package ru.toir.mobile.multi.rest.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.toir.mobile.multi.db.realm.User;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface IUserService {
    @GET("/api/account/me")
    Call<User> user();
}
