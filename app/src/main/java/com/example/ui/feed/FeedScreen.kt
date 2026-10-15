package com.example.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.components.YouTubeTopBar
import com.example.ui.theme.YouTubeChipBackground
import com.example.ui.theme.YouTubeChipSelected
import com.example.ui.theme.YouTubeChipTextSelected
import com.example.ui.theme.YouTubeChipTextUnselected
import com.example.ui.theme.YouTubeDarkBackground
import com.example.ui.theme.YouTubeDarkSurfaceVariant
import com.example.ui.theme.YouTubeRed
import com.example.ui.theme.YouTubeTextPrimary
import com.example.ui.theme.YouTubeTextSecondary

@Composable
fun FeedScreen(
    videos: List<VideoItem>,
    isLoading: Boolean,
    errorMessage: String?,
    currentUsername: String,
    onRefresh: () -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onChannelClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Videos", "Reels", "Posts")

    // Filter videos by category and search query
    val filteredVideos = videos.filter { video ->
        val matchesCategory = when (selectedCategory) {
            "Videos" -> video.kind == "video"
            "Reels" -> video.kind == "reel"
            "Posts" -> video.kind == "post"
            else -> true
        }

        val matchesSearch = if (searchQuery.isBlank()) {
            true
        } else {
            val q = searchQuery.trim().lowercase()
            video.title.lowercase().contains(q) ||
            video.owner.lowercase().contains(q) ||
            (video.channelName?.lowercase()?.contains(q) == true) ||
            (video.description?.lowercase()?.contains(q) == true)
        }

        matchesCategory && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YouTubeDarkBackground)
    ) {
        // Top Bar
        YouTubeTopBar(
            currentUsername = currentUsername,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onLogout = onLogout
        )

        // Filter Chips Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) YouTubeChipSelected else YouTubeChipBackground)
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("filter_chip_$category"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) YouTubeChipTextSelected else YouTubeChipTextUnselected,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (isLoading && videos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = YouTubeRed,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
            } else if (!errorMessage.isNullOrBlank() && videos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Could not load feed",
                        color = YouTubeTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        color = YouTubeTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry")
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text("Retry")
                    }
                }
            } else if (filteredVideos.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = "No videos",
                        tint = YouTubeTextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No videos match '$searchQuery'" else "No videos found in this category",
                        color = YouTubeTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (searchQuery.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { searchQuery = "" },
                            colors = ButtonDefaults.buttonColors(containerColor = YouTubeDarkSurfaceVariant)
                        ) {
                            Text("Clear Search")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("feed_lazy_column")
                ) {
                    items(filteredVideos, key = { it.fileId }) { video ->
                        VideoCard(
                            video = video,
                            onClick = { onVideoClick(video) },
                            onChannelClick = onChannelClick
                        )
                    }
                }
            }
        }
    }
}
