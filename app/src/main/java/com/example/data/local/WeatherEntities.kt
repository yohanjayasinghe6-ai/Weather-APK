package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.MonsoonZone
import com.example.model.SriLankaLocation

@Entity(tableName = "cached_weather")
data class CachedWeatherEntity(
    @PrimaryKey val locationId: String,
    val payloadJson: String,
    val fetchedAtMillis: Long
)

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameSi: String,
    val nameTa: String,
    val districtEn: String,
    val provinceEn: String,
    val provinceSi: String,
    val provinceTa: String,
    val latitude: Double,
    val longitude: Double,
    val monsoonZone: String,
    val isFavorite: Boolean,
    val sortOrder: Int,
    val addedAtMillis: Long
) {
    fun toDomain(): SriLankaLocation = SriLankaLocation(
        id = id,
        nameEn = nameEn,
        nameSi = nameSi,
        nameTa = nameTa,
        districtEn = districtEn,
        provinceEn = provinceEn,
        provinceSi = provinceSi,
        provinceTa = provinceTa,
        latitude = latitude,
        longitude = longitude,
        isDistrictCapital = true,
        monsoonZone = runCatching { MonsoonZone.valueOf(monsoonZone) }.getOrDefault(MonsoonZone.SOUTHWEST_WET)
    )

    companion object {
        fun fromDomain(
            location: SriLankaLocation,
            isFavorite: Boolean = true,
            sortOrder: Int = 0
        ): SavedLocationEntity = SavedLocationEntity(
            id = location.id,
            nameEn = location.nameEn,
            nameSi = location.nameSi,
            nameTa = location.nameTa,
            districtEn = location.districtEn,
            provinceEn = location.provinceEn,
            provinceSi = location.provinceSi,
            provinceTa = location.provinceTa,
            latitude = location.latitude,
            longitude = location.longitude,
            monsoonZone = location.monsoonZone.name,
            isFavorite = isFavorite,
            sortOrder = sortOrder,
            addedAtMillis = System.currentTimeMillis()
        )
    }
}
