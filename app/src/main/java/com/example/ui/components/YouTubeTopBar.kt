package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YouTubeDarkBackground
import com.example.ui.theme.YouTubeDarkBorder
import com.example.ui.theme.YouTubeDarkSurface
import com.example.ui.theme.YouTubeDarkSurfaceVariant
import com.example.ui.theme.YouTubeRed
import com.example.ui.theme.YouTubeTextPrimary
import com.example.ui.theme.YouTubeTextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouTubeTopBar(
    currentUsername: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(YouTubeDarkBackground)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        if (isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        isSearchActive = false
                        onSearchQueryChange("")
                    },
                    modifier = Modifier.testTag("btn_close_search")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close search",
                        tint = YouTubeTextPrimary
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search YouTube...", color = YouTubeTextSecondary, fontSize = 14.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = YouTubeTextPrimary,
                        unfocusedTextColor = YouTubeTextPrimary,
                        focusedBorderColor = YouTubeDarkBorder,
                        unfocusedBorderColor = YouTubeDarkBorder,
                        cursorColor = YouTubeRed,
                        focusedContainerColor = YouTubeDarkSurfaceVariant,
                        unfocusedContainerColor = YouTubeDarkSurfaceVariant
                    ),
                    shape = RoundedCornerShape(24.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { /* query is already live filtered */ }),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_search_query")
                )

                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = YouTubeTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // YouTube Logo + Text
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("header_youtube_logo")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp, 26.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(YouTubeRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Logo",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "YouTube",
                        color = YouTubeTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                }

                // Actions: Search, Notifications, Avatar
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isSearchActive = true },
                        modifier = Modifier.testTag("btn_open_search")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = YouTubeTextPrimary
                        )
                    }

                    IconButton(
                        onClick = { /* notification action */ },
                        modifier = Modifier.testTag("btn_notifications")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = YouTubeTextPrimary
                        )
                    }

                    // User Profile Avatar
                    Box {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(YouTubeDarkSurfaceVariant)
                                .clickable { showUserMenu = true }
                                .testTag("btn_user_profile"),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = currentUsername.take(1).uppercase(Locale.getDefault()).ifEmpty { "U" }
                            Text(
                                text = initial,
                                color = YouTubeTextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false },
                            modifier = Modifier.background(YouTubeDarkSurface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Signed in as @$currentUsername",
                                        fontWeight = FontWeight.Bold,
                                        color = YouTubeTextPrimary
                                    )
                                },
                                onClick = { showUserMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Sign Out", color = YouTubeRed) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Logout,
                                        contentDescription = "Sign out",
                                        tint = YouTubeRed
                                    )
                                },
                                onClick = {
                                    showUserMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
