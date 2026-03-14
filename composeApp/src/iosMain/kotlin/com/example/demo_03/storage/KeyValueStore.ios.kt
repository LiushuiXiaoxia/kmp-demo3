package com.example.demo_03.storage

import com.example.demo_03.PlatformContext
import platform.Foundation.NSUserDefaults

actual fun createKeyValueStore(
    platformContext: PlatformContext,
    name: String,
): KeyValueStore {
    val userDefaults = NSUserDefaults(suiteName = name)

    return object : KeyValueStore {
        override fun getString(key: String): String? = userDefaults.stringForKey(key)

        override fun putString(key: String, value: String) {
            userDefaults.setObject(value, forKey = key)
        }

        override fun remove(key: String) {
            userDefaults.removeObjectForKey(key)
        }
    }
}
