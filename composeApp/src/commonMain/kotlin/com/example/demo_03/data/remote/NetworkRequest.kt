package com.example.demo_03.data.remote

import com.example.demo_03.toast.ToastKit
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T,
    ) : NetworkResult<T>

    data class Error(
        val cause: NetworkError,
    ) : NetworkResult<Nothing>
}

val NetworkResult<*>.isSuccess: Boolean
    get() = this is NetworkResult.Success

val NetworkResult<*>.isFailure: Boolean
    get() = this is NetworkResult.Error

fun <T> NetworkResult<T>.getOrNull(): T? {
    return when (this) {
        is NetworkResult.Success -> data
        is NetworkResult.Error -> null
    }
}

fun <T> NetworkResult<T>.errorOrNull(): NetworkError? {
    return when (this) {
        is NetworkResult.Success -> null
        is NetworkResult.Error -> cause
    }
}

inline fun <T, R> NetworkResult<T>.fold(
    onSuccess: (T) -> R,
    onError: (NetworkError) -> R,
): R {
    return when (this) {
        is NetworkResult.Success -> onSuccess(data)
        is NetworkResult.Error -> onError(cause)
    }
}

inline fun <T, R> NetworkResult<T>.mapSuccess(transform: (T) -> R): NetworkResult<R> {
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

inline fun <T> safeApiCall(
    crossinline request: suspend () -> T,
    crossinline validate: (T) -> BusinessFailure? = { null },
): Flow<NetworkResult<T>> = flow {
    try {
        val data = request()
        val businessFailure = validate(data)
        emit(
            if (businessFailure != null) {
                NetworkResult.Error(
                    NetworkError.Business(
                        code = businessFailure.code,
                        message = businessFailure.message,
                    ),
                )
            } else {
                NetworkResult.Success(data)
            },
        )
    } catch (throwable: Throwable) {
        when (throwable) {
            is TimeoutCancellationException -> emit(NetworkResult.Error(NetworkError.Timeout()))
            is CancellationException -> throw throwable
            else -> emit(NetworkResult.Error(throwable.toNetworkError()))
        }
    }
}

inline fun <T, R> Flow<NetworkResult<T>>.mapSuccess(
    crossinline transform: suspend (T) -> R,
): Flow<NetworkResult<R>> {
    return transform { result ->
        when (result) {
            is NetworkResult.Success -> emit(NetworkResult.Success(transform(result.data)))
            is NetworkResult.Error -> emit(result)
        }
    }
}

inline fun <T> Flow<NetworkResult<T>>.onSuccess(
    crossinline action: suspend (T) -> Unit,
): Flow<NetworkResult<T>> {
    return onEach { result ->
        if (result is NetworkResult.Success) {
            action(result.data)
        }
    }
}

inline fun <T> Flow<NetworkResult<T>>.onError(
    crossinline action: suspend (NetworkError) -> Unit,
): Flow<NetworkResult<T>> {
    return onEach { result ->
        if (result is NetworkResult.Error) {
            action(result.cause)
        }
    }
}

fun <T> Flow<NetworkResult<T>>.onFailureToast(): Flow<NetworkResult<T>> {
    return onError { error ->
        ToastKit.show(error.message)
    }
}

suspend fun <T> Flow<NetworkResult<T>>.awaitResult(): NetworkResult<T> {
    return first()
}

suspend fun <T> Flow<NetworkResult<T>>.awaitSuccessOrNull(): T? {
    return awaitResult().getOrNull()
}

@Deprecated(
    message = "Use awaitSuccessOrNull() for clearer Flow semantics.",
    replaceWith = ReplaceWith("awaitSuccessOrNull()"),
)
suspend fun <T> Flow<NetworkResult<T>>.getSuccessOrNull(): T? {
    return awaitSuccessOrNull()
}

suspend fun <T, R> Flow<NetworkResult<T>>.foldResult(
    onSuccess: (T) -> R,
    onError: (NetworkError) -> R,
): R {
    return awaitResult().fold(
        onSuccess = onSuccess,
        onError = onError,
    )
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
