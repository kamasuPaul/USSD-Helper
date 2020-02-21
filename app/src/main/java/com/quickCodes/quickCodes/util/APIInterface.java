package com.quickCodes.quickCodes.util;

import com.quickCodes.quickCodes.modals.UssdActionApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {
    @GET("/quickcodes")
    Call<List<UssdActionApi>> getAllCustomActions();
}
