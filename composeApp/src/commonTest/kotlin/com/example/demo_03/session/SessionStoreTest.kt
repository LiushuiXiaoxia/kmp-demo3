package com.example.demo_03.session

import com.example.demo_03.observability.NoOpAnalyticsTracker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SessionStoreTest {
    @Test
    fun `restores previous snapshot on init`() {
        val storage = FakeSessionStorage(
            snapshot = SessionSnapshot(
                isLoggedIn = true,
                userName = "demo",
            ),
        )

        val store = SessionStore(storage, NoOpAnalyticsTracker)

        assertTrue(store.session.value.isLoggedIn)
        assertEquals("demo", store.session.value.userName)
    }

    @Test
    fun `login persists snapshot`() {
        val storage = FakeSessionStorage()
        val store = SessionStore(storage, NoOpAnalyticsTracker)

        store.login("alice")

        assertTrue(store.session.value.isLoggedIn)
        assertEquals("alice", storage.snapshot?.userName)
    }

    @Test
    fun `logout clears storage and state`() {
        val storage = FakeSessionStorage(
            snapshot = SessionSnapshot(
                isLoggedIn = true,
                userName = "demo",
            ),
        )
        val store = SessionStore(storage, NoOpAnalyticsTracker)

        store.logout()

        assertFalse(store.session.value.isLoggedIn)
        assertEquals(null, storage.snapshot)
    }
}

private class FakeSessionStorage(
    var snapshot: SessionSnapshot? = null,
) : SessionStorage {
    override fun restore(): SessionSnapshot? = snapshot

    override fun save(snapshot: SessionSnapshot) {
        this.snapshot = snapshot
    }

    override fun clear() {
        snapshot = null
    }
}
