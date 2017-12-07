package ru.toir.mobile.rest.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import ru.toir.mobile.db.realm.User;

/**
 * @author Dmitriy Logachev
 *         Created by koputo on 15.09.16.
 */
public interface IUserService {
    @GET("/account/me")
    Call<User> user();
}
