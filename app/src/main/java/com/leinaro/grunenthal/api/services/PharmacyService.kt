package com.leinaro.grunenthal.api.services

import com.leinaro.grunenthal.api.models.PharmarciesResponse
import io.reactivex.Single
import retrofit2.http.GET

interface PharmacyService {

    @GET("api/all")
    fun getAllPharmacies(): Single<PharmarciesResponse>

}