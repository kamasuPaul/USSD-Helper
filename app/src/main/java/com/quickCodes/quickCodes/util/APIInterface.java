package com.quickCodes.quickCodes.util;

import com.quickCodes.quickCodes.modals.CustomAction;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface APIInterface {
    @GET("/todos")
    Call<List<CustomAction>> getAllCustomActions();
}
