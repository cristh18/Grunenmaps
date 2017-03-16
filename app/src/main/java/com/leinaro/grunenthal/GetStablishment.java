package com.leinaro.grunenthal;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * Created by Adela on 11/03/2017.
 */

public interface GetStablishment {
    @GET("all")
    Call<ResponseGetAllPharmacies> getALlPharmacies();

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://grt.disenostudio.com.co/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
