package com.test.sample.data.datastore.service

import com.test.sample.data.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRemoteService {
    @GET("/v1/current.json")
    suspend fun getWeather(
        @Query("key") key: String?,
        @Query("q") q: String?
    ): Response<WeatherModel>

}