package com.example.demo_03.data

import com.example.demo_03.data.remote.BusinessFailure
import com.example.demo_03.data.remote.NetworkResult
import com.example.demo_03.data.remote.PostApi
import com.example.demo_03.data.remote.mapSuccess
import com.example.demo_03.data.remote.safeApiCall
import kotlinx.coroutines.flow.Flow

class PostRepository(
    private val postApi: PostApi,
) {
    fun getFeaturedPostTitle(): Flow<NetworkResult<String>> {
        return safeApiCall(
            request = { postApi.getFeaturedPost() },
            validate = { post ->
                if (post.title.isBlank()) {
                    BusinessFailure(message = "返回内容为空")
                } else {
                    null
                }
            },
        ).mapSuccess { post -> post.title }
    }
}
