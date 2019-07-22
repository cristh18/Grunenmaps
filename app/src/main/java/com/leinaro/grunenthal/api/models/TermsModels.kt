package com.leinaro.grunenthal.api.models

import com.google.gson.annotations.SerializedName

class TermsResponse(@SerializedName("result") val result: Boolean,
                    @SerializedName("data") val data: String)