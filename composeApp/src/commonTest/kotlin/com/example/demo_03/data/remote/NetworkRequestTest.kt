package com.example.demo_03.data.remote

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NetworkRequestTest {
    @Test
    fun `safeApiCall maps business failure`() = runTest {
        val result = safeApiCall(
            request = { "payload" },
            validate = { BusinessFailure(message = "bad business") },
        ).awaitResult()

        val error = assertIs<NetworkResult.Error>(result)
        assertIs<NetworkError.Business>(error.cause)
        assertEquals("bad business", error.cause.message)
    }

    @Test
    fun `safeApiCall maps timeout failure`() = runTest {
        val result = safeApiCall<String>(
            request = { throw IllegalStateException("timed out") },
        ).awaitResult()

        val error = assertIs<NetworkResult.Error>(result)
        assertIs<NetworkError.Timeout>(error.cause)
    }

    @Test
    fun `safeApiCall maps network failure by message`() = runTest {
        val result = safeApiCall<String>(
            request = { throw IllegalStateException("connect reset by peer") },
        ).awaitResult()

        val error = assertIs<NetworkResult.Error>(result)
        assertIs<NetworkError.Network>(error.cause)
    }

    @Test
    fun `safeApiCall maps unknown failure`() = runTest {
        val result = safeApiCall<String>(
            request = { throw IllegalStateException("boom") },
        ).awaitResult()

        val error = assertIs<NetworkResult.Error>(result)
        assertIs<NetworkError.Unknown>(error.cause)
        assertEquals("boom", error.cause.message)
    }
}
