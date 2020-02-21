package com.quickCodes.quickCodes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    public static String BASE_URL ="http://10.0.2.2:8000/api/" ;//"https://blackbooksuganda.com/api/quickcodes"//"http://10.0.2.2:8000"
    private static Retrofit retrofit;
    public static Retrofit getClient(){

        OkHttpClient client = new OkHttpClient.Builder().build();

        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build();
        return retrofit;
    }
}
