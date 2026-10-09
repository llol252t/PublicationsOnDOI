package org.example.project
import org.jetbrains.compose.resources.stringResource
import myapp.shared.generated.resources.Res
import myapp.shared.generated.resources.app_title
import myapp.shared.generated.resources.back_button
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.modules.polymorphic


@Composable
fun App() {
    val config = androidx.savedstate.serialization.SavedStateConfiguration {
        serializersModule = kotlinx.serialization.modules.SerializersModule {
            polymorphic(androidx.navigation3.runtime.NavKey::class) {
                subclass(ArticleListRoute::class, ArticleListRoute.serializer())
                subclass(ArticleDetailRoute::class, ArticleDetailRoute.serializer())
            }
        }
    }

    val backStack = rememberNavBackStack(config, ArticleListRoute)
    var isDarkTheme by remember { mutableStateOf(false) }
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                popTransitionSpec = {
                    slideInHorizontally(initialOffsetX = { -it }) togetherWith
                            slideOutHorizontally(targetOffsetX = { it })
                },
                entryProvider = entryProvider {
                    entry<ArticleListRoute> {
                        ArticleListScreen(
                            articles = mockArticles,
                            onArticleClick = { article ->
                                backStack.add(ArticleDetailRoute(article.doi))
                            },
                            onToggleTheme = { isDarkTheme = !isDarkTheme },
                            isDarkTheme = isDarkTheme
                        )
                    }
                    entry<ArticleDetailRoute> { route ->
                        val article = mockArticles.find { it.doi == route.doi }
                        if (article != null) {
                            ArticleDetailScreen(
                                article = article,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                    }
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    articles: List<Article>,
    onArticleClick: (Article) -> Unit,
    onToggleTheme: () -> Unit,
    isDarkTheme: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {Text(stringResource(Res.string.app_title)) },
            actions = {
                TextButton(onClick = onToggleTheme) {
                    Text(if (isDarkTheme) "Light" else "Dark")
                }
            }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(articles) { article ->
                ArticleCard(
                    article = article,
                    onClick = { onArticleClick(article) }
                )
            }
        }
    }
}


@Composable
fun ArticleCard(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (article.authors.isNotEmpty()) {
                Text(
                    text = article.authors.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = article.year.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "· ${article.journal}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    maxLines = 1
                )
            }
        }
    }
}
@Composable
fun ArticleDetailScreen(
    article: Article,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        TextButton(onClick = onBack) {
            Text("← " + stringResource(Res.string.back_button))
        }

        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (article.authors.isNotEmpty()) {
            Text(
                text = article.authors.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${article.year} · ${article.journal}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = article.type,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (article.abstract.isNotEmpty()) {
            Text(
                text = article.abstract,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
