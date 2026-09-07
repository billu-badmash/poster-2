package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.*

@Composable
fun CleanHeader(
    user: UserProfile,
    onRoleSwitch: (UserRole) -> Unit,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    unreadNotificationCount: Int = 0
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEEF1FF), BackgroundLight)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "GOOD MORNING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentBlue,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showRoleMenu = true }
                ) {
                    Text(
                        text = user.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val roleBg = when (user.role) {
                        UserRole.ADMIN -> AccentRedBg
                        UserRole.CREATOR -> AccentBlueBg
                        UserRole.USER -> SurfaceVariantLight
                    }
                    val roleColor = when (user.role) {
                        UserRole.ADMIN -> AccentRed
                        UserRole.CREATOR -> AccentBlue
                        UserRole.USER -> TextSecondaryLight
                    }
                    val roleIcon = when (user.role) {
                        UserRole.ADMIN -> "Shield"
                        UserRole.CREATOR -> "Art"
                        UserRole.USER -> "User"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(roleBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
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
                        onClick = { onRoleSwitch(UserRole.USER); showRoleMenu = false },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Creator View (Design & Publish)") },
                        onClick = { onRoleSwitch(UserRole.CREATOR); showRoleMenu = false },
                        leadingIcon = { Icon(Icons.Default.Brush, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Admin View (Moderate & Manage)") },
                        onClick = { onRoleSwitch(UserRole.ADMIN); showRoleMenu = false },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Notification Button with badge
                Box(modifier = Modifier.size(46.dp), contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SurfaceLight)
                            .shadow(4.dp, CircleShape)
                            .clickable { onNotificationsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = TextPrimaryLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (unreadNotificationCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(14.dp)
                                .background(AccentRed, CircleShape)
                                .border(2.dp, SurfaceLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (unreadNotificationCount > 9) "9+" else unreadNotificationCount.toString(),
                                fontSize = 7.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val avatarScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.92f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                    label = "avatar_scale"
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(avatarScale)
                        .background(
                            Brush.linearGradient(colors = listOf(GradStart, GradEnd)),
                            CircleShape
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(SurfaceLight)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) { onProfileClick() }
                            .testTag("profile_avatar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AccentBlue
                        )
                    }
                }
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
                .height(54.dp)
                .shadow(6.dp, RoundedCornerShape(18.dp))
                .testTag("search_input_field"),
            shape = RoundedCornerShape(18.dp),
            placeholder = {
                Text(text = placeholder, fontSize = 14.sp, color = TextTertiaryLight)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = AccentBlue,
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
                focusedTextColor = TextPrimaryLight,
                unfocusedTextColor = TextPrimaryLight,
                focusedContainerColor = SurfaceLight,
                unfocusedContainerColor = SurfaceLight,
                disabledContainerColor = SurfaceLight,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = AccentBlue
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

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) AccentBlue else SurfaceLight,
                animationSpec = tween(durationMillis = 200),
                label = "chip_bg_${category.id}"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else TextSecondaryLight,
                animationSpec = tween(durationMillis = 200),
                label = "chip_text_${category.id}"
            )
            val elevation by animateDpAsState(
                targetValue = if (isSelected) 6.dp else 2.dp,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "chip_elev_${category.id}"
            )

            Box(
                modifier = Modifier
                    .shadow(elevation, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(bgColor)
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "card_scale"
    )
    val cardElevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 6.dp,
        animationSpec = tween(150),
        label = "card_elev"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .shadow(cardElevation, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onCardClick() }
            .testTag("template_card_${template.templateId}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                PosterCanvasView(
                    modifier = Modifier.fillMaxSize(),
                    canvasWidth = template.canvasWidth,
                    canvasHeight = template.canvasHeight,
                    background = template.background,
                    elements = template.elements,
                    isInteractive = false
                )

                // Bottom gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.28f))
                            )
                        )
                )

                // PRO Badge
                if (template.isPro) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.linearGradient(colors = listOf(GradWarmStart, GradWarmEnd))
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = "PRO", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                    }
                }

                // Animated Favorite button
                val favScale by animateFloatAsState(
                    targetValue = if (isFavorite) 1.2f else 1f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                    label = "fav_scale"
                )
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(34.dp)
                        .align(Alignment.TopStart)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.92f))
                        .clickable { onFavoriteToggle() }
                        .testTag("favorite_btn_${template.templateId}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) AccentRed else Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp).scale(favScale)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = template.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFF8E1))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(text = String.format("%.1f", template.rating), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF92400E))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
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
            .shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = SurfaceLight,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(icon = Icons.Filled.Home, label = "Home", isSelected = currentRoute == "home", onClick = { onNavigate("home") }, tag = "nav_home")
            BottomNavItem(icon = Icons.Filled.GridView, label = "Templates", isSelected = currentRoute == "templates", onClick = { onNavigate("templates") }, tag = "nav_templates")
            BottomNavItem(icon = Icons.Filled.Folder, label = "Projects", isSelected = currentRoute == "projects", onClick = { onNavigate("projects") }, tag = "nav_projects")
            BottomNavItem(icon = Icons.Filled.Favorite, label = "Favorites", isSelected = currentRoute == "favorites", onClick = { onNavigate("favorites") }, tag = "nav_favorites")
            BottomNavItem(icon = Icons.Filled.Person, label = "Profile", isSelected = currentRoute == "profile", onClick = { onNavigate("profile") }, tag = "nav_profile")
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val iconScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.85f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "nav_icon_scale"
    )
    val pillWidth by animateDpAsState(
        targetValue = if (isSelected) 48.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "nav_pill_width"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .height(3.dp)
                .width(pillWidth)
                .clip(RoundedCornerShape(2.dp))
                .background(Brush.horizontalGradient(colors = listOf(GradStart, GradEnd)))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AccentBlue else TextTertiaryLight,
            modifier = Modifier.size(22.dp).scale(iconScale)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AccentBlue else TextTertiaryLight
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
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(Brush.linearGradient(colors = listOf(AccentBlueBg, AccentPurpleBg)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(38.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimaryLight)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = message, fontSize = 13.sp, color = TextSecondaryLight, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onButtonClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                modifier = Modifier.shadow(6.dp, RoundedCornerShape(14.dp))
            ) {
                Text(buttonText, fontWeight = FontWeight.Bold)
            }
        }
    }
}
