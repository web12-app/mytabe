package com.example.data.network

import com.example.data.model.ApiResponse
import com.example.data.model.AuthData
import com.example.data.model.ChannelData
import com.example.data.model.CommentRequest
import com.example.data.model.CommentVoteData
import com.example.data.model.CommentsData
import com.example.data.model.FeedData
import com.example.data.model.LoginRequest
import com.example.data.model.SingleCommentData
import com.example.data.model.SubscribeData
import com.example.data.model.ViewData
import com.example.data.model.VoteData
import com.example.data.model.VoteRequest
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TubeApiService {

    @POST("api/signup")
    suspend fun signup(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthData>>

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<AuthData>>

    @POST("api/logout")
    suspend fun logout(): Response<ApiResponse<Map<String, Any>>>

    @GET("api/tube/feed")
    suspend fun getFeed(): Response<ApiResponse<FeedData>>

    @GET("api/tube/channels/{user}")
    suspend fun getChannel(
        @Path("user") user: String
    ): Response<ApiResponse<ChannelData>>

    @GET("api/tube/videos/{fileId}/comments")
    suspend fun getComments(
        @Path("fileId") fileId: String
    ): Response<ApiResponse<CommentsData>>

    @POST("api/tube/videos/{fileId}/comments")
    suspend fun addComment(
        @Path("fileId") fileId: String,
        @Body request: CommentRequest
    ): Response<ApiResponse<SingleCommentData>>

    @POST("api/tube/videos/{fileId}/vote")
    suspend fun voteVideo(
        @Path("fileId") fileId: String,
        @Body request: VoteRequest
    ): Response<ApiResponse<VoteData>>

    @POST("api/tube/videos/{fileId}/comments/{cid}/vote")
    suspend fun voteComment(
        @Path("fileId") fileId: String,
        @Path("cid") cid: String,
        @Body request: VoteRequest
    ): Response<ApiResponse<CommentVoteData>>

    @POST("api/tube/channels/{user}/subscribe")
    suspend fun subscribeChannel(
        @Path("user") user: String,
        @Body emptyBody: RequestBody
    ): Response<ApiResponse<SubscribeData>>

    @POST("api/tube/videos/{fileId}/view")
    suspend fun recordView(
        @Path("fileId") fileId: String,
        @Body emptyBody: RequestBody
    ): Response<ApiResponse<ViewData>>
}
