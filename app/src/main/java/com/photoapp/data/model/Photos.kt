package com.photoapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Photos {
    @SerializedName("page")
    var page: String = "0"

    @SerializedName("pages")
    var pages: String? = null

    @SerializedName("perpage")
    var perpage: String = "1"

    @SerializedName("total")
    var total: String? = null

    @SerializedName("photo")
    var photo: ArrayList<Photo>? = null
}