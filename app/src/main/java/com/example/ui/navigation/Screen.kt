package com.example.ui.navigation

import com.example.data.model.VideoItem

sealed interface Screen {
    data object Auth : Screen
    data object Feed : Screen
    data class Watch(val video: VideoItem) : Screen
    data class Channel(val username: String) : Screen
    data object Library : Screen
}
