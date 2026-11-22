package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.localization.LocalAppStrings
import com.example.localization.stringsForLanguage
import com.example.model.AlertsResult
import com.example.ui.components.LocationPickerSheet
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.ForecastScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WeatherMapScreen
import com.example.ui.theme.LankaWeatherTheme
import com.example.util.LocationHelper

object LankaRoutes {
    const val HOME = "home"
    const val FORECAST = "forecast"
    const val MAP = "map"
    const val ALERTS = "alerts"
    const val SETTINGS = "settings"
}

private data class NavTabItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val badgeCount: Int = 0
)

@Composable
fun LankaWeatherApp(viewModel: MainViewModel) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val favoriteLocations by viewModel.favoriteLocations.collectAsStateWithLifecycle()
    val weatherState by viewModel.weatherState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val comparisonBundles by viewModel.comparisonBundles.collectAsStateWithLifecycle()
    val districtSnapshots by viewModel.districtSnapshots.collectAsStateWithLifecycle()
    val isMapLoading by viewModel.isMapLoading.collectAsStateWithLifecycle()
    val radarData by viewModel.radarData.collectAsStateWithLifecycle()
    val alertsResult by viewModel.alertsResult.collectAsStateWithLifecycle()
    val isAlertsLoading by viewModel.isAlertsLoading.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
    val isGpsDetecting by viewModel.isGpsDetecting.collectAsStateWithLifecycle()
    val gpsMessage by viewModel.gpsMessage.collectAsStateWithLifecycle()

    val strings = remember(preferences.languageCode) {
        stringsForLanguage(preferences.languageCode)
    }

    val context = LocalContext.current
    LaunchedEffect(preferences.useGpsLocation) {
        if (preferences.useGpsLocation && LocationHelper.hasLocationPermission(context)) {
            viewModel.detectCurrentLocation(context, strings.gpsDisabledMessage)
        }
    }

    var showLocationPicker by remember { mutableStateOf(false) }

    val activeAlertsCount = (alertsResult as? AlertsResult.Available)?.alerts?.size ?: 0
    val navTabs = remember(strings, activeAlertsCount) {
        listOf(
            NavTabItem(
                route = LankaRoutes.HOME,
                label = strings.navHome,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home
            ),
            NavTabItem(
                route = LankaRoutes.FORECAST,
                label = strings.navForecast,
                selectedIcon = Icons.Filled.CalendarMonth,
                unselectedIcon = Icons.Outlined.CalendarMonth
            ),
            NavTabItem(
                route = LankaRoutes.MAP,
                label = strings.navMap,
                selectedIcon = Icons.Filled.Map,
                unselectedIcon = Icons.Outlined.Map
            ),
            NavTabItem(
                route = LankaRoutes.ALERTS,
                label = strings.navAlerts,
                selectedIcon = Icons.Filled.Notifications,
                unselectedIcon = Icons.Outlined.Notifications,
                badgeCount = activeAlertsCount
            ),
            NavTabItem(
                route = LankaRoutes.SETTINGS,
                label = strings.navSettings,
                selectedIcon = Icons.Filled.Settings,
                unselectedIcon = Icons.Outlined.Settings
            )
        )
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    fun navigateToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    LankaWeatherTheme(
        themeMode = preferences.themeMode,
        languageCode = preferences.languageCode
    ) {
        CompositionLocalProvider(LocalAppStrings provides strings) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val useNavigationRail = maxWidth >= 680.dp

                Row(modifier = Modifier.fillMaxSize()) {
                    if (useNavigationRail) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            navTabs.forEach { item ->
                                val selected = currentDestination?.hierarchy?.any {
                                    it.route == item.route
                                } == true
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateToTab(item.route) },
                                    icon = {
                                        TabIconWithBadge(item = item, selected = selected)
                                    },
                                    label = {
                                        Text(
                                            text = item.label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    modifier = Modifier.testTag("nav_tab_${item.route}")
                                )
                            }
                        }
                    }

                    Scaffold(
                        modifier = Modifier.weight(1f),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        bottomBar = {
                            if (!useNavigationRail) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    navTabs.forEach { item ->
                                        val selected = currentDestination?.hierarchy?.any {
                                            it.route == item.route
                                        } == true
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = { navigateToTab(item.route) },
                                            icon = {
                                                TabIconWithBadge(item = item, selected = selected)
                                            },
                                            label = {
                                                Text(
                                                    text = item.label,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            },
                                            colors = NavigationBarItemDefaults.colors(
                                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                            ),
                                            modifier = Modifier.testTag("nav_tab_${item.route}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = LankaRoutes.HOME
                            ) {
                                composable(LankaRoutes.HOME) {
                                    HomeScreen(
                                        selectedLocation = selectedLocation,
                                        favoriteLocations = favoriteLocations,
                                        weatherState = weatherState,
                                        isRefreshing = isRefreshing,
                                        comparisonBundles = comparisonBundles,
                                        preferences = preferences,
                                        onOpenLocationPicker = { showLocationPicker = true },
                                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                                        onRefresh = { viewModel.refreshCurrentWeather(forceRefresh = true) },
                                        onSelectLocation = { viewModel.selectLocation(it) },
                                        onNavigateToForecast = { navigateToTab(LankaRoutes.FORECAST) }
                                    )
                                }
                                composable(LankaRoutes.FORECAST) {
                                    ForecastScreen(
                                        selectedLocation = selectedLocation,
                                        weatherState = weatherState,
                                        preferences = preferences,
                                        onRefresh = { viewModel.refreshCurrentWeather(forceRefresh = true) }
                                    )
                                }
                                composable(LankaRoutes.MAP) {
                                    WeatherMapScreen(
                                        selectedLocation = selectedLocation,
                                        districtSnapshots = districtSnapshots,
                                        isMapLoading = isMapLoading,
                                        radarData = radarData,
                                        preferences = preferences,
                                        onRefreshMap = {
                                            viewModel.refreshDistrictSnapshots()
                                            viewModel.refreshRadar()
                                        },
                                        onSelectLocationAndOpenHome = { loc ->
                                            viewModel.selectLocation(loc)
                                            navigateToTab(LankaRoutes.HOME)
                                        }
                                    )
                                }
                                composable(LankaRoutes.ALERTS) {
                                    AlertsScreen(
                                        selectedLocation = selectedLocation,
                                        weatherState = weatherState,
                                        alertsResult = alertsResult,
                                        isAlertsLoading = isAlertsLoading,
                                        preferences = preferences,
                                        onRefreshAlerts = { viewModel.refreshOfficialAlerts() }
                                    )
                                }
                                composable(LankaRoutes.SETTINGS) {
                                    SettingsScreen(
                                        preferences = preferences,
                                        selectedLocation = selectedLocation,
                                        onLanguageSelected = { viewModel.setLanguage(it) },
                                        onTempUnitSelected = { viewModel.setTemperatureUnit(it) },
                                        onWindUnitSelected = { viewModel.setWindSpeedUnit(it) },
                                        onTimeFormatSelected = { viewModel.setTimeFormat(it) },
                                        onThemeModeSelected = { viewModel.setThemeMode(it) },
                                        onAutoRefreshChanged = { viewModel.setAutoRefresh(it) },
                                        onUseGpsChanged = { viewModel.setUseGpsLocation(it) },
                                        onNotificationsChanged = { viewModel.setNotificationsEnabled(it) },
                                        onSevereAlertNotifChanged = { viewModel.setSevereAlertNotifications(it) },
                                        onRainNotifChanged = { viewModel.setRainNotifications(it) },
                                        onOpenLocationPicker = { showLocationPicker = true },
                                        onSendSampleNotification = {
                                            viewModel.sendSampleNotification(context, strings)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (showLocationPicker) {
                    LocationPickerSheet(
                        selectedLocation = selectedLocation,
                        favoriteLocations = favoriteLocations,
                        searchResults = searchResults,
                        isSearching = isSearching,
                        isGpsDetecting = isGpsDetecting,
                        gpsMessage = gpsMessage,
                        langCode = preferences.languageCode,
                        onSearchQueryChanged = { viewModel.searchLocations(it) },
                        onSelectLocation = { viewModel.selectLocation(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onDetectGps = {
                            viewModel.detectCurrentLocation(context, strings.gpsDisabledMessage)
                        },
                        onDismiss = {
                            viewModel.clearGpsMessage()
                            viewModel.searchLocations("")
                            showLocationPicker = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TabIconWithBadge(item: NavTabItem, selected: Boolean) {
    val iconVector = if (selected) item.selectedIcon else item.unselectedIcon
    if (item.badgeCount > 0) {
        BadgedBox(
            badge = {
                Badge { Text("${item.badgeCount}") }
            }
        ) {
            Icon(imageVector = iconVector, contentDescription = item.label)
        }
    } else {
        Icon(imageVector = iconVector, contentDescription = item.label)
    }
}
