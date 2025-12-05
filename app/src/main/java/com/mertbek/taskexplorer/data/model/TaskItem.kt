package com.mertbek.taskexplorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,

    @SerializedName("task") val task: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("colorCode") val colorCode: String?,
    @SerializedName("sort") val sort: String? = null,
    @SerializedName("wageType") val wageType: String? = null
)