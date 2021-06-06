package com.photoapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PhotosModel {
    @SerializedName("photos")
    var photos: Photos? = null

    @SerializedName("stat")
    var stat: String? = null
}