package com.example.ui.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.data.repository.PosterRepository
import com.example.domain.models.Category
import com.example.domain.models.Project
import com.example.domain.models.Template
import com.example.domain.models.UserProfile
import com.example.domain.models.UserRole
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.CleanHeader
import com.example.ui.components.CleanSearchBar
import com.example.ui.components.PosterCanvasView
import com.example.ui.components.TemplateCard
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight

@Composable
fun HomeScreen(
    repository: PosterRepository,
    onNavigateToTemplateDetail: (String) -> Unit,
    onNavigateToGenerator: (String) -> Unit,
    onNavigateToEditor: (String?, String?) -> Unit,
    onNavigateToTemplates: () -> Unit,
    onNavigateToProjects: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToCreator: () -> Unit
) {
    val currentUser by repository.currentUser.collectAsState()
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val allTemplates by repository.getApprovedTemplates().collectAsState(initial = emptyList())
    val categories by repository.categories.collectAsState()
    val userProjects by repository.getProjectsByUser(currentUser.id).collectAsState(initial = emptyList())
    val favorites by repository.getFavoriteTemplates(currentUser.id).collectAsState(initial = emptyList())
    val notifications by repository.getNotifications(currentUser.id).collectAsState(initial = emptyList())

    val unreadNotificationsCount = notifications.count { !it.isRead }
    val favoriteIds = remember(favorites) { favorites.map { it.templateId }.toSet() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("cat_all") }

    val filteredTemplates = remember(allTemplates, searchQuery, selectedCategoryId) {
        allTemplates.filter { template ->
            val matchesCategory = selectedCategoryId == "cat_all" || template.categoryId == selectedCategoryId
            val matchesQuery = searchQuery.isEmpty() ||
                    template.name.contains(searchQuery, ignoreCase = true) ||
                    template.categoryName.contains(searchQuery, ignoreCase = true) ||
                    template.creatorName.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val featuredTemplates = remember(allTemplates) {
        allTemplates.filter { it.isFeatured }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. Header (Greeting, Avatar, Role badge & switch dropdown)
            item {
                CleanHeader(
                    user = currentUser,
                    onRoleSwitch = { newRole ->
                        repository.switchRole(newRole)
                    },
                    onNotificationsClick = onNavigateToNotifications,
                    onProfileClick = onNavigateToProfile,
                    unreadNotificationCount = unreadNotificationsCount
                )
            }

            // Role quick access banner for Admin or Creator
            if (currentUser.role == UserRole.ADMIN) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFEF2F2))
                            .border(1.dp, Color(0xFFFCA5A5), RoundedCornerShape(16.dp))
                            .clickable { onNavigateToAdmin() }
                            .padding(14.dp)
                            .testTag("admin_banner_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🛡️ Admin Control Panel",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                                Text(
                                    text = "Moderate pending templates, categories & reports",
                                    fontSize = 11.sp,
                                    color = Color(0xFFB91C1C)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF991B1B),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else if (currentUser.role == UserRole.CREATOR) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEFF6FF))
                            .border(1.dp, Color(0xFF93C5FD), RoundedCornerShape(16.dp))
                            .clickable { onNavigateToCreator() }
                            .padding(14.dp)
                            .testTag("creator_banner_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "🎨 Creator Studio Dashboard",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E40AF)
                                )
                                Text(
                                    text = "Design templates, define dynamic fields & track analytics",
                                    fontSize = 11.sp,
                                    color = Color(0xFF2563EB)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF1E40AF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 2. Search Bar
            item {
                CleanSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // 3. Category Filter Chips
            item {
                CategoryFilterRow(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onSelectCategory = { selectedCategoryId = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Featured Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedCategoryId != "cat_all") "Matching Templates" else "Featured Templates",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "See All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiaryLight,
                        modifier = Modifier.clickable { onNavigateToTemplates() }
                    )
                }
            }

            // 5. Featured 2-Column Grid (or Search Results)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    val displayList = if (searchQuery.isNotEmpty() || selectedCategoryId != "cat_all") {
                        filteredTemplates
                    } else {
                        featuredTemplates.ifEmpty { allTemplates.take(4) }
                    }

                    // Display rows of 2
                    val chunked = displayList.chunked(2)
                    for (row in chunked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            for (tmpl in row) {
                                Box(modifier = Modifier.weight(1f)) {
                                    TemplateCard(
                                        template = tmpl,
                                        isFavorite = tmpl.templateId in favoriteIds,
                                        onCardClick = { onNavigateToTemplateDetail(tmpl.templateId) },
                                        onFavoriteToggle = {
                                            scope.launch {
                                                repository.toggleFavorite(tmpl.templateId, currentUser.id)
                                            }
                                        }
                                    )
                                }
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 6. Recent Projects Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Projects",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    if (userProjects.isNotEmpty()) {
                        Text(
                            text = "View All",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextTertiaryLight,
                            modifier = Modifier.clickable { onNavigateToProjects() }
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick "+ New Project" blank canvas card
                    item {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(2.dp, Color(0xFFE5E7EB), RoundedCornerShape(22.dp))
                                .background(SurfaceLight)
                                .clickable { onNavigateToEditor(null, null) }
                                .testTag("new_project_canvas_btn"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Project",
                                    tint = TextTertiaryLight,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "New Project",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }

                    // Existing Projects
                    items(userProjects) { project ->
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(1.dp, CardBorderLight, RoundedCornerShape(22.dp))
                                .background(Color(0xFF1E293B))
                                .clickable { onNavigateToEditor(null, project.projectId) }
                                .testTag("project_item_${project.projectId}")
                        ) {
                            // Poster mini canvas
                            PosterCanvasView(
                                modifier = Modifier.fillMaxSize(),
                                canvasWidth = project.canvasWidth,
                                canvasHeight = project.canvasHeight,
                                background = project.background,
                                elements = project.elements,
                                fieldValues = project.fieldValues,
                                isInteractive = false
                            )

                            // Title overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = project.projectName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button (+) for Quick Create / Custom Poster
        FloatingActionButton(
            onClick = { onNavigateToEditor(null, null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .size(56.dp)
                .testTag("fab_quick_create"),
            shape = RoundedCornerShape(18.dp),
            containerColor = AccentBlack,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Quick Create Poster",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
