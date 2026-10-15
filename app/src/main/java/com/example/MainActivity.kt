package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.TubeViewModel
import com.example.ui.auth.AuthScreen
import com.example.ui.channel.ChannelScreen
import com.example.ui.components.NavTab
import com.example.ui.components.YouTubeBottomNav
import com.example.ui.feed.FeedScreen
import com.example.ui.navigation.Screen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.YouTubeDarkBackground
import com.example.ui.watch.WatchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: TubeViewModel = viewModel()
                YouTubeApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun YouTubeApp(viewModel: TubeViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val authState by viewModel.authUiState.collectAsStateWithLifecycle()
    val feedState by viewModel.feedUiState.collectAsStateWithLifecycle()
    val watchState by viewModel.watchUiState.collectAsStateWithLifecycle()
    val channelState by viewModel.channelUiState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(NavTab.HOME) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(YouTubeDarkBackground),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (currentScreen is Screen.Feed) {
                YouTubeBottomNav(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        if (tab == NavTab.HOME) {
                            viewModel.loadFeed()
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(YouTubeDarkBackground)
        ) {
            when (val screen = currentScreen) {
                is Screen.Auth -> {
                    AuthScreen(
                        isLoading = authState.isLoading,
                        errorMessage = authState.errorMessage,
                        onLogin = { u, p -> viewModel.login(u, p) },
                        onSignup = { u, p -> viewModel.signup(u, p) }
                    )
                }

                is Screen.Feed -> {
                    // Filter based on bottom nav tab if applicable
                    val tabFilteredVideos = when (selectedTab) {
                        NavTab.SHORTS -> feedState.videos.filter { it.kind == "reel" }
                        NavTab.SUBSCRIPTIONS -> feedState.videos // can show subscribed creator uploads
                        else -> feedState.videos
                    }

                    FeedScreen(
                        videos = tabFilteredVideos,
                        isLoading = feedState.isLoading,
                        errorMessage = feedState.errorMessage,
                        currentUsername = authState.username,
                        onRefresh = { viewModel.loadFeed() },
                        onVideoClick = { video -> viewModel.playVideo(video) },
                        onChannelClick = { username -> viewModel.openChannel(username) },
                        onLogout = { viewModel.logout() }
                    )
                }

                is Screen.Watch -> {
                    val activeVideo = watchState.currentVideo ?: screen.video
                    WatchScreen(
                        video = activeVideo,
                        channel = watchState.channel,
                        comments = watchState.comments,
                        isCommentsLoading = watchState.isCommentsLoading,
                        isSubscribed = watchState.isSubscribed,
                        subscribersCount = watchState.subscribersCount,
                        currentVote = watchState.currentVote,
                        likesCount = watchState.likesCount,
                        onBack = { viewModel.navigateBack() },
                        onChannelClick = { username -> viewModel.openChannel(username) },
                        onVoteVideo = { vote -> viewModel.voteVideo(vote) },
                        onSubscribe = { username -> viewModel.subscribeChannel(username) },
                        onAddComment = { text -> viewModel.addComment(text) },
                        onVoteComment = { cid, vote -> viewModel.voteComment(cid, vote) },
                        onVideoStarted = { viewModel.recordVideoView(activeVideo.fileId) }
                    )
                }

                is Screen.Channel -> {
                    ChannelScreen(
                        channelData = channelState.channelData,
                        isLoading = channelState.isLoading,
                        errorMessage = channelState.errorMessage,
                        onBack = { viewModel.navigateBack() },
                        onVideoClick = { video -> viewModel.playVideo(video) },
                        onSubscribe = { username -> viewModel.subscribeChannel(username) }
                    )
                }

                else -> {}
            }
        }
    }
}
