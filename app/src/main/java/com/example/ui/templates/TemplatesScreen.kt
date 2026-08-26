package com.example.ui.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.launch
import com.example.data.repository.PosterRepository
import com.example.domain.models.Template
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.CleanSearchBar
import com.example.ui.components.EmptyStateView
import com.example.ui.components.TemplateCard
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight

enum class SortOption(val label: String) {
    POPULAR("Popular"),
    LATEST("Latest"),
    MOST_USED("Most Used"),
    HIGHEST_RATED("Highest Rated")
}

@Composable
fun TemplatesScreen(
    repository: PosterRepository,
    onNavigateToTemplateDetail: (String) -> Unit,
    initialCategoryId: String = "cat_all"
) {
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()
    val allTemplates by repository.getApprovedTemplates().collectAsState(initial = emptyList())
    val categories by repository.categories.collectAsState()
    val favorites by repository.getFavoriteTemplates(currentUser.id).collectAsState(initial = emptyList())
    val favoriteIds = remember(favorites) { favorites.map { it.templateId }.toSet() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf(initialCategoryId) }
    var sortOption by remember { mutableStateOf(SortOption.POPULAR) }
    var showSortMenu by remember { mutableStateOf(false) }

    val filteredAndSorted = remember(allTemplates, searchQuery, selectedCategoryId, sortOption) {
        val filtered = allTemplates.filter { template ->
            val matchesCategory = selectedCategoryId == "cat_all" || template.categoryId == selectedCategoryId
            val matchesQuery = searchQuery.isEmpty() ||
                    template.name.contains(searchQuery, ignoreCase = true) ||
                    template.categoryName.contains(searchQuery, ignoreCase = true) ||
                    template.creatorName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }

        when (sortOption) {
            SortOption.POPULAR -> filtered.sortedByDescending { it.favoriteCount + it.usageCount }
            SortOption.LATEST -> filtered.sortedByDescending { it.createdAt }
            SortOption.MOST_USED -> filtered.sortedByDescending { it.usageCount }
            SortOption.HIGHEST_RATED -> filtered.sortedByDescending { it.rating }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "EXPLORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextTertiaryLight,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Template Library",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
            }

            // Sort Pill Button
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorderLight, RoundedCornerShape(14.dp))
                        .clickable { showSortMenu = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("sort_filter_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = TextPrimaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = sortOption.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight
                        )
                    }
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOption.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                sortOption = option
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Search Bar
        CleanSearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Search 100+ designer templates..."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Categories
        CategoryFilterRow(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onSelectCategory = { selectedCategoryId = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of Templates
        if (filteredAndSorted.isEmpty()) {
            EmptyStateView(
                title = "No Templates Found",
                message = "Try searching for different keywords or select another category.",
                buttonText = "Clear Filters",
                onButtonClick = {
                    searchQuery = ""
                    selectedCategoryId = "cat_all"
                }
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredAndSorted, key = { it.templateId }) { template ->
                    TemplateCard(
                        template = template,
                        isFavorite = template.templateId in favoriteIds,
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
