package com.example.demo_03.storage

import com.example.demo_03.PlatformContext

interface KeyValueStore {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun remove(key: String)
}

expect fun createKeyValueStore(
    platformContext: PlatformContext,
    name: String,
): KeyValueStore
