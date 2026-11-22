package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.UserPreferencesRepository
import com.example.data.repository.WeatherRepository
import com.example.localization.AppStrings
import com.example.model.AlertsResult
import com.example.model.DistrictWeatherSnapshot
import com.example.model.RadarData
import com.example.model.SriLankaLocation
import com.example.model.SriLankaLocations
import com.example.model.TemperatureUnit
import com.example.model.ThemeMode
import com.example.model.TimeFormatPreference
import com.example.model.UserPreferences
import com.example.model.WeatherBundle
import com.example.model.WeatherUiState
import com.example.model.WindSpeedUnit
import com.example.util.LocationHelper
import com.example.util.WeatherNotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val weatherRepository: WeatherRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = prefsRepository.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserPreferences()
        )

    val favoriteLocations: StateFlow<List<SriLankaLocation>> = weatherRepository.favoriteLocations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

    private val _selectedLocation = MutableStateFlow(SriLankaLocations.allDistricts.first())
    val selectedLocation: StateFlow<SriLankaLocation> = _selectedLocation.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _comparisonBundles = MutableStateFlow<Map<String, WeatherBundle>>(emptyMap())
    val comparisonBundles: StateFlow<Map<String, WeatherBundle>> = _comparisonBundles.asStateFlow()

    private val _districtSnapshots = MutableStateFlow<List<DistrictWeatherSnapshot>>(emptyList())
    val districtSnapshots: StateFlow<List<DistrictWeatherSnapshot>> = _districtSnapshots.asStateFlow()

    private val _isMapLoading = MutableStateFlow(false)
    val isMapLoading: StateFlow<Boolean> = _isMapLoading.asStateFlow()

    private val _radarData = MutableStateFlow<RadarData?>(null)
    val radarData: StateFlow<RadarData?> = _radarData.asStateFlow()

    private val _alertsResult = MutableStateFlow<AlertsResult?>(null)
    val alertsResult: StateFlow<AlertsResult?> = _alertsResult.asStateFlow()

    private val _isAlertsLoading = MutableStateFlow(false)
    val isAlertsLoading: StateFlow<Boolean> = _isAlertsLoading.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SriLankaLocation>>(SriLankaLocations.allDistricts)
    val searchResults: StateFlow<List<SriLankaLocation>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _isGpsDetecting = MutableStateFlow(false)
    val isGpsDetecting: StateFlow<Boolean> = _isGpsDetecting.asStateFlow()

    private val _gpsMessage = MutableStateFlow<String?>(null)
    val gpsMessage: StateFlow<String?> = _gpsMessage.asStateFlow()

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            weatherRepository.ensureDefaultFavorites()
            val prefs = prefsRepository.preferences.first()
            val savedLoc = weatherRepository.getSavedLocationById(prefs.selectedLocationId)
                ?: SriLankaLocations.allDistricts.first()
            _selectedLocation.value = savedLoc
            loadWeatherFor(savedLoc, forceRefresh = prefs.autoRefreshEnabled)
            refreshOfficialAlerts()
            refreshDistrictSnapshots()
            refreshRadar()
        }

        viewModelScope.launch {
            favoriteLocations.collect { favs ->
                if (favs.isNotEmpty()) {
                    _comparisonBundles.value = weatherRepository.fetchComparisonWeather(favs)
                } else {
                    _comparisonBundles.value = emptyMap()
                }
            }
        }
    }

    fun selectLocation(location: SriLankaLocation) {
        viewModelScope.launch {
            _selectedLocation.value = location
            prefsRepository.setSelectedLocationId(location.id)
            if (location.id.startsWith("lk_") || location.id.startsWith("gps_")) {
                weatherRepository.saveCustomLocation(location)
            }
            loadWeatherFor(location, forceRefresh = false)
        }
    }

    fun refreshCurrentWeather(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            loadWeatherFor(_selectedLocation.value, forceRefresh = forceRefresh)
            val favs = favoriteLocations.value
            if (favs.isNotEmpty()) {
                _comparisonBundles.value = weatherRepository.fetchComparisonWeather(favs)
            }
        }
    }

    private suspend fun loadWeatherFor(location: SriLankaLocation, forceRefresh: Boolean) {
        val currentSuccess = (_weatherState.value as? WeatherUiState.Success)?.bundle
        val cached = weatherRepository.getCachedWeather(location.id)
        if (currentSuccess == null || currentSuccess.locationId != location.id) {
            if (cached != null) {
                _weatherState.value = WeatherUiState.Success(cached)
            } else {
                _weatherState.value = WeatherUiState.Loading
            }
        }
        _isRefreshing.value = true

        val result = weatherRepository.fetchWeather(location, forceRefresh = forceRefresh)
        result.fold(
            onSuccess = { bundle ->
                _weatherState.value = WeatherUiState.Success(bundle)
            },
            onFailure = { err ->
                val fallback = weatherRepository.getCachedWeather(location.id)
                if (fallback != null) {
                    _weatherState.value = WeatherUiState.Success(fallback.copy(isFromCache = true))
                } else {
                    _weatherState.value = WeatherUiState.Error(
                        message = err.localizedMessage ?: "Network error",
                        cachedBundle = null
                    )
                }
            }
        )
        _isRefreshing.value = false
    }

    fun toggleFavorite(location: SriLankaLocation) {
        viewModelScope.launch {
            weatherRepository.toggleFavorite(location)
        }
    }

    fun searchLocations(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResults.value = SriLankaLocations.allDistricts
            _isSearching.value = false
            return
        }
        val trimmed = query.trim()
        val instantLocal = SriLankaLocations.allPresetLocations.filter { loc ->
            loc.nameEn.contains(trimmed, ignoreCase = true) ||
                loc.nameSi.contains(trimmed, ignoreCase = true) ||
                loc.nameTa.contains(trimmed, ignoreCase = true) ||
                loc.districtEn.contains(trimmed, ignoreCase = true) ||
                loc.provinceEn.contains(trimmed, ignoreCase = true)
        }
        _searchResults.value = instantLocal

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(250)
            _searchResults.value = weatherRepository.searchSriLankaLocations(trimmed)
            _isSearching.value = false
        }
    }

    fun detectCurrentLocation(context: Context, unavailableMsg: String) {
        viewModelScope.launch {
            _isGpsDetecting.value = true
            _gpsMessage.value = null
            val coords = LocationHelper.getCurrentCoordinates(context)
            if (coords != null) {
                val nearest = SriLankaLocations.findNearest(coords.first, coords.second)
                // If user is within Sri Lanka bounding box, use exact coordinates matched to nearest district
                val inSriLanka = coords.first in 5.5..10.2 && coords.second in 79.3..82.2
                val targetLoc = if (inSriLanka) {
                    nearest.copy(
                        latitude = coords.first,
                        longitude = coords.second
                    )
                } else {
                    // Outside Sri Lanka: default to nearest Sri Lankan district gracefully
                    nearest
                }
                selectLocation(targetLoc)
            } else {
                _gpsMessage.value = unavailableMsg
            }
            _isGpsDetecting.value = false
        }
    }

    fun clearGpsMessage() {
        _gpsMessage.value = null
    }

    fun refreshDistrictSnapshots() {
        viewModelScope.launch {
            _isMapLoading.value = true
            weatherRepository.fetchAll25DistrictSnapshots().onSuccess { list ->
                _districtSnapshots.value = list
            }
            _isMapLoading.value = false
        }
    }

    fun refreshRadar() {
        viewModelScope.launch {
            weatherRepository.fetchRadarData().onSuccess { data ->
                _radarData.value = data
            }
        }
    }

    fun refreshOfficialAlerts() {
        viewModelScope.launch {
            _isAlertsLoading.value = true
            _alertsResult.value = weatherRepository.fetchOfficialAlerts()
            _isAlertsLoading.value = false
        }
    }

    fun setLanguage(code: String) {
        viewModelScope.launch { prefsRepository.setLanguage(code) }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch { prefsRepository.setTemperatureUnit(unit) }
    }

    fun setWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch { prefsRepository.setWindSpeedUnit(unit) }
    }

    fun setTimeFormat(format: TimeFormatPreference) {
        viewModelScope.launch { prefsRepository.setTimeFormat(format) }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { prefsRepository.setThemeMode(mode) }
    }

    fun setAutoRefresh(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setAutoRefresh(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setNotificationsEnabled(enabled) }
    }

    fun setSevereAlertNotifications(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setSevereAlertNotifications(enabled) }
    }

    fun setRainNotifications(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setRainNotifications(enabled) }
    }

    fun setUseGpsLocation(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setUseGpsLocation(enabled) }
    }

    fun sendSampleNotification(context: Context, strings: AppStrings): Boolean {
        val bundle = (weatherState.value as? WeatherUiState.Success)?.bundle ?: return false
        val prefs = preferences.value
        return WeatherNotificationHelper.sendWeatherSummaryNotification(
            context = context,
            location = selectedLocation.value,
            bundle = bundle,
            tempUnit = prefs.temperatureUnit,
            langCode = prefs.languageCode,
            strings = strings
        )
    }

    class Factory(
        private val weatherRepository: WeatherRepository,
        private val prefsRepository: UserPreferencesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(weatherRepository, prefsRepository) as T
        }
    }
}
