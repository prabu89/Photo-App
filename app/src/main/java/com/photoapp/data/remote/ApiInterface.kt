package com.photoapp.data.remote

import com.photoapp.data.model.PhotosModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiInterface {
    // Photos List
    @GET("?method=flickr.photos.search")
    fun getPhotosByTag(@QueryMap hashMap: HashMap<String,String>): Single<PhotosModel>
}
