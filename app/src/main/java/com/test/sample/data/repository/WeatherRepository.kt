package com.test.sample.data.repository

import com.test.sample.data.datastore.service.RetrofitInstance

class WeatherRepository {
    suspend fun getWeatherData(
        key: String,
        city: String
    ) = RetrofitInstance.api.getWeather(key, city)
}