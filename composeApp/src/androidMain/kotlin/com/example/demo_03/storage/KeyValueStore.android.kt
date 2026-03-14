package com.example.demo_03.storage

import android.content.Context.MODE_PRIVATE
import com.example.demo_03.PlatformContext

actual fun createKeyValueStore(
    platformContext: PlatformContext,
    name: String,
): KeyValueStore {
    val sharedPreferences = platformContext.androidContext.getSharedPreferences(name, MODE_PRIVATE)

    return object : KeyValueStore {
        override fun getString(key: String): String? = sharedPreferences.getString(key, null)

        override fun putString(key: String, value: String) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        override fun remove(key: String) {
            sharedPreferences.edit().remove(key).apply()
        }
    }
}
