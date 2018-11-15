package com.myquotepro.myquotepro.mpesa.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created  on 7/13/2017.
 */

class AccessToken(
    @field:SerializedName("access_token")
    @field:Expose
    var accessToken: String, @field:SerializedName("expires_in")
    @field:Expose
    private val expiresIn: String
)