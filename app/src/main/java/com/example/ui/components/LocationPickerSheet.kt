package com.example.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.LocalAppStrings
import com.example.model.SriLankaLocation
import com.example.model.SriLankaLocations
import com.example.ui.theme.SunAmber
import com.example.util.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerSheet(
    selectedLocation: SriLankaLocation,
    favoriteLocations: List<SriLankaLocation>,
    searchResults: List<SriLankaLocation>,
    isSearching: Boolean,
    isGpsDetecting: Boolean,
    gpsMessage: String?,
    langCode: String,
    onSearchQueryChanged: (String) -> Unit,
    onSelectLocation: (SriLankaLocation) -> Unit,
    onToggleFavorite: (SriLankaLocation) -> Unit,
    onDetectGps: () -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var query by remember { mutableStateOf("") }
    var permissionDeniedNote by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            permissionDeniedNote = false
            onDetectGps()
        } else {
            permissionDeniedNote = true
        }
    }

    val favoriteIds = remember(favoriteLocations) {
        favoriteLocations.map { it.id }.toSet()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.selectLocation,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_location_picker")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = strings.close
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (LocationHelper.hasLocationPermission(context)) {
                        permissionDeniedNote = false
                        onDetectGps()
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }
                },
                enabled = !isGpsDetecting,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("gps_detect_button")
            ) {
                if (isGpsDetecting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(strings.gpsLocating)
                } else {
                    Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = strings.useCurrentLocation,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(strings.useCurrentLocation)
                }
            }

            if (permissionDeniedNote) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = strings.gpsPermissionRequired,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (!gpsMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = gpsMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    onSearchQueryChanged(it)
                },
                placeholder = { Text(strings.searchCitiesDistricts) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = strings.searchCitiesDistricts
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            query = ""
                            onSearchQueryChanged("")
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = strings.close)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("location_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 420.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                if (query.isBlank()) {
                    if (favoriteLocations.isNotEmpty()) {
                        item {
                            Text(
                                text = strings.favoriteLocations,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(favoriteLocations, key = { "fav_${it.id}" }) { loc ->
                            LocationPickerRow(
                                location = loc,
                                langCode = langCode,
                                isSelected = loc.id == selectedLocation.id,
                                isFavorite = true,
                                onSelect = {
                                    onSelectLocation(loc)
                                    onDismiss()
                                },
                                onToggleFavorite = { onToggleFavorite(loc) }
                            )
                        }
                        item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
                    }

                    item {
                        Text(
                            text = "${strings.all25Districts} (${SriLankaLocations.allDistricts.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(SriLankaLocations.allDistricts, key = { "dist_${it.id}" }) { loc ->
                        LocationPickerRow(
                            location = loc,
                            langCode = langCode,
                            isSelected = loc.id == selectedLocation.id,
                            isFavorite = loc.id in favoriteIds,
                            onSelect = {
                                onSelectLocation(loc)
                                onDismiss()
                            },
                            onToggleFavorite = { onToggleFavorite(loc) }
                        )
                    }

                    item { HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) }
                    item {
                        Text(
                            text = strings.townsAndCities,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(SriLankaLocations.majorTowns, key = { "town_${it.id}" }) { loc ->
                        LocationPickerRow(
                            location = loc,
                            langCode = langCode,
                            isSelected = loc.id == selectedLocation.id,
                            isFavorite = loc.id in favoriteIds,
                            onSelect = {
                                onSelectLocation(loc)
                                onDismiss()
                            },
                            onToggleFavorite = { onToggleFavorite(loc) }
                        )
                    }
                } else {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = strings.searchResults,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (isSearching) {
                                Spacer(modifier = Modifier.width(8.dp))
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }

                    if (searchResults.isEmpty() && !isSearching) {
                        item {
                            Text(
                                text = strings.noMatchingLocations,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 24.dp)
                            )
                        }
                    } else {
                        items(searchResults, key = { "search_${it.id}" }) { loc ->
                            LocationPickerRow(
                                location = loc,
                                langCode = langCode,
                                isSelected = loc.id == selectedLocation.id,
                                isFavorite = loc.id in favoriteIds,
                                onSelect = {
                                    onSelectLocation(loc)
                                    onDismiss()
                                },
                                onToggleFavorite = { onToggleFavorite(loc) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationPickerRow(
    location: SriLankaLocation,
    langCode: String,
    isSelected: Boolean,
    isFavorite: Boolean,
    onSelect: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(onClick = onSelect)
            .testTag("location_row_${location.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location.localizedName(langCode),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${location.districtEn} • ${location.localizedProvince(langCode)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("fav_toggle_${location.id}")
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) SunAmber else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
