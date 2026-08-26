package com.example.ui.projects

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.PosterRepository
import com.example.domain.models.Project
import com.example.ui.components.EmptyStateView
import com.example.ui.components.PosterCanvasView
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight
import com.example.utils.CanvasUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsScreen(
    repository: PosterRepository,
    onOpenProjectInEditor: (String) -> Unit,
    onCreateNew: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()
    val projects by repository.getProjectsByUser(currentUser.id).collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("All Projects", "Completed", "Drafts")

    val displayedProjects = remember(projects, selectedTab) {
        when (selectedTab) {
            1 -> projects.filter { !it.isDraft }
            2 -> projects.filter { it.isDraft }
            else -> projects
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                        text = "YOUR WORKSPACE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiaryLight,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "My Projects",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorderLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${projects.size} saved",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                }
            }

            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BackgroundLight,
                contentColor = PrimaryBlack,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (displayedProjects.isEmpty()) {
                EmptyStateView(
                    title = "No Projects Yet",
                    message = "Generate posters from templates or start with a blank canvas.",
                    buttonText = "Create New Poster",
                    onButtonClick = onCreateNew
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 90.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedProjects, key = { it.projectId }) { project ->
                        ProjectItemCard(
                            project = project,
                            onOpen = { onOpenProjectInEditor(project.projectId) },
                            onShare = {
                                val bitmap = CanvasUtils.renderPosterBitmap(
                                    width = project.canvasWidth,
                                    height = project.canvasHeight,
                                    background = project.background,
                                    elements = project.elements,
                                    fieldValues = project.fieldValues
                                )
                                val uri = CanvasUtils.saveBitmapToCache(context, bitmap, "${project.projectName}.png")
                                if (uri != null) {
                                    CanvasUtils.shareImageUri(context, uri, "Share '${project.projectName}'")
                                }
                            },
                            onDelete = {
                                scope.launch {
                                    repository.deleteProject(project.projectId)
                                    Toast.makeText(context, "Project deleted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onCreateNew,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .size(54.dp)
                .testTag("fab_new_project"),
            shape = RoundedCornerShape(16.dp),
            containerColor = AccentBlack,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Project")
        }
    }
}

@Composable
private fun ProjectItemCard(
    project: Project,
    onOpen: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(project.updatedAt) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(project.updatedAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(22.dp))
            .clickable { onOpen() }
            .testTag("project_card_${project.projectId}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
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
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.projectName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TextTertiaryLight
                )

                Row {
                    IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = TextSecondaryLight, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
