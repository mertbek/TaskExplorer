package com.mertbek.taskexplorer.data.model

import com.google.gson.annotations.SerializedName

data class TaskItem(
    @SerializedName("task") val task: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("colorCode") val colorCode: String?,
    @SerializedName("sort") val sort: String? = null,
    @SerializedName("wageType") val wageType: String? = null
)