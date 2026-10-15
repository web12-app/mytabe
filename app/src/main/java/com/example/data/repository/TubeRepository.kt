package com.example.data.repository

import com.example.data.model.ChannelData
import com.example.data.model.CommentItem
import com.example.data.model.CommentRequest
import com.example.data.model.CommentVoteData
import com.example.data.model.SubscribeData
import com.example.data.model.VideoItem
import com.example.data.model.ViewData
import com.example.data.model.VoteData
import com.example.data.model.VoteRequest
import com.example.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class TubeRepository {
    private val api = ApiClient.apiService
    private val emptyJsonBody = "{}".toRequestBody("application/json".toMediaTypeOrNull())

    suspend fun getFeed(): Result<List<VideoItem>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFeed()
            if (response.isSuccessful && response.body()?.success == true) {
                val videos = response.body()?.data?.videos ?: emptyList()
                Result.success(videos)
            } else {
                val err = response.body()?.error?.message ?: "Failed to load feed (${response.code()})"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChannel(username: String): Result<ChannelData> = withContext(Dispatchers.IO) {
        try {
            val response = api.getChannel(username)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return@withContext Result.failure(Exception("Channel not found"))
                Result.success(data)
            } else {
                val err = response.body()?.error?.message ?: "Failed to load channel"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComments(fileId: String): Result<List<CommentItem>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getComments(fileId)
            if (response.isSuccessful && response.body()?.success == true) {
                val comments = response.body()?.data?.comments ?: emptyList()
                Result.success(comments)
            } else {
                val err = response.body()?.error?.message ?: "Failed to load comments"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(fileId: String, text: String): Result<CommentItem> = withContext(Dispatchers.IO) {
        try {
            val response = api.addComment(fileId, CommentRequest(text.trim()))
            if (response.isSuccessful && response.body()?.success == true) {
                val comment = response.body()?.data?.comment ?: return@withContext Result.failure(Exception("Comment missing"))
                Result.success(comment)
            } else {
                val err = response.body()?.error?.message ?: "Failed to post comment"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun voteVideo(fileId: String, vote: String): Result<VoteData> = withContext(Dispatchers.IO) {
        try {
            val response = api.voteVideo(fileId, VoteRequest(vote))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return@withContext Result.failure(Exception("Vote data missing"))
                Result.success(data)
            } else {
                val err = response.body()?.error?.message ?: "Failed to vote"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun voteComment(fileId: String, cid: String, vote: String): Result<CommentVoteData> = withContext(Dispatchers.IO) {
        try {
            val response = api.voteComment(fileId, cid, VoteRequest(vote))
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return@withContext Result.failure(Exception("Vote data missing"))
                Result.success(data)
            } else {
                val err = response.body()?.error?.message ?: "Failed to vote on comment"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun subscribe(username: String): Result<SubscribeData> = withContext(Dispatchers.IO) {
        try {
            val response = api.subscribeChannel(username, emptyJsonBody)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: return@withContext Result.failure(Exception("Subscribe failed"))
                Result.success(data)
            } else {
                val err = response.body()?.error?.message ?: "Failed to update subscription"
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordView(fileId: String): Result<ViewData> = withContext(Dispatchers.IO) {
        try {
            val response = api.recordView(fileId, emptyJsonBody)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: ViewData()
                Result.success(data)
            } else {
                Result.failure(Exception("Failed to record view"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
