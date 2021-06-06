package com.photoapp.data.model

import com.google.gson.annotations.SerializedName

class Photo {
    @SerializedName("id")
    var id: String? = null

    @SerializedName("owner")
    var owner: String? = null

    @SerializedName("secret")
    var secret: String? = null

    @SerializedName("server")
    var server: String? = null

    @SerializedName("farm")
    var farm: String? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("ispublic")
    var ispublic: String? = null

    @SerializedName("isfriend")
    var isfriend: String? = null

    @SerializedName("isfamily")
    var isfamily: String? = null
}