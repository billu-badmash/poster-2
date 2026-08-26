package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.domain.models.Category
import com.example.domain.models.Template
import com.example.domain.models.UserProfile
import com.example.domain.models.UserRole
import com.example.ui.theme.AccentBlack
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentOrangeBg
import com.example.ui.theme.BackgroundLight
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.PrimaryBlack
import com.example.ui.theme.SurfaceLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.TextTertiaryLight

@Composable
fun CleanHeader(
    user: UserProfile,
    onRoleSwitch: (UserRole) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    unreadNotificationCount: Int = 0
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "GOOD MORNING",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextTertiaryLight,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showRoleMenu = true }
            ) {
                Text(
                    text = user.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.width(6.dp))
                // Role Badge
                val roleBg = when (user.role) {
                    UserRole.ADMIN -> Color(0xFFFEF2F2)
                    UserRole.CREATOR -> Color(0xFFEFF6FF)
                    UserRole.USER -> Color(0xFFF3F4F6)
                }
                val roleColor = when (user.role) {
                    UserRole.ADMIN -> Color(0xFFDC2626)
                    UserRole.CREATOR -> Color(0xFF2563EB)
                    UserRole.USER -> Color(0xFF4B5563)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(roleBg)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = user.role.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = roleColor
                    )
                }
            }

            DropdownMenu(
                expanded = showRoleMenu,
                onDismissRequest = { showRoleMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("User View (Browse & Generate)") },
                    onClick = {
                        onRoleSwitch(UserRole.USER)
                        showRoleMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Creator View (Design & Publish)") },
                    onClick = {
                        onRoleSwitch(UserRole.CREATOR)
                        showRoleMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.Brush, contentDescription = null) }
                )
                DropdownMenuItem(
                    text = { Text("Admin View (Moderate & Manage)") },
                    onClick = {
                        onRoleSwitch(UserRole.ADMIN)
                        showRoleMenu = false
                    },
                    leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) }
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Notifications Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceLight)
                    .border(1.dp, CardBorderLight, CircleShape)
                    .clickable { onNotificationsClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = TextPrimaryLight,
                    modifier = Modifier.size(20.dp)
                )
                if (unreadNotificationCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // User Avatar Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(SurfaceLight)
                    .border(1.dp, CardBorderLight, CircleShape)
                    .clickable { onProfileClick() }
                    .testTag("profile_avatar_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.take(1).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlack
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CleanSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search templates, categories, events..."
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .shadow(2.dp, RoundedCornerShape(16.dp))
                .testTag("search_input_field"),
            shape = RoundedCornerShape(16.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = TextTertiaryLight
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = TextTertiaryLight,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = TextSecondaryLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SurfaceLight,
                disabledContainerColor = SurfaceLight,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun CategoryFilterRow(
    categories: List<Category>,
    selectedCategoryId: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId
            val bg = if (isSelected) AccentBlack else SurfaceLight
            val textColor = if (isSelected) Color.White else TextSecondaryLight
            val borderModifier = if (isSelected) Modifier else Modifier.border(1.dp, CardBorderLight, RoundedCornerShape(24.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .then(borderModifier)
                    .background(bg)
                    .clickable { onSelectCategory(category.id) }
                    .padding(horizontal = 18.dp, vertical = 10.dp)
                    .testTag("category_pill_${category.id}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            }
        }
    }
}

@Composable
fun TemplateCard(
    template: Template,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, CardBorderLight, RoundedCornerShape(24.dp))
            .clickable { onCardClick() }
            .testTag("template_card_${template.templateId}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Preview container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(18.dp))
            ) {
                // Canvas preview
                PosterCanvasView(
                    modifier = Modifier.fillMaxSize(),
                    canvasWidth = template.canvasWidth,
                    canvasHeight = template.canvasHeight,
                    background = template.background,
                    elements = template.elements,
                    isInteractive = false
                )

                // PRO Badge
                if (template.isPro) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlack
                        )
                    }
                }

                // Favorite Heart Button
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(34.dp)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                        .clickable { onFavoriteToggle() }
                        .testTag("favorite_btn_${template.templateId}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFEF4444) else Color(0xFF6B7280),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info details
            Text(
                text = template.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "By ${template.creatorName}",
                    fontSize = 11.sp,
                    color = TextTertiaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", template.rating),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CleanBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp),
        color = SurfaceLight,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Home,
                label = "Home",
                isSelected = currentRoute == "home",
                onClick = { onNavigate("home") },
                tag = "nav_home"
            )
            BottomNavItem(
                icon = Icons.Filled.GridView,
                label = "Templates",
                isSelected = currentRoute == "templates",
                onClick = { onNavigate("templates") },
                tag = "nav_templates"
            )
            BottomNavItem(
                icon = Icons.Filled.Folder,
                label = "Projects",
                isSelected = currentRoute == "projects",
                onClick = { onNavigate("projects") },
                tag = "nav_projects"
            )
            BottomNavItem(
                icon = Icons.Filled.Favorite,
                label = "Favorites",
                isSelected = currentRoute == "favorites",
                onClick = { onNavigate("favorites") },
                tag = "nav_favorites"
            )
            BottomNavItem(
                icon = Icons.Filled.Person,
                label = "Profile",
                isSelected = currentRoute == "profile",
                onClick = { onNavigate("profile") },
                tag = "nav_profile"
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AccentBlack else Color(0xFF9CA3AF),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AccentBlack else Color(0xFF9CA3AF)
        )
    }
}

@Composable
fun EmptyStateView(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.Folder,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFF3F4F6)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryLight
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            fontSize = 13.sp,
            color = TextSecondaryLight,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
            ) {
                Text(buttonText, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
