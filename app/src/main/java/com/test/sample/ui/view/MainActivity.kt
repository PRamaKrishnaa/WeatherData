package com.test.sample.ui.view

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.test.sample.R
import com.test.sample.data.model.WeatherModel
import com.test.sample.data.repository.WeatherRepository
import com.test.sample.databinding.ActivityMainBinding
import com.test.sample.ui.viewmodel.MainViewModel
import com.test.sample.ui.viewmodel.MainViewModel.WeatherViewState
import com.test.sample.ui.viewmodel.MainViewModelProviderFactory

class MainActivity : AppCompatActivity() {
    // region Variable Declaration
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = WeatherRepository()
        val provider = MainViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this, provider)[MainViewModel::class.java]

        initializeViews()
    }

    //region initialization
    private fun initializeViews() {
        with(binding) {

            searchBtn.setOnClickListener {
                val cityName = searchEdt.text.toString().trim()
                if (cityName.isNotEmpty()) {
                    setUpViewModelBindings(cityName)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter city name",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }

    }

    //end region
    //region viewmodel data
    private fun setUpViewModelBindings(cityName: String) {
        if (isNetworkConnected()) {
            viewModel.getLocationData(cityName)
            observeViewModel(viewModel)
        } else {
            renderErrorState("No Internet!!")
        }

    }


    private fun observeViewModel(viewModel: MainViewModel) {
        with(viewModel) {
            viewState.observe(this@MainActivity) { renderViewState(it) }
        }
    }

    private fun renderViewState(viewState: WeatherViewState?) {

        when (viewState) {
            is WeatherViewState.Loading -> {
                showProgressBar()
            }
            is WeatherViewState.Data -> {
                renderDataState(viewState.data)
            }
            is WeatherViewState.Error -> {
                renderErrorState(viewState.errorMsg)
            }
            else -> {}
        }
    }

    private fun renderDataState(weatherDataValues: WeatherModel) {
        with(binding) {
            hideProgressBar()

            cityText.text =
                getString(R.string.city_name, weatherDataValues.location.name)

            cityRegion.text =
                getString(R.string.city_region, weatherDataValues.location.region)

            country.text =
                getString(R.string.country, weatherDataValues.location.country)

            weatherState.text =
                getString(
                    R.string.weather_state,
                    weatherDataValues.current.condition.text
                )

            temp.text = getString(
                R.string.temp, (weatherDataValues.current.temp_c.toString()) + " C"
            )

            feelsLike.text =
                getString(
                    R.string.feels_like,
                    (weatherDataValues.current.feelslike_c.toString()) + " C"
                )
            windSpeed.text =
                getString(
                    R.string.wind_speed,
                    (weatherDataValues.current.wind_kph.toString()) + " km/h"
                )


            val icon = weatherDataValues.current.condition.icon

            Glide.with(applicationContext).load("https://" + icon).into(iconImage)

        }
    }

    private fun renderErrorState(errorMsg: String) {
        hideProgressBar()
        Toast.makeText(applicationContext, errorMsg, Toast.LENGTH_LONG).show()
    }
    //end region

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    //Checking active internet connection
    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return connectivityManager.activeNetwork?.let {
                connectivityManager.getNetworkCapabilities(it)
            }?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            @Suppress("deprecation")
            return connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }
}