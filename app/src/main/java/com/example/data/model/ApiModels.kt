package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: T? = null,
    @Json(name = "error") val error: ApiError? = null
)

@JsonClass(generateAdapter = true)
data class ApiError(
    @Json(name = "code") val code: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthData(
    @Json(name = "user") val user: ApiUser,
    @Json(name = "api_token") val apiToken: ApiToken? = null
)

@JsonClass(generateAdapter = true)
data class ApiUser(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String
)

@JsonClass(generateAdapter = true)
data class ApiToken(
    @Json(name = "token") val token: String,
    @Json(name = "id") val id: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class FeedData(
    @Json(name = "videos") val videos: List<VideoItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class VideoItem(
    @Json(name = "fileId") val fileId: String,
    @Json(name = "owner") val owner: String,
    @Json(name = "channelName") val channelName: String? = null,
    @Json(name = "channelLogoUrl") val channelLogoUrl: String? = null,
    @Json(name = "title") val title: String = "",
    @Json(name = "description") val description: String? = null,
    @Json(name = "thumbnailUrl") val thumbnailUrl: String? = null,
    @Json(name = "kind") val kind: String? = "video",
    @Json(name = "durationMs") val durationMs: Long? = 0L,
    @Json(name = "mime") val mime: String? = null,
    @Json(name = "size") val size: Long? = 0L,
    @Json(name = "publishedAt") val publishedAt: String? = null,
    @Json(name = "views") val views: Int = 0,
    @Json(name = "likes") val likes: Int = 0,
    @Json(name = "dislikes") val dislikes: Int = 0,
    @Json(name = "myVote") val myVote: String? = null
)

@JsonClass(generateAdapter = true)
data class ChannelData(
    @Json(name = "channel") val channel: ChannelDetail,
    @Json(name = "videos") val videos: List<VideoItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ChannelDetail(
    @Json(name = "username") val username: String,
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String? = null,
    @Json(name = "logoUrl") val logoUrl: String? = null,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "subscribers") val subscribers: Int = 0,
    @Json(name = "subscribed") val subscribed: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CommentsData(
    @Json(name = "comments") val comments: List<CommentItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CommentItem(
    @Json(name = "id") val id: String,
    @Json(name = "user") val user: String,
    @Json(name = "text") val text: String,
    @Json(name = "at") val at: String? = null,
    @Json(name = "likes") val likes: Int = 0,
    @Json(name = "dislikes") val dislikes: Int = 0,
    @Json(name = "myVote") val myVote: String? = null
)

@JsonClass(generateAdapter = true)
data class SingleCommentData(
    @Json(name = "comment") val comment: CommentItem
)

@JsonClass(generateAdapter = true)
data class CommentRequest(
    @Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class VoteRequest(
    @Json(name = "vote") val vote: String // "like", "dislike", "none"
)

@JsonClass(generateAdapter = true)
data class VoteData(
    @Json(name = "likes") val likes: Int = 0,
    @Json(name = "dislikes") val dislikes: Int = 0,
    @Json(name = "myVote") val myVote: String? = null
)

@JsonClass(generateAdapter = true)
data class CommentVoteData(
    @Json(name = "likes") val likes: Int = 0,
    @Json(name = "dislikes") val dislikes: Int = 0
)

@JsonClass(generateAdapter = true)
data class SubscribeData(
    @Json(name = "ok") val ok: Boolean = true,
    @Json(name = "subscribed") val subscribed: Boolean = false,
    @Json(name = "subscribers") val subscribers: Int = 0
)

@JsonClass(generateAdapter = true)
data class ViewData(
    @Json(name = "views") val views: Int = 0
)
