package com.leinaro.grunenthal.api.models

import com.google.gson.annotations.SerializedName

data class PharmarciesResponse(@SerializedName("result") val result: Boolean,
                               @SerializedName("data") val data: List<Pharmacies>,
                               @SerializedName("msg") val msg: String)

data class Pharmacies(@SerializedName("pharmacies1") val pharmacies1: List<Pharmacy>,
                      @SerializedName("pharmacies2") val pharmacies2: List<Pharmacy>,
                      @SerializedName("pharmacies3") val pharmacies3: List<Pharmacy>)

data class Pharmacy(@SerializedName("name") val name: String,
                    @SerializedName("address") val address: String,
                    @SerializedName("city") val city: String,
                    @SerializedName("lat") val lat: String?,
                    @SerializedName("lon") val lon: String?,
                    @SerializedName("product") val productId: String,
                    @SerializedName("channel") val channel: String,
                    @SerializedName("idfranchise") val idFranchise: String,
                    @SerializedName("franchise") val franchise: String,
                    @SerializedName("phone") val phone: String,
                    @SerializedName("presentations") val presentations: String) {
    var color: String? = null
}