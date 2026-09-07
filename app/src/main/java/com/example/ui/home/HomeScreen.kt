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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.*
import androidx.compose.ui.graphics.Brush

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
    val allTemplates by repository.getApprovedTemplates().collectAsState()
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

    val popularCreators = remember { repository.getPopularCreators() }

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
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFE53E3E), Color(0xFFFC7C33))
                                )
                            )
                            .clickable { onNavigateToAdmin() }
                            .padding(16.dp)
                            .testTag("admin_banner_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Admin Control Panel",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Moderate templates, categories & reports",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
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
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF4361EE), Color(0xFF7C3AED))
                                )
                            )
                            .clickable { onNavigateToCreator() }
                            .padding(16.dp)
                            .testTag("creator_banner_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Creator Studio",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Design templates, publish & track analytics",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
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

            // 4. Continue Editing (Recent Projects)
            if (userProjects.isNotEmpty() && searchQuery.isEmpty() && selectedCategoryId == "cat_all") {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Continue Editing",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "My Projects",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextTertiaryLight,
                            modifier = Modifier.clickable { onNavigateToProjects() }
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // "+ New Project" Card
                        item {
                            Box(
                                modifier = Modifier
                                    .size(width = 120.dp, height = 150.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.5.dp, BorderLight, RoundedCornerShape(20.dp))
                                    .background(SurfaceLight)
                                    .clickable { onNavigateToEditor(null, null) }
                                    .testTag("new_project_card_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(SurfaceVariantLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "New Project",
                                            tint = AccentBlue,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Blank Canvas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimaryLight
                                    )
                                    Text(
                                        text = "Create from zero",
                                        fontSize = 10.sp,
                                        color = TextTertiaryLight
                                    )
                                }
                            }
                        }

                        // Project Cards
                        items(userProjects.take(6)) { project ->
                            Card(
                                modifier = Modifier
                                    .size(width = 120.dp, height = 150.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { onNavigateToEditor(null, project.projectId) }
                                    .testTag("recent_proj_${project.projectId}"),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                            .background(Color(0xFF1E293B))
                                    ) {
                                        PosterCanvasView(
                                            modifier = Modifier.fillMaxSize(),
                                            canvasWidth = project.canvasWidth,
                                            canvasHeight = project.canvasHeight,
                                            background = project.background,
                                            elements = project.elements,
                                            fieldValues = project.fieldValues,
                                            isInteractive = false
                                        )

                                        // Status badge
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (project.isDraft) Color(0xFFF59E0B) else Color(0xFF059669))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = if (project.isDraft) "Draft" else "Saved",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = project.projectName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimaryLight,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Tap to edit",
                                            fontSize = 9.sp,
                                            color = TextTertiaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // 5. Featured Templates (Horizontal Carousel)
            if (featuredTemplates.isNotEmpty() && searchQuery.isEmpty() && selectedCategoryId == "cat_all") {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Featured Templates",
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

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(featuredTemplates) { tmpl ->
                            Box(modifier = Modifier.width(180.dp)) {
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
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // 6. Trending Templates (Grid or filtered results)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedCategoryId != "cat_all") "Matching Templates" else "Trending Templates",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "Explore",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiaryLight,
                        modifier = Modifier.clickable { onNavigateToTemplates() }
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    val displayList = if (searchQuery.isNotEmpty() || selectedCategoryId != "cat_all") {
                        filteredTemplates
                    } else {
                        allTemplates.sortedByDescending { it.usageCount }.take(6)
                    }

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

            // 7. Popular Creators Section
            if (searchQuery.isEmpty() && selectedCategoryId == "cat_all") {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Popular Creators",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(popularCreators) { creator ->
                            Card(
                                modifier = Modifier
                                    .width(200.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .clickable { onNavigateToTemplates() },
                                shape = RoundedCornerShape(18.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(AccentBlueBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = creator.name.take(1),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = AccentBlue
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = creator.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryLight,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${creator.totalTemplatesCreated} Templates",
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = creator.bio,
                                        fontSize = 11.sp,
                                        color = TextTertiaryLight,
                                        maxLines = 2
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { onNavigateToEditor(null, null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 84.dp, end = 20.dp)
                .size(58.dp)
                .testTag("fab_quick_create"),
            shape = RoundedCornerShape(18.dp),
            containerColor = AccentBlue,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Quick Create Poster",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}
