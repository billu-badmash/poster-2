package com.example.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.data.repository.PosterRepository
import com.example.ui.components.EmptyStateView
import com.example.ui.components.TemplateCard
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextTertiaryLight

@Composable
fun FavoritesScreen(
    repository: PosterRepository,
    onNavigateToTemplateDetail: (String) -> Unit,
    onExploreTemplates: () -> Unit
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()
    val favorites by repository.getFavoriteTemplates(currentUser.id).collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SAVED TEMPLATES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextTertiaryLight,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Favorites",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
            }
        }

        if (favorites.isEmpty()) {
            EmptyStateView(
                title = "No Favorites Yet",
                message = "Tap the heart icon on any template to quickly access it here later.",
                icon = Icons.Default.Favorite,
                buttonText = "Explore Templates",
                onButtonClick = onExploreTemplates
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favorites, key = { it.templateId }) { template ->
                    TemplateCard(
                        template = template,
                        isFavorite = true,
                        onCardClick = { onNavigateToTemplateDetail(template.templateId) },
                        onFavoriteToggle = {
                            scope.launch {
                                repository.toggleFavorite(template.templateId, currentUser.id)
                            }
                        }
                    )
                }
            }
        }
    }
}
