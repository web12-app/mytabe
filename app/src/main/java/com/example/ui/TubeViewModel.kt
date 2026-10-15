package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChannelData
import com.example.data.model.ChannelDetail
import com.example.data.model.CommentItem
import com.example.data.model.VideoItem
import com.example.data.repository.AuthRepository
import com.example.data.repository.TubeRepository
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val username: String = ""
)

data class FeedUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class WatchUiState(
    val currentVideo: VideoItem? = null,
    val channel: ChannelDetail? = null,
    val comments: List<CommentItem> = emptyList(),
    val isCommentsLoading: Boolean = false,
    val isSubscribed: Boolean = false,
    val subscribersCount: Int = 0,
    val currentVote: String? = null, // "like", "dislike", "none", null
    val likesCount: Int = 0
)

data class ChannelUiState(
    val channelData: ChannelData? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class TubeViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepo = AuthRepository(application)
    private val tubeRepo = TubeRepository()

    private val _currentScreen = MutableStateFlow<Screen>(
        if (authRepo.isLoggedIn()) Screen.Feed else Screen.Auth
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _authUiState = MutableStateFlow(
        AuthUiState(username = authRepo.getUsername())
    )
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _feedUiState = MutableStateFlow(FeedUiState())
    val feedUiState: StateFlow<FeedUiState> = _feedUiState.asStateFlow()

    private val _watchUiState = MutableStateFlow(WatchUiState())
    val watchUiState: StateFlow<WatchUiState> = _watchUiState.asStateFlow()

    private val _channelUiState = MutableStateFlow(ChannelUiState())
    val channelUiState: StateFlow<ChannelUiState> = _channelUiState.asStateFlow()

    init {
        if (authRepo.isLoggedIn()) {
            loadFeed()
        }
    }

    fun loadFeed() {
        viewModelScope.launch {
            _feedUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = tubeRepo.getFeed()
            result.fold(
                onSuccess = { videos ->
                    _feedUiState.update { it.copy(videos = videos, isLoading = false) }
                },
                onFailure = { err ->
                    _feedUiState.update { it.copy(errorMessage = err.message, isLoading = false) }
                }
            )
        }
    }

    fun login(u: String, p: String) {
        viewModelScope.launch {
            _authUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepo.login(u, p)
            result.fold(
                onSuccess = { authData ->
                    _authUiState.update {
                        it.copy(isLoading = false, username = authData.user.username)
                    }
                    _currentScreen.value = Screen.Feed
                    loadFeed()
                },
                onFailure = { err ->
                    _authUiState.update {
                        it.copy(isLoading = false, errorMessage = err.message ?: "Login failed")
                    }
                }
            )
        }
    }

    fun signup(u: String, p: String) {
        viewModelScope.launch {
            _authUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepo.signup(u, p)
            result.fold(
                onSuccess = { authData ->
                    _authUiState.update {
                        it.copy(isLoading = false, username = authData.user.username)
                    }
                    _currentScreen.value = Screen.Feed
                    loadFeed()
                },
                onFailure = { err ->
                    _authUiState.update {
                        it.copy(isLoading = false, errorMessage = err.message ?: "Sign up failed")
                    }
                }
            )
        }
    }

    fun logout() {
        authRepo.logout()
        _authUiState.update { AuthUiState() }
        _currentScreen.value = Screen.Auth
    }

    fun playVideo(video: VideoItem) {
        _watchUiState.update {
            WatchUiState(
                currentVideo = video,
                currentVote = video.myVote,
                likesCount = video.likes
            )
        }
        _currentScreen.value = Screen.Watch(video)

        // Load comments, channel info and record view
        loadComments(video.fileId)
        loadWatchChannel(video.owner)
        recordVideoView(video.fileId)
    }

    fun openChannel(username: String) {
        _currentScreen.value = Screen.Channel(username)
        _channelUiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = tubeRepo.getChannel(username)
            result.fold(
                onSuccess = { channelData ->
                    _channelUiState.update {
                        it.copy(channelData = channelData, isLoading = false)
                    }
                },
                onFailure = { err ->
                    _channelUiState.update {
                        it.copy(errorMessage = err.message, isLoading = false)
                    }
                }
            )
        }
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            is Screen.Watch, is Screen.Channel -> {
                _currentScreen.value = Screen.Feed
            }
            else -> {}
        }
    }

    private fun loadComments(fileId: String) {
        viewModelScope.launch {
            _watchUiState.update { it.copy(isCommentsLoading = true) }
            val result = tubeRepo.getComments(fileId)
            result.fold(
                onSuccess = { comments ->
                    _watchUiState.update { it.copy(comments = comments, isCommentsLoading = false) }
                },
                onFailure = {
                    _watchUiState.update { it.copy(isCommentsLoading = false) }
                }
            )
        }
    }

    private fun loadWatchChannel(username: String) {
        viewModelScope.launch {
            val result = tubeRepo.getChannel(username)
            result.onSuccess { channelData ->
                _watchUiState.update {
                    it.copy(
                        channel = channelData.channel,
                        isSubscribed = channelData.channel.subscribed,
                        subscribersCount = channelData.channel.subscribers
                    )
                }
            }
        }
    }

    fun recordVideoView(fileId: String) {
        viewModelScope.launch {
            tubeRepo.recordView(fileId)
        }
    }

    fun voteVideo(vote: String) {
        val currentVideo = _watchUiState.value.currentVideo ?: return
        viewModelScope.launch {
            val result = tubeRepo.voteVideo(currentVideo.fileId, vote)
            result.onSuccess { voteData ->
                _watchUiState.update {
                    it.copy(
                        currentVote = voteData.myVote,
                        likesCount = voteData.likes
                    )
                }
            }
        }
    }

    fun addComment(text: String) {
        val currentVideo = _watchUiState.value.currentVideo ?: return
        viewModelScope.launch {
            val result = tubeRepo.addComment(currentVideo.fileId, text)
            result.onSuccess { newComment ->
                _watchUiState.update {
                    it.copy(comments = listOf(newComment) + it.comments)
                }
            }
        }
    }

    fun voteComment(commentId: String, vote: String) {
        val currentVideo = _watchUiState.value.currentVideo ?: return
        viewModelScope.launch {
            val result = tubeRepo.voteComment(currentVideo.fileId, commentId, vote)
            result.onSuccess { voteData ->
                _watchUiState.update { state ->
                    val updatedList = state.comments.map { c ->
                        if (c.id == commentId) {
                            c.copy(
                                likes = voteData.likes,
                                dislikes = voteData.dislikes,
                                myVote = if (vote == "none") null else vote
                            )
                        } else c
                    }
                    state.copy(comments = updatedList)
                }
            }
        }
    }

    fun subscribeChannel(username: String) {
        viewModelScope.launch {
            val result = tubeRepo.subscribe(username)
            result.onSuccess { subData ->
                _watchUiState.update {
                    it.copy(
                        isSubscribed = subData.subscribed,
                        subscribersCount = subData.subscribers
                    )
                }
                _channelUiState.update { state ->
                    state.channelData?.let { cd ->
                        state.copy(
                            channelData = cd.copy(
                                channel = cd.channel.copy(
                                    subscribed = subData.subscribed,
                                    subscribers = subData.subscribers
                                )
                            )
                        )
                    } ?: state
                }
            }
        }
    }
}
