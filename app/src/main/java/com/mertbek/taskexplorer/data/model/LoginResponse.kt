package com.mertbek.taskexplorer.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("oauth") val oauth: OauthData
)

data class OauthData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("expires_in") val expiresIn: Int?
)