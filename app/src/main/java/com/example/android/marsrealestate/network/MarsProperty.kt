package com.example.android.marsrealestate.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MarsProperty(
        val id: String,
        @Json(name = "img_src") val imgSrcUrl: String,  //Moshi either need Json annotation with key name of response or the key name and variable name in data class should match
        val type: String,
        val price: Double) : Parcelable {
    val isRental
        get() = type == "rent"
}
