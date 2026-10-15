package com.example.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.VideoItem
import com.example.data.network.ApiClient
import com.example.ui.theme.YouTubeDarkBackground
import com.example.ui.theme.YouTubeDarkBorder
import com.example.ui.theme.YouTubeDarkSurface
import com.example.ui.theme.YouTubeDarkSurfaceVariant
import com.example.ui.theme.YouTubeRed
import com.example.ui.theme.YouTubeTextPrimary
import com.example.ui.theme.YouTubeTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun VideoCard(
    video: VideoItem,
    onClick: () -> Unit,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 16.dp)
            .testTag("video_card_${video.fileId}")
    ) {
        // Thumbnail Container (16:9)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color(0xFF1E1E1E))
        ) {
            val fullThumbnailUrl = ApiClient.getFullMediaUrl(video.thumbnailUrl)

            if (!fullThumbnailUrl.isNullOrBlank()) {
                AsyncImage(
                    model = fullThumbnailUrl,
                    contentDescription = video.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Stylish YouTube video backdrop gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF262626),
                                    Color(0xFF141414),
                                    Color(0xFF2B1010)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play video",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Kind Tag (e.g. REEL, POST) if special
            if (!video.kind.isNullOrBlank() && video.kind != "video") {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart)
                        .clip(RoundedCornerShape(4.dp))
                        .background(YouTubeRed.copy(alpha = 0.9f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.kind.uppercase(Locale.getDefault()),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Duration pill at bottom-right
            val durationText = formatDuration(video.durationMs)
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.85f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = durationText,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Metadata Row (Channel avatar + Title/Stats + Overflow menu)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Avatar
            val channelLogo = ApiClient.getFullMediaUrl(video.channelLogoUrl)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(YouTubeDarkSurfaceVariant)
                    .clickable { onChannelClick(video.owner) },
                contentAlignment = Alignment.Center
            ) {
                if (!channelLogo.isNullOrBlank()) {
                    AsyncImage(
                        model = channelLogo,
                        contentDescription = video.channelName ?: video.owner,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val initial = (video.channelName ?: video.owner).take(1).uppercase(Locale.getDefault())
                    Text(
                        text = initial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title & Channel info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = video.title.ifBlank { "Untitled Video" },
                    color = YouTubeTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                val channelDisplay = video.channelName ?: video.owner
                val viewsDisplay = formatViews(video.views)
                val timeDisplay = formatRelativeTime(video.publishedAt)
                Text(
                    text = "$channelDisplay • $viewsDisplay • $timeDisplay",
                    color = YouTubeTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3-dots Menu
            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More actions",
                        tint = YouTubeTextSecondary
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(YouTubeDarkSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Visit Channel", color = YouTubeTextPrimary) },
                        onClick = {
                            showMenu = false
                            onChannelClick(video.owner)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Watch Now", color = YouTubeTextPrimary) },
                        onClick = {
                            showMenu = false
                            onClick()
                        }
                    )
                }
            }
        }
    }
}

fun formatViews(views: Int): String {
    return when {
        views >= 1_000_000 -> String.format(Locale.US, "%.1fM views", views / 1_000_000.0)
        views >= 1_000 -> String.format(Locale.US, "%.1fK views", views / 1_000.0)
        views == 1 -> "1 view"
        else -> "$views views"
    }
}

fun formatDuration(durationMs: Long?): String {
    if (durationMs == null || durationMs <= 0L) return "0:00"
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}

fun formatRelativeTime(isoString: String?): String {
    if (isoString.isNullOrBlank()) return "recently"
    try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        val date = sdf.parse(isoString) ?: return "recently"
        val diffMs = System.currentTimeMillis() - date.time
        val diffSec = diffMs / 1000
        val diffMin = diffSec / 60
        val diffHours = diffMin / 60
        val diffDays = diffHours / 24

        return when {
            diffDays > 30 -> "${diffDays / 30} months ago"
            diffDays > 0 -> if (diffDays == 1L) "1 day ago" else "$diffDays days ago"
            diffHours > 0 -> if (diffHours == 1L) "1 hour ago" else "$diffHours hours ago"
            diffMin > 0 -> "$diffMin min ago"
            else -> "Just now"
        }
    } catch (_: Exception) {
        return "recently"
    }
}
