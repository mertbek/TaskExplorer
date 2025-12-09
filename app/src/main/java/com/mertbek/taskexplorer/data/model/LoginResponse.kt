package com.mertbek.taskexplorer.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("oauth") val oauth: OauthData,
    @SerializedName("userInfo") val userInfo: UserInfo
)

data class OauthData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String?,
    @SerializedName("expires_in") val expiresIn: Int?
)

data class UserInfo(
    @SerializedName("personalNo") val personalNo: Int,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("businessUnit") val businessUnit: String?
)