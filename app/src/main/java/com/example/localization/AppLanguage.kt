package com.example.localization

enum class AppLanguage(val code: String, val nativeLabel: String, val englishLabel: String) {
    ENGLISH("en", "English", "English"),
    SINHALA("si", "සිංහල", "Sinhala"),
    TAMIL("ta", "தமிழ்", "Tamil");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}
