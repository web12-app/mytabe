package com.example.ui.channel

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChannelData
import com.example.data.model.VideoItem
import com.example.data.network.ApiClient
import com.example.ui.feed.VideoCard
import com.example.ui.theme.YouTubeDarkBackground
import com.example.ui.theme.YouTubeDarkBorder
import com.example.ui.theme.YouTubeDarkSurface
import com.example.ui.theme.YouTubeDarkSurfaceVariant
import com.example.ui.theme.YouTubeRed
import com.example.ui.theme.YouTubeTextPrimary
import com.example.ui.theme.YouTubeTextSecondary
import java.util.Locale

@Composable
fun ChannelScreen(
    channelData: ChannelData?,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onVideoClick: (VideoItem) -> Unit,
    onSubscribe: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YouTubeDarkBackground)
    ) {
        // Channel Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(YouTubeDarkBackground)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_channel_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = channelData?.channel?.name ?: "Channel",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YouTubeRed, strokeWidth = 3.dp)
            }
        } else if (!errorMessage.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = errorMessage, color = Color(0xFFFF8080), fontSize = 14.sp)
            }
        } else if (channelData != null) {
            val channel = channelData.channel
            val videos = channelData.videos

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // Header Banner
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF8B0000),
                                        Color(0xFF281020),
                                        Color(0xFF101525)
                                    )
                                )
                            )
                    )
                }

                // Channel Info Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Large Avatar
                        val logoUrl = ApiClient.getFullMediaUrl(channel.logoUrl)
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(YouTubeDarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!logoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = logoUrl,
                                    contentDescription = channel.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                val initial = channel.name.take(1).uppercase(Locale.getDefault())
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = channel.name,
                            color = YouTubeTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "@${channel.username} • ${channel.subscribers} subscribers • ${videos.size} videos",
                            color = YouTubeTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        if (!channel.description.isNullOrBlank()) {
                            Text(
                                text = channel.description,
                                color = YouTubeTextSecondary,
                                fontSize = 13.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Subscribe Button
                        Button(
                            onClick = { onSubscribe(channel.username) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (channel.subscribed) YouTubeDarkSurfaceVariant else YouTubeRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(42.dp)
                                .testTag("btn_channel_subscribe")
                        ) {
                            if (channel.subscribed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Subscribed",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Subscribed", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text(text = "Subscribe", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Divider(color = YouTubeDarkBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Section Title
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "Videos",
                                color = YouTubeTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Channel Videos
                if (videos.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "This channel hasn't uploaded any videos yet.",
                                color = YouTubeTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    items(videos, key = { it.fileId }) { video ->
                        VideoCard(
                            video = video,
                            onClick = { onVideoClick(video) },
                            onChannelClick = { /* already on channel */ }
                        )
                    }
                }
            }
        }
    }
}
