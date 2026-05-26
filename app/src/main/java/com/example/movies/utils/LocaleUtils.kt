//package com.example.movies.utils
//
//import android.content.Context
//import java.util.Locale
//
//fun Context.updateLocale(languageCode: String): Context {
//    val locale = Locale(languageCode)
//    Locale.setDefault(locale)
//
//    val config = resources.configuration
//    config.setLocale(locale)
//
//    return createConfigurationContext(config)
//}