package com.test.sample.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.sample.data.datastore.utils.Constants.Companion.KEY
import com.test.sample.data.model.WeatherModel
import com.test.sample.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class MainViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    sealed class WeatherViewState {
        object Loading : WeatherViewState()
        data class Data(val data: WeatherModel) : WeatherViewState()
        data class Error(val errorMsg: String) : WeatherViewState()
    }

    private val _viewState = MutableLiveData<WeatherViewState>()
    val viewState: LiveData<WeatherViewState> = _viewState

    fun getLocationData(city: String) {
        viewModelScope.launch {
            _viewState.value = WeatherViewState.Loading

            val weatherDataResponse = weatherRepository.getWeatherData(KEY, city)

            if (weatherDataResponse.isSuccessful) {
                weatherDataResponse.body()?.let {
                    handleWeatherResponse(it)
                }

            } else {
                handleError("No matching location found.")

            }

        }
    }

    private fun handleWeatherResponse(weatherResponse: WeatherModel) {
        _viewState.value = WeatherViewState.Data(weatherResponse)
    }

    private fun handleError(message: String) {
        _viewState.value = WeatherViewState.Error(message)
    }

}