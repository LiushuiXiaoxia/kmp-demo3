package com.example.demo_03.session

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SessionSnapshot(
    val isLoggedIn: Boolean,
    val userName: String,
)

interface SessionStorage {
    fun restore(): SessionSnapshot?
    fun save(snapshot: SessionSnapshot)
    fun clear()
}

class DefaultSessionStorage(
    private val keyValueStore: com.example.demo_03.storage.KeyValueStore,
    private val json: Json,
) : SessionStorage {
    override fun restore(): SessionSnapshot? {
        return keyValueStore.getString(SessionKey)?.let { raw ->
            runCatching { json.decodeFromString<SessionSnapshot>(raw) }.getOrNull()
        }
    }

    override fun save(snapshot: SessionSnapshot) {
        keyValueStore.putString(
            key = SessionKey,
            value = json.encodeToString(snapshot),
        )
    }

    override fun clear() {
        keyValueStore.remove(SessionKey)
    }

    private companion object {
        const val SessionKey = "session_snapshot"
    }
}
