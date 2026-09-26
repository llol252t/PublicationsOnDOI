package org.example.project

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey

@Serializable
data object ArticleListRoute : Route

@Serializable
data class ArticleDetailRoute(val doi: String) : Route