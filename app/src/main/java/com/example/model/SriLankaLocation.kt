package com.example.model

enum class MonsoonZone {
    SOUTHWEST_WET,
    NORTH_EAST_DRY,
    CENTRAL_HIGHLANDS,
    INTERMEDIATE
}

data class SriLankaLocation(
    val id: String,
    val nameEn: String,
    val nameSi: String,
    val nameTa: String,
    val districtEn: String,
    val provinceEn: String,
    val provinceSi: String,
    val provinceTa: String,
    val latitude: Double,
    val longitude: Double,
    val isDistrictCapital: Boolean = true,
    val monsoonZone: MonsoonZone = MonsoonZone.SOUTHWEST_WET
) {
    fun localizedName(langCode: String): String = when (langCode) {
        "si" -> nameSi
        "ta" -> nameTa
        else -> nameEn
    }

    fun localizedProvince(langCode: String): String = when (langCode) {
        "si" -> provinceSi
        "ta" -> provinceTa
        else -> provinceEn
    }
}

object SriLankaLocations {

    /**
     * All 25 administrative districts of Sri Lanka with accurate coordinates,
     * trilingual names (English, Sinhala, Tamil), provinces, and monsoon climate zones.
     */
    val allDistricts: List<SriLankaLocation> = listOf(
        SriLankaLocation(
            id = "colombo",
            nameEn = "Colombo",
            nameSi = "කොළඹ",
            nameTa = "கொழும்பு",
            districtEn = "Colombo",
            provinceEn = "Western Province",
            provinceSi = "බස්නාහිර පළාත",
            provinceTa = "மேல் மாகாணம்",
            latitude = 6.9271,
            longitude = 79.8612,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "gampaha",
            nameEn = "Gampaha",
            nameSi = "ගම්පහ",
            nameTa = "கம்பஹா",
            districtEn = "Gampaha",
            provinceEn = "Western Province",
            provinceSi = "බස්නාහිර පළාත",
            provinceTa = "மேல் மாகாணம்",
            latitude = 7.0873,
            longitude = 80.0144,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "kalutara",
            nameEn = "Kalutara",
            nameSi = "කළුතර",
            nameTa = "களுத்துறை",
            districtEn = "Kalutara",
            provinceEn = "Western Province",
            provinceSi = "බස්නාහිර පළාත",
            provinceTa = "மேல் மாகாணம்",
            latitude = 6.5854,
            longitude = 79.9607,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "kandy",
            nameEn = "Kandy",
            nameSi = "මහනුවර",
            nameTa = "கண்டி",
            districtEn = "Kandy",
            provinceEn = "Central Province",
            provinceSi = "මධ්‍යම පළාත",
            provinceTa = "மத்திய மாகாணம்",
            latitude = 7.2906,
            longitude = 80.6337,
            monsoonZone = MonsoonZone.CENTRAL_HIGHLANDS
        ),
        SriLankaLocation(
            id = "matale",
            nameEn = "Matale",
            nameSi = "මාතලේ",
            nameTa = "மாத்தளை",
            districtEn = "Matale",
            provinceEn = "Central Province",
            provinceSi = "මධ්‍යම පළාත",
            provinceTa = "மத்திய மாகாணம்",
            latitude = 7.4675,
            longitude = 80.6234,
            monsoonZone = MonsoonZone.INTERMEDIATE
        ),
        SriLankaLocation(
            id = "nuwara_eliya",
            nameEn = "Nuwara Eliya",
            nameSi = "නුවරඑළිය",
            nameTa = "நுவரெலியா",
            districtEn = "Nuwara Eliya",
            provinceEn = "Central Province",
            provinceSi = "මධ්‍යම පළාත",
            provinceTa = "மத்திய மாகாணம்",
            latitude = 6.9497,
            longitude = 80.7891,
            monsoonZone = MonsoonZone.CENTRAL_HIGHLANDS
        ),
        SriLankaLocation(
            id = "galle",
            nameEn = "Galle",
            nameSi = "ගාල්ල",
            nameTa = "காலி",
            districtEn = "Galle",
            provinceEn = "Southern Province",
            provinceSi = "දකුණු පළාත",
            provinceTa = "தென் மாகாணம்",
            latitude = 6.0535,
            longitude = 80.2210,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "matara",
            nameEn = "Matara",
            nameSi = "මාතර",
            nameTa = "மாத்தறை",
            districtEn = "Matara",
            provinceEn = "Southern Province",
            provinceSi = "දකුණු පළාත",
            provinceTa = "தென் மாகாணம்",
            latitude = 5.9549,
            longitude = 80.5550,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "hambantota",
            nameEn = "Hambantota",
            nameSi = "හම්බන්තොට",
            nameTa = "அம்பாந்தோட்டை",
            districtEn = "Hambantota",
            provinceEn = "Southern Province",
            provinceSi = "දකුණු පළාත",
            provinceTa = "தென் மாகாணம்",
            latitude = 6.1429,
            longitude = 81.1212,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "jaffna",
            nameEn = "Jaffna",
            nameSi = "යාපනය",
            nameTa = "யாழ்ப்பாணம்",
            districtEn = "Jaffna",
            provinceEn = "Northern Province",
            provinceSi = "උතුරු පළාත",
            provinceTa = "வட மாகாணம்",
            latitude = 9.6615,
            longitude = 80.0255,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "kilinochchi",
            nameEn = "Kilinochchi",
            nameSi = "කිලිනොච්චිය",
            nameTa = "கிளிநொச்சி",
            districtEn = "Kilinochchi",
            provinceEn = "Northern Province",
            provinceSi = "උතුරු පළාත",
            provinceTa = "வட மாகாணம்",
            latitude = 9.3803,
            longitude = 80.3770,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "mannar",
            nameEn = "Mannar",
            nameSi = "මන්නාරම",
            nameTa = "மன்னார்",
            districtEn = "Mannar",
            provinceEn = "Northern Province",
            provinceSi = "උතුරු පළාත",
            provinceTa = "வட மாகாணம்",
            latitude = 8.9810,
            longitude = 79.9044,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "vavuniya",
            nameEn = "Vavuniya",
            nameSi = "වවුනියාව",
            nameTa = "வவுனியா",
            districtEn = "Vavuniya",
            provinceEn = "Northern Province",
            provinceSi = "උතුරු පළාත",
            provinceTa = "வட மாகாணம்",
            latitude = 8.7514,
            longitude = 80.4971,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "mullaitivu",
            nameEn = "Mullaitivu",
            nameSi = "මුලතිව්",
            nameTa = "முல்லைத்தீவு",
            districtEn = "Mullaitivu",
            provinceEn = "Northern Province",
            provinceSi = "උතුරු පළාත",
            provinceTa = "வட மாகாணம்",
            latitude = 9.2671,
            longitude = 80.8142,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "batticaloa",
            nameEn = "Batticaloa",
            nameSi = "මඩකලපුව",
            nameTa = "மட்டக்களப்பு",
            districtEn = "Batticaloa",
            provinceEn = "Eastern Province",
            provinceSi = "නැගෙනහිර පළාත",
            provinceTa = "கிழக்கு மாகாணம்",
            latitude = 7.7310,
            longitude = 81.6747,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "ampara",
            nameEn = "Ampara",
            nameSi = "අම්පාර",
            nameTa = "அம்பாறை",
            districtEn = "Ampara",
            provinceEn = "Eastern Province",
            provinceSi = "නැගෙනහිර පළාත",
            provinceTa = "கிழக்கு மாகாணம்",
            latitude = 7.2975,
            longitude = 81.6820,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "trincomalee",
            nameEn = "Trincomalee",
            nameSi = "ත්‍රිකුණාමලය",
            nameTa = "திருகோணமலை",
            districtEn = "Trincomalee",
            provinceEn = "Eastern Province",
            provinceSi = "නැගෙනහිර පළාත",
            provinceTa = "கிழக்கு மாகாணம்",
            latitude = 8.5874,
            longitude = 81.2152,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "kurunegala",
            nameEn = "Kurunegala",
            nameSi = "කුරුණෑගල",
            nameTa = "குருநாகல்",
            districtEn = "Kurunegala",
            provinceEn = "North Western Province",
            provinceSi = "වයඹ පළාත",
            provinceTa = "வட மேல் மாகாணம்",
            latitude = 7.4863,
            longitude = 80.3623,
            monsoonZone = MonsoonZone.INTERMEDIATE
        ),
        SriLankaLocation(
            id = "puttalam",
            nameEn = "Puttalam",
            nameSi = "පුත්තලම",
            nameTa = "புத்தளம்",
            districtEn = "Puttalam",
            provinceEn = "North Western Province",
            provinceSi = "වයඹ පළාත",
            provinceTa = "வட மேல் மாகாணம்",
            latitude = 8.0362,
            longitude = 79.8283,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "anuradhapura",
            nameEn = "Anuradhapura",
            nameSi = "අනුරාධපුරය",
            nameTa = "அனுராதபுரம்",
            districtEn = "Anuradhapura",
            provinceEn = "North Central Province",
            provinceSi = "උතුරු මැද පළාත",
            provinceTa = "வட மத்திய மாகாணம்",
            latitude = 8.3114,
            longitude = 80.4037,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "polonnaruwa",
            nameEn = "Polonnaruwa",
            nameSi = "පොළොන්නරුව",
            nameTa = "பொலன்னறுவை",
            districtEn = "Polonnaruwa",
            provinceEn = "North Central Province",
            provinceSi = "උතුරු මැද පළාත",
            provinceTa = "வட மத்திய மாகாணம்",
            latitude = 7.9403,
            longitude = 81.0188,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "badulla",
            nameEn = "Badulla",
            nameSi = "බදුල්ල",
            nameTa = "பதுளை",
            districtEn = "Badulla",
            provinceEn = "Uva Province",
            provinceSi = "ඌව පළාත",
            provinceTa = "ஊவா மாகாணம்",
            latitude = 6.9934,
            longitude = 81.0550,
            monsoonZone = MonsoonZone.INTERMEDIATE
        ),
        SriLankaLocation(
            id = "monaragala",
            nameEn = "Monaragala",
            nameSi = "මොණරාගල",
            nameTa = "மொனராகலை",
            districtEn = "Monaragala",
            provinceEn = "Uva Province",
            provinceSi = "ඌව පළාත",
            provinceTa = "ஊவா மாகாணம்",
            latitude = 6.8728,
            longitude = 81.3507,
            monsoonZone = MonsoonZone.INTERMEDIATE
        ),
        SriLankaLocation(
            id = "ratnapura",
            nameEn = "Ratnapura",
            nameSi = "රත්නපුර",
            nameTa = "இரத்தினபுரி",
            districtEn = "Ratnapura",
            provinceEn = "Sabaragamuwa Province",
            provinceSi = "සබරගමුව පළාත",
            provinceTa = "சபரகமுவ மாகாணம்",
            latitude = 6.6828,
            longitude = 80.3992,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "kegalle",
            nameEn = "Kegalle",
            nameSi = "කෑගල්ල",
            nameTa = "கேகாலை",
            districtEn = "Kegalle",
            provinceEn = "Sabaragamuwa Province",
            provinceSi = "සබරගමුව පළාත",
            provinceTa = "சபரகமுவ மாகாணம்",
            latitude = 7.2513,
            longitude = 80.3464,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        )
    )

    val majorTowns: List<SriLankaLocation> = listOf(
        SriLankaLocation(
            id = "negombo",
            nameEn = "Negombo",
            nameSi = "මීගමුව",
            nameTa = "நீர்கொழும்பு",
            districtEn = "Gampaha",
            provinceEn = "Western Province",
            provinceSi = "බස්නාහිර පළාත",
            provinceTa = "மேல் மாகாணம்",
            latitude = 7.2008,
            longitude = 79.8737,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "kotte",
            nameEn = "Sri Jayawardenepura Kotte",
            nameSi = "ශ්‍රී ජයවර්ධනපුර කෝට්ටේ",
            nameTa = "ஸ்ரீ ஜெயவர்தனபுர கோட்டை",
            districtEn = "Colombo",
            provinceEn = "Western Province",
            provinceSi = "බස්නාහිර පළාත",
            provinceTa = "மேல் மாகாணம்",
            latitude = 6.8941,
            longitude = 79.9025,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "ella",
            nameEn = "Ella",
            nameSi = "ඇල්ල",
            nameTa = "எல்ல",
            districtEn = "Badulla",
            provinceEn = "Uva Province",
            provinceSi = "ඌව පළාත",
            provinceTa = "ஊவா மாகாணம்",
            latitude = 6.8667,
            longitude = 81.0466,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.CENTRAL_HIGHLANDS
        ),
        SriLankaLocation(
            id = "dambulla",
            nameEn = "Dambulla",
            nameSi = "දඹුල්ල",
            nameTa = "தம்புள்ளை",
            districtEn = "Matale",
            provinceEn = "Central Province",
            provinceSi = "මධ්‍යම පළාත",
            provinceTa = "மத்திய மாகாணம்",
            latitude = 7.8742,
            longitude = 80.6511,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "sigiriya",
            nameEn = "Sigiriya",
            nameSi = "සීගිරිය",
            nameTa = "சிகிரியா",
            districtEn = "Matale",
            provinceEn = "Central Province",
            provinceSi = "මධ්‍යම පළාත",
            provinceTa = "மத்திய மாகாணம்",
            latitude = 7.9570,
            longitude = 80.7603,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "hatton",
            nameEn = "Hatton",
            nameSi = "හැටන්",
            nameTa = "ஹட்டன்",
            districtEn = "Nuwara Eliya",
            provinceEn = "Central Province",
            provinceSi = "මධ්‍යම පළාත",
            provinceTa = "மத்திய மாகாணம்",
            latitude = 6.8916,
            longitude = 80.5955,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.CENTRAL_HIGHLANDS
        ),
        SriLankaLocation(
            id = "bandarawela",
            nameEn = "Bandarawela",
            nameSi = "බණ්ඩාරවෙල",
            nameTa = "பண்டாரவளை",
            districtEn = "Badulla",
            provinceEn = "Uva Province",
            provinceSi = "ඌව පළාත",
            provinceTa = "ஊவா மாகாணம்",
            latitude = 6.8259,
            longitude = 80.9982,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.INTERMEDIATE
        ),
        SriLankaLocation(
            id = "chilaw",
            nameEn = "Chilaw",
            nameSi = "හලාවත",
            nameTa = "சிலாபம்",
            districtEn = "Puttalam",
            provinceEn = "North Western Province",
            provinceSi = "වයඹ පළාත",
            provinceTa = "வட மேல் மாகாணம்",
            latitude = 7.5758,
            longitude = 79.7953,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.INTERMEDIATE
        ),
        SriLankaLocation(
            id = "kalmunai",
            nameEn = "Kalmunai",
            nameSi = "කල්මුණේ",
            nameTa = "கல்முனை",
            districtEn = "Ampara",
            provinceEn = "Eastern Province",
            provinceSi = "නැගෙනහිර පළාත",
            provinceTa = "கிழக்கு மாகாணம்",
            latitude = 7.4167,
            longitude = 81.8167,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "tangalle",
            nameEn = "Tangalle",
            nameSi = "තංගල්ල",
            nameTa = "தங்கல்லை",
            districtEn = "Hambantota",
            provinceEn = "Southern Province",
            provinceSi = "දකුණු පළාත",
            provinceTa = "தென் மாகாணம்",
            latitude = 6.0243,
            longitude = 80.7941,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.INTERMEDIATE
        ),
        SriLankaLocation(
            id = "arugam_bay",
            nameEn = "Arugam Bay",
            nameSi = "ආරුගම්බේ",
            nameTa = "அறுகம் குடா",
            districtEn = "Ampara",
            provinceEn = "Eastern Province",
            provinceSi = "නැගෙනහිර පළාත",
            provinceTa = "கிழக்கு மாகாணம்",
            latitude = 6.8407,
            longitude = 81.8368,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "kataragama",
            nameEn = "Kataragama",
            nameSi = "කතරගම",
            nameTa = "கதிர்காமம்",
            districtEn = "Monaragala",
            provinceEn = "Uva Province",
            provinceSi = "ඌව පළාත",
            provinceTa = "ஊவா மாகாணம்",
            latitude = 6.4134,
            longitude = 81.3346,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.NORTH_EAST_DRY
        ),
        SriLankaLocation(
            id = "panadura",
            nameEn = "Panadura",
            nameSi = "පානදුර",
            nameTa = "பாணந்துறை",
            districtEn = "Kalutara",
            provinceEn = "Western Province",
            provinceSi = "බස්නාහිර පළාත",
            provinceTa = "மேல் மாகாணம்",
            latitude = 6.7132,
            longitude = 79.9026,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        ),
        SriLankaLocation(
            id = "moratuwa",
            nameEn = "Moratuwa",
            nameSi = "මොරටුව",
            nameTa = "மொரட்டுவை",
            districtEn = "Colombo",
            provinceEn = "Western Province",
            provinceSi = "බස්නාහිර පළාත",
            provinceTa = "மேல் மாகாணம்",
            latitude = 6.7730,
            longitude = 79.8816,
            isDistrictCapital = false,
            monsoonZone = MonsoonZone.SOUTHWEST_WET
        )
    )

    val allPresetLocations: List<SriLankaLocation> = allDistricts + majorTowns

    fun findById(id: String): SriLankaLocation? =
        allPresetLocations.firstOrNull { it.id == id }

    fun findNearest(latitude: Double, longitude: Double): SriLankaLocation {
        return allDistricts.minByOrNull { loc ->
            val dLat = loc.latitude - latitude
            val dLon = loc.longitude - longitude
            dLat * dLat + dLon * dLon
        } ?: allDistricts.first()
    }
}
