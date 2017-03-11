package com.leinaro.grunenthal;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Adela on 11/03/2017.
 */

    public interface GetStablishment {
        @GET("all")
        Call<List<Pharmacies>> getALlPharmacies();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://grt.disenostudio.com.co/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
