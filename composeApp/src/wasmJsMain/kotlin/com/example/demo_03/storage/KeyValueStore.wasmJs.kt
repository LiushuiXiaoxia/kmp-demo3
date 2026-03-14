package com.example.demo_03.storage

import com.example.demo_03.PlatformContext
import kotlinx.browser.window

actual fun createKeyValueStore(
    platformContext: PlatformContext,
    name: String,
): KeyValueStore {
    val storage = window.localStorage

    return object : KeyValueStore {
        override fun getString(key: String): String? = storage.getItem(namespacedKey(name, key))

        override fun putString(key: String, value: String) {
            storage.setItem(namespacedKey(name, key), value)
        }

        override fun remove(key: String) {
            storage.removeItem(namespacedKey(name, key))
        }
    }
}

private fun namespacedKey(name: String, key: String): String = "$name::$key"
