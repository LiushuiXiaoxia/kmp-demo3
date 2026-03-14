package com.example.demo_03.data.remote

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T,
    ) : NetworkResult<T>

    data class Error(
        val cause: NetworkError,
    ) : NetworkResult<Nothing>
}

inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> this
    }
}

sealed interface NetworkError {
    val message: String

    data class Timeout(
        override val message: String = "请求超时，请稍后重试",
    ) : NetworkError

    data class Server(
        val code: Int,
        override val message: String = "服务器开小差了，请稍后再试",
    ) : NetworkError

    data class Business(
        val code: String? = null,
        override val message: String,
    ) : NetworkError

    data class Network(
        override val message: String = "网络连接异常，请检查网络后重试",
    ) : NetworkError

    data class Unknown(
        override val message: String = "请求失败，请稍后重试",
    ) : NetworkError
}

data class BusinessFailure(
    val message: String,
    val code: String? = null,
)

suspend inline fun <T> safeApiCall(
    crossinline request: suspend () -> T,
    crossinline validate: (T) -> BusinessFailure? = { null },
): NetworkResult<T> {
    return try {
        val data = request()
        val businessFailure = validate(data)
        if (businessFailure != null) {
            NetworkResult.Error(
                NetworkError.Business(
                    code = businessFailure.code,
                    message = businessFailure.message,
                ),
            )
        } else {
            NetworkResult.Success(data)
        }
    } catch (throwable: Throwable) {
        NetworkResult.Error(throwable.toNetworkError())
    }
}

fun Throwable.toNetworkError(): NetworkError {
    return when (this) {
        is HttpRequestTimeoutException, is TimeoutCancellationException -> NetworkError.Timeout()
        is ServerResponseException -> NetworkError.Server(
            code = response.status.value,
            message = response.status.defaultMessage(),
        )
        is ClientRequestException -> NetworkError.Business(
            code = response.status.value.toString(),
            message = response.status.defaultMessage(),
        )
        is RedirectResponseException -> NetworkError.Network("请求被重定向，请稍后重试")
        is ResponseException -> NetworkError.Unknown(
            message = response.status.defaultMessage(),
        )
        else -> {
            val message = message.orEmpty()
            if (
                message.contains("timeout", ignoreCase = true) ||
                message.contains("timed out", ignoreCase = true)
            ) {
                NetworkError.Timeout()
            } else if (
                message.contains("network", ignoreCase = true) ||
                message.contains("connect", ignoreCase = true) ||
                message.contains("unreachable", ignoreCase = true)
            ) {
                NetworkError.Network()
            } else {
                NetworkError.Unknown(
                    message = message.ifBlank { "请求失败，请稍后重试" },
                )
            }
        }
    }
}

private fun HttpStatusCode.defaultMessage(): String {
    return when (value) {
        in 500..599 -> "服务器异常($value)，请稍后再试"
        HttpStatusCode.Unauthorized.value -> "登录状态已失效，请重新登录"
        HttpStatusCode.Forbidden.value -> "没有权限执行该操作"
        HttpStatusCode.NotFound.value -> "请求的资源不存在"
        else -> "请求失败($value)"
    }
}
