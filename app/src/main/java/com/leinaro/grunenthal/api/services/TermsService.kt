package com.leinaro.grunenthal.api.services

import com.leinaro.grunenthal.api.models.TermsResponse
import io.reactivex.Single
import retrofit2.http.GET

interface TermsService {

    @GET("api/conditions")
    fun getTerms(): Single<TermsResponse>

}