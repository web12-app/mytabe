package com.example.ui.watch

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChannelDetail
import com.example.data.model.CommentItem
import com.example.data.model.VideoItem
import com.example.data.network.ApiClient
import com.example.ui.feed.formatRelativeTime
import com.example.ui.feed.formatViews
import com.example.ui.theme.YouTubeDarkBackground
import com.example.ui.theme.YouTubeDarkBorder
import com.example.ui.theme.YouTubeDarkElevated
import com.example.ui.theme.YouTubeDarkSurface
import com.example.ui.theme.YouTubeDarkSurfaceVariant
import com.example.ui.theme.YouTubeRed
import com.example.ui.theme.YouTubeTextPrimary
import com.example.ui.theme.YouTubeTextSecondary
import java.util.Locale

@Composable
fun WatchScreen(
    video: VideoItem,
    channel: ChannelDetail?,
    comments: List<CommentItem>,
    isCommentsLoading: Boolean,
    isSubscribed: Boolean,
    subscribersCount: Int,
    currentVote: String?, // "like", "dislike", null
    likesCount: Int,
    onBack: () -> Unit,
    onChannelClick: (String) -> Unit,
    onVoteVideo: (String) -> Unit,
    onSubscribe: (String) -> Unit,
    onAddComment: (String) -> Unit,
    onVoteComment: (String, String) -> Unit,
    onVideoStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isDescExpanded by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }

    BackHandler {
        onBack()
    }

    LaunchedEffect(video.fileId) {
        onVideoStarted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(YouTubeDarkBackground)
    ) {
        // Top Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_watch_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Feed",
                    tint = Color.White
                )
            }
            Text(
                text = video.title.ifBlank { "YouTube Video" },
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        // Native 16:9 Video Player View
        VideoPlayerView(
            fileId = video.fileId,
            onVideoStarted = onVideoStarted,
            modifier = Modifier.fillMaxWidth()
        )

        // Scrollable Video Details & Comments
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp)
        ) {
            // Video Title
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = video.title.ifBlank { "Untitled Video" },
                    color = YouTubeTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Stats & Expandable Description Box
                val viewsFormatted = formatViews(video.views)
                val timeFormatted = formatRelativeTime(video.publishedAt)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(YouTubeDarkElevated)
                        .clickable { isDescExpanded = !isDescExpanded }
                        .padding(10.dp)
                ) {
                    Text(
                        text = "$viewsFormatted  $timeFormatted",
                        color = YouTubeTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (!video.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = video.description,
                            color = YouTubeTextSecondary,
                            fontSize = 13.sp,
                            maxLines = if (isDescExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isDescExpanded) "Show less" else "...more",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Channel Bar & Subscribe
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Channel Info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onChannelClick(video.owner) }
                            .testTag("channel_row_clickable")
                    ) {
                        val channelLogo = ApiClient.getFullMediaUrl(channel?.logoUrl ?: video.channelLogoUrl)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(YouTubeDarkSurfaceVariant),
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
                                val letter = (video.channelName ?: video.owner).take(1).uppercase(Locale.getDefault())
                                Text(
                                    text = letter,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = video.channelName ?: channel?.name ?: video.owner,
                                color = YouTubeTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val subsText = if (subscribersCount == 1) "1 subscriber" else "$subscribersCount subscribers"
                            Text(
                                text = subsText,
                                color = YouTubeTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Subscribe Button
                    Button(
                        onClick = { onSubscribe(video.owner) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubscribed) YouTubeDarkSurfaceVariant else YouTubeRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("btn_subscribe")
                    ) {
                        if (isSubscribed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Subscribed",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Subscribed", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        } else {
                            Text(text = "Subscribe", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            // Actions Bar (Like / Dislike, Share)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Combined Like / Dislike pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(YouTubeDarkElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Like Button
                        Row(
                            modifier = Modifier
                                .clickable {
                                    val next = if (currentVote == "like") "none" else "like"
                                    onVoteVideo(next)
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("btn_like"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (currentVote == "like") Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                contentDescription = "Like",
                                tint = if (currentVote == "like") YouTubeRed else YouTubeTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (likesCount > 0) "$likesCount" else "Like",
                                color = if (currentVote == "like") YouTubeRed else YouTubeTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Divider
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(18.dp)
                                .background(YouTubeDarkBorder)
                        )

                        // Dislike Button
                        IconButton(
                            onClick = {
                                val next = if (currentVote == "dislike") "none" else "dislike"
                                onVoteVideo(next)
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("btn_dislike")
                        ) {
                            Icon(
                                imageVector = if (currentVote == "dislike") Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                contentDescription = "Dislike",
                                tint = if (currentVote == "dislike") YouTubeRed else YouTubeTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Share Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(YouTubeDarkElevated)
                            .clickable {
                                val shareIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "${video.title} - Watch on YouTube: ${ApiClient.BASE_URL}tube/watch/${video.fileId}")
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share video via"))
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("btn_share"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = YouTubeTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share",
                            color = YouTubeTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = YouTubeDarkBorder, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Comments Header & Input
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Comments (${comments.size})",
                        color = YouTubeTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Post New Comment Input Field
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentInput,
                        onValueChange = { commentInput = it },
                        placeholder = { Text("Add a comment...", color = YouTubeTextSecondary, fontSize = 13.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = YouTubeTextPrimary,
                            unfocusedTextColor = YouTubeTextPrimary,
                            focusedBorderColor = YouTubeRed,
                            unfocusedBorderColor = YouTubeDarkBorder,
                            cursorColor = YouTubeRed,
                            focusedContainerColor = YouTubeDarkSurfaceVariant,
                            unfocusedContainerColor = YouTubeDarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (commentInput.isNotBlank()) {
                                    onAddComment(commentInput)
                                    commentInput = ""
                                }
                            }
                        ),
                        trailingIcon = {
                            if (commentInput.isNotBlank()) {
                                IconButton(
                                    onClick = {
                                        onAddComment(commentInput)
                                        commentInput = ""
                                    },
                                    modifier = Modifier.testTag("btn_submit_comment")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Submit comment",
                                        tint = YouTubeRed
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_comment")
                    )
                }
            }

            // Comments Loading State
            if (isCommentsLoading && comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = YouTubeRed,
                            modifier = Modifier.size(28.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            } else if (comments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No comments yet. Be the first to comment!",
                            color = YouTubeTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(comments, key = { it.id }) { comment ->
                    CommentRow(
                        comment = comment,
                        onVote = { vote -> onVoteComment(comment.id, vote) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CommentRow(
    comment: CommentItem,
    onVote: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comment_${comment.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Commenter Avatar
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(YouTubeDarkSurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            val letter = comment.user.take(1).uppercase(Locale.getDefault())
            Text(
                text = letter,
                color = YouTubeTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "@${comment.user}",
                    color = YouTubeTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatRelativeTime(comment.at),
                    color = YouTubeTextSecondary.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = comment.text,
                color = YouTubeTextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Comment Like Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    val nextVote = if (comment.myVote == "like") "none" else "like"
                    onVote(nextVote)
                }
            ) {
                Icon(
                    imageVector = if (comment.myVote == "like") Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Like comment",
                    tint = if (comment.myVote == "like") YouTubeRed else YouTubeTextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                if (comment.likes > 0) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${comment.likes}",
                        color = YouTubeTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
