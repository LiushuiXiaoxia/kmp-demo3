package com.example.demo_03.storage

import com.example.demo_03.PlatformContext
import java.io.File
import java.util.Properties

actual fun createKeyValueStore(
    platformContext: PlatformContext,
    name: String,
): KeyValueStore {
    val directory = File(System.getProperty("user.home"), ".demo03")
    val file = File(directory, "$name.properties")

    fun loadProperties(): Properties {
        val properties = Properties()
        if (file.exists()) {
            file.inputStream().use(properties::load)
        }
        return properties
    }

    fun saveProperties(properties: Properties) {
        directory.mkdirs()
        file.outputStream().use { output ->
            properties.store(output, "demo03 store")
        }
    }

    return object : KeyValueStore {
        override fun getString(key: String): String? {
            return loadProperties().getProperty(key)
        }

        override fun putString(key: String, value: String) {
            val properties = loadProperties()
            properties.setProperty(key, value)
            saveProperties(properties)
        }

        override fun remove(key: String) {
            val properties = loadProperties()
            properties.remove(key)
            saveProperties(properties)
        }
    }
}
