package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.local.LankaWeatherDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.WeatherRepository
import com.example.ui.LankaWeatherApp
import com.example.ui.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        val db = LankaWeatherDatabase.getInstance(applicationContext)
        val weatherRepo = WeatherRepository.create(db.weatherDao())
        val prefsRepo = UserPreferencesRepository(applicationContext)
        MainViewModel.Factory(weatherRepo, prefsRepo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LankaWeatherApp(viewModel = viewModel)
        }
    }
}
